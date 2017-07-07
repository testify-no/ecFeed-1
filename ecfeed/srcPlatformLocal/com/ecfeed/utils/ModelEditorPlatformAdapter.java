/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.UriHelper;
import com.ecfeed.ui.dialogs.basic.EcSaveAsDialog;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.dialogs.basic.SaveAsEctDialogWithConfirm;
import com.ecfeed.ui.editor.CanAddDocumentChecker;
import com.ecfeed.ui.editor.ModelEditor;

public class ModelEditorPlatformAdapter {

	public static Object getFileEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileEditorInput)) {
			return null;
		}
		return  (FileEditorInput)editorInput;
	}

	public static void openEditorOnExistingExtFile(String pathWithFileName) {
		IWorkbenchPage page = EclipseHelper.getActiveWorkBenchPage();
		IFileStore fileStore = ModelEditorPlatformAdapter.getFileStoreForExistingFile(pathWithFileName);
		if (fileStore == null) {
			ExceptionHelper.reportRuntimeException("Can not open editor on file: " + pathWithFileName + " .");
		}
		openEditorOnFileStore(page, fileStore);
	}

	private static IFileStore getFileStoreForExistingFile(String filePath) {
		if (filePath == null) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		if (!file.isFile()) {
			return null;
		}

		return getFileStore(file.toURI());
	}

	private static IFileStore getFileStore(URI uri) {
		return EFS.getLocalFileSystem().getStore(uri);
	}

	public static void openEditorOnFileInMemory(String tmpPathWithFileName) {
		IWorkbenchPage page = EclipseHelper.getActiveWorkBenchPage();
		File file = new File(tmpPathWithFileName);
		IFileStore fileStore = null;
		try {
			fileStore = EFS.getStore(file.toURI());
		} catch (CoreException e) {
			ExceptionHelper.reportRuntimeException("Can not get store. Message:" + e.getMessage());
		}

		if (fileStore == null) {
			ExceptionHelper.reportRuntimeException("Can not open editor on temporary file.");
		}

		openEditorOnFileStore(page, fileStore);
	}		

	private static void openEditorOnFileStore(IWorkbenchPage page, IFileStore fileStore) {
		try {
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	public static String selectFileForSaveAs(IEditorInput editorInput, Shell shell) {
		if (editorInput instanceof FileEditorInput) {
			return selectFileForFileEditorInput((FileEditorInput)editorInput);
		}
		if (editorInput instanceof FileStoreEditorInput) { 
			return selectFileForFileStoreEditorInput((FileStoreEditorInput)editorInput, shell);
		}
		return null;
	}

	private static String selectFileForFileEditorInput(FileEditorInput fileEditorInput) {
		EcSaveAsDialog dialog = new EcSaveAsDialog(Display.getDefault().getActiveShell());
		IFile original = fileEditorInput.getFile();
		dialog.setOriginalFile(original);

		dialog.create();
		if (dialog.open() == Window.CANCEL) {
			return null;
		}

		IPath path = (IPath)dialog.getResultPath();
		return path.toOSString();
	}	

	private static String selectFileForFileStoreEditorInput(FileStoreEditorInput fileStoreEditorInput, Shell shell) {

		String pathWithFileName = UriHelper.convertUriToFilePath(fileStoreEditorInput.getURI());
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String path = DiskFileHelper.extractPathWithSeparator(pathWithFileName);

		CanAddDocumentChecker checker = new CanAddDocumentChecker();
		return SaveAsEctDialogWithConfirm.open(path, fileName, checker, shell);
	}

	public static InputStream getInitialInputStreamForIDE(IEditorInput input) throws ModelOperationException {
		if (input instanceof FileStoreEditorInput) {
			final String CAN_NOT_OPEN_FILE = "Can not open file: ";
			final String ERR_MSG_1 = "It is not allowed to open standalone ect files created outside of Java project structure.";
			final String ERR_MSG_2 = "Please add ect file to the Java project first."; 

			ModelOperationException.report(
					CAN_NOT_OPEN_FILE + input.getName() + ". "+ ERR_MSG_1 + " " + ERR_MSG_2);
			return null;
		}

		FileEditorInput fileInput = (FileEditorInput)ModelEditorPlatformAdapter.getFileEditorInput(input);
		if (fileInput == null) {
			reportInvalidInputTypeException();
		}

		IFile file = fileInput.getFile();
		try {
			return file.getContents();
		} catch (CoreException e) {
			displayDialogErrInputStream(e);
			return null;
		}
	}

	private static void reportInvalidInputTypeException() {
		ExceptionHelper.reportRuntimeException("Invalid input type.");
	}

	private static void displayDialogErrInputStream(Exception e) {
		ExceptionCatchDialog.open("Can not get input stream for file.", e.getMessage());
	}

	public static String getFileNameFromEditorInput(IEditorInput editorInput) {
		FileStoreEditorInput fileStoreInput = castToFileStoreEditorInput(editorInput);
		if (fileStoreInput == null) {
			return null;
		}

		return UriHelper.convertUriToFilePath(fileStoreInput.getURI());
	}

	private static FileStoreEditorInput castToFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	


	public static URI getUriFromFileStoreEditor(ModelEditor modelEditor) {

		if (modelEditor == null) {
			return null;
		}

		FileStoreEditorInput editorInput = getFileStoreEditorInput(modelEditor);

		if (editorInput == null) {
			return null;
		}

		return editorInput.getURI();
	}

	private static FileStoreEditorInput getFileStoreEditorInput(ModelEditor modelEditor) {
		IEditorInput editorInput = modelEditor.getEditorInput();
		return castToFileStoreEditorInput(editorInput);
	}

	public static void refreshWorkspace(IProgressMonitor monitor) throws CoreException {
		for(IResource resource : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

}
