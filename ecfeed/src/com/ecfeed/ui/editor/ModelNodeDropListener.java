/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.EclipseModelBuilder;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.NodeDnDBuffer;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;
import com.ecfeed.ui.modelif.SelectionInterface;

public class ModelNodeDropListener extends ViewerDropAdapter {

	private final IModelUpdateContext fUpdateContext;
	IFileInfoProvider fFileInfoProvider;
	private boolean fEnabled;


	protected ModelNodeDropListener(Viewer viewer, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(viewer);
		fUpdateContext = updateContext;
		fFileInfoProvider = fileInfoProvider;
		fEnabled = true;
	}

	@Override
	public boolean performDrop(Object data) {

		if(fEnabled == false) 
			return false;

		List<AbstractNode> dragged = NodeDnDBuffer.getInstance().getDraggedNodes();
		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		selectionIf.setOwnListOfNodes(dragged);

		if ((dragged.size() == 0) || (selectionIf.isSingleType() == false)) 
			return false;

		AbstractNode newParent = determineNewParent(getCurrentTarget(), getCurrentLocation());
		int index = determineNewIndex((AbstractNode)getCurrentTarget(), getCurrentLocation());

		if(newParent == null || index < 0 || index > newParent.getMaxChildIndex(dragged.get(0))) {
			return false;
		}

		switch(getCurrentOperation()) {
		case DND.DROP_COPY:
			try {
				return (boolean)newParent.accept(new CopyHandler(index));
			}catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
				return false;
			}
		case DND.DROP_MOVE:
			return selectionIf.move(newParent, index);
		case DND.DROP_LINK:
			try {
				return (boolean)newParent.accept(new LinkHandler(index, fFileInfoProvider));
			}catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
				return false;
			}
		default:
			return false;
		}
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {

		if (!fEnabled) { 
			return false;
		}

		AbstractNode parent = determineNewParent(target, getCurrentLocation());
		if (parent == null) {
			return false;
		}

		List<AbstractNode>dragged = NodeDnDBuffer.getInstance().getDraggedNodes();
		if (dragged.isEmpty()) {
			return false;
		}

		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		selectionIf.setOwnListOfNodes(dragged);
		if (!selectionIf.isSingleType()) {
			return false;
		}

		try {
			return (boolean)parent.accept(new DropValidator(operation));
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return false;
		}
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}

	protected AbstractNode determineNewParent(Object target, int location) {

		int position = determineLocation(getCurrentEvent());

		if(target instanceof AbstractNode == false) { 
			return null;
		}

		AbstractNode parent = (AbstractNode)target;

		switch(position) {
		case LOCATION_ON:
			return parent;
		case LOCATION_AFTER:
		case LOCATION_BEFORE:
			return parent.getParent();
		}

		return null;
	}

	protected int determineNewIndex(AbstractNode target, int location) {

		int position = determineLocation(getCurrentEvent());

		switch(position){
		case LOCATION_ON:
			return target.getMaxChildIndex(NodeDnDBuffer.getInstance().getDraggedNodes().get(0));
		case LOCATION_AFTER:
			return target.getMyIndex() + 1;
		case LOCATION_BEFORE:
			return target.getMyIndex();
		}

		return -1;
	}

	private class DropValidator implements IModelVisitor {

		private int fOperation;

		public DropValidator(int operation) {
			fOperation = operation;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			boolean result = false;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ClassNode;
			result |= (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof GlobalParameterNode) && ((fOperation & (DND.DROP_COPY | DND.DROP_MOVE)) != 0);
			result |= (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode) && ((fOperation & (DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK)) != 0);
			return result;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			boolean result = false;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodNode;
			result |= (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof GlobalParameterNode) && ((fOperation & (DND.DROP_COPY | DND.DROP_MOVE)) != 0);
			result |= (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode) && ((fOperation & (DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK)) != 0);
			return result;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {

			boolean result = 
					NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode && fOperation != DND.DROP_LINK;

			if (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof GlobalParameterNode) {
				if(fOperation == DND.DROP_LINK) {
					for(AbstractNode dragged : NodeDnDBuffer.getInstance().getDraggedNodes()) {
						if(node.getAncestors().contains(dragged.getParent()) == false) {
							return false;
						}
					}
				}

				result = true;
			}

			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ConstraintNode && fOperation != DND.DROP_LINK;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof TestCaseNode && fOperation != DND.DROP_LINK;

			return result;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {

			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode && fOperation != DND.DROP_LINK;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode && fOperation != DND.DROP_LINK;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode && fOperation != DND.DROP_LINK;
		}

	}


	private class CopyHandler implements IModelVisitor {

		private int fIndex;

		public CopyHandler(int index) {
			fIndex = index;
		}

		@Override
		public Object visit(RootNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);
			List<AbstractNode> children;

			if (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode) {
				children = new ArrayList<AbstractNode>();

				for(AbstractNode dragged : NodeDnDBuffer.getInstance().getDraggedNodes()) {
					children.add(new GlobalParameterNode((AbstractParameterNode)dragged));
				}
			}else {
				children = NodeDnDBuffer.getInstance().getDraggedNodesCopy();
			}

			return nodeIf.addChildren(children, fIndex);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);
			List<AbstractNode> children;

			if (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode) {

				children = new ArrayList<AbstractNode>();

				for(AbstractNode dragged : NodeDnDBuffer.getInstance().getDraggedNodes()) {
					children.add(new GlobalParameterNode((AbstractParameterNode)dragged));
				}
			}else {
				children = NodeDnDBuffer.getInstance().getDraggedNodesCopy();
			}

			return nodeIf.addChildren(children, fIndex);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);
			List<AbstractNode> children;

			if (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof GlobalParameterNode) {

				children = new ArrayList<AbstractNode>();

				for (AbstractNode dragged : NodeDnDBuffer.getInstance().getDraggedNodes()) {
					GlobalParameterNode source = (GlobalParameterNode)dragged;
					String defaultValue = new EclipseModelBuilder().getDefaultExpectedValue(source.getType());
					children.add(new MethodParameterNode((AbstractParameterNode)dragged, defaultValue, false));
				}
			}else {
				children = NodeDnDBuffer.getInstance().getDraggedNodesCopy();
			}

			return nodeIf.addChildren(children, fIndex);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);

			return nodeIf.addChildren(NodeDnDBuffer.getInstance().getDraggedNodesCopy(), fIndex);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);

			return nodeIf.addChildren(NodeDnDBuffer.getInstance().getDraggedNodesCopy(), fIndex);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);

			return nodeIf.addChildren(NodeDnDBuffer.getInstance().getDraggedNodesCopy(), fIndex);
		}

	}

	private class LinkHandler implements IModelVisitor {

		IFileInfoProvider fFileInfoProvider;
		private int fIndex;

		public LinkHandler(int index, IFileInfoProvider fileInfoProvider) {
			fIndex = index;
			fFileInfoProvider = fileInfoProvider;
		}

		@Override
		public Object visit(RootNode node) throws Exception {

			if(NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode){
				return replaceParametersWithLinks(node, NodeDnDBuffer.getInstance().getDraggedNodes());
			}

			return false;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {

			if(NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode){
				return replaceParametersWithLinks(node, NodeDnDBuffer.getInstance().getDraggedNodes());
			}

			return false;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {

			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, fUpdateContext, fFileInfoProvider);
			List<AbstractNode> children;

			if (NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof GlobalParameterNode) {
				children = new ArrayList<AbstractNode>();

				for (AbstractNode dragged : NodeDnDBuffer.getInstance().getDraggedNodes()) {
					GlobalParameterNode source = (GlobalParameterNode)dragged;
					String defaultValue = new EclipseModelBuilder().getDefaultExpectedValue(source.getType());
					children.add(new MethodParameterNode((AbstractParameterNode)dragged, defaultValue, false, true, source));
				}
			}else {
				children = NodeDnDBuffer.getInstance().getDraggedNodesCopy();
			}

			return nodeIf.addChildren(children, fIndex);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return false;
		}

		private Object replaceParametersWithLinks(GlobalParametersParentNode newParent, List<AbstractNode> originals) {

			List<MethodParameterNode> originalParameters = new ArrayList<MethodParameterNode>();

			for (AbstractNode node : originals) {
				if(node instanceof MethodParameterNode) {
					originalParameters.add((MethodParameterNode)node);
				}else {
					return false;
				}
			}

			GlobalParametersParentInterface parentIf = 
					(GlobalParametersParentInterface)NodeInterfaceFactory.getNodeInterface(
							newParent, fUpdateContext, fFileInfoProvider);

			return parentIf.replaceMethodParametersWithGlobal(originalParameters);
		}
	}

}
