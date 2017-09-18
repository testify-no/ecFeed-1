/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;

public class RootOperationAddNewClass extends AbstractModelOperation {

	private RootNode fRootNode;
	private ClassNode fclassToAdd;
	private int fAddIndex;

	public RootOperationAddNewClass(RootNode rootNode, ClassNode classToAdd, int addIndex) {
		super(OperationNames.ADD_CLASS);
		fRootNode = rootNode;
		fclassToAdd = classToAdd;
		fAddIndex = addIndex;
	}

	public RootOperationAddNewClass(RootNode target, ClassNode classToAdd) {
		this(target, classToAdd, -1);
	}

	@Override
	public void execute() throws ModelOperationException {

		setNodeToBeSelectedAfterTheOperation(fRootNode);
		String name = fclassToAdd.getName();
		if(fAddIndex == -1){
			fAddIndex = fRootNode.getClasses().size();
		}
		if(name.matches(AdapterConstants.REGEX_CLASS_NODE_NAME) == false){
			ModelOperationException.report(Messages.CLASS_NAME_REGEX_PROBLEM);
		}
		if(fRootNode.getClassModel(name) != null){
			ModelOperationException.report(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
		}
		fRootNode.addClass(fclassToAdd, fAddIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationRemoveClass(fRootNode, fclassToAdd);
	}

}
