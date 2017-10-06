/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.common.utils.local;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class EclipsePackageFragmentGetter {

	public static IPackageFragment getPackageFragment(
			String name, 
			IJavaProjectProvider javaProjectProvider) throws CoreException {

		IPackageFragmentRoot packageFragmentRoot = getPackageFragmentRoot(javaProjectProvider);
		IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(name);

		if(packageFragment.exists() == false){
			packageFragment = packageFragmentRoot.createPackageFragment(name, false, null);
		}
		return packageFragment;
	}

	private static IPackageFragmentRoot getPackageFragmentRoot(
			IJavaProjectProvider javaProjectProvider) throws CoreException{

		if (javaProjectProvider == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		Object rootObject = javaProjectProvider.getPackageFragmentRoot();

		IPackageFragmentRoot root = null;

		if (rootObject instanceof IPackageFragmentRoot) {
			root = (IPackageFragmentRoot)rootObject;
		}

		if(root == null){
			root = getAnySourceFolder(javaProjectProvider);
		}
		if(root == null){
			root = createNewSourceFolder("src", javaProjectProvider);
		}

		return root;
	}

	private static IPackageFragmentRoot getAnySourceFolder(
			IJavaProjectProvider javaProjectProvider) throws CoreException {

		IProject projectCurrent = (IProject)javaProjectProvider.getProject();

		if (projectCurrent.hasNature(JavaCore.NATURE_ID)) {

			IJavaProject projectNew = JavaCore.create(projectCurrent);

			for (IPackageFragmentRoot packageFragmentRoot: projectNew.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					return packageFragmentRoot;
				}
			}
		}

		return null;
	}

	private static IPackageFragmentRoot createNewSourceFolder(
			String name, 
			IJavaProjectProvider javaProjectProvider) throws CoreException {

		IProject project = (IProject)javaProjectProvider.getProject();

		IJavaProject javaProject = JavaCore.create(project);
		IFolder srcFolder = project.getFolder(name);

		int i = 0;
		while(srcFolder.exists()){
			String newName = name + i++;
			srcFolder = project.getFolder(newName);
		}
		srcFolder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(srcFolder);

		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] updated = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, updated, 0, entries.length);
		updated[entries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(updated, null);
		return root;
	}
}
