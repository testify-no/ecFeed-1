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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.ecfeed.android.utils.AndroidManifestAccessor;
import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.ClassOperationAddMethod;
import com.ecfeed.core.adapter.operations.ClassOperationAddMethods;
import com.ecfeed.core.adapter.operations.ClassOperationRemoveMethod;
import com.ecfeed.core.adapter.operations.ClassOperationSetAndroidBaseRunner;
import com.ecfeed.core.adapter.operations.ClassOperationSetRunOnAndroid;
import com.ecfeed.core.adapter.operations.FactoryRenameOperation;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.PackageClassHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.EclipseModelBuilder;
import com.ecfeed.ui.common.JavaModelAnalyser;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class ClassInterface extends GlobalParametersParentInterface {

	public ClassInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}

	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}

	public String getQualifiedName(){
		return getQualifiedName(getOwnNode());
	}

	public String getLocalName(){
		return getLocalName(getOwnNode());
	}

	public static String getLocalName(ClassNode classNode){
		return ModelHelper.convertToLocalName(classNode.getName());
	}

	public static String getPackageName(ClassNode classNode){
		return ModelHelper.getPackageName(classNode.getName());
	}

	public String getPackageName(){
		return getPackageName(getOwnNode());
	}

	public boolean getRunOnAndroid(){
		return getOwnNode().getRunOnAndroid();
	}

	public String getAndroidBaseRunner(){
		return getOwnNode().getAndroidRunner();
	}

	@Override
	public boolean setName(String newName) {
		return setQualifiedName(newName);
	}

	public boolean setQualifiedName(String newName){
		if (!PackageClassHelper.hasPackageName(newName)){
			MessageDialog.openError(
					Display.getDefault().getActiveShell(), 
					Messages.DIALOG_RENAME_CLASS_TITLE, 
					Messages.DIALOG_RENAME_CLASS_MESSAGE_PACKAGE_NOT_EMPTY);
			return false;
		}
		if(newName.equals(getQualifiedName())){
			return false;
		}

		if (ApplicationContext.isProjectAvailable() && 
				getImplementationStatus(getOwnNode()) != EImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_TITLE,
					Messages.DIALOG_RENAME_IMPLEMENTED_CLASS_MESSAGE) == false){
				return false;
			}
		}
		return execute(FactoryRenameOperation.getRenameOperation(getOwnNode(), newName), Messages.DIALOG_RENAME_CLASS_PROBLEM_TITLE);
	}

	public boolean setLocalName(String newLocalName){
		String newQualifiedName = getPackageName() + "." + newLocalName;
		return setQualifiedName(newQualifiedName);
	}

	public boolean setPackageName(String newPackageName){
		String newQualifiedName = newPackageName + "." + getLocalName();
		return setQualifiedName(newQualifiedName);
	}

	public boolean setRunOnAndroid(boolean runOnAndroid) {
		if(getImplementationStatus(getOwnNode()) != EImplementationStatus.NOT_IMPLEMENTED){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_RUN_ON_ANDROID_TITLE,
					Messages.DIALOG_RENAME_RUN_ON_ANDROID_MESSAGE) == false){
				return false;
			}
		}
		IModelOperation operation = new ClassOperationSetRunOnAndroid(getOwnNode(), runOnAndroid);
		return execute(operation, Messages.DIALOG_ANDROID_RUNNER_SET_PROBLEM_TITLE);
	}

	public boolean setAndroidBaseRunner(String androidBaseRunner) {
		IModelOperation operation = new ClassOperationSetAndroidBaseRunner(getOwnNode(), androidBaseRunner);
		return execute(operation, Messages.DIALOG_ANDROID_RUNNER_SET_PROBLEM_TITLE);
	}

	public MethodNode addNewMethod(){
		return addNewMethod(generateNewMethodName());
	}

	public MethodNode addNewMethod(String name){
		MethodNode method = new MethodNode(name);
		if(addMethod(method)){
			return method;
		}
		return null;
	}

	public boolean addMethods(Collection<MethodNode> methods){
		IModelOperation operation = new ClassOperationAddMethods(getOwnNode(), methods, getOwnNode().getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHODS_PROBLEM_TITLE);
	}

	public boolean addMethod(MethodNode method){
		IModelOperation operation = new ClassOperationAddMethod(getOwnNode(), method, getOwnNode().getMethods().size());
		return execute(operation, Messages.DIALOG_ADD_METHOD_PROBLEM_TITLE);
	}

	public boolean removeMethod(MethodNode method){
		if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_REMOVE_METHOD_TITLE,
				Messages.DIALOG_REMOVE_METHOD_MESSAGE)){
			IModelOperation operation = new ClassOperationRemoveMethod(getOwnNode(), method);
			return execute(operation, Messages.DIALOG_REMOVE_METHOD_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean removeMethods(Collection<MethodNode> methods){
		if(methods.size() == 0){
			return false;
		}
		if(methods.size() == 1){
			return removeMethod(new ArrayList<MethodNode>(methods).get(0));
		}
		else if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_REMOVE_METHODS_TITLE,
				Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
			return removeChildren(methods, Messages.DIALOG_REMOVE_METHODS_PROBLEM_TITLE);
		}
		return false;
	}

	public static List<MethodNode> getOtherMethods(ClassNode target){
		List<MethodNode> otherMethods = new ArrayList<MethodNode>();
		EclipseModelBuilder builder = new EclipseModelBuilder();
		try{
			ClassNode completeModel = builder.buildClassModel(ClassNodeHelper.getQualifiedName(target), false);
			for(MethodNode method : completeModel.getMethods()){
				if(target.getMethod(method.getName(), method.getParameterTypes()) == null){
					otherMethods.add(method);
				}
			}
		}catch (ModelOperationException e){SystemLogger.logCatch(e.getMessage());}
		return otherMethods;
	}


	public List<MethodNode> getOtherMethods(){
		return getOtherMethods(getOwnNode());
	}

	public void reassignClass() {
		TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());

		if (dialog.open() == IDialogConstants.OK_ID) {
			IType selectedClass = (IType)dialog.getFirstResult();
			String qualifiedName = selectedClass.getFullyQualifiedName();
			setQualifiedName(qualifiedName);
		}
	}

	public List<String> createRunnerList(String projectPath) throws EcException {
		List<String> runners = new AndroidManifestAccessor(projectPath).getRunnerNames();
		String ecFeedTestRunner = AndroidBaseRunnerHelper.createAndroidBaseRunnerName(projectPath);

		runners.remove(ecFeedTestRunner);
		return runners;
	}

	@Override
	public void goToImplementation(){
		IType type = JavaModelAnalyser.getIType(getQualifiedName());
		if(type != null){
			try{
				JavaUI.openInEditor(type);
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		}
	}

	@Override
	public ClassNode getOwnNode(){
		return (ClassNode)super.getOwnNode();
	}

	private String generateNewMethodName() {
		String name = CommonConstants.DEFAULT_NEW_METHOD_NAME;
		int i = 0;
		while(getOwnNode().getMethod(name, new ArrayList<String>()) != null){
			name = CommonConstants.DEFAULT_NEW_METHOD_NAME + i++;
		}
		return name;
	}
}
