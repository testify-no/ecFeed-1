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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
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
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.ImageManager;
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
import com.ecfeed.ui.editor.data.ModelTreeLabelProvider;
import com.ecfeed.ui.editor.data.ModelWrapper;
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
	private IJavaProjectProvider fJavaProjectProvider;


	public ModelMasterSection(ModelMasterDetailsBlock parentBlock, IJavaProjectProvider javaProjectProvider) {
		super(parentBlock.getMasterSectionContext(), 
				parentBlock.getModelUpdateContext(), 
				javaProjectProvider, 
				StyleDistributor.getSectionStyle());

		fMasterDetailsBlock = parentBlock;
		fJavaProjectProvider = javaProjectProvider;

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
						getTreeViewer(), this, fJavaProjectProvider, basicActionRunnerProvider, false);

		setActionProvider(modelViewerActionProvider, includeDeleteAction);		

		getTreeViewer().addDragSupport(
				DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK,
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDragListener(getTreeViewer()));

		getTreeViewer().addDropSupport(
				DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, 
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDropListener(getTreeViewer(), this, fJavaProjectProvider));
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
		return new DecoratingLabelProvider(new ModelTreeLabelProvider(), new ModelLabelDecorator());
	}

	@Override
	protected ViewerMenuListener getMenuListener() {
		return new MasterViewerMenuListener(getMenu());
	}

	@Override
	public List<IModelUpdateListener> getUpdateListeners(){
		if (fUpdateListener == null) {
			fUpdateListener = new UpdateListener();
		}
		return Arrays.asList(new IModelUpdateListener[]{fUpdateListener});
	}

	public void setInput(RootNode model) {
		setInput(new ModelWrapper(model));
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
		for(GlobalParameterNode parameter : ((ModelWrapper)getViewer().getInput()).getModel().getGlobalParameters()){
			getTreeViewer().collapseToLevel(parameter, 1);
		}
	}

	private Image getImageFromFile(String file) {
		return ImageManager.getInstance().getImage(file);
	}

	private class UpdateListener implements IModelUpdateListener {
		@Override
		public void modelUpdated(AbstractFormPart source) {
			source.markDirty();
			refresh();
		}
	}

	private class ModelLabelDecorator implements ILabelDecorator {

		Map<List<Image>, Image> fFusedImages;

		public ModelLabelDecorator() {
			fFusedImages = new HashMap<List<Image>, Image>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Image decorateImage(Image image, Object element) {
			if(!(element instanceof AbstractNode)){
				return image;
			}

			try {
				List<Image> decorations = (List<Image>)((AbstractNode)element).accept(
						new DecorationProvider(fJavaProjectProvider, ApplicationContext.isProjectAvailable()));
				List<Image> all = new ArrayList<Image>(decorations);
				all.add(0, image);
				if(fFusedImages.containsKey(all) == false){
					Image decorated = new Image(Display.getCurrent(), image.getImageData());
					for(Image decoration : decorations){
						if(decoration != null){
							decorated = fuseImages(decorated, decoration, 0, 0);
						}
					}
					fFusedImages.put(decorations, decorated);
				}
				return fFusedImages.get(decorations);
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
			return image;
		}

		@Override
		public String decorateText(String text, Object element) {
			return text;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		private Image fuseImages(Image icon, Image decorator, int x, int y){
			ImageData idIcon = (ImageData)icon.getImageData().clone();
			ImageData idDecorator = decorator.getImageData();
			if(idIcon.width <= x || idIcon.height <= y){
				return icon;
			}
			int rbw = (idDecorator.width + x > idIcon.width) ? (idDecorator.width + x - idIcon.width) : idDecorator.width;
			int rbh = (idDecorator.height + y > idIcon.height) ? (idDecorator.height + y - idIcon.height) : idDecorator.height;

			int indexa = y*idIcon.scanlinePad + x;
			int indexb = 0;

			for(int row = 0; row < rbh; row ++){
				for(int col = 0; col < rbw; col++){
					if(idDecorator.alphaData[indexb] < 0){
						idIcon.alphaData[indexa] = (byte)-1;
						idIcon.data[4*indexa]=idDecorator.data[4*indexb];
						idIcon.data[4*indexa+1]=idDecorator.data[4*indexb+1];
						idIcon.data[4*indexa+2]=idDecorator.data[4*indexb+2];
					}
					indexa += 1;
					indexb += 1;
				}
				indexa += x;
			}
			return new Image(Display.getDefault(), idIcon);
		}

		private class DecorationProvider implements IModelVisitor{
			AbstractNodeInterface fNodeInterface;
			boolean fIsProjectAvailable;

			public DecorationProvider(IJavaProjectProvider javaProjectProvider, boolean isProjectAvailable) {

				fNodeInterface = new AbstractNodeInterface(ModelMasterSection.this, javaProjectProvider);
				fIsProjectAvailable = isProjectAvailable;
			}

			@Override
			public Object visit(RootNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ClassNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(MethodNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				if(node.isExpected()){
					decorations.add(getImageFromFile("expected.png"));
				}
				if(node.isLinked()){
					decorations.add(getImageFromFile("linked.png"));
				}
				return decorations;
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				decorations.add(getImageFromFile("global.png"));
				return decorations;
			}

			@Override
			public Object visit(TestCaseNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ConstraintNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ChoiceNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				if(node.isAbstract()){
					decorations.add(getImageFromFile("abstract.png"));
				}
				return decorations;
			}

			private Image implementationStatusDecoration(AbstractNode node) {
				if (!fIsProjectAvailable) {
					return null;
				}

				switch (fNodeInterface.getImplementationStatus(node)){
				case IMPLEMENTED:
					return getImageFromFile("implemented.png");
				case PARTIALLY_IMPLEMENTED:
					return getImageFromFile("partially_implemented.png");
				case NOT_IMPLEMENTED:
					return getImageFromFile("unimplemented.png");
				case IRRELEVANT:
				default:
					return null;
				}
			}
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
					new AddChildActionProvider(getTreeViewer(), ModelMasterSection.this, fJavaProjectProvider);

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

			ExecuteTestCaseAction action = new ExecuteTestCaseAction(ModelMasterSection.this, testCaseInterface);
			addMenuItem(action.getName(), action);

			new MenuItem(getMenu(), SWT.SEPARATOR);
		}

		private MethodInterface getMethodInterface() {
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, fJavaProjectProvider);

			if (!(nodeIf instanceof MethodInterface)) {
				final String MSG = "Invalid type of node interface. Method node interface expected"; 
				ExceptionHelper.reportRuntimeException(MSG);
			}

			return (MethodInterface)nodeIf; 
		}

		private TestCaseInterface getTestCaseInterface() {
			AbstractNodeInterface nodeInterface = 
					NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, fJavaProjectProvider);

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
					new TestOnlineAction(fJavaProjectProvider, ModelMasterSection.this, methodInterface);

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
					new ExportOnlineAction(fJavaProjectProvider, ModelMasterSection.this, methodInterface);

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
