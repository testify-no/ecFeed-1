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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.StringHelper;

public class ClassOperationAddMethod extends AbstractModelOperation{

	private ClassNode fClassNode;
	private MethodNode fMethod;
	private int fIndex;

	public ClassOperationAddMethod(ClassNode target, MethodNode method, int index) {
		super(OperationNames.ADD_METHOD);
		fClassNode = target;
		fMethod = method;
		fIndex = index;
	}

	public ClassOperationAddMethod(ClassNode target, MethodNode method) {
		this(target, method, -1);
	}

	@Override
	public void execute() throws ModelOperationException {

		List<String> problems = new ArrayList<String>();

		if(fIndex == -1){
			fIndex = fClassNode.getMethods().size();
		}

		generateUniqeMethodName(fMethod);

		if(ClassNodeHelper.validateNewMethodSignature(fClassNode, fMethod.getName(), fMethod.getParameterTypes(), problems) == false){
			ModelOperationException.report(StringHelper.convertToMultilineString(problems));
		}

		if(fClassNode.addMethod(fMethod, fIndex) == false){
			ModelOperationException.report(Messages.UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT);
		}

		markModelUpdated();
	}

	private void generateUniqeMethodName(MethodNode methodNode) {

		String oldName = methodNode.getName();
		String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);
		String newName = ClassNodeHelper.generateNewMethodName(fClassNode, oldNameCore, methodNode.getParameterTypes());
		methodNode.setName(newName);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationRemoveMethod(fClassNode, fMethod);
	}

}
