package com.ecfeed.ui.common.utils;

import java.net.URI;

import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ecfeed.ui.editor.ModelEditor;
import com.ecfeed.ui.editor.ModelEditorHelper;

public class FileStoreEditorHelper {

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
	
}
