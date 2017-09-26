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
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.AboutDialog;
import com.ecfeed.ui.dialogs.CheckForUpdatesDialog;
import com.ecfeed.ui.editor.actions.MainActionProvider;
import com.ecfeed.ui.editor.data.ModelTreeContentProvider;
import com.ecfeed.ui.editor.data.ModelTreeLabelDecorator;
import com.ecfeed.ui.editor.data.ModelTreeLabelProvider;
import com.ecfeed.ui.editor.data.TreeRootNodeWrapper;
import com.ecfeed.ui.modelif.IModelUpdateListener;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

public class ModelMasterSection extends TreeViewerSection {

	private static final int AUTO_EXPAND_LEVEL = 3;

	private final ModelMasterDetailsBlock fMasterDetailsBlock;


	public ModelMasterSection(ModelMasterDetailsBlock parentBlock, IJavaProjectProvider javaProjectProvider) {
		super(parentBlock.getMasterSectionContext(), 
				parentBlock.getModelUpdateContext(), 
				javaProjectProvider, 
				StyleDistributor.getSectionStyle());

		fMasterDetailsBlock = parentBlock;

		MainActionProvider modelViewerActionProvider = 
				new MainActionProvider(
						getTreeViewer(), 
						getModelUpdateContext(), 
						javaProjectProvider, 
						false);

		setActionProvider(modelViewerActionProvider);		

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

		return new ModelMasterMenuListener(
				getMenu(), getActionProvider(), 
				getTreeViewer(), getModelUpdateContext(), 
				getJavaProjectProvider(), ModelMasterSection.this);
	}

	public void expandChildren(AbstractNode abstractNode) {

		if (abstractNode == null) {
			return;

		}
		getTreeViewer().expandToLevel(abstractNode, 1);
	}

	public List<IModelUpdateListener> createUpdateListeners(AbstractNode nodeToSelectAfterTheOperation) {

		IModelUpdateListener updateListener = new UpdateListener(nodeToSelectAfterTheOperation);
		return Arrays.asList(new IModelUpdateListener[]{updateListener});
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

		AbstractNode fNodeToSelectAfterTheOperation;

		UpdateListener(AbstractNode nodeToSelectAfterTheOperation) {
			fNodeToSelectAfterTheOperation = nodeToSelectAfterTheOperation;
		}

		@Override
		public void notifyModelUpdated(Object source) {

			if (!(source instanceof AbstractFormPart)) {
				return;
			}

			AbstractFormPart abstractFormPart = (AbstractFormPart)source;
			abstractFormPart.markDirty();
			refresh();

			IMainTreeProvider mainTreeProvider = fMasterDetailsBlock.getMainTreeProvider();
			mainTreeProvider.expandChildren(fNodeToSelectAfterTheOperation);
			mainTreeProvider.setCurrentNode(fNodeToSelectAfterTheOperation);
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
