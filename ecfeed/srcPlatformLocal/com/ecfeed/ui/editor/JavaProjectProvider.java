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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.part.FileEditorInput;

import com.ecfeed.core.utils.ApplicationContext;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class JavaProjectProvider implements IJavaProjectProvider {

	IEditorInputProvider fEditorInputProvider;

	JavaProjectProvider(IEditorInputProvider editorInputProvider) {
		fEditorInputProvider = editorInputProvider;
	}

	@Override
	public Object getProject() { 

		if (!ApplicationContext.isProjectAvailable()) {
			return null;
		}

		IFile file = getEditorFile();
		if (file == null) {
			return null;
		}

		return file.getProject();
	}


	@Override
	public Object getPackageFragmentRoot() {

		if (!ApplicationContext.isProjectAvailable()) {
			return null;
		}		
		try {

			IProject project = (IProject)getProject();

			if (!project.hasNature(JavaCore.NATURE_ID)) {
				return null;
			}

			IJavaProject javaProject = JavaCore.create(project);
			IPath path = getEditorFilePath();

			if (javaProject == null) {
				return null;
			}

			return getPackageFragmentRootInProjectPath(javaProject, path);

		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return null;
	}

	private IPackageFragmentRoot getPackageFragmentRootInProjectPath(IJavaProject javaProject, IPath path) throws JavaModelException {

		for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
			if (root.getPath().isPrefixOf(path)) {
				return root;
			}
		}

		return null;
	}


	private IFile getEditorFile() {

		Object editorInput = fEditorInputProvider.getEditorInputObject();

		if (!(editorInput instanceof FileEditorInput)) {
			return null;
		}

		return ((FileEditorInput)editorInput).getFile();
	}

	private IPath getEditorFilePath() {

		IFile file = getEditorFile();

		if (file == null) {
			return null;
		}

		IPath path = file.getFullPath();
		return path;
	}

}
