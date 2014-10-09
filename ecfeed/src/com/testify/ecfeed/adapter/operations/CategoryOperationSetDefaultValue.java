package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.CategoryNode;

public class CategoryOperationSetDefaultValue extends AbstractModelOperation {

	private CategoryNode fTarget;
	private ITypeAdapter fTypeAdapter;
	private String fNewValue;
	private String fOriginalValue;

	public CategoryOperationSetDefaultValue(CategoryNode target, String newValue, ITypeAdapter typeAdapter) {
		super(OperationNames.SET_DEFAULT_VALUE);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getDefaultValue();
		fTypeAdapter = typeAdapter;
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fTypeAdapter.convert(fNewValue) == null){
			throw new ModelOperationException(Messages.CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM);
		}
		fTarget.setDefaultValueString(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new CategoryOperationSetDefaultValue(fTarget, fOriginalValue, fTypeAdapter);
	}

}