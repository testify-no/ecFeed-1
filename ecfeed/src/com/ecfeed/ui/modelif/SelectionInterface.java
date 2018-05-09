/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.operations.FactoryShiftOperation;
import com.ecfeed.core.adapter.operations.GenericMoveOperation;
import com.ecfeed.core.adapter.operations.GenericRemoveNodesOperation;
import com.ecfeed.core.adapter.operations.GenericShiftOperation;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.Messages;

public class SelectionInterface extends OperationExecuter {

	private ITypeAdapterProvider fAdapterProvider;

	public SelectionInterface(IModelUpdateContext updateContext) {

		super(updateContext);

		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	private List<? extends AbstractNode> fListOfNodes;

	public void setOwnListOfNodes(List<AbstractNode> listOfNodes){

		fListOfNodes = listOfNodes;
	}

	public boolean delete() {

		if (fListOfNodes.size() > 0) {

			AbstractNode parentNode = fListOfNodes.get(0).getParent();

			GenericRemoveNodesOperation genericRemoveNodesOperation =
					new GenericRemoveNodesOperation(
							fListOfNodes, fAdapterProvider, true, parentNode, parentNode);

			return execute(genericRemoveNodesOperation, Messages.DIALOG_REMOVE_NODES_PROBLEM_TITLE);
		}

		return false;
	}

	public boolean deleteEnabled() {

		if (fListOfNodes.size() == 0) { 
			return false;
		}

		AbstractNode root = fListOfNodes.get(0).getRoot();

		for (AbstractNode selected : fListOfNodes) {
			if (selected == root) { 
				return false;
			}
		}
		return true;
	}

	public boolean move(AbstractNode newParent) {
		return move(newParent, -1);
	}

	public boolean move(AbstractNode newParent, int newIndex) {

		try {
			IModelOperation operation;
			if (newIndex == -1) {
				operation = new GenericMoveOperation(fListOfNodes, newParent, fAdapterProvider);
			} else {
				operation = new GenericMoveOperation(fListOfNodes, newParent, fAdapterProvider, newIndex);
			}

			return execute(operation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);

		} catch(ModelOperationException e) {

			return false;
		}
	}

	public boolean moveUpDown(boolean up) {

		AbstractNode parent = fListOfNodes.get(0).getParent();

		for (AbstractNode node : fListOfNodes) {
			if (node.getParent() != parent) {
				return false;
			}
		}

		try {
			IModelOperation operation = FactoryShiftOperation.getShiftOperation(fListOfNodes, up);
			executeMoveOperation(operation);
		} catch(Exception e){
			SystemLogger.logCatch(e.getMessage());
		}

		return false;
	}

	public boolean moveUpDownEnabed(boolean up) {

		AbstractNode parent = getCommonParent();

		if(parent != null) {
			try {
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(fListOfNodes, up);
				return operation.getShift() != 0;
			} catch (Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
		}
		return false;
	}

	public AbstractNode getCommonParent() {

		if(fListOfNodes == null || fListOfNodes.size() == 0) { 
			return null;
		}

		AbstractNode parent = fListOfNodes.get(0).getParent();

		for (AbstractNode node : fListOfNodes) {
			if (node.getParent() != parent) { 
				return null;
			}
		}

		return parent;
	}

	public boolean isSingleType() {

		if (fListOfNodes == null || fListOfNodes.size() == 0) { 
			return false;
		}

		Class<?> type = fListOfNodes.get(0).getClass();

		for (AbstractNode node : fListOfNodes) {
			if (node.getClass().equals(type) == false) { 
				return false;
			}
		}

		return true;
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {

		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
	}
}
