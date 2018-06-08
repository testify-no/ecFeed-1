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
import com.ecfeed.core.utils.SystemLogger;

public class ChoiceOperationSetRandomizedValue extends AbstractModelOperation {

	private boolean fNewValue;
	private ChoiceNode fChoiceNode;
	private ITypeAdapterProvider fAdapterProvider;


	public ChoiceOperationSetRandomizedValue(ChoiceNode choiceNode, boolean newValue, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.SET_PARTITION_VALUE);
		fNewValue = newValue;
		fChoiceNode = choiceNode;
		fAdapterProvider = adapterProvider;
	}

	@Override
	public void execute() throws ModelOperationException {

		boolean convertedValue = validateChoiceValue(fChoiceNode.getParameter().getDescription(), fNewValue);
		if (convertedValue == false) {
			ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(Boolean.toString(fNewValue)));
		}

		fChoiceNode.setRandomizedValue(convertedValue);
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
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

	private class ReverseOperation extends AbstractModelOperation {
		public ReverseOperation() {
			super(ChoiceOperationSetRandomizedValue.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetRandomizedValue(fChoiceNode, fNewValue,
					fAdapterProvider);
		}

		@Override
		public String getName() {
			return null;
		}

	}

}

