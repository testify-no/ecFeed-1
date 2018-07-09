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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.IModelSerializer;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.Pair;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.utils.EclipseHelper;
import com.ecfeed.utils.ModelEditorPlatformAdapter;
//import com.ecfeed.utils.Sleak;

public class ModelEditor extends FormEditor 
{
	private static Shell fGlobalShellForDialogs = null;

	private RootNode fModel;
	private ModelPage fModelPage;
	private ModelOperationManager fModelManager;
	private ObjectUndoContext fUndoContext;
	private ModelSourceEditor fSourcePageEditor;
	private JavaProjectProvider fJavaProjectProvider;
	private int fSourcePageIndex = -1;

	public ModelEditor() {

		super();

		IModelPageProvider modelPageProvider = new IModelPageProvider() {

			@Override
			public ModelPage getModelPage() {
				return fModelPage;
			}
		};

		createResourceProfilerDialog();

		ResourceChangeReporter.registerResourceChangeListener(modelPageProvider);

		fModelManager = new ModelOperationManager();
		fUndoContext = new ObjectUndoContext(fModelManager);

		IEditorInputProvider editorInputProvider = new IEditorInputProvider() {

			@Override
			public Object getEditorInputObject() {
				return getEditorInput();
			}
		};

		fJavaProjectProvider = new JavaProjectProvider(editorInputProvider);
	}

	public RootNode getModel() throws ModelOperationException{
		if (fModel == null){
			fModel = createModel();
		}
		return fModel;
	}

	@Override
	protected void addPages() {
		try {
			setPartName(getEditorInput().getName());
			addPage(fModelPage = new ModelPage(this, fJavaProjectProvider));

			addSourcePage();

		} catch (PartInitException e) {
			ExceptionCatchDialog.open("Can not add page.", e.getMessage());
		}

		setGlobalShellForDialogsIfNull();
	}

	private void createResourceProfilerDialog() {

		//		In order to run the profiler:
		//		- In Eclipse open Run/Debug configuration which should be run/debugged.
		//		- Go to "Tracing" tab.
		//		- Check "Enable tracing"
		//		- In the left window select org.eclipse.ui plugin
		//		- Put the check in the checkbox on before plugin name.
		//		- While org.eclipse.ui is selected, in the right window, on the list click checkboxes labelled: debug, trace/graphics
		//		- Click Run/Debug
		//		- As a result of this configuration the constructor: public Display (DeviceData data) should be called.
		//
		//		Sleak sleak = new Sleak();
		//		sleak.open();
	}

	private RootNode createModel() throws ModelOperationException {
		IEditorInput input = getEditorInput();
		InputStream stream = getInitialInputStream(input);

		if (stream == null) {
			return null;
		}

		return ModelEditorHelper.parseModel(stream);
	}

	private InputStream getInitialInputStream(IEditorInput input) throws ModelOperationException {
		if (ApplicationContext.isProjectAvailable()) {
			return ModelEditorPlatformAdapter.getInitialInputStreamForIDE(input);
		} else {
			return ModelEditorHelper.getInitialInputStreamForRCP(input);
		}		
	}

	private void setGlobalShellForDialogsIfNull() {
		if (fGlobalShellForDialogs == null) {
			fGlobalShellForDialogs = EclipseHelper.getActiveShell();
		}
	}

	private void addSourcePage() throws PartInitException {
		IEditorInput editorInput = new SourceEditorInput();
		setPartName(getEditorInput().getName());
		fSourcePageEditor = new ModelSourceEditor(getSite().getShell());

		fSourcePageIndex = addPage(fSourcePageEditor, editorInput);
		setPageText(fSourcePageIndex, fSourcePageEditor.getTitle());
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (newPageIndex != fSourcePageIndex)
			return;

		if (fModel.subtreeSize() <= CommonConstants.SOURCE_VIEWER_MAX_SUBTREE_SIZE) {
			refreshSourceViewer();
		}
		else {
			fSourcePageEditor.refreshContent(Messages.MODEL_SOURCE_SIZE_EXCEEDED(CommonConstants.SOURCE_VIEWER_MAX_SUBTREE_SIZE));
		}
	}

	private void refreshSourceViewer()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IModelSerializer serializer = 
				new EctSerializer(outputStream, ModelVersionDistributor.getCurrentSoftwareVersion());
		try{
			serializer.serialize(fModel);
		}
		catch(Exception e){
			ErrorDialog.open("Error", "Could not serialize the file:" + e.getMessage());
		}

		fSourcePageEditor.refreshContent(outputStream.toString());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!isDirty()) {
			return;
		}

		if (ApplicationContext.isProjectAvailable()) {
			doSaveForIDE();
		} else {
			doSaveForRCP();
		}
	}

	public void doSaveForIDE() {

		OutputStream outputStream = 
				ModelEditorPlatformAdapter.createOutputStreamForIdeFileSave(getEditorInput());

		saveModelToStream(outputStream);
	}

	public void doSaveForRCP() {
		String fileName = 
				ModelEditorPlatformAdapter.getFileNameFromEditorInput(getEditorInput());

		if (fileName == null) {
			final String MSG = "Empty file name from editor input.";
			ExceptionHelper.reportRuntimeException(MSG);
		}

		if (EditorInMemFileHelper.isInMemFile(fileName)) {
			saveInMemFile();
		} else {
			saveDiskFile(fileName);
		}
	}

	private void saveInMemFile() {
		doSaveAs();
	}

	private void saveDiskFile(String fileName) {
		File file = new File(fileName);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			ModelEditorHelper.reportOpenForWriteException(e);
		}

		saveModelToStream(outputStream);
		try {
			outputStream.close();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("Can not close output stream.");
		}
	}

	@Override
	public void doSaveAs(){
		setGlobalShellForDialogsIfNull();
		if (fGlobalShellForDialogs == null) {
			ExceptionHelper.reportRuntimeException("Invalid model editor shell.");
		}

		String fileWithPath = ModelEditorPlatformAdapter.selectFileForSaveAs(getEditorInput(), fGlobalShellForDialogs);
		if (fileWithPath == null) {
			return;
		}

		saveModelToFile(fileWithPath);
		setEditorFile(fileWithPath);
	}

	public void saveModelToFile(String fileWithPath) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileWithPath);
		} catch (FileNotFoundException e) {
			ModelEditorHelper.reportOpenForWriteException(e);
		}
		saveModelToStream(outputStream);
	}

	private void saveModelToStream(OutputStream outputStream) {
		try{
			IModelSerializer serializer = 
					new EctSerializer(outputStream, ModelVersionDistributor.getCurrentSoftwareVersion());
			serializer.serialize(fModel);
			ModelEditorPlatformAdapter.refreshWorkspace(null);
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		}
		catch(Exception e){
			ExceptionCatchDialog.open("Can not save editor file.", e.getMessage());
		}
	}

	public void setEditorFile(String fileWithPath) {

		Pair<IEditorInput, String> editorProperties = null;

		if (ApplicationContext.isProjectAvailable()) {
			editorProperties = ModelEditorPlatformAdapter.getEditorFilePropertiesForIde(fileWithPath);
		} else {
			editorProperties = ModelEditorPlatformAdapter.getEditorFilePropertiesForRcp(fileWithPath);
		}

		setInput(editorProperties.getFirst());
		setPartName(editorProperties.getSecond());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void commitPages(boolean onSave){
		super.commitPages(onSave);
		fModelPage.commitMasterPart(onSave);
	}

	@Override
	public void setFocus() {
		ModelMasterDetailsBlock masterBlock = fModelPage.getMasterBlock();
		if (masterBlock == null) {
			return;
		}

		masterBlock.refreshToolBarActions();
		masterBlock.getMasterSection().refresh();

		BasicDetailsPage page = masterBlock.getCurrentPage();
		if (page == null){
			return;
		}

		page.refresh();
	}

	public ModelOperationManager getModelOperationManager(){
		return fModelManager;
	}

	public IUndoContext getUndoContext() {
		return fUndoContext;
	}

	public class SourceEditorInput implements IEditorInput{

		@Override
		@SuppressWarnings({ "rawtypes" })
		public Object getAdapter(Class adapter) {
			return null;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public String getName() {
			return "source";
		}

		@Override
		public IPersistableElement getPersistable() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return "XML view of model";
		}
	}

}
