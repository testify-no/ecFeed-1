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
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
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

public class ChoiceOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private ChoiceNode fTarget;

	private ITypeAdapterProvider fAdapterProvider;

	private class ParameterAdapter implements IParameterVisitor{

		@Override
		public Object visit(MethodParameterNode parameter) throws Exception {
			fOriginalDefaultValue = parameter.getDefaultValue();
			if(parameter != null && JavaTypeHelper.isUserType(parameter.getType())){
				if(parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false){
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

	private class ReverseOperation extends AbstractModelOperation{

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
			fTarget.setValueString(fOriginalValue);
			adaptParameter(fTarget.getParameter());
			markModelUpdated();
		}

		private void adaptParameter(AbstractParameterNode parameter) {
			try{
				parameter.accept(new ReverseParameterAdapter());
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetValue(fTarget, fNewValue, fAdapterProvider);
		}
	}

	public ChoiceOperationSetValue(ChoiceNode target, String newValue, ITypeAdapterProvider adapterProvider){
		super(OperationNames.SET_PARTITION_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = fTarget.getValueString();
		fAdapterProvider = adapterProvider;
	}

	@Override
	public void execute() throws ModelOperationException {

		String convertedValue = adaptChoiceValue(fTarget.getParameter().getType(), fNewValue);
		if(convertedValue == null){
			ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		fTarget.setValueString(convertedValue);
		adaptParameter(fTarget.getParameter());
		markModelUpdated();
	}

	private void adaptParameter(AbstractParameterNode parameter) {
		try{
			parameter.accept(new ParameterAdapter());
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

	@Override
	public String toString(){
		return "setValue[" + fTarget + "](" + fNewValue + ")";
	}

	private String adaptChoiceValue(String type, String value) throws ModelOperationException {

		if (value.length() > AdapterConstants.MAX_PARTITION_VALUE_STRING_LENGTH) {
			return null;
		}

		ITypeAdapter<?> typeAdapter = fAdapterProvider.getAdapter(type); 
		
		try {
			return typeAdapter.convert(value, fTarget.isRandomizedValue(), EConversionMode.WITH_EXCEPTION);
		} catch (RuntimeException ex) {
			ModelOperationException.report(ex.getMessage());
		}
		return null;
	}
}
