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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.IModelSerializer;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.utils.EclipseHelper;
import com.ecfeed.utils.ModelEditorPlatformAdapter;

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
		ResourceChangeReporter listener = new ResourceChangeReporter();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

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
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Could not serialize the file:" + e.getMessage());
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
		IFile file = ((FileEditorInput)getEditorInput()).getFile();
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file.getLocation().toOSString());
		} catch (FileNotFoundException e) {
			reportOpenForWriteException(e);
		}
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
			reportOpenForWriteException(e);
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
			reportOpenForWriteException(e);
		}
		saveModelToStream(outputStream);
	}

	private void saveModelToStream(FileOutputStream outputStream){
		try{
			IModelSerializer serializer = 
					new EctSerializer(outputStream, ModelVersionDistributor.getCurrentSoftwareVersion());
			serializer.serialize(fModel);
			refreshWorkspace(null);
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		}
		catch(Exception e){
			ExceptionCatchDialog.open("Can not save editor file.", e.getMessage());
		}
	}

	public void setEditorFile(String fileWithPath) {
		if (ApplicationContext.isProjectAvailable()) {
			setEditorFileForIde(fileWithPath);
		} else {
			setEditorFileForRcp(fileWithPath);
		}
	}

	public void setEditorFileForIde(String fileWithPath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = new Path(fileWithPath);
		IFile file = workspace.getRoot().getFile(path);

		setInput(new FileEditorInput(file));
		setPartName(file.getName());
	}

	public void setEditorFileForRcp(String fileWithPath) {
		File file = new File(fileWithPath);
		IFileStore fileStore = null;
		try {
			fileStore = EFS.getStore(file.toURI());
		} catch (CoreException e) {
			final String CAN_NOT_GET_STORE = "Can not get store for file: %s. Message: %s";
			ExceptionHelper.reportRuntimeException(String.format(CAN_NOT_GET_STORE, fileWithPath, e.getMessage()));
		}

		setInput(new FileStoreEditorInput(fileStore));
		setPartName(file.getName());
	}	

	private void reportOpenForWriteException(Exception e) {
		ExceptionCatchDialog.open("Can not open file for writing", e.getMessage());
	}

	private void refreshWorkspace(IProgressMonitor monitor) throws CoreException {
		for(IResource resource : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
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

	private class ResourceChangeReporter implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
			case IResourceChangeEvent.POST_BUILD:
				try {
					event.getDelta().accept(new ResourceDeltaVisitor());
				} catch (CoreException e) {
					SystemLogger.logCatch(e.getMessage());
				}
				break;
			default:
				break;
			}
		}
	}

	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.REMOVED:
			case IResourceDelta.CHANGED:
				if (!Display.getDefault().isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							CachedImplementationStatusResolver.clearCache();
							if(fModelPage.getMasterBlock().getMasterSection() != null){
								fModelPage.getMasterBlock().getMasterSection().refresh();
							}
							if(fModelPage.getMasterBlock().getCurrentPage() != null){
								fModelPage.getMasterBlock().getCurrentPage().refresh();
							}
						}
					});
				}
				break;
			default:
				break;
			}
			return false;
		}
	}

}
