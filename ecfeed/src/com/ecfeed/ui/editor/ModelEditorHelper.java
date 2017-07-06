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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ecfeed.core.model.ModelConverter;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.UriHelper;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.utils.EclipseHelper;
import com.ecfeed.utils.ModelEditorPlatformAdapter;

public class ModelEditorHelper {

	public static void saveActiveEditor() {
		ModelEditor modelEditor = ModelEditorHelper.getActiveModelEditor(); 
		if (modelEditor == null) {
			return;
		}
		modelEditor.doSave(null);
	}

	public static ModelEditor getActiveModelEditor() {
		IEditorPart editorPart = ModelEditorPlatformAdapter.getActiveEditor();		
		if (editorPart == null) {
			return null;
		}

		if (!(editorPart instanceof ModelEditor)) {
			return null;
		}

		return (ModelEditor)editorPart;
	}	
	
	public static URI getUriFromFileStoreEditor(ModelEditor modelEditor) {
		
		if (modelEditor == null) {
			return null;
		}

		FileStoreEditorInput editorInput = ModelEditorHelper.getFileStoreEditorInput(modelEditor);
		
		if (editorInput == null) {
			return null;
		}
		
		return editorInput.getURI();
	}

	private static FileStoreEditorInput getFileStoreEditorInput(ModelEditor modelEditor) {
		IEditorInput editorInput = modelEditor.getEditorInput();
		return castToFileStoreEditorInput(editorInput);
	}	

	private static FileStoreEditorInput castToFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	

	public static boolean isInMemFileInput(IEditorInput editorInput) {
		String fileName = getFileNameFromEditorInput(editorInput);

		if (fileName == null) {
			return false;
		}

		if (EditorInMemFileHelper.isInMemFile(fileName)) {
			return true;
		}

		return false;		
	}

	public static String getFileNameFromEditorInput(IEditorInput editorInput) {
		FileStoreEditorInput fileStoreInput = ModelEditorHelper.castToFileStoreEditorInput(editorInput);
		if (fileStoreInput == null) {
			return null;
		}

		return UriHelper.convertUriToFilePath(fileStoreInput.getURI());
	}

	public static InputStream getInitialInputStreamForRCP(IEditorInput input) {
		String fileName = ModelEditorHelper.getFileNameFromEditorInput(input);

		if (fileName == null) {
			return null;
		}

		if (EditorInMemFileHelper.isInMemFile(fileName)) {
			return EditorInMemFileHelper.getInitialInputStream(fileName);
		}

		File file = new File(fileName);
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			displayDialogErrInputStream(e);
			return null;
		}
	}

	private static void displayDialogErrInputStream(Exception e) {
		ExceptionCatchDialog.open("Can not get input stream for file.", e.getMessage());
	}

	public static RootNode parseModel(InputStream iStream) {
		IModelParser parser = new EctParser();

		RootNode parsedModel = null;
		try {
			parsedModel = parser.parseModel(iStream);
		} catch (ParserException e) {
			ExceptionCatchDialog.open("Can not parse model. ", e.getMessage());
		}

		RootNode convertedModel = null;
		try {
			convertedModel = ModelConverter.convertToCurrentVersion(parsedModel);
		} catch (ModelOperationException e) {
			ExceptionCatchDialog.open("Can not convert model to current version. ", e.getMessage());
		}

		return convertedModel;
	}	


	private interface IModelEditorWorker {
		public enum WorkStatus {
			OK,
			CANCEL_ALL_NEXT
		}
		WorkStatus doWork(ModelEditor modelEditor);
	}

	private static void iterateOverModelEditors(IModelEditorWorker modelEditorWorker) {
		IWorkbenchPage page = EclipseHelper.getActiveWorkBenchPage();

		IEditorReference editors[] = page.getEditorReferences();

		for (int i = 0; i < editors.length; i++) {
			IEditorPart editorPart = editors[i].getEditor(true);
			if (!(editorPart instanceof ModelEditor)) {
				continue;
			}
			ModelEditor modelEditor = (ModelEditor)editorPart;
			IModelEditorWorker.WorkStatus workStatus = modelEditorWorker.doWork(modelEditor);

			if (workStatus == IModelEditorWorker.WorkStatus.CANCEL_ALL_NEXT) {
				break;
			}
		}		
	}

	private static class NextFreeNumberFinder implements IModelEditorWorker {

		int fMaxNumber = 0;

		@Override
		public WorkStatus doWork(ModelEditor modelEditor) {
			IEditorInput editorInput = modelEditor.getEditorInput();

			int extractedNumber = extractUntitledDocNumber(editorInput.getToolTipText());

			fMaxNumber = Math.max(fMaxNumber, extractedNumber);
			return IModelEditorWorker.WorkStatus.OK;
		}

		private int extractUntitledDocNumber(String pathWithFileName) {
			String fileNameWithExt = DiskFileHelper.extractFileName(pathWithFileName);
			String fileName = DiskFileHelper.extractFileNameWithoutExtension(fileNameWithExt);

			String untitledFilePrefix = EditorInMemFileHelper.getUntitledFilePrefix();
			if (!StringHelper.startsWithPrefix(untitledFilePrefix, fileName)) {
				return 0;
			}

			String fileNumber = StringHelper.removeToPrefix(untitledFilePrefix, fileName);

			int extractedNumber = 0;
			try { 
				extractedNumber = Integer.parseInt(fileNumber);
			} catch (NumberFormatException e) {
				return 0;
			}

			return extractedNumber;

		}

		public int getNextFreeNumber() {
			return fMaxNumber + 1;
		}

	}

	public static int getNextFreeUntitledNumber() {
		NextFreeNumberFinder nextFreeNumberFinder = new NextFreeNumberFinder();
		iterateOverModelEditors(nextFreeNumberFinder);
		return nextFreeNumberFinder.getNextFreeNumber();
	}

	private static class FileFinder implements IModelEditorWorker {

		private String fFileToFind;
		private boolean fFound;

		FileFinder(String fileToFind) {
			fFileToFind = fileToFind;
			fFound = false;
		}

		@Override
		public WorkStatus doWork(ModelEditor modelEditor) {
			IEditorInput editorInput = modelEditor.getEditorInput();

			String fileWithPath = editorInput.getToolTipText();
			if (fileWithPath.equals(fFileToFind)) {
				fFound = true;
				return IModelEditorWorker.WorkStatus.CANCEL_ALL_NEXT;
			}

			return IModelEditorWorker.WorkStatus.OK;
		}

		public boolean isFileFound() {
			return fFound;
		}

	}

	public static boolean isFileAlreadyOpen(String pathWithFileName) {
		FileFinder fileFinder = new FileFinder(pathWithFileName);
		iterateOverModelEditors(fileFinder);
		return fileFinder.isFileFound();
	}

}
