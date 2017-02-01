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

import org.eclipse.swt.events.SelectionEvent;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	protected class ChoiceImportSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getOwnNode().getChoices().size() > 0){
				getTargetIf().importAllJavadocComments();
			}else{
				getTargetIf().importJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			}
		}
	}

	protected class ChoiceExportSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getOwnNode().getChoices().size() > 0){
				getTargetIf().exportAllComments();
			}else{
				getTargetIf().exportCommentsToJavadoc(getOwnNode().getDescription());
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			}
		}
	}

	public ChoiceCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider);
	}

	@Override
	protected void createCommentsButtons() {
		super.createCommentsButtons();
	}

	@Override
	protected ButtonClickListener createExportButtonSelectionListener(){
		return new ChoiceExportSelectionAdapter();
	}

	@Override
	protected ButtonClickListener createImportButtonSelectionListener(){
		return new ChoiceImportSelectionAdapter();
	}

	@Override
	public void refresh(){
		super.refresh();
		updateExportImportButtons();
	}

	@Override
	protected ChoiceNode getOwnNode(){
		return (ChoiceNode)super.getOwnNode();
	}

	private void updateExportImportButtons() {
		boolean importExportEnabled = getTargetIf().commentsImportExportEnabled();
		getExportButton().setEnabled(importExportEnabled);
		getImportButton().setEnabled(importExportEnabled);
		getJavaDocText().setEnabled(importExportEnabled);
		if(getOwnNode().isAbstract()){
			getExportButton().setText("Export all");
			getExportButton().setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_SUBTREE_COMMENTS_TO_JAVADOC);
			getImportButton().setText("Import all");
			getImportButton().setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_SUBTREE_COMMENTS_FROM_JAVADOC);
		}else{
			getExportButton().setText("Export");
			getExportButton().setToolTipText(Messages.TOOLTIP_EXPORT_CHOICE_COMMENTS_TO_JAVADOC);
			getImportButton().setText("Import");
			getImportButton().setToolTipText(Messages.TOOLTIP_IMPORT_CHOICE_COMMENTS_FROM_JAVADOC);
		}
	}
}
