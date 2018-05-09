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
import org.eclipse.swt.widgets.TabItem;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.JavaDocSupport;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends ExportableJavaDocCommentsSection {

	private TabItem fParameterCommentsTab;

	protected class ImportTypeSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ImportFullTypeSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importFullTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ExportFullTypeSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportFullTypeJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getTypeJavadocTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class EditButtonListener extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getActiveItem() == fParameterCommentsTab){
				getTargetIf().editComments();
			}
			else if(getActiveItem() == getTypeCommentsTab() || getActiveItem() == getTypeJavadocTab()){
				getTargetIf().editTypeComments();
			}
		}
	}

	public AbstractParameterCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);

		fParameterCommentsTab = addTextTab("Parameter", 0);
		getTypeCommentsTab().setText("Type");
		getTypeJavadocTab().setText("Type javadoc");
	}

	@Override
	public void refresh(){
		super.refresh();

		String javadoc = JavaDocSupport.getTypeJavadoc(getOwnNode());
		getTextFromTabItem(getTypeJavadocTab()).setText(javadoc != null ? javadoc : "");

		if(getTargetIf().getComments() != null){
			getTextFromTabItem(fParameterCommentsTab).setText(getTargetIf().getComments());
		}else{
			getTextFromTabItem(fParameterCommentsTab).setText("");
		}
		if(getTargetIf().getTypeComments() != null){
			getTextFromTabItem(getTypeCommentsTab()).setText(getTargetIf().getTypeComments());
		}else{
			getTextFromTabItem(getTypeCommentsTab()).setText("");
		}

		boolean importExportEnabled = getTargetIf().nodeImplementedFullyOrPartially();
		getExportButton().setEnabled(importExportEnabled);
		getImportButton().setEnabled(importExportEnabled);
	}

	@Override
	public AbstractParameterNode getOwnNode(){
		return (AbstractParameterNode)super.getOwnNode();
	}

	public void setInput(AbstractParameterNode input){
		super.setInput(input);
		getTargetIf().setOwnNode(input);
	}

	@Override
	protected void refreshEditButton() {
		TabItem activeItem = getActiveItem();
		boolean enabled = true;
		if(activeItem == getTypeCommentsTab() || activeItem == getTypeJavadocTab()){
			if(JavaTypeHelper.isJavaType(getOwnNode().getType())){
				enabled = false;
			}
		}
		getEditButton().setEnabled(enabled);

		AbstractParameterInterface targetIf = getTargetIf();
		String editButtonText;
		if(getActiveItem() == getTypeCommentsTab() || getActiveItem() == getTypeJavadocTab()){
			if(targetIf.getTypeComments() != null && targetIf.getTypeComments().length() > 0){
				editButtonText = "Edit type comments";
			}else{
				editButtonText = "Add type comments";
			}
		}else{
			if(targetIf.getComments() != null && targetIf.getComments().length() > 0){
				editButtonText = "Edit comments";
			}else{
				editButtonText = "Add comments";
			}
		}
		getEditButton().setText(editButtonText);
		getButtonsComposite().layout();
	}

	@Override
	protected ButtonClickListener createEditButtonSelectionAdapter(){
		return new EditButtonListener();
	}

	protected TabItem getParameterCommentsTab(){
		return fParameterCommentsTab;
	}

	protected TabItem getTypeCommentsTab(){
		return getCommentsItem();
	}

	protected TabItem getTypeJavadocTab(){
		return getJavaDocItem();
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

}
