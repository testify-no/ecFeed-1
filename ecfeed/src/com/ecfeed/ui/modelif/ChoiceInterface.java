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
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationAddLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRemoveLabels;
import com.ecfeed.core.adapter.operations.ChoiceOperationRenameLabel;
import com.ecfeed.core.adapter.operations.ChoiceOperationSetValue;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.local.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.local.JavaModelAnalyser;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class ChoiceInterface extends ChoicesParentInterface {

	public ChoiceInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		
		super(updateContext, fileInfoProvider);
	}

	public void setValue(String newValue) {
		
		IModelOperation operation = new ChoiceOperationSetValue(getOwnNode(), newValue, new EclipseTypeAdapterProvider());
		execute(operation, Messages.DIALOG_SET_CHOICE_VALUE_PROBLEM_TITLE);
	}

	public String getValue() {
		
		return getOwnNode().getValueString();
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
		
		return execute(new ChoiceOperationRemoveLabels(getOwnNode(), labels), Messages.DIALOG_REMOVE_LABEL_PROBLEM_TITLE);
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
		
		IModelOperation operation = new ChoiceOperationAddLabels(getOwnNode(), labels);
		return execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean addLabel(String newLabel) {
		
		IModelOperation operation = new ChoiceOperationAddLabel(getOwnNode(), newLabel);
		return execute(operation, Messages.DIALOG_ADD_LABEL_PROBLEM_TITLE);
	}

	public boolean isLabelInherited(String label) {
		return getOwnNode().getInheritedLabels().contains(label);
	}

	public boolean renameLabel(String label, String newValue) {
		
		if(label.equals(newValue)){
			return false;
		}
		
		if(getOwnNode().getInheritedLabels().contains(newValue)) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
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
		return execute(operation, Messages.DIALOG_CHANGE_LABEL_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		
		if(JavaTypeHelper.isJavaType(getOwnNode().getParameter().getType())){
			return false;
		}
		if(getOwnNode().isAbstract()) {
			return false;
		}
		return super.goToImplementationEnabled();
	}

	@Override
	public void goToImplementation() {
		
		try{
			IType type = JavaModelAnalyser.getIType(getParameter().getType());
			
			if (type != null && getOwnNode().isAbstract() == false) {
				for (IField field : type.getFields()) {
					if (field.getElementName().equals(getOwnNode().getValueString())) {
						JavaUI.openInEditor(field);
						break;
					}
				}
			}
		} catch(Exception e){SystemLogger.logCatch(e.getMessage());}
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
}
