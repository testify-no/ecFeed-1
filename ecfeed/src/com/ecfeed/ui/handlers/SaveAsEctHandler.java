/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.handlers;

import java.net.URI;

import org.eclipse.core.commands.ExecutionException;

import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.UriHelper;
import com.ecfeed.ui.dialogs.basic.SaveAsEctDialogWithConfirm;
import com.ecfeed.ui.editor.CanAddDocumentChecker;
import com.ecfeed.ui.editor.ModelEditor;
import com.ecfeed.ui.editor.ModelEditorHelper;
import com.ecfeed.utils.EclipseHelper;

public class SaveAsEctHandler {

	public static void execute() throws ExecutionException {
		
		ModelEditor modelEditor = ModelEditorHelper.getActiveModelEditor();

		URI uri = ModelEditorHelper.getUriFromFileStoreEditor(modelEditor);

		String pathWithFileName = UriHelper.convertUriToFilePath(uri);
		if (pathWithFileName == null) {
			return;
		}

		executeSaveAs(pathWithFileName, modelEditor);
		return;
	}

	private static void executeSaveAs(String pathWithFileName, ModelEditor modelEditor) {
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String path = DiskFileHelper.extractPathWithSeparator(pathWithFileName);

		CanAddDocumentChecker checker = new CanAddDocumentChecker();
		String newFile = SaveAsEctDialogWithConfirm.open(path, fileName, checker, EclipseHelper.getActiveShell());

		if (newFile == null) {
			return;
		}

		modelEditor.saveModelToFile(newFile); 
		modelEditor.setEditorFile(newFile);
	}


}
