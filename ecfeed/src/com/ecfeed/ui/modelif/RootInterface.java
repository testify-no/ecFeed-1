/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.RootOperationAddClasses;
import com.ecfeed.core.adapter.operations.RootOperationAddNewClass;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.local.EclipseModelBuilder;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.TestClassImportDialog;

public class RootInterface extends GlobalParametersParentInterface {

	public RootInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	@Override
	public RootNode getOwnNode() {

		return (RootNode)super.getOwnNode();
	}

	public ClassNode addNewClass(){

		return addNewClass(generateNewClassName()); 
	}

	public ClassNode addNewClass(String className) {

		ClassNode addedClass = new ClassNode(className);
		if (execute(
				new RootOperationAddNewClass(
						getOwnNode(), 
						addedClass, 
						getOwnNode().getClasses().size()), 
						Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE)) {
			return addedClass;
		}
		return null;
	}

	public ClassNode addImplementedClass() {

		TestClassImportDialog dialog = new TestClassImportDialog(Display.getCurrent().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			boolean testOnly = dialog.getTestOnlyFlag();

			if (selectedClass != null) {
				ClassNode classModel;
				try {
					classModel = new EclipseModelBuilder().buildClassModel(selectedClass, testOnly);
					if (execute(
							new RootOperationAddNewClass(
									getOwnNode(), 
									classModel, 
									getOwnNode().getClasses().size()), 
									Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE)) {

						return classModel;
					}
				} catch (ModelOperationException e) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_ADD_NEW_CLASS_PROBLEM_TITLE,
							e.getMessage());
				}
			}
		}
		return null;
	}

	public boolean removeClasses(Collection<ClassNode> removedClasses){

		if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_REMOVE_CLASSES_TITLE,
				Messages.DIALOG_REMOVE_CLASSES_MESSAGE)) {
			return removeChildren(removedClasses, Messages.DIALOG_REMOVE_CLASSES_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean addClasses(Collection<ClassNode> classes) {

		IModelOperation operation = new RootOperationAddClasses(getOwnNode(), classes, getOwnNode().getClasses().size());
		return execute(operation, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}

	private String generateNewClassName() {

		String startClassName = CommonConstants.DEFAULT_NEW_PACKAGE_NAME + "." + CommonConstants.DEFAULT_NEW_CLASS_NAME;
		return RootNodeHelper.generateNewClassName(getOwnNode(), startClassName);
	}

	@Override
	public boolean goToImplementationEnabled(){

		return false;
	}
}
