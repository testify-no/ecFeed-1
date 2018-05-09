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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.AboutDialog;
import com.ecfeed.ui.dialogs.CheckForUpdatesDialog;
import com.ecfeed.ui.editor.actions.MainActionGrouppingProvider;
import com.ecfeed.ui.editor.data.ModelTreeContentProvider;
import com.ecfeed.ui.editor.data.ModelTreeLabelDecorator;
import com.ecfeed.ui.editor.data.ModelTreeLabelProvider;
import com.ecfeed.ui.editor.data.TreeRootNodeWrapper;
import com.ecfeed.ui.modelif.IModelUpdateListener;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

public class ModelMasterSection extends TreeViewerSection {

	private static final int AUTO_EXPAND_LEVEL = 3;

	private final ModelMasterDetailsBlock fMasterDetailsBlock;

	// <<<<<<< HEAD
	// private class ModelWrapper{
	// private final RootNode fModel;
	// // private final RootNode fSimpleModel;
	//
	// public ModelWrapper(RootNode model){
	// fModel = model;
	// // fSimpleModel = null;
	// }
	//
	// // public ModelWrapper(RootNode model, RootNode simpleModel){
	// // fModel = model;
	// // fSimpleModel = simpleModel;
	// // }
	//
	// public RootNode getModel(){
	// return fModel;
	// }
	//
	// // public RootNode getSimpleModel(){
	// // return fSimpleModel;
	// // }
	// }

	// private class UpdateListener implements IModelUpdateListener{
	// @Override
	// public void modelUpdated(AbstractFormPart source) {
	// source.markDirty();
	// refresh();
	// }
	// }

	// private class ModelContentProvider extends TreeNodeContentProvider
	// implements ITreeContentProvider {
	// =======

	public ModelMasterSection(ModelMasterDetailsBlock parentBlock, IJavaProjectProvider javaProjectProvider) {
		super(parentBlock.getMasterSectionContext(), parentBlock.getModelUpdateContext(), javaProjectProvider,
				StyleDistributor.getSectionStyle());
		// >>>>>>> master

		fMasterDetailsBlock = parentBlock;

		MainActionGrouppingProvider modelViewerActionProvider = new MainActionGrouppingProvider(getTreeViewer(),
				getModelUpdateContext(), javaProjectProvider, false);

		setActionGrouppingProvider(modelViewerActionProvider);

		getTreeViewer().addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK,
				new Transfer[] { ModelNodesTransfer.getInstance() }, new ModelNodeDragListener(getTreeViewer()));

		getTreeViewer().addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK,
				new Transfer[] { ModelNodesTransfer.getInstance() },
				new ModelNodeDropListener(getTreeViewer(), getModelUpdateContext(), javaProjectProvider));
	}

	// <<<<<<< HEAD
	// private class ModelLabelProvider extends LabelProvider {
	//
	// private class TextProvider implements IModelVisitor{
	//
	// @Override
	// public Object visit(RootNode node) throws Exception {
	// return node.toString();
	// }
	//
	// @Override
	// public Object visit(ClassNode node) throws Exception {
	// if(ApplicationContext.getSimplifiedUI()) {
	// return ClassNodeHelper.simpleModeName(node);
	// } else {
	// return node.toString();
	// }
	// }
	//
	// @Override
	// public Object visit(MethodNode node) throws Exception {
	//
	// if(ApplicationContext.getSimplifiedUI()) {
	// return MethodNodeHelper.simplifiedToSimpleModeString(node);
	// } else {
	// return MethodNodeHelper.simplifiedToString(node);
	// }
	// }
	//
	// @Override
	// public Object visit(MethodParameterNode node) throws Exception {
	// String result;
	// if(ApplicationContext.getSimplifiedUI()){
	// result = ModelHelper.convertParameterToSimpleModeString(node);
	// } else {
	// result = ModelHelper.convertParameterToSimplifiedString(node);
	// }
	// if(node.isLinked()){
	// result += "[LINKED]->" + node.getLink().getQualifiedName();
	// }
	// return result;
	// }
	//
	// @Override
	// public Object visit(GlobalParameterNode node) throws Exception {
	// String result;
	// if(ApplicationContext.getSimplifiedUI()){
	// result = ModelHelper.convertParameterToSimpleModeString(node);
	// } else {
	// result = ModelHelper.convertParameterToSimplifiedString(node);
	// }
	// return result;
	// }
	//
	// @Override
	// public Object visit(TestCaseNode node) throws Exception {
	// return node.toString();
	// }
	//
	// @Override
	// public Object visit(ConstraintNode node) throws Exception {
	// return node.toString();
	// }
	//
	// @Override
	// public Object visit(ChoiceNode node) throws Exception {
	// return node.toString();
	// }
	// }

	// private class ImageProvider implements IModelVisitor{
	//
	// @Override
	// public Object visit(RootNode node) throws Exception {
	// return getImageFromFile("root_node.png");
	// }
	//
	// @Override
	// public Object visit(ClassNode node) throws Exception {
	// return getImageFromFile("class_node.png");
	// }
	//
	// @Override
	// public Object visit(MethodNode node) throws Exception {
	// return getImageFromFile("method_node.png");
	// }
	//
	// @Override
	// public Object visit(MethodParameterNode node) throws Exception {
	// return getImageFromFile("parameter_node.png");
	// }
	//
	// @Override
	// public Object visit(GlobalParameterNode node) throws Exception {
	// return getImageFromFile("parameter_node.png");
	// }
	//
	// @Override
	// public Object visit(TestCaseNode node) throws Exception {
	// return getImageFromFile("test_case_node.png");
	// }
	//
	// @Override
	// public Object visit(ConstraintNode node) throws Exception {
	// return getImageFromFile("constraint_node.png");
	// }
	//
	// @Override
	// public Object visit(ChoiceNode node) throws Exception {
	// return getImageFromFile("choice_node.png");
	// }
	//
	// }

	// @Override
	// public String getText(Object element){
	// if(element instanceof AbstractNode){
	// try {
	// return (String)((AbstractNode)element).accept(new TextProvider());
	// } catch(Exception e) {
	// SystemLogger.logCatch(e.getMessage());
	// }
	// }
	// return null;
	// }
	// }
	// @Override
	// public Image getImage(Object element){
	// if(element instanceof AbstractNode){
	// try {
	// return (Image)((AbstractNode)element).accept(new ImageProvider());
	// } catch(Exception e) {
	// SystemLogger.logCatch(e.getMessage());
	// }
	// }
	// return getImageFromFile("sample.png");
	// =======
	@Override
	public void refresh() {
		super.refresh();
		IDetailsPage page = fMasterDetailsBlock.getCurrentPage();
		if (page != null) {
			page.refresh();
			// >>>>>>> master
		}
	}

	@Override
	protected void createContent() {
		super.createContent();
		getSection().setText("Structure");
		getTreeViewer().setAutoExpandLevel(AUTO_EXPAND_LEVEL);

		createToolbarAndAddIcons();
		showCheckForUpdatesDialogWhenPossible();
	}

	// <<<<<<< HEAD
	// protected class MasterViewerMenuListener extends ViewerMenuListener{
	// public MasterViewerMenuListener(Menu menu) {
	// super(menu);
	// }
	//
	// @Override
	// protected void populateMenu() {
	// AbstractNode firstSelectedNode = getFirstSelectedNode();
	// if (firstSelectedNode == null) {
	// return;
	// }
	//
	// addChildAddingActions(firstSelectedNode);
	// addActionsForMethod(firstSelectedNode);
	// addActionsForTestCase(firstSelectedNode);
	// super.populateMenu();
	// }
	//
	// private void addChildAddingActions(AbstractNode abstractNode) {
	// AddChildActionProvider actionProvider =
	// new AddChildActionProvider(getTreeViewer(), ModelMasterSection.this,
	// fFileInfoProvider);
	//
	// List<AbstractAddChildAction> actions =
	// actionProvider.getPossibleActions(abstractNode);
	//
	// boolean menuItemAdded = false;
	// boolean actionNameConverted = false;
	//
	// for(AbstractAddChildAction action : actions) {
	//
	// String actionName = action.getName();
	//
	// if (!actionNameConverted) {
	// actionName = convertActionName(action.getName());
	// actionNameConverted = true;
	// }
	//
	// addMenuItem(actionName, action);
	// menuItemAdded = true;
	// }
	//
	// if (menuItemAdded) {
	// new MenuItem(getMenu(), SWT.SEPARATOR);
	// }
	// }
	//
	// private String convertActionName(String actionName) {
	//
	// if (!actionName.startsWith("Add")) {
	// return actionName;
	// }
	//
	// final String shortcut = "Insert";
	//
	// if (ApplicationContext.isStandaloneApplication()) {
	// return actionName;
	// }
	//
	// return ActionHelper.addShortcut(actionName, shortcut);
	// }
	//
	// private void addActionsForMethod(AbstractNode abstractNode) {
	//
	// if (!(abstractNode instanceof MethodNode)) {
	// return;
	// }
	//
	// MethodNode methodNode = (MethodNode)abstractNode;
	// MethodInterface methodInterface = getMethodInterface();
	// boolean isAction = false;
	//
	// if (addTestOnlineAction(methodInterface)) {
	// isAction = true;
	// }
	// if (addExportOnlineAction(methodNode, methodInterface)) {
	// isAction = true;
	// }
	//
	// if (isAction) {
	// new MenuItem(getMenu(), SWT.SEPARATOR);
	// }
	// }
	//
	// private void addActionsForTestCase(AbstractNode abstractNode) {
	//
	// if (!(abstractNode instanceof TestCaseNode)) {
	// return;
	// }
	//
	// TestCaseInterface testCaseInterface = getTestCaseInterface();
	//
	// AbstractNodeInterface nodeIf =
	// NodeInterfaceFactory.getNodeInterface(testCaseInterface.getMethod(),
	// null, fFileInfoProvider);
	//
	// MethodInterface methodInterface = (MethodInterface)nodeIf;
	//
	// if (!isActionExecutable(methodInterface)) {
	// return;
	// }
	//
	// ExecuteTestCaseAction action = new
	// ExecuteTestCaseAction(ModelMasterSection.this, testCaseInterface);
	// addMenuItem(action.getName(), action);
	//
	// new MenuItem(getMenu(), SWT.SEPARATOR);
	// }
	//
	// private MethodInterface getMethodInterface() {
	// AbstractNodeInterface nodeIf =
	// NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null,
	// fFileInfoProvider);
	//
	// if (!(nodeIf instanceof MethodInterface)) {
	// final String MSG = "Invalid type of node interface. Method node interface
	// expected";
	// ExceptionHelper.reportRuntimeException(MSG);
	// }
	//
	// return (MethodInterface)nodeIf;
	// }
	//
	// private TestCaseInterface getTestCaseInterface() {
	// AbstractNodeInterface nodeInterface =
	// NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null,
	// fFileInfoProvider);
	//
	// if (!(nodeInterface instanceof TestCaseInterface)) {
	// final String MSG = "Invalid type of node interface. Test case interface
	// expected";
	// ExceptionHelper.reportRuntimeException(MSG);
	// }
	//
	// return (TestCaseInterface)nodeInterface;
	// }
	//
	// private boolean addTestOnlineAction(MethodInterface methodInterface) {
	//
	// if (!isActionExecutable(methodInterface)) {
	// return false;
	// }
	//
	// TestOnlineAction testOnlineAction = new
	// TestOnlineAction(fFileInfoProvider, ModelMasterSection.this,
	// methodInterface);
	// addMenuItem(testOnlineAction.getName(), testOnlineAction);
	// return true;
	// }
	//
	// private boolean isActionExecutable(MethodInterface methodInterface) {
	// MethodNode methodNode = methodInterface.getOwnNode();
	//
	// if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
	// return true;
	// }
	//
	// if (ApplicationContext.isStandaloneApplication()) {
	// return false;
	// }
	//
	// EImplementationStatus methodStatus =
	// methodInterface.getImplementationStatus();
	//
	// if (methodStatus != EImplementationStatus.IMPLEMENTED) {
	// return false;
	// }
	//
	// return true;
	// }
	//
	// private boolean addExportOnlineAction(MethodNode methodNode,
	// MethodInterface methodInterface) {
	// if (methodNode.getParametersCount() == 0) {
	// return false;
	// }
	//
	// ExportOnlineAction exportOnlineAction = new
	// ExportOnlineAction(fFileInfoProvider, ModelMasterSection.this,
	// methodInterface);
	// addMenuItem(exportOnlineAction.getName(), exportOnlineAction);
	// return true;
	// }

	// =======
	@Override
	protected IContentProvider createViewerContentProvider() {
		return new ModelTreeContentProvider();
		// >>>>>>> master
	}

	@Override
	protected IBaseLabelProvider createViewerLabelProvider() {
		IJavaProjectProvider javaProjectProvider = getJavaProjectProvider();

		return new DecoratingLabelProvider(new ModelTreeLabelProvider(),
				new ModelTreeLabelDecorator(getModelUpdateContext(), javaProjectProvider));
	}

	@Override
	protected ViewerMenuListener getMenuListener() {

		return new ModelMasterMenuListener(getMenu(), getActionGroupingProvider(), getTreeViewer(),
				getModelUpdateContext(), getJavaProjectProvider(), ModelMasterSection.this);
	}

	public void expandChildren(AbstractNode abstractNode) {

		if (abstractNode == null) {
			return;

		}
		getTreeViewer().expandToLevel(abstractNode, 1);
	}

	public List<IModelUpdateListener> createUpdateListeners(List<AbstractNode> nodesToSelectAfterTheOperation) {

		IModelUpdateListener updateListener = new UpdateListener(nodesToSelectAfterTheOperation);
		return Arrays.asList(new IModelUpdateListener[] { updateListener });
	}

	// <<<<<<< HEAD
	// public void setInput(RootNode model){
	//
	// setInput(new ModelWrapper(model));
	// =======
	public void setInput(RootNode model) {
		setInput(new TreeRootNodeWrapper(model));
		// >>>>>>> master
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

		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				CheckForUpdatesDialog.openConditionally();
				refresh();
			}
		});
	}

	private void collapseGlobalParameters() {
		for (GlobalParameterNode parameter : ((TreeRootNodeWrapper) getViewer().getInput()).getModel()
				.getGlobalParameters()) {
			getTreeViewer().collapseToLevel(parameter, 1);
		}
	}

	private class UpdateListener implements IModelUpdateListener {

		List<AbstractNode> fNodesToSelect;

		UpdateListener(List<AbstractNode> nodesToSelectAfterTheOperation) {
			fNodesToSelect = nodesToSelectAfterTheOperation;
		}

		@Override
		public void notifyModelUpdated(Object source) {

			if (!(source instanceof AbstractFormPart)) {
				return;
			}

			AbstractFormPart abstractFormPart = (AbstractFormPart) source;
			abstractFormPart.markDirty();
			refresh();

			IMainTreeProvider mainTreeProvider = fMasterDetailsBlock.getMainTreeProvider();

			MainTreeProviderHelper.notifyModelUpdated(mainTreeProvider, fNodesToSelect);
		}
	}

	protected class ShowInfoToolbarAction extends Action {

		public ShowInfoToolbarAction() {
			setToolTipText("About ecFeed");
			setImageDescriptor(getIconDescription("aboutEcFeed.png"));
		}

		@Override
		public void run() {
			AboutDialog.open();
		}

	}

}
