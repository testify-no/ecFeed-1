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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.IModelUpdateListener;

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements ISelectionChangedListener, ISectionContext{

	private ModelMasterSection fMasterSection;
	private ISectionContext fMasterSectionContext;
	private ModelPage fPage;
	private FormToolkitAdapter fToolkit;
	private ModelUpdateContext fUpdateContext;
	private IJavaProjectProvider fJavaProjectProvider;
	private UndoActionHandler fUndoActionHandler;
	private RedoActionHandler fRedoActionHandler;
	private RootNode fModel; 

	public ModelMasterDetailsBlock(ModelPage modelPage, IJavaProjectProvider javaProjectProvider) {
		fPage = modelPage;
		fUpdateContext = new ModelUpdateContext();
		fJavaProjectProvider = javaProjectProvider;
		fModel = null;
	}

	@Override
	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		detailsPart.selectionChanged(fMasterSection, event.getSelection());
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		try {
			fModel = getModel();
		} catch (ModelOperationException e) {
			ExceptionCatchDialog.open(null, e.getMessage());
			return;
		}

		fToolkit = new FormToolkitAdapter(managedForm.getToolkit());

		fMasterSection = new ModelMasterSection(this, fJavaProjectProvider);
		fMasterSection.initialize(managedForm);
		fMasterSection.addSelectionChangedListener(this);
		fMasterSection.setInput(fModel);

		if (isInMemFile(fJavaProjectProvider)) {
			fMasterSection.markDirty();
		}
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		if (fModel == null) {
			return;
		}
		detailsPart.registerPage(
				RootNode.class, 
				new ModelDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				ClassNode.class, 
				new ClassDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				MethodNode.class, 
				new MethodDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				MethodParameterNode.class, 
				new MethodParameterDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				GlobalParameterNode.class, 
				new GlobalParameterDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				TestCaseNode.class, 
				new TestCaseDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				ConstraintNode.class, 
				new ConstraintDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		detailsPart.registerPage(
				ChoiceNode.class, 
				new ChoiceDetailsPage(fMasterSection, fUpdateContext, fJavaProjectProvider));

		selectNode(fModel);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		IActionBars actionBars = getActionBars();
		IEditorSite editorSite = fPage.getEditorSite();
		IUndoContext undoContext = fUpdateContext.getUndoContext();

		fUndoActionHandler = new UndoActionHandler(editorSite, undoContext);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), fUndoActionHandler);

		fRedoActionHandler = new RedoActionHandler(editorSite, undoContext);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), fRedoActionHandler);

		actionBars.setGlobalActionHandler(
				ActionFactory.COPY.getId(), new GenericToolbarAction(ActionFactory.COPY.getId()));

		actionBars.setGlobalActionHandler(
				ActionFactory.CUT.getId(), new GenericToolbarAction(ActionFactory.CUT.getId()));

		actionBars.setGlobalActionHandler(
				ActionFactory.PASTE.getId(), new GenericToolbarAction(ActionFactory.PASTE.getId()));

		actionBars.setGlobalActionHandler(
				ActionFactory.DELETE.getId(), new GenericToolbarAction(ActionFactory.DELETE.getId()));

		actionBars.setGlobalActionHandler(
				ActionFactory.SELECT_ALL.getId(), new GenericToolbarAction(ActionFactory.SELECT_ALL.getId()));

		actionBars.updateActionBars();
	}

	@Override
	public Composite getSectionComposite() {
		return sashForm;
	}

	@Override
	public FormToolkitAdapter getToolkit() {
		return fToolkit;
	}

	public void selectNode(AbstractNode node) {
		fMasterSection.selectElement(node);
	}

	public BasicDetailsPage getCurrentPage() {

		if (detailsPart == null) {
			return null;
		}

		try {
			return (BasicDetailsPage)detailsPart.getCurrentPage();
		} catch(SWTException e)	{
			return null;
		}		
	}

	public ModelPage getPage(){
		return fPage;
	}

	public ISectionContext getMasterSectionContext() {

		if (fMasterSectionContext == null) {
			fMasterSectionContext = new MasterSectionContext();
		}
		return fMasterSectionContext;
	}

	public void refreshToolBarActions() {
		IActionBars actionBars = getActionBars();

		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), fUndoActionHandler);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), fRedoActionHandler);

		actionBars.updateActionBars();
	}

	public IModelUpdateContext getModelUpdateContext() {
		return fUpdateContext;
	}

	protected BasicSection getFocusedSection() {

		if (fMasterSection.getViewer().getControl().isFocusControl()) {
			return fMasterSection;
		} else {
			return getCurrentPage().getFocusedViewerSection();
		}
	}

	private boolean isInMemFile(IJavaProjectProvider javaProjectProvider) {

		if (!(javaProjectProvider instanceof ModelEditor)) {
			return false;
		}

		ModelEditor modelEditor = (ModelEditor)javaProjectProvider;
		IEditorInput input = modelEditor.getEditorInput();

		return ModelEditorHelper.isInMemFileInput(input);
	}

	private RootNode getModel() throws ModelOperationException {
		return fPage.getModel();
	}

	private IActionBars getActionBars() {
		return fPage.getEditorSite().getActionBars();
	}


	private class ModelUpdateContext implements IModelUpdateContext {

		@Override
		public ModelOperationManager getOperationManager() {
			return getPage().getEditor().getModelOperationManager();
		}

		@Override
		public AbstractFormPart getSourceForm() {
			return null;
		}

		@Override
		public List<IModelUpdateListener> getUpdateListeners() {
			return null;
		}

		@Override
		public IUndoContext getUndoContext() {
			return getPage().getEditor().getUndoContext();
		}
	}

	private class MasterSectionContext implements ISectionContext{

		@Override
		public ModelMasterSection getMasterSection() {
			return null;
		}

		@Override
		public Composite getSectionComposite() {
			return sashForm;
		}

		@Override
		public FormToolkitAdapter getToolkit() {
			return fToolkit;
		}

	}

	private class GenericToolbarAction extends Action {

		private final String fActionId;

		public GenericToolbarAction(String id){
			fActionId = id;
		}

		@Override
		public boolean isEnabled(){
			if (fActionId == null) {
				return false;
			}

			BasicSection focusedSection = getFocusedSection();
			if (focusedSection == null) {
				return false;
			}

			Action action = focusedSection.getAction(fActionId);
			if(action == null){
				return false;
			}

			return action.isEnabled();
		}

		@Override
		public void run(){
			Action action = getFocusedSection().getAction(fActionId);
			if(action != null){
				action.run();
			}
		}
	}

}
