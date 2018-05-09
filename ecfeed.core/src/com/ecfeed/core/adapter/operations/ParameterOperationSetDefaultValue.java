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
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;

public class ParameterOperationSetDefaultValue extends AbstractModelOperation {

	private MethodParameterNode fTarget;
	private ITypeAdapter fTypeAdapter;
	private String fNewValue;
	private String fOriginalValue;

	public ParameterOperationSetDefaultValue(MethodParameterNode target, String newValue, ITypeAdapter typeAdapter) {
		super(OperationNames.SET_DEFAULT_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getDefaultValue();
		fTypeAdapter = typeAdapter;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		String convertedValue = fTypeAdapter.convert(fNewValue);

		if (convertedValue == null) {
			ModelOperationException.report(Messages.CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM);
		}

		fTarget.setDefaultValueString(convertedValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ParameterOperationSetDefaultValue(fTarget, fOriginalValue, fTypeAdapter);
	}

}
