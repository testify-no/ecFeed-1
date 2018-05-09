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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.JavaDocSupport;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private IJavaProjectProvider fJavaProjectProvider;
	private MethodParameterInterface fTargetIf;
	private TabItem fParameterJavadocTab;
	private MenuItem fExportAllItem;
	private MenuItem fExportParameterCommentsItem;
	private MenuItem fExportTypeCommentsItem;
	private MenuItem fImportAllItem;
	private MenuItem fImportParameterCommentsItem;
	private MenuItem fImportTypeCommentsItem;

	protected class ImportParameterCommentsSelectionAdapter extends MenuItemSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importJavadocComments();
				getTabFolder().setSelection(getTabFolder().indexOf(getParameterCommentsTab()));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_IMPORT, e.getMessage());
			}
		}
	}

	protected class ExportParameterCommentsSelectionAdapter extends MenuItemSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportCommentsToJavadoc(getOwnNode().getDescription());
				getTabFolder().setSelection(getTabFolder().indexOf(fParameterJavadocTab));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS, e.getMessage());
			}
		}
	}

	protected class ExportAllParameterCommentsSelectionAdapter extends MenuItemSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportAllComments();
				getTabFolder().setSelection(getTabFolder().indexOf(fParameterJavadocTab));
			} catch (Exception e) {
				ExceptionCatchDialog.open(Messages.EXCEPTION_CAN_NOT_EXPORT, e.getMessage());
			}
		}
	}

	protected class ImportAllParameterCommentsSelectionAdapter extends MenuItemSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importAllJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getParameterCommentsTab()));
		}
	}


	public MethodParameterCommentsSection(
			ISectionContext sectionContext,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;

		int typeJavadocTabIndex = Arrays.asList(getTabFolder().getItems()).indexOf(getTypeJavadocTab());
		fParameterJavadocTab = addTextTab("Parameter javadoc", typeJavadocTabIndex);
	}

	@Override
	public MethodParameterNode getOwnNode(){
		return (MethodParameterNode)super.getOwnNode();
	}

	@Override
	public void refresh(){
		super.refresh();
		if(JavaDocSupport.getJavadoc(getOwnNode())!= null){
			getTextFromTabItem(fParameterJavadocTab).setText(JavaDocSupport.getJavadoc(getOwnNode()));
		}else{
			getTextFromTabItem(fParameterJavadocTab).setText("");
		}
		if(JavaDocSupport.getTypeJavadoc(getOwnNode()) != null){
			getTextFromTabItem(getTypeJavadocTab()).setText(JavaDocSupport.getTypeJavadoc(getOwnNode()));
		}else{
			getTextFromTabItem(getTypeJavadocTab()).setText("");
		}
		refreshMenuItems();
	}

	@Override
	protected MethodParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new MethodParameterInterface(getModelUpdateContext(), fJavaProjectProvider);
		}
		return fTargetIf;
	}

	@Override
	protected void createExportMenuItems() {
		fExportAllItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 0);
		fExportAllItem.setText("Export all");
		fExportAllItem.addSelectionListener(new ExportAllParameterCommentsSelectionAdapter()); 
		fExportParameterCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		fExportParameterCommentsItem.setText("Export method comments");
		fExportParameterCommentsItem.addSelectionListener(new ExportParameterCommentsSelectionAdapter());
		fExportTypeCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		fExportTypeCommentsItem.setText("Export only type comments");
		fExportTypeCommentsItem.addSelectionListener(new ExportFullTypeSelectionAdapter());
	}

	@Override
	protected void createImportMenuItems() {
		fImportAllItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 0);
		fImportAllItem.setText("Import all");
		fImportAllItem.addSelectionListener(new ImportAllParameterCommentsSelectionAdapter());
		fImportParameterCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		fImportParameterCommentsItem.setText("Import only parameter comments");
		fImportParameterCommentsItem.addSelectionListener(new ImportParameterCommentsSelectionAdapter());
		fImportTypeCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		fImportTypeCommentsItem.setText("Import only type comments");
		fImportTypeCommentsItem.addSelectionListener(new ImportFullTypeSelectionAdapter());
	}

	private void refreshMenuItems() {
		EImplementationStatus methodStatus = getTargetIf().getImplementationStatus(getOwnNode().getMethod());
		boolean parameterCommentsExportEnabled = methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
		boolean typeCommentsExportEnabled = JavaTypeHelper.isUserType(getOwnNode().getType()) && getTargetIf().getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED && getOwnNode().isLinked() == false;
		fExportParameterCommentsItem.setEnabled(parameterCommentsExportEnabled);
		fImportParameterCommentsItem.setEnabled(parameterCommentsExportEnabled);
		fExportTypeCommentsItem.setEnabled(typeCommentsExportEnabled);
		fImportTypeCommentsItem.setEnabled(typeCommentsExportEnabled);
		fExportAllItem.setEnabled(typeCommentsExportEnabled && parameterCommentsExportEnabled);
		fImportAllItem.setEnabled(typeCommentsExportEnabled && parameterCommentsExportEnabled);
	}

	@Override
	protected void refreshEditButton() {
		super.refreshEditButton();
		TabItem item = getActiveItem();
		if(item == getTypeCommentsTab() || item == getTypeJavadocTab()){
			if(getOwnNode().isLinked()){
				getEditButton().setEnabled(false);
			}
		}
	}

}
