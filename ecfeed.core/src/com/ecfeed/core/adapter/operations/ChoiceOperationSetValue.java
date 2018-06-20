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
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;
//import com.ecfeed.core.utils.ValueFieldHelper;

public class ChoiceOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private ChoiceNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;

	public ChoiceOperationSetValue(ChoiceNode target, String newValue, ITypeAdapterProvider adapterProvider) {

		super(OperationNames.SET_PARTITION_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fAdapterProvider = adapterProvider;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);

		String convertedValue = validateChoiceValue(fTarget.getParameter().getType(), fNewValue);

		if(convertedValue == null){
			ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}

		fTarget.setValueString(convertedValue);
		adaptParameter(fTarget.getParameter());

		markModelUpdated();
	}

	private void adaptParameter(AbstractParameterNode parameter) {

		try {
			parameter.accept(new ParameterAdapter());
		}catch(Exception e) { 
			SystemLogger.logCatch(e.getMessage());
		}
	}

	@Override
	public IModelOperation reverseOperation() {

		return new ReverseOperation();
	}

	private String validateChoiceValue(String type, String value) throws ModelOperationException {
		if (value.length() > AdapterConstants.MAX_PARTITION_VALUE_STRING_LENGTH) {
			return null;
		}

		if(!fTarget.isRandomizeValue()) {
			return fAdapterProvider.getAdapter(type).convert(value);
		}
		else {

			return fAdapterProvider.getAdapter(type).convert(value);
			//			return ValueFieldHelper.adapt(type, value, fTarget.isRandomizeValue(), fAdapterProvider);

			//fAdapterProvider;
			//			/return "nothing";
		}

		//fTarget.isRandomizeValue()


	}


	private class ParameterAdapter implements IParameterVisitor{

		@Override
		public Object visit(MethodParameterNode parameter) throws Exception {

			fOriginalDefaultValue = parameter.getDefaultValue();

			if (parameter != null && JavaTypeHelper.isUserType(parameter.getType())) {
				if (parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false) {
					parameter.setDefaultValueString(fNewValue);
				}
			}

			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

	}

	private class ReverseOperation extends AbstractModelOperation {

		private class ReverseParameterAdapter implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode parameter) throws Exception {

				parameter.setDefaultValueString(fOriginalDefaultValue);
				return null;
			}

			@Override
			public Object visit(GlobalParameterNode parameter) throws Exception {
				return null;
			}

		}

		public ReverseOperation() {

			super(ChoiceOperationSetValue.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);
			fTarget.setValueString(fOriginalValue);
			adaptParameter(fTarget.getParameter());
			markModelUpdated();
		}

		private void adaptParameter(AbstractParameterNode parameter) {

			try {
				parameter.accept(new ReverseParameterAdapter());
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ChoiceOperationSetValue(fTarget, fNewValue, fAdapterProvider);
		}

	}

	@Override
	public String toString(){
		return "setValue[" + fTarget + "](" + fNewValue + ")";
	}

}
