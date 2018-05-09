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
import org.eclipse.swt.widgets.Button;

import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ExportableSingleTextCommentsSection extends SingleTextCommentsSection {

	private Button fExportButton;
	private Button fImportButton;

	public ExportableSingleTextCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);
	}

	@Override
	protected void createCommentsButtons() {
		super.createCommentsButtons();
		fExportButton = addButton("Export all", null);
		fExportButton.setToolTipText(Messages.TOOLTIP_EXPORT_SUBTREE_COMMENTS_TO_JAVADOC);
		fExportButton.addSelectionListener(new ExportAllSelectionAdapter());
		fImportButton = addButton("Import all", null);
		fImportButton.setToolTipText(Messages.TOOLTIP_IMPORT_SUBTREE_COMMENTS_FROM_JAVADOC);
		fImportButton.addSelectionListener(new ImportAllSelectionAdapter());
	}

	private class ExportAllSelectionAdapter extends ButtonClickListener{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().exportAllComments();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not export.", e.getMessage());
			}
		}
	}

	private class ImportAllSelectionAdapter extends ButtonClickListener{
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				getTargetIf().importAllJavadocComments();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not import.", e.getMessage());
			}
		}
	}

}
