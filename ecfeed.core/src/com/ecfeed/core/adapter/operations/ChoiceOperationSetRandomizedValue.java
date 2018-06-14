package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ModelOperationException;

public class ChoiceOperationSetRandomizedValue extends AbstractModelOperation { 

	private boolean fNewRandomized;
	private boolean fOriginalRandomized;
	private ChoiceNode fChoiceNode;
	private ITypeAdapterProvider fAdapterProvider;


	public ChoiceOperationSetRandomizedValue(
			ChoiceNode choiceNode, boolean newRandomized, ITypeAdapterProvider adapterProvider) {

		super(OperationNames.SET_CHOICE_RANDOMIZED_FLAG);

		fNewRandomized = newRandomized;
		fChoiceNode = choiceNode;
		fAdapterProvider = adapterProvider;

		fOriginalRandomized = choiceNode.isRandomizedValue();
	}

	@Override
	public void execute() throws ModelOperationException {
		adaptChoice(fNewRandomized);
		markModelUpdated();
	}

	private void adaptChoice(boolean newRandomized) throws ModelOperationException {

		String newValue = adaptChoiceValue(newRandomized);
		fChoiceNode.setValueString(newValue);
		fChoiceNode.setRandomizedValue(newRandomized);
	}

	private String adaptChoiceValue(boolean randomized) throws ModelOperationException {

		String type = fChoiceNode.getParameter().getType();

		ITypeAdapter<?> typeAdapter = fAdapterProvider.getAdapter(type); 

		try {
			return typeAdapter.convert(
					fChoiceNode.getValueString(), randomized, EConversionMode.QUIET);

		} catch (RuntimeException ex) {
			ModelOperationException.report(ex.getMessage());
		}

		return null;
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
			adaptChoice(fOriginalRandomized);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetRandomizedValue(fChoiceNode, fNewRandomized, fAdapterProvider);
		}

		@Override
		public String getName() {
			return null;
		}

	}

}

