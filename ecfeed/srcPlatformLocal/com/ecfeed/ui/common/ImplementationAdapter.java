/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.operations.RootOperationAddNewClass;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.dialogs.TestClassImportDialog;
import com.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.ecfeed.ui.modelif.OperationExecuter;


public class ImplementationAdapter {

	public static void goToMethodImplementation(MethodNode methodNode) {

		IMethod method = JavaModelAnalyser.getIMethod(methodNode);

		if (method == null) {
			return;
		}

		try {
			JavaUI.openInEditor(method);
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	public static void goToParameterImplementation(AbstractParameterNode abstractParameterNode, String typeName) {

		if (JavaTypeHelper.isUserType(abstractParameterNode.getType())) {

			IType type = JavaModelAnalyser.getIType(typeName);
			if(type != null){
				try {
					JavaUI.openInEditor(type);
				} catch (Exception e) {
					SystemLogger.logCatch(e.getMessage());
				}
			}
		}
	}

	public static void goToChoiceImplementation(ChoiceNode choiceNode, AbstractParameterNode parameter) {

		try {
			IType type = JavaModelAnalyser.getIType(parameter.getType());

			if (type != null && choiceNode.isAbstract() == false) {
				for (IField field : type.getFields()) {
					if (field.getElementName().equals(choiceNode.getValueString())) {
						JavaUI.openInEditor(field);
						break;
					}
				}
			}
		} catch(Exception e) { 
			SystemLogger.logCatch(e.getMessage());
		}
	}


	public static ClassNode addImplementedClass(RootNode rootNode, OperationExecuter operationExecuter) {

		TestClassImportDialog dialog = new TestClassImportDialog(Display.getCurrent().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			boolean testOnly = dialog.getTestOnlyFlag();

			if (selectedClass != null) {
				ClassNode classModel;
				try {
					classModel = new JavaModelBuilder().buildClassModel(selectedClass, testOnly);
					if (operationExecuter.execute(
							new RootOperationAddNewClass(
									rootNode, 
									classModel, 
									rootNode.getClasses().size()), 
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

	public static void goToClassImplementation(String qualifiedName){
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		if(type != null){
			try{
				JavaUI.openInEditor(type);
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		}
	}

	public static String reassignImplementedClass() {

		TestClassSelectionDialog dialog = 
				new TestClassSelectionDialog(Display.getDefault().getActiveShell());

		if (dialog.open() != IDialogConstants.OK_ID) {
			return null;
		}

		IType selectedClass = (IType)dialog.getFirstResult();
		return selectedClass.getFullyQualifiedName();
	}	
}
