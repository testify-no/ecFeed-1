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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.MethodParameterOperationSetLink;
import com.ecfeed.core.adapter.operations.MethodParameterOperationSetLinked;
import com.ecfeed.core.adapter.operations.MethodParameterOperationSetType;
import com.ecfeed.core.adapter.operations.ParameterOperationSetDefaultValue;
import com.ecfeed.core.adapter.operations.ParameterOperationSetExpected;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class MethodParameterInterface extends AbstractParameterInterface {

	public MethodParameterInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	public boolean isExpected() {
		return getOwnNode().isExpected();
	}

	public String getDefaultValue() {
		return getOwnNode().getDefaultValue();
	}

	public boolean setExpected(boolean expected){
		if(expected != getOwnNode().isExpected()){
			MethodNode method = getOwnNode().getMethod();
			if(method != null){
				boolean testCases = method.getTestCases().size() > 0;
				boolean constraints = method.mentioningConstraints(getOwnNode()).size() > 0;
				if(testCases || constraints){
					String message = "";
					if(testCases){
						if(expected){
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED + "\n";
						}
						else{
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED + "\n";
						}
					}
					if(constraints){
						message += Messages.DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED;
					}
					if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE, message) == false){
						return false;
					}
				}
			}
			return execute(new ParameterOperationSetExpected(getOwnNode(), expected), Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setDefaultValue(String valueString) {
		if(getOwnNode().getDefaultValue().equals(valueString) == false){
			IModelOperation operation = new ParameterOperationSetDefaultValue(getOwnNode(), valueString, getAdapterProvider().getAdapter(getOwnNode().getType()));
			return execute(operation, Messages.DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE);
		}
		return false;
	}

	public String[] defaultValueSuggestions(){
		Set<String> items = new HashSet<String>(getSpecialValues());
		if(JavaTypeHelper.isJavaType(getType()) == false){
			for(ChoiceNode p : getOwnNode().getLeafChoices()){
				items.add(p.getValueString());
			}
			if(items.contains(getOwnNode().getDefaultValue())== false){
				items.add(getOwnNode().getDefaultValue());
			}
		}
		return items.toArray(new String[]{});
	}

	public boolean setLinked(boolean linked) {
		MethodParameterOperationSetLinked operation = new MethodParameterOperationSetLinked(getOwnNode(), linked);
		MethodNode method = getOwnNode().getMethod();
		if(linked){
			GlobalParameterNode link = getOwnNode().getLink();
			if(link == null || method.getAvailableGlobalParameters().contains(link) == false || method.checkDuplicate(getOwnNode().getIndex(), link.getType())){
				GlobalParameterNode newLink = findNewLink();
				if(newLink == null){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_SET_PARAMETER_LINKED_PROBLEM_TITLE, Messages.DIALOG_NO_VALID_LINK_AVAILABLE_PROBLEM_MESSAGE);
				}
				operation.addOperation(0, new MethodParameterOperationSetLink(getOwnNode(), newLink));
			}
		}else{
			//check the type of the unlinked parameter. If it causes collision, set new type
			if(method.checkDuplicate(getOwnNode().getIndex(), getOwnNode().getRealType())){
				operation.addOperation(0, new MethodParameterOperationSetType(getOwnNode(), getOwnNode().getType(), getAdapterProvider()));
			}
		}

		return execute(operation, Messages.DIALOG_SET_PARAMETER_LINKED_PROBLEM_TITLE);
	}

	public boolean isLinked() {
		return getOwnNode().isLinked();
	}

	public boolean setLink(GlobalParameterNode link) {
		IModelOperation operation = new MethodParameterOperationSetLink(getOwnNode(), link);
		return execute(operation, Messages.DIALOG_SET_PARAMETER_LINK_PROBLEM_TITLE);
	}


	public GlobalParameterNode getGlobalParameter(String path) {
		String parameterName = path;
		GlobalParametersParentNode parametersParent;
		if(path.indexOf(":") != -1){
			String parentName = path.substring(0, path.indexOf(":"));
			parameterName = path.substring(path.indexOf(":") + 1);
			parametersParent = getOwnNode().getMethod().getClassNode();
			if(parametersParent.getName().equals(parentName) == false){
				return null;
			}
		}
		else{
			parametersParent = (RootNode)getOwnNode().getRoot();
		}
		return parametersParent.getGlobalParameter(parameterName);
	}

	public GlobalParameterNode getLink() {
		return getOwnNode().getLink();
	}

	public List<GlobalParameterNode> getAvailableLinks() {
		List<GlobalParameterNode> result = new ArrayList<GlobalParameterNode>();
		result.addAll(((RootNode)getOwnNode().getRoot()).getGlobalParameters());
		result.addAll(getOwnNode().getMethod().getClassNode().getGlobalParameters());
		return result;
	}

	@Override
	public MethodParameterNode getOwnNode(){
		return (MethodParameterNode)super.getOwnNode();
	}

	@Override
	protected IModelOperation setTypeOperation(String type) {
		return new MethodParameterOperationSetType(getOwnNode(), type, getAdapterProvider());
	}

	@Override
	public boolean nodeImplementedFullyOrPartially(){
		if(getImplementationStatus(getOwnNode().getMethod()) != EImplementationStatus.NOT_IMPLEMENTED){
			return true;
		}
		if(getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED){
			return true;
		}
		return false;
	}

	@Override
	public boolean exportAllComments(){
		boolean result = super.exportAllComments();
		exportTypeJavadocComments();
		return result;
	}

	@Override
	public boolean importTypeCommentsEnabled(){
		return getOwnNode().isLinked() == false;
	}

	private GlobalParameterNode findNewLink() {
		MethodNode method = getOwnNode().getMethod();
		int index = getOwnNode().getIndex();
		for(GlobalParameterNode parameter : method.getAvailableGlobalParameters()){
			if(method.checkDuplicate(index, parameter.getType()) == false){
				return parameter;
			}
		}
		return null;
	}

}
