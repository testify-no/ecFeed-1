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

import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.editor.actions.AbstractAddChildAction;
import com.ecfeed.ui.editor.actions.AddChildActionProvider;
import com.ecfeed.ui.editor.actions.ExecuteTestCaseAction;
import com.ecfeed.ui.editor.actions.ExportOnlineAction;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.editor.actions.TestOnlineAction;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;
import com.ecfeed.ui.modelif.TestCaseInterface;
import com.ecfeed.utils.SeleniumHelper;


public class ModelMasterMenuListener extends ViewerMenuListener {

	TreeViewer fTreeViewer;
	IModelUpdateContext fModelUpdateContext;
	IJavaProjectProvider fJavaProjectProvider;
	ISelectionProvider fSelectionProvider;


	public ModelMasterMenuListener(
			Menu menu, 
			IActionProvider actionProvider, 
			TreeViewer treeViewer,
			IModelUpdateContext modelUpdateContext,
			IJavaProjectProvider javaProjectProvider,
			ISelectionProvider selectionProvider) {
		super(menu, actionProvider, selectionProvider);

		fTreeViewer = treeViewer;
		fModelUpdateContext = modelUpdateContext;
		fJavaProjectProvider = javaProjectProvider;
		fSelectionProvider = selectionProvider;
	}

	@Override
	protected void populateMenu() {

		AbstractNode firstSelectedNode = getFirstSelectedNode();

		if (firstSelectedNode == null) {
			return;
		}

		addChildAddingActions(firstSelectedNode);
		addActionsForMethod(firstSelectedNode);
		addActionsForTestCase(firstSelectedNode);
		super.populateMenu();
	}

	private void addChildAddingActions(AbstractNode abstractNode) {
		AddChildActionProvider actionProvider = 
				new AddChildActionProvider(fTreeViewer, fModelUpdateContext, fJavaProjectProvider);

		List<AbstractAddChildAction> actions = actionProvider.getPossibleActions(abstractNode);

		boolean menuItemAdded = false;
		boolean actionNameConverted = false;

		for(AbstractAddChildAction action : actions) {

			String actionName = action.getName();

			if (!actionNameConverted) {
				actionName = convertActionName(action.getName());
				actionNameConverted = true;
			}

			addMenuItem(actionName, action);
			menuItemAdded = true;
		}

		if (menuItemAdded) {
			new MenuItem(getMenu(), SWT.SEPARATOR);
		}
	}

	private String convertActionName(String actionName) {

		if (!actionName.startsWith("Add")) {
			return actionName;
		}

		final String insertKey = "INS";

		if (SystemHelper.isOperatingSystemMacOs()) {
			return actionName + "   (" + insertKey + ")";
		}
		return actionName + "\t" + insertKey;
	}

	private void addActionsForMethod(AbstractNode abstractNode) {

		if (!(abstractNode instanceof MethodNode)) {
			return;
		}

		MethodNode methodNode = (MethodNode)abstractNode;
		MethodInterface methodInterface = getMethodInterface();
		boolean isAction = false;

		if (addTestOnlineAction(methodInterface)) {
			isAction = true;
		}
		if (addExportOnlineAction(methodNode, methodInterface)) {
			isAction = true;
		}

		if (isAction) {
			new MenuItem(getMenu(), SWT.SEPARATOR);
		}
	}

	private void addActionsForTestCase(AbstractNode abstractNode) {

		if (!(abstractNode instanceof TestCaseNode)) {
			return;
		}

		TestCaseInterface testCaseInterface = getTestCaseInterface();

		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(testCaseInterface.getMethod(), null, fJavaProjectProvider);

		MethodInterface methodInterface = (MethodInterface)nodeIf; 

		if (!isActionExecutable(methodInterface)) {
			return;
		}

		ExecuteTestCaseAction action = new ExecuteTestCaseAction(fSelectionProvider, testCaseInterface);
		addMenuItem(action.getName(), action);

		new MenuItem(getMenu(), SWT.SEPARATOR);
	}

	private MethodInterface getMethodInterface() {

		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(
						getFirstSelectedNode(), null, fJavaProjectProvider);

		if (!(nodeIf instanceof MethodInterface)) {
			final String MSG = "Invalid type of node interface. Method node interface expected"; 
			ExceptionHelper.reportRuntimeException(MSG);
		}

		return (MethodInterface)nodeIf; 
	}

	private TestCaseInterface getTestCaseInterface() {

		AbstractNodeInterface nodeInterface = 
				NodeInterfaceFactory.getNodeInterface(
						getFirstSelectedNode(), null, fJavaProjectProvider);

		if (!(nodeInterface instanceof TestCaseInterface)) {
			final String MSG = "Invalid type of node interface. Test case interface expected"; 
			ExceptionHelper.reportRuntimeException(MSG);
		}

		return (TestCaseInterface)nodeInterface; 
	}

	private boolean addTestOnlineAction(MethodInterface methodInterface) {

		if (!isActionExecutable(methodInterface)) {
			return false;
		}

		TestOnlineAction testOnlineAction = 
				new TestOnlineAction(fJavaProjectProvider, fSelectionProvider, methodInterface);

		addMenuItem(testOnlineAction.getName(), testOnlineAction);
		return true;
	}

	private boolean isActionExecutable(MethodInterface methodInterface) {
		MethodNode methodNode = methodInterface.getOwnNode();

		if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
			return true;
		}

		if (ApplicationContext.isApplicationTypeLocalStandalone()) {
			return false;
		}

		EImplementationStatus methodStatus = methodInterface.getImplementationStatus();

		if (methodStatus != EImplementationStatus.IMPLEMENTED) {
			return false;
		}

		return true;
	}

	private boolean addExportOnlineAction(MethodNode methodNode, MethodInterface methodInterface) {
		if (methodNode.getParametersCount() == 0) {
			return false;
		}

		ExportOnlineAction exportOnlineAction = 
				new ExportOnlineAction(fJavaProjectProvider, fSelectionProvider, methodInterface);

		addMenuItem(exportOnlineAction.getName(), exportOnlineAction);
		return true;
	}

}

