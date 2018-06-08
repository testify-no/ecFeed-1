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

import com.ecfeed.core.adapter.operations.GenericOperationAddParameter;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public abstract class ParametersParentInterface extends AbstractNodeInterface {

	public ParametersParentInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	public abstract AbstractParameterNode addNewParameter();

	public boolean addParameter(AbstractParameterNode parameter, int index) {

		return getOperationExecuter().execute(
				new GenericOperationAddParameter(getOwnNode(), parameter, index, true), 
				Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	protected boolean removeParameters(Collection<? extends AbstractParameterNode> parameters){
		return super.removeChildren(parameters, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	protected String generateNewParameterType() {
		return JavaTypeHelper.getSupportedJavaTypes()[0];
	}

	protected String generateNewParameterName() {
		int i = 0;
		String name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(getOwnNode().getParameter(name) != null){
			name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}

	@Override
	public ParametersParentNode getOwnNode(){
		return (ParametersParentNode)super.getOwnNode();
	}
}
