/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.adapter.java.ChoiceValueParser;
import com.ecfeed.core.adapter.java.Constants;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.JavaTypeHelper;

public class JavaTestRunner {

	private ModelClassLoader fLoader;
	private boolean fIsExport;
	private MethodNode fTarget;
	private Class<?> fTestClass;
	private Method fTestMethod;
	private ITestMethodInvoker fTestMethodInvoker;

	public JavaTestRunner(ModelClassLoader loader, boolean isExport, ITestMethodInvoker testMethodInvoker) {
		fLoader = loader;
		fIsExport = isExport;
		fTestMethodInvoker = testMethodInvoker;
		fTestClass = null;
		fTestMethod = null;
	}

	public void setTarget(MethodNode target) throws RunnerException {
		fTarget = target;
	}

	public void createTestClassAndMethod(MethodNode methodNode) throws RunnerException {
		ClassNode classNode = methodNode.getClassNode();
		fTestClass = getTestClass(classNode.getName());
		fTestMethod = getTestMethod(fTestClass, fTarget);
	}	

	public void runTestCase(List<ChoiceNode> testData) throws RunnerException{

		validateTestData(testData);

		Object instance = null;

		if (fTestMethodInvoker.isClassInstanceRequired())	{
			try {
				instance = fTestClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				RunnerException.report(
						Messages.CANNOT_INVOKE_TEST_METHOD(
								fTarget.toString(), 
								testData.toString(), 
								e.getMessage()));
			}
		}

		Object[] arguments = getArguments(testData);
		Object[] choiceNames = getChoiceNames(testData);

		try {
			fTestMethodInvoker.invoke(fTestMethod, getClassName(fTestClass), instance, arguments, choiceNames, testData.toString());
		} catch (Exception e) {
			RunnerException.report(e.getMessage());
		}
	}

	private static String getClassName(Class<?> theClass) {
		if (theClass == null) {
			return null;
		}
		return theClass.getName();
	}

	public void prepareTestCaseForExport(List<ChoiceNode> testData) throws RunnerException{
		validateTestData(testData);
		Object[] arguments = getArguments(testData);
		fTestMethodInvoker.invoke(null, null, null, arguments, null, null);
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) throws RunnerException {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel)){
				return method;
			}
		}
		RunnerException.report(Messages.METHOD_NOT_FOUND(methodModel.toString()));
		return null;
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		List<String> types = new ArrayList<String>();
		for(Class<?> type : parameterTypes){
			types.add(JavaUtils.getTypeName(type.getCanonicalName()));
		}
		return methodName.equals(methodModel.getName()) && types.equals(methodModel.getParametersTypes());
	}

	protected Object[] getArguments(List<ChoiceNode> testData) throws RunnerException {
		List<Object> args = new ArrayList<Object>();
		ChoiceValueParser parser = new ChoiceValueParser(fLoader, fIsExport);
		for(ChoiceNode choice : testData){
			Object value = parser.parseValue(choice);
			if(value == null){
				String type = choice.getParameter().getType();
				//check if null value acceptable
				if(JavaUtils.isString(type) || JavaTypeHelper.isUserType(type)){
					if(choice.getValueString().equals(Constants.VALUE_REPRESENTATION_NULL) == false){
						RunnerException.report(Messages.CANNOT_PARSE_PARAMETER(type, choice.getValueString()));
					}
				}
			}

			args.add(value);
		}
		return args.toArray();
	}

	protected Object[] getChoiceNames(List<ChoiceNode> testData) throws RunnerException {
		List<String> args = new ArrayList<String>();

		for(ChoiceNode choice : testData){
			args.add(choice.getName());
		}
		return args.toArray();
	}	

	private void validateTestData(List<ChoiceNode> testData) throws RunnerException {
		List<String> dataTypes = new ArrayList<String>();
		for(ChoiceNode parameter : testData){
			dataTypes.add(parameter.getParameter().getType());
		}
		if(dataTypes.equals(fTarget.getParametersTypes()) == false){
			RunnerException.report(Messages.WRONG_TEST_METHOD_SIGNATURE(fTarget.toString()));
		}
	}

	private Class<?> getTestClass(String qualifiedName) throws RunnerException {
		Class<?> testClass = fLoader.loadClass(qualifiedName);
		if(testClass == null){
			RunnerException.report(Messages.CANNOT_LOAD_CLASS(qualifiedName));
		}
		return testClass;
	}

}