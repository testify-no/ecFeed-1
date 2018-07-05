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
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.AbstractParameterOperationSetType;
import com.ecfeed.core.adapter.operations.BulkOperation;
import com.ecfeed.core.adapter.operations.ParameterSetTypeCommentsOperation;
import com.ecfeed.core.adapter.operations.ReplaceChoicesOperation;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.EclipseTypeHelper;
import com.ecfeed.ui.common.ImplementationAdapter;
import com.ecfeed.ui.common.JavaCodeModelBuilder;
import com.ecfeed.ui.common.JavaDocSupport;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.TextAreaDialog;

public abstract class AbstractParameterInterface extends ChoicesParentInterface {

	IJavaProjectProvider fJavaProjectProvider;

	public AbstractParameterInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
	}

	public String getType() {
		return getOwnNode().getType();
	}

	public String getTypeComments() {
		return getOwnNode().getTypeComments() != null ? getOwnNode().getTypeComments() : "";
	}

	public boolean editTypeComments() {
		TextAreaDialog dialog = new TextAreaDialog(Display.getCurrent().getActiveShell(),
				Messages.DIALOG_EDIT_COMMENTS_TITLE, Messages.DIALOG_EDIT_COMMENTS_MESSAGE, getTypeComments());
		if(dialog.open() == IDialogConstants.OK_ID){
			return getOperationExecuter().execute(new ParameterSetTypeCommentsOperation(getOwnNode(), dialog.getText()), Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setTypeComments(String comments){
		if(comments != null && comments.equals(getOwnNode().getTypeComments()) == false){
			return getOperationExecuter().execute(new ParameterSetTypeCommentsOperation(getOwnNode(), comments), Messages.DIALOG_EDIT_COMMENTS_TITLE);
		}
		return false;
	}

	public boolean importType() {
		return ImplementationAdapter.importType(getOwnNode(), getOperationExecuter(), getAdapterProvider());
	}


	public boolean resetChoicesToDefault(){
		String type = getOwnNode().getType();
		List<ChoiceNode> defaultChoices = new JavaCodeModelBuilder().getDefaultChoices(type);
		IModelOperation operation = new ReplaceChoicesOperation(getOwnNode(), defaultChoices, getAdapterProvider());
		return getOperationExecuter().execute(operation, Messages.DIALOG_RESET_CHOICES_PROBLEM_TITLE);
	}

	public static boolean hasLimitedValuesSet(String typeName) {

		if (JavaTypeHelper.isBooleanTypeName(typeName)) {
			return true;
		}

		return (!JavaTypeHelper.isJavaType(typeName));
	}

	public static boolean hasLimitedValuesSet(AbstractParameterNode parameter) {
		return hasLimitedValuesSet(parameter.getType());
	}

	public static List<String> getSpecialValues(String type) {
		return EclipseTypeHelper.getSpecialValues(type);
	}

	@Override
	public boolean goToImplementationEnabled(){
		if(JavaTypeHelper.isUserType(getOwnNode().getType()) == false){
			return false;
		}
		return super.goToImplementationEnabled();
	}

	@Override
	public void goToImplementation(){
		ImplementationAdapter.goToParameterImplementation(getOwnNode(), getType());
	}

	@Override
	public AbstractParameterNode getOwnNode(){
		return (AbstractParameterNode)super.getOwnNode();
	}

	public boolean setType(String newType) {
		if(newType.equals(getOwnNode().getType())){
			return false;
		}
		return getOperationExecuter().execute(setTypeOperation(newType), Messages.DIALOG_SET_PARAMETER_TYPE_PROBLEM_TITLE);
	}

	protected IModelOperation setTypeOperation(String type) {
		return new AbstractParameterOperationSetType(getOwnNode(), type, getAdapterProvider());
	}

	public boolean importTypeJavadocComments() {
		return setTypeComments(JavaDocSupport.importTypeJavadoc(getOwnNode()));
	}

	public void importFullTypeJavadocComments() {
		IModelOperation operation = 
				new BulkOperation(
						"Import javadoc", getFullTypeImportOperations(), false, getOwnNode(), getOwnNode());

		getOperationExecuter().execute(operation, Messages.DIALOG_SET_COMMENTS_PROBLEM_TITLE);
	}

	protected List<IModelOperation> getFullTypeImportOperations() {

		List<IModelOperation> result = new ArrayList<IModelOperation>();
		String typeJavadoc = JavaDocSupport.importTypeJavadoc(getOwnNode());

		result.add(new ParameterSetTypeCommentsOperation(getOwnNode(), typeJavadoc));

		for (ChoiceNode choice : getOwnNode().getChoices()) {
			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(
							choice, getOperationExecuter().getUpdateContext(), fJavaProjectProvider);

			result.addAll(nodeIf.getImportAllJavadocCommentsOperations());
		}

		return result;
	}

	public void exportTypeJavadocComments() {
		JavaDocSupport.exportTypeJavadoc(getOwnNode());
	}

	public void exportFullTypeJavadocComments() {
		exportTypeJavadocComments();
		for(ChoiceNode choice : getOwnNode().getChoices()){
			ChoiceInterface choiceIf = 
					(ChoiceInterface)NodeInterfaceFactory.getNodeInterface(
							choice, getOperationExecuter().getUpdateContext(), fJavaProjectProvider);
			choiceIf.exportAllComments();
		}
	}

	@Override
	protected List<IModelOperation> getImportAllJavadocCommentsOperations(){
		List<IModelOperation> result = super.getImportAllJavadocCommentsOperations();
		String typeJavadoc = JavaDocSupport.importTypeJavadoc(getOwnNode());
		if(typeJavadoc != null && typeJavadoc.equals(getTypeComments()) == false && importTypeCommentsEnabled()){
			result.add(new ParameterSetTypeCommentsOperation(getOwnNode(), typeJavadoc));
		}
		return result;
	}

	public abstract boolean importTypeCommentsEnabled();
}
