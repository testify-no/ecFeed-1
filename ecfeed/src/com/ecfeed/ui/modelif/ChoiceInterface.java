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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRemoveLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRenameLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationSetRandomizedValue;
import com.ecfeed.core.adapter.operations.ChoiceOperationSetValue;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.ImplementationAdapter;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;

public class ChoiceInterface extends ChoicesParentInterface {

	public ChoiceInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {

		super(updateContext, javaProjectProvider);
	}

	public void setValue(String newValue) {

		IModelOperation operation = 
				new ChoiceOperationSetValue(getOwnNode(), newValue, new EclipseTypeAdapterProvider());
		getOperationExecuter().execute(operation, Messages.DIALOG_SET_CHOICE_VALUE_PROBLEM_TITLE);
	}

	public String getValue() {

		return getOwnNode().getValueString();
	}

	public void setRandomized(boolean isRandomized) {

		IModelOperation operation = 
				new ChoiceOperationSetRandomizedValue(getOwnNode(), isRandomized, new EclipseTypeAdapterProvider());

		getOperationExecuter().execute(operation, Messages.DIALOG_SET_CHOICE_RANDOMIZED_PROBLEM_TITLE);
	}
	
	public boolean isRandomized() {
		return getOwnNode().isRandomizedValue();
	}

	@Override
	public AbstractParameterNode getParameter() {
		return getOwnNode().getParameter();
	}

	public boolean removeLabels(Collection<String> labels) {

		boolean removeMentioningConstraints = false;

		for (String label : labels) {

			if (getOwnNode().getParameter().mentioningConstraints(label).size() > 0 
					&& getOwnNode().getParameter().getLabeledChoices(label).size() == 1) {
				removeMentioningConstraints = true;
			}
		}

		if(removeMentioningConstraints){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_LABELS_WARNING_TITLE,
					Messages.DIALOG_REMOVE_LABELS_WARNING_MESSAGE) == false){
				return false;
			}
		}

		return getOperationExecuter().execute(new ChoiceOperationRemoveLabels(getOwnNode(), labels), Messages.DIALOG_REMOVE_LABEL_PROBLEM_TITLE);
	}

	public String addNewLabel() {

		String newLabel = CommonConstants.DEFAULT_NEW_PARTITION_LABEL;
		int i = 1;
		while(getOwnNode().getLeafLabels().contains(newLabel)){
			newLabel = CommonConstants.DEFAULT_NEW_PARTITION_LABEL + "(" + i + ")";
			i++;
		}
		if(addLabel(newLabel)){
			return newLabel;
		}
		return null;
	}

	public boolean addLabels(List<String> labels) {

		IModelOperation operation = 
				new ChoiceOperationAddLabels(getOwnNode(), labels, getOwnNode(), getOwnNode());

		return getOperationExecuter().execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean addLabel(String newLabel) {

		IModelOperation operation = new ChoiceOperationAddLabel(getOwnNode(), newLabel);
		return getOperationExecuter().execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean isLabelInherited(String label) {
		return getOwnNode().getInheritedLabels().contains(label);
	}

	public boolean renameLabel(String label, String newValue) {

		if(label.equals(newValue)){
			return false;
		}

		if(getOwnNode().getInheritedLabels().contains(newValue)) {
			ErrorDialog.open(
					Messages.DIALOG_RENAME_LABELS_ERROR_TITLE,
					Messages.DIALOG_LABEL_IS_ALREADY_INHERITED);
			return false;
		}

		if(getOwnNode().getLeafLabels().contains(newValue)) {
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_RENAME_LABELS_WARNING_TITLE,
					Messages.DIALOG_DESCENDING_LABELS_WILL_BE_REMOVED_WARNING_TITLE) == false){
				return false;
			}
		}

		IModelOperation operation = new ChoiceOperationRenameLabel(getOwnNode(), label, newValue);
		return getOperationExecuter().execute(operation, Messages.DIALOG_CHANGE_LABEL_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		if(JavaTypeHelper.isJavaType(getOwnNode().getParameter().getType())){
			return false;
		}

		if (getOwnNode().isAbstract()) {
			return false;
		}

		return super.goToImplementationEnabled();
	}

	@Override
	public void goToImplementation() {
		ImplementationAdapter.goToChoiceImplementation(getOwnNode(), getParameter());
	}


	@Override
	public ChoiceNode getOwnNode() {
		return (ChoiceNode)super.getOwnNode();
	}

	@Override
	public boolean nodeImplementedFullyOrPartially() {
		return super.nodeImplementedFullyOrPartially() && getImplementationStatus() != EImplementationStatus.NOT_IMPLEMENTED;
	}

	@Override
	public String canAddChildren(Collection<? extends AbstractNode> newChildren) {
		// Always can add. For colliding choice names other names will be generated later.
		return null;
	}

	List<String> getListOfChildrenChoiceNames() {
		ChoiceNode choiceNode = getOwnNode();
		return choiceNode.getListOfChildrenChoiceNames();
	}

	public ITypeAdapter<?> getTypeAdapter() {

		EclipseTypeAdapterProvider eclipseTypeAdapterProvider = new EclipseTypeAdapterProvider();

		ChoiceNode choiceNode = getOwnNode();
		AbstractParameterNode abstractParameterNode = choiceNode.getParameter();
		String typeName = abstractParameterNode.getType();

		return eclipseTypeAdapterProvider.getAdapter(typeName);
	}

	public Set<String> convertItemsToMatchChoice(Set<String> items, EConversionMode conversionMode) {

		ChoiceNode choiceNode = getOwnNode();

		Set<String> newItems = new LinkedHashSet<String>();
		ITypeAdapter<?> typeAdapter = getTypeAdapter();

		for (String item : items) {

			String newItem = 
					typeAdapter.convert(item, choiceNode.isRandomizedValue(), conversionMode);

			newItems.add(newItem);
		}

		return newItems;
	}


}
