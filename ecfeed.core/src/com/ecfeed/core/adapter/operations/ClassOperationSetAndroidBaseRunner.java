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
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ModelOperationException;

public class ClassOperationSetAndroidBaseRunner extends AbstractModelOperation {

	private ClassNode fTarget;
	private String fNewValue;
	private String fOriginalValue;

	public ClassOperationSetAndroidBaseRunner(ClassNode target, String newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getAndroidRunner();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setAndroidRunner(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetAndroidBaseRunner(fTarget, fOriginalValue);
	}

	@Override
	public AbstractNode getNodeToBeSelectedAfterTheOperation() {
		return fTarget;
	}

}
