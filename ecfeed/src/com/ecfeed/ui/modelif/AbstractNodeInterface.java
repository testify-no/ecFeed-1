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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.application.ApplicationContext;
//import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.operations.AbstractNodeOperationSetProperty;
import com.ecfeed.core.adapter.operations.BulkOperation;
import com.ecfeed.core.adapter.operations.FactoryRemoveOperation;
import com.ecfeed.core.adapter.operations.FactoryRenameOperation;
import com.ecfeed.core.adapter.operations.FactoryShiftOperation;
import com.ecfeed.core.adapter.operations.GenericAddChildrenOperation;
import com.ecfeed.core.adapter.operations.GenericRemoveNodesOperation;
import com.ecfeed.core.adapter.operations.GenericSetCommentsOperation;
import com.ecfeed.core.adapter.operations.GenericShiftOperation;
import com.ecfeed.core.adapter.operations.OperationNames;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.JavaDocSupport;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.TextAreaDialog;
//import com.ecfeed.ui.editor.TypeConverter;
import com.ecfeed.ui.editor.TypeConverter;

public class AbstractNodeInterface {

	private IJavaProjectProvider fJavaProjectProvider;
	private AbstractNode fNode;
	private EclipseImplementationStatusResolver fStatusResolver;
	private ITypeAdapterProvider fAdapterProvider;
	private OperationExecuter fOperationExecuter;

	public AbstractNodeInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		fJavaProjectProvider = javaProjectProvider;
		fStatusResolver = new EclipseImplementationStatusResolver(javaProjectProvider);
		fAdapterProvider = new EclipseTypeAdapterProvider();
		fOperationExecuter = new OperationExecuter(updateContext);
	}

	public void setOwnNode(AbstractNode node) {
		fNode = node;
	}

	public EImplementationStatus getImplementationStatus(AbstractNode node) {
		return fStatusResolver.getImplementationStatus(node);
	}

	public EImplementationStatus getImplementationStatus() {
		return getImplementationStatus(fNode);
	}

	static public boolean validateName(String name) {
		return true;
	}

//<<<<<<< HEAD
//	public String getName() {
//		return fNode.getName();
//	}
//
//	protected IFileInfoProvider getFileInfoProvider() {
//		return fFileInfoProvider;
//	}

//	public boolean setName(String newName) {
//		if (newName.equals(getName())) {
//			return false;
//		}
//		String problemTitle = "";
//		try {
//			problemTitle = (String) fNode.accept(new RenameParameterProblemTitleProvider());
//		} catch (Exception e) {
//			SystemLogger.logCatch(e.getMessage());
//		}
//		ApplicationContext.setSimplifiedUI(true);
//		if(ApplicationContext.getSimplifiedUI()){
//			TypeConverter typeConverter = new TypeConverter(newName);
//			newName = typeConverter.convertToValidJaveIdentifier();
//			return execute(FactoryRenameOperation.getRenameOperation(fNode, newName), problemTitle);
//		} else {
//			TypeConverter typeConverter = new TypeConverter(newName);
//			newName = typeConverter.getString();
//			return execute(FactoryRenameOperation.getRenameOperation(fNode, newName), problemTitle);
//		}
//	}
//	public boolean setName(String newName) {
//		if (newName.equals(getName())) {
//=======
	public String getNodeName(){
		return fNode.getName();
	}

	public boolean setName(String newName){
		if(newName.equals(getNodeName())){
//>>>>>>> master
			return false;
		}
		String problemTitle = "";
		try{
			problemTitle = (String)fNode.accept(new RenameParameterProblemTitleProvider());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		
		if(ApplicationContext.getSimplifiedUI()){
			TypeConverter typeConverter = new TypeConverter(newName);
			newName = typeConverter.convertToValidJaveIdentifier();
			return getOperationExecuter().execute(FactoryRenameOperation.getRenameOperation(fNode, newName), problemTitle);
		} else {
			TypeConverter typeConverter = new TypeConverter(newName);
			newName = typeConverter.getString();
			return getOperationExecuter().execute(FactoryRenameOperation.getRenameOperation(fNode, newName), problemTitle);
		}
//		return getOperationExecuter().execute(FactoryRenameOperation.getRenameOperation(fNode, newName), problemTitle);
	}

	public boolean setProperty(NodePropertyDefs.PropertyId propertyId, String value) {
		String oldValue = fNode.getPropertyValue(propertyId);

		if (StringHelper.isEqual(oldValue, value)) {
			return false;
		}

//<<<<<<< HEAD
//		IModelOperation operation = new AbstractNodeOperationSetProperty(propertyId, value, fNode);
//		return execute(operation, Messages.DIALOG_SET_PROPERTY_PROBLEM_TITLE);
//	}
//=======
		IModelOperation operation = new AbstractNodeOperationSetProperty(propertyId, value, fNode); 
		return getOperationExecuter().execute(operation, Messages.DIALOG_SET_PROPERTY_PROBLEM_TITLE);
	}	
//>>>>>>> master

	public boolean editComments() {

		TextAreaDialog dialog = new TextAreaDialog(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_EDIT_COMMENTS_TITLE, Messages.DIALOG_EDIT_COMMENTS_MESSAGE, getComments());

		if (dialog.open() == IDialogConstants.OK_ID) {
			return setComments(dialog.getText());
		}
		return false;
	}

	public boolean setComments(String comments) {

		if (comments.equals(getComments())) {
			return false;
		}

		return getOperationExecuter().execute(
				new GenericSetCommentsOperation(fNode, comments), 
				Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
	}

	public String getComments() {
		if (fNode != null && fNode.getDescription() != null) {
			return fNode.getDescription();
		}
		return "";
	}

//<<<<<<< HEAD
//	public boolean remove() {
//		return execute(FactoryRemoveOperation.getRemoveOperation(fNode, fAdapterProvider, true),
//				Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
//	}
//
//	public boolean removeChildren(Collection<? extends AbstractNode> children, String message) {
//		if (children == null || children.size() == 0) {
//=======
	public boolean remove(){
		return getOperationExecuter().execute(FactoryRemoveOperation.getRemoveOperation(fNode, fAdapterProvider, true), Messages.DIALOG_REMOVE_NODE_PROBLEM_TITLE);
	}

	public boolean removeChildren(Collection<? extends AbstractNode> children, String message){

		if (children == null || children.size() == 0) { 
//>>>>>>> master
			return false;
		}

		for (AbstractNode node : children) {
			if (node.getParent() != fNode) { 
				return false;
			}
		}

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				new GenericRemoveNodesOperation(children, fAdapterProvider, true, fNode, fNode);

		return getOperationExecuter().execute(genericRemoveNodesOperation, message);
	}

	public String canAddChildren(Collection<? extends AbstractNode> children) {
		return null; // error message if can not
	}

	public boolean addChildren(Collection<? extends AbstractNode> children) {
		IModelOperation operation = new GenericAddChildrenOperation(fNode, children, fAdapterProvider, true);
		return getOperationExecuter().execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean addChildren(Collection<? extends AbstractNode> children, int index) {
		IModelOperation operation;
		if (index == -1) {
			operation = new GenericAddChildrenOperation(fNode, children, fAdapterProvider, true);
		} else {
			operation = new GenericAddChildrenOperation(fNode, children, index, fAdapterProvider, true);
		}
		return getOperationExecuter().execute(operation, Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	public boolean pasteEnabled(Collection<? extends AbstractNode> pasted) {
		return pasteEnabled(pasted, -1);
	}

	public boolean pasteEnabled(Collection<? extends AbstractNode> pasted, int index) {
		GenericAddChildrenOperation operation;
		if (index == -1) {
			operation = new GenericAddChildrenOperation(fNode, pasted, fAdapterProvider, true);
		} else {
			operation = new GenericAddChildrenOperation(fNode, pasted, index, fAdapterProvider, true);
		}
		return operation.enabled();
	}

	public boolean moveUpDown(boolean up) {
		try {
			GenericShiftOperation operation = FactoryShiftOperation
					.getShiftOperation(Arrays.asList(new AbstractNode[] { fNode }), up);
			if (operation.getShift() > 0) {
				return executeMoveOperation(operation);
			}
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return false;
	}

	protected IJavaProjectProvider getJavaProjectProvider() {
		return fJavaProjectProvider;
	}

	protected OperationExecuter getOperationExecuter() {
		return fOperationExecuter;
	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return getOperationExecuter().execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
	}

	protected ITypeAdapterProvider getAdapterProvider() {
		return fAdapterProvider;
	}

	public AbstractNode getOwnNode() {
		return fNode;
	}

//<<<<<<< HEAD
//	public boolean goToImplementationEnabled() {
//		return getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
//=======
	public boolean goToImplementationEnabled(){
		EImplementationStatus implemenationStatus = getImplementationStatus();
		
		return implemenationStatus != EImplementationStatus.NOT_IMPLEMENTED;
//>>>>>>> master
	}

	public void goToImplementation() {

	}

	public boolean importJavadocComments() {
		String comments = JavaDocSupport.importJavadoc(getOwnNode());
		if (comments != null) {
			return setComments(comments);
		}
		return false;
	}

	public void exportCommentsToJavadoc(String comments) {
		JavaDocSupport.exportJavadoc(getOwnNode());
	}

	public boolean importAllJavadocComments() {
		List<IModelOperation> operations = getImportAllJavadocCommentsOperations();
//<<<<<<< HEAD
//		if (operations.size() > 0) {
//			IModelOperation operation = new BulkOperation(OperationNames.SET_COMMENTS, operations, false);
//			return execute(operation, Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
//=======
		if(operations.size() > 0){
			IModelOperation operation = 
					new BulkOperation(
							OperationNames.SET_COMMENTS, operations, false, getOwnNode(), getOwnNode());

			return getOperationExecuter().execute(operation, Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
//>>>>>>> master
		}
		return false;
	}

	public boolean exportAllComments() {
		exportCommentsToJavadoc(getComments());
//<<<<<<< HEAD
//		for (AbstractNode child : getOwnNode().getChildren()) {
//			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(child, getUpdateContext(),
//					fFileInfoProvider);
//=======
		for(AbstractNode child : getOwnNode().getChildren()){
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(
							child, getOperationExecuter().getUpdateContext(), fJavaProjectProvider);
//>>>>>>> master
			nodeIf.exportAllComments();
		}
		return true;
	}

	protected List<IModelOperation> getImportAllJavadocCommentsOperations() {
//<<<<<<< HEAD
//		List<IModelOperation> result = new ArrayList<IModelOperation>();
//		String javadoc = JavaDocSupport.importJavadoc(getOwnNode());
//		if (javadoc != null && getComments() != javadoc) {
//			result.add(new GenericSetCommentsOperation(fNode, javadoc));
//		}
//		for (AbstractNode child : getOwnNode().getChildren()) {
//			AbstractNodeInterface childIf = NodeInterfaceFactory.getNodeInterface(child, getUpdateContext(),
//					fFileInfoProvider);
//=======

		List<IModelOperation> result = new ArrayList<IModelOperation>();
		String javadoc = JavaDocSupport.importJavadoc(getOwnNode());

		if(javadoc != null && getComments() != javadoc){
			result.add(new GenericSetCommentsOperation(fNode, javadoc));
		}

		for(AbstractNode child : getOwnNode().getChildren()){
			AbstractNodeInterface childIf = 
					NodeInterfaceFactory.getNodeInterface(
							child, getOperationExecuter().getUpdateContext(), fJavaProjectProvider);

//>>>>>>> master
			result.addAll(childIf.getImportAllJavadocCommentsOperations());
		}
		return result;
	}

	public boolean nodeImplementedFullyOrPartially() {
		return getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
	}

	private class RenameParameterProblemTitleProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return Messages.DIALOG_RENAME_MODEL_PROBLEM_TITLE;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Messages.DIALOG_RENAME_CONSTRAINT_PROBLEM_TITLE;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Messages.DIALOG_RENAME_CHOICE_PROBLEM_TITLE;
		}

	}

}
