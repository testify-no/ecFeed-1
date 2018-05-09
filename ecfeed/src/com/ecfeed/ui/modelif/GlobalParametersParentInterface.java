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

import com.ecfeed.core.adapter.operations.ReplaceMethodParametersWithGlobalOperation;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class GlobalParametersParentInterface extends ParametersParentInterface {

	public GlobalParametersParentInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	@Override
	public AbstractParameterNode addNewParameter() {

		GlobalParameterNode parameter = new GlobalParameterNode(generateNewParameterName(), generateNewParameterType());

		if (addParameter(parameter, getOwnNode().getParameters().size())) {
			return parameter;
		}

		return null;
	}

	public boolean removeGlobalParameters(Collection<GlobalParameterNode> parameters){
		return super.removeParameters(parameters);
	}

	@Override
	public GlobalParametersParentNode getOwnNode(){
		return (GlobalParametersParentNode)super.getOwnNode();
	}

	public boolean replaceMethodParametersWithGlobal(List<MethodParameterNode> originalParameters) {
		return getOperationExecuter().execute(new ReplaceMethodParametersWithGlobalOperation(getOwnNode(), originalParameters, getAdapterProvider()), Messages.DIALOG_REPLACE_PARAMETERS_WITH_LINKS_TITLE);
	}
}
