package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;

public class ChoiceOperationSetRandomizeValue extends AbstractModelOperation {
	private boolean fNewValue;
	//	private boolean fOriginalValue;
	//	private boolean fOriginalDefaultValue;
	private ChoiceNode fChoiceNode;

	//BooleanTypeAdapter
	private ITypeAdapterProvider fAdapterProvider;


	public ChoiceOperationSetRandomizeValue(ChoiceNode choiceNode, boolean newValue, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_PARTITION_VALUE);
		fNewValue = newValue;
		fChoiceNode = choiceNode;
		fAdapterProvider = adapterProvider;
//		fOriginalValue = fChoiceNode.isRandomizeValue();
	}

	@Override
	public void execute() throws ModelOperationException {
		//		String convertedValue = validateChoiceValue(fTarget.getParameter().getType(), fNewValue);
		//		if(convertedValue == null){
		//			ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(Boolean.toString(fNewValue)));
		//		}
		boolean convertedValue = validateChoiceValue(fChoiceNode.getParameter().getDescription(), fNewValue);
		if (convertedValue == false) {
			ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(Boolean.toString(fNewValue)));
		}
		fChoiceNode.setRandomizeValue(convertedValue);
		adaptParameter(fChoiceNode.getParameter());
		markModelUpdated();
	}

	private void adaptParameter(AbstractParameterNode parameter) {
		try {
			parameter.accept(new ParameterAdapter());
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	private class ParameterAdapter implements IParameterVisitor{

		@Override
		public Object visit(MethodParameterNode parameter) throws Exception {
			//fOriginalDefaultValue = parameter.getDefaultValue();
			if(parameter != null && JavaTypeHelper.isUserType(parameter.getType())){
				if(parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false){
					//parameter.setDefaultValueString(fNewValue);
				}
			}
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

	}

	private boolean validateChoiceValue(String type, boolean fNewValue2) {
		return type.equalsIgnoreCase("true") || type.equalsIgnoreCase("false");
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	private class ReverseOperation extends AbstractModelOperation {
		public ReverseOperation() {
			super(ChoiceOperationSetRandomizeValue.this.getName());
		}

//		private class ReverseParameterAdapter implements IParameterVisitor {
//
//			@Override
//			public Object visit(MethodParameterNode parameter) throws Exception {
//				return null;
//			}
//
//			@Override
//			public Object visit(GlobalParameterNode parameter) throws Exception {
//				return null;
//			}
//
//		}

//		private void adaptParameter(AbstractParameterNode parameter) {
//			try{
//				parameter.accept(new ReverseParameterAdapter());
//			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
//		}

		@Override
		public void execute() throws ModelOperationException {
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ChoiceOperationSetRandomizeValue(fChoiceNode, fNewValue,
					fAdapterProvider);
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}

