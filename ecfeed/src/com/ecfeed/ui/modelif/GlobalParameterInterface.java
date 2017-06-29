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

import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.GlobalParameterOperationSetType;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class GlobalParameterInterface extends AbstractParameterInterface {

	public GlobalParameterInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	public List<MethodParameterNode> getLinkers(){
		return getOwnNode().getLinkers();
	}

	@Override
	public GlobalParameterNode getOwnNode(){
		return (GlobalParameterNode)super.getOwnNode();
	}

	@Override
	protected IModelOperation setTypeOperation(String type) {
		return new GlobalParameterOperationSetType(getOwnNode(), type, getAdapterProvider());
	}

	@Override
	public boolean nodeImplementedFullyOrPartially(){
		return super.nodeImplementedFullyOrPartially() && JavaTypeHelper.isUserType(getType());
	}

	@Override
	public boolean importTypeCommentsEnabled(){
		return true;
	}

}
