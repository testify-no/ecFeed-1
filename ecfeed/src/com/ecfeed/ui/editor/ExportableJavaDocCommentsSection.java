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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.JavaDocSupport;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ExportableJavaDocCommentsSection extends AbstractCommentsSection {

	private TabItem fCommentsTab;
	private TabItem fJavadocTab;
	private Button fExportButton;
	private Button fImportButton;
	private Menu fExportButtonMenu;
	private Menu fImportButtonMenu;

	public ExportableJavaDocCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);

		fCommentsTab = addTextTab("Comments");
		fJavadocTab = addTextTab("JavaDoc");
	}

	@Override
	public void refresh(){
		super.refresh();

		boolean importExportEnabled = importExportEnabled();
		if(getExportButton() != null && getExportButton().isDisposed() == false){
			getExportButton().setEnabled(importExportEnabled);
		}
		if(getImportButton() != null && getImportButton().isDisposed() == false){
			getImportButton().setEnabled(importExportEnabled);
		}

		String comments = getTargetIf().getComments();
		getCommentsText().setText(comments != null ? comments : "");
		String javadoc = JavaDocSupport.getJavadoc(getOwnNode());
		getJavaDocText().setText(javadoc != null ? javadoc : "");
	}

	@Override
	protected void setInput(AbstractNode input) {
		super.setInput(input);
		refresh();
	}

	@Override
	protected SelectionAdapter createEditButtonSelectionAdapter(){
		return new JavadocEditButtonListener();
	}

	@Override
	protected void createCommentsButtons(){
		super.createCommentsButtons();

		fExportButton = addButton("Export...", null);
		fImportButton = addButton("Import...", null);

		fExportButton.addSelectionListener(createExportButtonSelectionListener());
		fImportButton.addSelectionListener(createImportButtonSelectionListener());
	}

	protected TabItem getCommentsItem(){
		return fCommentsTab;
	}

	protected TabItem getJavaDocItem(){
		return fJavadocTab;
	}

	protected Text getCommentsText(){
		return getTextFromTabItem(fCommentsTab);
	}

	protected Text getJavaDocText(){
		return getTextFromTabItem(fJavadocTab);
	}

	protected Button getExportButton(){
		return fExportButton;
	}

	protected Button getImportButton(){
		return fImportButton;
	}

	protected ButtonClickListener createExportButtonSelectionListener(){
		fExportButtonMenu = new Menu(fExportButton);
		createExportMenuItems();

		return new ButtonClickListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fExportButtonMenu.setVisible(true);
			}
		};
	}

	protected ButtonClickListener createImportButtonSelectionListener(){
		fImportButtonMenu = new Menu(fImportButton);
		createImportMenuItems();

		return new ButtonClickListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fImportButtonMenu.setVisible(true);
			}
		};
	}

	protected void createExportMenuItems() {
		MenuItem exportAllItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportAllItem.setText("Export all");
		exportAllItem.addSelectionListener(new JavadocExportAllSelectionAdapter());
		MenuItem exportItem = new MenuItem(getExportButtonMenu(), SWT.NONE);
		exportItem.setText("Export comments of this element only");
		exportItem.addSelectionListener(new JavadocExportSelectionAdapter());
	}

	protected void createImportMenuItems() {
		MenuItem importAllItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importAllItem.setText("Import all");
		importAllItem.addSelectionListener(new JavadocImportAllSelectionAdapter());
		MenuItem importItem = new MenuItem(getImportButtonMenu(), SWT.NONE);
		importItem.setText("Import comments of this element only");
		importItem.addSelectionListener(new JavadocImportSelectionAdapter());
	}

	protected boolean importExportEnabled(){
		return getTargetIf().nodeImplementedFullyOrPartially();
	}

	protected Menu getExportButtonMenu(){
		return fExportButtonMenu;
	}

	protected Menu getImportButtonMenu(){
		return fImportButtonMenu;
	}

	private class JavadocEditButtonListener extends ButtonClickListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			getTargetIf().editComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
		}
	}

	protected class JavadocExportSelectionAdapter extends MenuItemSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportCommentsToJavadoc(getTargetIf().getComments());
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class JavadocImportSelectionAdapter extends MenuItemSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class JavadocExportAllSelectionAdapter extends MenuItemSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportAllComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class JavadocImportAllSelectionAdapter extends MenuItemSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importAllJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

}
