package com.testify.ecfeed.core.adapter.operations;

import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.model.ClassNode;

public class ClassOperationSetRunOnAndroid extends AbstractModelOperation {

	private ClassNode fClassNode;
	private boolean fNewValue;
	private boolean fOriginalValue;

	public ClassOperationSetRunOnAndroid(ClassNode classNode, boolean newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fClassNode = classNode;
		fNewValue = newValue;
		fOriginalValue = classNode.getRunOnAndroid();
	}

	@Override
	public void execute() throws ModelOperationException {
		fClassNode.setRunOnAndroid(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetRunOnAndroid(fClassNode, fOriginalValue);
	}

}