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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.GenericOperationAddChoice;
import com.ecfeed.core.adapter.operations.GenericOperationRemoveChoice;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.EclipseTypeHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class ChoicesParentInterface extends AbstractNodeInterface {

	public ChoicesParentInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider){
		super(updateContext, javaProjectProvider);
	}

	public AbstractParameterNode getParameter() {
		return getOwnNode().getParameter();
	}

	public ChoiceNode addNewChoice() {
		String name = generateChoiceName();
		String value = generateNewChoiceValue();
		ChoiceNode newChoice = new ChoiceNode(name, value);
		if(addChoice(newChoice)){
			return newChoice;
		}
		return null;
	}

	public boolean addChoice(ChoiceNode newChoice) {
		IModelOperation operation = new GenericOperationAddChoice(getOwnNode(), newChoice, new EclipseTypeAdapterProvider(), getOwnNode().getChoices().size(), true);
		return getOperationExecuter().execute(operation, Messages.DIALOG_ADD_CHOICE_PROBLEM_TITLE);
	}

	public boolean removeChoice(ChoiceNode choice) {
		IModelOperation operation = new GenericOperationRemoveChoice(getOwnNode(), choice, getAdapterProvider(), true);
		return getOperationExecuter().execute(operation, Messages.DIALOG_REMOVE_CHOICE_TITLE);
	}

	public boolean removeChoices(Collection<ChoiceNode> choices) {
		boolean displayWarning = false;
		for(MethodNode method : getOwnNode().getParameter().getMethods()){
			for(ChoiceNode p : choices){
				if(method.mentioningConstraints(p).size() > 0 || method.mentioningTestCases(p).size() > 0){
					displayWarning = true;
				}
			}
		}
		if(displayWarning){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_CHOICE_WARNING_TITLE,
					Messages.DIALOG_REMOVE_CHOICE_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return removeChildren(choices, Messages.DIALOG_REMOVE_CHOICES_PROBLEM_TITLE);
	}

	public boolean isPrimitive() {
		return JavaTypeHelper.isJavaType(getOwnNode().getParameter().getType());
	}

	public boolean isUserType() {
		return !isPrimitive();
	}

	public List<String> getSpecialValues() {
		return EclipseTypeHelper.getSpecialValues(getOwnNode().getParameter().getType());
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive() || isBoolean();
	}

	public  boolean isBoolean() {
		String typeName = getOwnNode().getParameter().getType();
		return JavaTypeHelper.isBooleanTypeName(typeName);
	}

	@Override
	public ChoicesParentNode getOwnNode(){
		return (ChoicesParentNode)super.getOwnNode();
	}

	protected String generateNewChoiceValue() {
		String type = getOwnNode().getParameter().getType();
		String value = EclipseTypeHelper.getDefaultExpectedValue(type);
		if(isPrimitive() == false && EclipseTypeHelper.getSpecialValues(type).size() == 0){
			int i = 0;
			while(getOwnNode().getLeafChoiceValues().contains(value)){
				value = EclipseTypeHelper.getDefaultExpectedValue(type) + i++;
			}
		}
		return value;
	}

	protected String generateChoiceName(){
		String name = CommonConstants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;

		ArrayList<String> choiceNames = new ArrayList<>();
		for(ChoiceNode choice: getOwnNode().getChoices()){
			choiceNames.add(choice.getName());
		}

		while(choiceNames.contains(name)){
			name = CommonConstants.DEFAULT_NEW_PARTITION_NAME + i++;
		}
		return name;
	}

}
