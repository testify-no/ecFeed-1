package com.testify.ecfeed.modelif.java.classx;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.common.Messages;

public class ClassOperationAddMethod implements IModelOperation {
	
	private ClassNode fTarget;
	private MethodNode fMethod;

	public ClassOperationAddMethod(ClassNode target, MethodNode method) {
		fTarget = target;
		fMethod = method;
	}

	@Override
	public void execute() throws ModelIfException {
		List<String> problems = new ArrayList<String>();
		if(JavaClassUtils.validateNewMethodSignature(fTarget, fMethod.getName(), fMethod.getCategoriesTypes(), problems) == false){
			throw new ModelIfException(JavaUtils.consolidate(problems));
		}
		if(fTarget.addMethod(fMethod) == false){
			throw new ModelIfException(Messages.UNEXPECTED_PROBLEM_WHILE_ADDING_METHOD);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationRemoveMethod(fTarget, fMethod);
	}

}