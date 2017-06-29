/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.ChoicesParentInterface;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.RootInterface;

public class AddChildActionProvider {

	private StructuredViewer fViewer;
	private IModelUpdateContext fContext;
	private IJavaProjectProvider fJavaProjectProvider;
	private static final boolean CHILD_SELECT_ON = true;
	private static final boolean CHILD_SELECT_OFF = false;


	private void reportExceptionInvalidNodeType() {
		final String MSG = "Invalid type of selected node.";
		ExceptionHelper.reportRuntimeException(MSG);
	}

	private AbstractNode getOneNode(List<AbstractNode> nodes) {
		if(nodes.size() != 1) {
			final String MSG = "Too many nodes selected for action.";
			ExceptionHelper.reportRuntimeException(MSG);
		}
		return nodes.get(0); 
	}

	private void setOwnNode(AbstractNode abstractNode, AbstractNodeInterface abstractNodeInterface) {
		if (abstractNodeInterface == null ) {
			final String MSG = "Invalid parent interface.";
			ExceptionHelper.reportRuntimeException(MSG);
		}
		abstractNodeInterface.setOwnNode(abstractNode);
	}

	private void expandCurrentTreeViewerNode() {
		if (!(fViewer instanceof TreeViewer)) {
			return;
		}

		TreeViewer treeViewer = (TreeViewer)fViewer;
		ExpandOneLevelAction expandOneLevelAction = new ExpandOneLevelAction(treeViewer);
		expandOneLevelAction.run();
	}

	private class AddGlobalParameterAction extends AbstractAddChildAction{
		private GlobalParametersParentInterface fParentIf;
		private boolean fSelectChild;

		public AddGlobalParameterAction(boolean selectChild) {
			super(ADD_GLOBAL_PARAMETER_ACTION_ID, ADD_GLOBAL_PARAMETER_ACTION_NAME, fViewer, fContext);
			fParentIf = new GlobalParametersParentInterface(fContext, fJavaProjectProvider);
			fSelectChild = selectChild;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof GlobalParametersParentNode)) {
				reportExceptionInvalidNodeType();
			}

			setOwnNode(selectedNode, fParentIf);

			AbstractNode newNode = fParentIf.addNewParameter();
			if (fSelectChild) {
				select(newNode);
			} else {
				expandCurrentTreeViewerNode();
			}
		}

		@Override
		protected GlobalParametersParentInterface getParentInterface(){
			return fParentIf;
		}

	}

	private class AddClassAction extends AbstractAddChildAction{
		private RootInterface fParentIf;
		private boolean fSelectChild; 

		public AddClassAction(boolean selectChild){
			super(ADD_CLASS_ACTION_ID, ADD_CLASS_ACTION_NAME, fViewer, fContext);
			fParentIf = new RootInterface(fContext, fJavaProjectProvider);
			fSelectChild = selectChild;
		}

		@Override
		protected RootInterface getParentInterface(){
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (selectedNode instanceof RootNode == false) {
				reportExceptionInvalidNodeType();
			}

			setOwnNode(selectedNode, fParentIf);

			AbstractNode node = getParentInterface().addNewClass();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}
	}

	private class AddMethodAction extends AbstractAddChildAction{
		private ClassInterface fParentIf;
		private boolean fSelectChild;

		public AddMethodAction(boolean selectChild) {
			super(ADD_METHOD_ACTION_ID, ADD_METHOD_ACTION_NAME, fViewer, fContext);
			fParentIf = new ClassInterface(fContext, fJavaProjectProvider);
			fSelectChild = selectChild;
		}

		@Override
		protected ClassInterface getParentInterface(){
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof ClassNode)) {
				reportExceptionInvalidNodeType();
			}

			setOwnNode(selectedNode, fParentIf);

			AbstractNode node = getParentInterface().addNewMethod();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}
	}

	private abstract class AddMethodChildAction extends AbstractAddChildAction{
		private MethodInterface fParentIf;

		public AddMethodChildAction(String id, String name) {
			super(id, name, fViewer, fContext);
			fParentIf = new MethodInterface(fContext, fJavaProjectProvider);
		}

		@Override
		protected MethodInterface getParentInterface(){
			return fParentIf;
		}

		protected void prepareRun() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof MethodNode)) {
				reportExceptionInvalidNodeType();
			}

			setOwnNode(selectedNode, fParentIf);
		}
	}

	private class AddMethodParameterAction extends AddMethodChildAction{
		private boolean fSelectChild;

		public AddMethodParameterAction(boolean selectChild) {
			super(ADD_METHOD_PARAMETER_ACTION_ID, ADD_METHOD_PARAMETER_ACTION_NAME);
			fSelectChild = selectChild;
		}

		@Override
		public void run() {
			prepareRun();

			AbstractNode node = getParentInterface().addNewParameter();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}
	}

	private class AddConstraintAction extends AddMethodChildAction{
		private boolean fSelectChild;

		public AddConstraintAction(boolean selectChild) {
			super(ADD_CONSTRAINT_ACTION_ID, ADD_CONSTRAINT_ACTION_NAME);
			fSelectChild = selectChild;
		}

		@Override
		public void run() {
			prepareRun();

			AbstractNode node = getParentInterface().addNewConstraint();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}
	}

	private class AddTestCaseAction extends AddMethodChildAction{
		private boolean fSelectChild;
		public AddTestCaseAction(boolean selectChild) {
			super(ADD_TEST_CASE_ACTION_ID, ADD_TEST_CASE_ACTION_NAME);
			fSelectChild = selectChild;
		}

		@Override
		public void run() {
			prepareRun();

			AbstractNode node = getParentInterface().addTestCase();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}
	}

	private class AddTestSuiteAction extends AddMethodChildAction{
		public AddTestSuiteAction() {
			super(ADD_TEST_SUITE_ACTION_ID, ADD_TEST_SUITE_ACTION_NAME);
		}

		@Override
		public void run() {
			prepareRun();
			getParentInterface().generateTestSuite();
		}
	}

	private class AddChoiceAction extends AbstractAddChildAction{
		private ChoicesParentInterface fParentIf;
		private boolean fSelectChild;

		private class EnableVisitor implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				return (node.isLinked() == false) && (node.isExpected() == false || JavaTypeHelper.isUserType(node.getType()));
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return true;
			}

		}

		public AddChoiceAction(IJavaProjectProvider javaProjectProvider, boolean selectChild){
			super(ADD_PARTITION_ACTION_ID, ADD_PARTITION_ACTION_NAME, fViewer, fContext);
			fParentIf = new ChoicesParentInterface(fContext, javaProjectProvider);
			fSelectChild = selectChild;
		}

		@Override
		protected ChoicesParentInterface getParentInterface() {
			return fParentIf;
		}

		@Override
		public void run() {
			AbstractNode selectedNode = getOneNode(getSelectedNodes());

			if (!(selectedNode instanceof ChoicesParentNode)) {
				reportExceptionInvalidNodeType();
			}

			setOwnNode(selectedNode, fParentIf);

			AbstractNode node = fParentIf.addNewChoice();
			if (fSelectChild) {
				select(node);
			} else {
				expandCurrentTreeViewerNode();
			}
		}

		@Override
		public boolean isEnabled(){
			if(super.isEnabled() == false) 
				return false;

			ChoicesParentNode target = (ChoicesParentNode)getSelectedNodes().get(0);

			AbstractParameterNode parameter = target.getParameter();
			try {
				return (boolean)parameter.accept(new EnableVisitor());
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
			return false;
		}
	}

	private class AddNewChilActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddClassAction(CHILD_SELECT_ON),
					new AddGlobalParameterAction(CHILD_SELECT_ON)
			});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodAction(CHILD_SELECT_ON),
					new AddGlobalParameterAction(CHILD_SELECT_ON)
			});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodParameterAction(CHILD_SELECT_ON),
					new AddConstraintAction(CHILD_SELECT_ON),
					new AddTestCaseAction(CHILD_SELECT_ON),
					new AddTestSuiteAction()
			});
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_ON)
			});
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_ON)
			});
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[] {
					new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_ON)
			});
		}
	}

	public AddChildActionProvider(
			StructuredViewer viewer, 
			IModelUpdateContext context, 
			IJavaProjectProvider javaProjectProvider) {
		fJavaProjectProvider = javaProjectProvider;
		fContext = context;
		fViewer = viewer;
	}


	@SuppressWarnings("unchecked")
	public List<AbstractAddChildAction> getPossibleActions(AbstractNode parent){
		try {
			return (List<AbstractAddChildAction>)parent.accept(new AddNewChilActionProvider());
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

	private class GetMainInsertActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return new AddClassAction(CHILD_SELECT_OFF);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new AddMethodAction(CHILD_SELECT_OFF);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new AddMethodParameterAction(CHILD_SELECT_OFF);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_OFF);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_OFF); 
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new AddChoiceAction(fJavaProjectProvider, CHILD_SELECT_OFF);
		}
	}

	public AbstractAddChildAction getMainInsertAction(AbstractNode parent) {
		try {
			return (AbstractAddChildAction)parent.accept(new GetMainInsertActionProvider());
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

}
