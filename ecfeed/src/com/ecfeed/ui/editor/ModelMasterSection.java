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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.About2Dialog;
import com.ecfeed.ui.dialogs.CheckForUpdatesDialog;
import com.ecfeed.ui.editor.actions.AbstractAddChildAction;
import com.ecfeed.ui.editor.actions.AddChildActionProvider;
import com.ecfeed.ui.editor.actions.BasicActionRunnerProvider;
import com.ecfeed.ui.editor.actions.ExecuteTestCaseAction;
import com.ecfeed.ui.editor.actions.ExportOnlineAction;
import com.ecfeed.ui.editor.actions.IActionRunner;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.editor.actions.TestOnlineAction;
import com.ecfeed.ui.editor.data.ModelTreeContentProvider;
import com.ecfeed.ui.editor.data.ModelTreeLabelDecorator;
import com.ecfeed.ui.editor.data.ModelTreeLabelProvider;
import com.ecfeed.ui.editor.data.TreeRootNodeWrapper;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateListener;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;
import com.ecfeed.ui.modelif.TestCaseInterface;
import com.ecfeed.utils.SeleniumHelper;

public class ModelMasterSection extends TreeViewerSection {

	private static final int AUTO_EXPAND_LEVEL = 3;

	private final ModelMasterDetailsBlock fMasterDetailsBlock;
	private IModelUpdateListener fUpdateListener;


	public ModelMasterSection(ModelMasterDetailsBlock parentBlock, IJavaProjectProvider javaProjectProvider) {
		super(parentBlock.getMasterSectionContext(), 
				parentBlock.getModelUpdateContext(), 
				javaProjectProvider, 
				StyleDistributor.getSectionStyle());

		fMasterDetailsBlock = parentBlock;

		boolean includeDeleteAction = false;
		if (ApplicationContext.isStandaloneApplication()) {
			includeDeleteAction = true;
		}

		BasicActionRunnerProvider basicActionRunnerProvider = 
				new BasicActionRunnerProvider(
						new SaveActionRunner(),
						new UndoActionRunner(),
						new RedoActionRunner()); 

		ModelViewerActionProvider modelViewerActionProvider = 
				new ModelViewerActionProvider(
						getTreeViewer(), 
						getModelUpdateContext(), 
						javaProjectProvider, 
						basicActionRunnerProvider, 
						false);

		setActionProvider(modelViewerActionProvider, includeDeleteAction);		

		getTreeViewer().addDragSupport(
				DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK,
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDragListener(getTreeViewer()));

		getTreeViewer().addDropSupport(
				DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, 
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDropListener(getTreeViewer(), getModelUpdateContext(), javaProjectProvider));
	}

	@Override
	public void refresh() {
		super.refresh();
		IDetailsPage page = fMasterDetailsBlock.getCurrentPage();
		if(page != null){
			page.refresh();
		}
	}

	@Override
	protected void createContent(){
		super.createContent();
		getSection().setText("Structure");
		getTreeViewer().setAutoExpandLevel(AUTO_EXPAND_LEVEL);

		createToolbarAndAddIcons();
		showCheckForUpdatesDialogWhenPossible();
	}

	@Override
	protected IContentProvider createViewerContentProvider() {
		return new ModelTreeContentProvider();
	}

	@Override
	protected IBaseLabelProvider createViewerLabelProvider() {
		IJavaProjectProvider javaProjectProvider = getJavaProjectProvider();

		return new DecoratingLabelProvider(
				new ModelTreeLabelProvider(), 
				new ModelTreeLabelDecorator(
						getModelUpdateContext(), javaProjectProvider));
	}

	@Override
	protected ViewerMenuListener getMenuListener() {
		return new MasterViewerMenuListener(getMenu());
	}

	public List<IModelUpdateListener> getUpdateListeners(){
		if (fUpdateListener == null) {
			fUpdateListener = new UpdateListener();
		}
		return Arrays.asList(new IModelUpdateListener[]{fUpdateListener});
	}

	public void setInput(RootNode model) {
		setInput(new TreeRootNodeWrapper(model));
		collapseGlobalParameters();
	}

	private void createToolbarAndAddIcons() {
		Section section = getSection();

		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);

		final Cursor handCursor = Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);

		toolBarManager.add(new ShowInfoToolbarAction());
		toolBarManager.update(true);

		moveToolbarToTheRightSide(toolbar, section); 
	}

	private void moveToolbarToTheRightSide(ToolBar toolbar, Section section) {
		section.setTextClient(toolbar);
	}

	private void showCheckForUpdatesDialogWhenPossible() {

		Display.getCurrent().asyncExec
		(new Runnable() {
			public void run() {
				CheckForUpdatesDialog.openConditionally();
				refresh();
			}
		});
	}

	private void collapseGlobalParameters() {
		for(GlobalParameterNode parameter : ((TreeRootNodeWrapper)getViewer().getInput()).getModel().getGlobalParameters()){
			getTreeViewer().collapseToLevel(parameter, 1);
		}
	}

	private class UpdateListener implements IModelUpdateListener {
		@Override
		public void modelUpdated(Object source) {

			if (!(source instanceof AbstractFormPart)) {
				return;
			}

			AbstractFormPart abstractFormPart = (AbstractFormPart)source;
			abstractFormPart.markDirty();
			refresh();
		}
	}

	protected class MasterViewerMenuListener extends ViewerMenuListener{
		public MasterViewerMenuListener(Menu menu) {
			super(menu);
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
					new AddChildActionProvider(getTreeViewer(), getModelUpdateContext(), getJavaProjectProvider());

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
					NodeInterfaceFactory.getNodeInterface(testCaseInterface.getMethod(), null, getJavaProjectProvider());

			MethodInterface methodInterface = (MethodInterface)nodeIf; 

			if (!isActionExecutable(methodInterface)) {
				return;
			}

			ExecuteTestCaseAction action = new ExecuteTestCaseAction(ModelMasterSection.this, testCaseInterface);
			addMenuItem(action.getName(), action);

			new MenuItem(getMenu(), SWT.SEPARATOR);
		}

		private MethodInterface getMethodInterface() {
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, getJavaProjectProvider());

			if (!(nodeIf instanceof MethodInterface)) {
				final String MSG = "Invalid type of node interface. Method node interface expected"; 
				ExceptionHelper.reportRuntimeException(MSG);
			}

			return (MethodInterface)nodeIf; 
		}

		private TestCaseInterface getTestCaseInterface() {
			AbstractNodeInterface nodeInterface = 
					NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, getJavaProjectProvider());

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
					new TestOnlineAction(getJavaProjectProvider(), ModelMasterSection.this, methodInterface);

			addMenuItem(testOnlineAction.getName(), testOnlineAction);
			return true;
		}

		private boolean isActionExecutable(MethodInterface methodInterface) {
			MethodNode methodNode = methodInterface.getOwnNode();

			if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
				return true;
			}

			if (ApplicationContext.isStandaloneApplication()) {
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
					new ExportOnlineAction(getJavaProjectProvider(), ModelMasterSection.this, methodInterface);

			addMenuItem(exportOnlineAction.getName(), exportOnlineAction);
			return true;
		}

	}

	protected class ShowInfoToolbarAction extends Action {

		public ShowInfoToolbarAction() {
			setToolTipText("About ecFeed");
			setImageDescriptor(getIconDescription("aboutEcFeed.png"));
		}

		@Override
		public void run() {
			About2Dialog.open();
		}

	}	

	private class SaveActionRunner implements IActionRunner {

		@Override
		public void run() {
			ModelEditorHelper.saveActiveEditor();
		}

	}

	private class UndoActionRunner implements IActionRunner {

		@Override
		public void run() {
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			IUndoContext undoContext = ModelEditorHelper.getActiveModelEditor().getUndoContext();

			try {
				operationHistory.undo(undoContext, null, null);
			} catch (ExecutionException e) {
				SystemLogger.logCatch("Can not undo operation.");
			}
		}

	}

	private class RedoActionRunner implements IActionRunner {

		@Override
		public void run() {
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			IUndoContext undoContext = ModelEditorHelper.getActiveModelEditor().getUndoContext();

			try {
				operationHistory.redo(undoContext, null, null);
			} catch (ExecutionException e) {
				SystemLogger.logCatch("Can not undo operation.");
			}
		}

	}

}
