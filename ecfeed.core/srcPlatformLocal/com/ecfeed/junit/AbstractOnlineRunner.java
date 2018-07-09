/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorParameter;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.junit.annotations.Constraints;
import com.ecfeed.junit.annotations.Generator;
import com.ecfeed.junit.annotations.GeneratorParameter;
import com.ecfeed.junit.annotations.GeneratorParameterNames;
import com.ecfeed.junit.annotations.GeneratorParameterValues;

public abstract class AbstractOnlineRunner extends AbstractJUnitRunner {

	public AbstractOnlineRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected abstract void addFrameworkMethods(
			FrameworkMethod frameworkMethod,
			IGenerator<ChoiceNode> initializedGenerator,
			List<FrameworkMethod> inOutFrameworkMethods) throws RunnerException;

	@Override
	protected void addMethodsForOneCustomMethod(
			FrameworkMethod frameworkMethod,
			MethodNode methodNode, 
			List<FrameworkMethod> inOutFrameworkMethods) throws RunnerException {

		IGenerator<ChoiceNode> generator = getGenerator(frameworkMethod);
		List<List<ChoiceNode>> input = getInput(methodNode);

		Collection<IConstraint<ChoiceNode>> constraints = getConstraints(frameworkMethod, methodNode);
		Map<String, Object> parameters = getGeneratorParameters(generator, frameworkMethod);

		try {
			generator.initialize(input, constraints, parameters, null);
		} catch (GeneratorException e) {
			RunnerException.report(Messages.GENERATOR_INITIALIZATION_PROBLEM(e.getMessage()));
		}

		addFrameworkMethods(frameworkMethod, generator, inOutFrameworkMethods);
	}

	private Collection<IConstraint<ChoiceNode>> getConstraints(
			FrameworkMethod method, 
			MethodNode methodNode) {

		Collection<String> constraintsNames = getConstraintsNames(method);

		if (constraintsNames == null || constraintsNames.contains(Constraints.ALL)) {
			return getAllConstraints(methodNode);
		}

		if (constraintsNames.contains(Constraints.NONE)) {
			return getEmptyConstraints();
		}

		return getConstraintsForNames(constraintsNames, methodNode);
	}

	private Collection<IConstraint<ChoiceNode>> getEmptyConstraints() {
		return new HashSet<IConstraint<ChoiceNode>>();
	}

	private Collection<IConstraint<ChoiceNode>> getAllConstraints(MethodNode methodNode) {

		Collection<String> constraintsNames = methodNode.getConstraintsNames();
		return getConstraintsForNames(constraintsNames, methodNode);
	}

	private Collection<IConstraint<ChoiceNode>> getConstraintsForNames(
			Collection<String> constraintsNames,
			MethodNode methodNode) {

		Collection<IConstraint<ChoiceNode>> constraints = new HashSet<IConstraint<ChoiceNode>>();

		for (String name : constraintsNames) {
			constraints.addAll(methodNode.getConstraints(name));
		}

		return constraints;
	}

	private List<List<ChoiceNode>> getInput(MethodNode methodModel) {

		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();

		for (MethodParameterNode parameter : methodModel.getMethodParameters()) {

			if (parameter.isExpected()) {
				ChoiceNode choice = new ChoiceNode("expected", parameter.getDefaultValue());
				choice.setParent(parameter);
				result.add(Arrays.asList(new ChoiceNode[]{choice}));
			} else {
				result.add(parameter.getLeafChoices());
			}
		}

		return result;
	}

	private IGenerator<ChoiceNode> getGenerator(FrameworkMethod method) throws RunnerException {

		IGenerator<ChoiceNode> generator = getGenerator(method.getAnnotations());

		if (generator == null) {
			generator = getGenerator(getTestClass().getAnnotations());
		}

		if (generator == null) {
			RunnerException.report(Messages.NO_VALID_GENERATOR(method.getName()));
		}

		return generator;
	}

	private Set<String> getConstraintsNames(FrameworkMethod method) {

		Set<String> names = constraintsNames(method.getAnnotations());

		if (names == null) {
			names = constraintsNames(getTestClass().getAnnotations());
		}

		return names;
	}

	private Map<String, Object> getGeneratorParameters(
			IGenerator<ChoiceNode> generator, 
			FrameworkMethod method) throws RunnerException {

		List<IGeneratorParameter> parameters = generator.parameters();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String>	parsedParameters = parseParameters(method.getAnnotations());

		if (parsedParameters.size() == 0) {
			parsedParameters = parseParameters(getTestClass().getAnnotations());
		}

		for (IGeneratorParameter parameter : parameters) {

			Object value = getParameterValue(parameter, parsedParameters);

			if (value == null && parameter.isRequired()) {
				RunnerException.report(Messages.MISSING_REQUIRED_PARAMETER(parameter.getName()));
			} else if (value != null) {
				result.put(parameter.getName(), value);
			}
		}

		return result;
	}

	private Object getParameterValue(
			IGeneratorParameter parameter,
			Map<String, String> parsedParameters) throws RunnerException {

		String valueString = parsedParameters.get(parameter.getName());

		if (valueString != null) {
			try {
				switch (parameter.getType()) {
				case BOOLEAN:
					return Boolean.parseBoolean(valueString);
				case DOUBLE:
					return Double.parseDouble(valueString);
				case INTEGER:
					return Integer.parseInt(valueString);
				case STRING:
					return valueString;
				}
			}
			catch(Throwable e){
				RunnerException.report(Messages.WRONG_PARAMETER_TYPE(parameter.getName(), e.getMessage()));
			}
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IGenerator<ChoiceNode> getGenerator(Annotation[] annotations) throws RunnerException{

		IGenerator<ChoiceNode> generator = null;

		for (Annotation annotation : annotations) {

			if (annotation instanceof Generator) {
				try {
					Class<? extends IGenerator> generatorClass = ((Generator)annotation).value();
					generatorClass.getTypeParameters();
					Constructor<? extends IGenerator> constructor = generatorClass.getConstructor(new Class<?>[]{});
					generator = (constructor.newInstance(new Object[]{}));
				} catch (Exception e) {
					RunnerException.report(Messages.CANNOT_INSTANTIATE_GENERATOR(e.getMessage()));
				}
			}
		}
		return generator;
	}

	private Map<String, String> parseParameters(Annotation[] annotations) throws RunnerException {

		Map<String, String> result = new HashMap<String, String>();

		String[] parameterNames = null;
		String[] parameterValues = null;

		for (Annotation annotation : annotations) {
			if (annotation instanceof GeneratorParameter) {
				GeneratorParameter parameter = (GeneratorParameter)annotation;
				result.put(parameter.name(), parameter.value());
			} else if (annotation instanceof GeneratorParameterNames) {
				parameterNames = ((GeneratorParameterNames)annotation).value();
			} else if(annotation instanceof GeneratorParameterValues) {
				parameterValues = ((GeneratorParameterValues)annotation).value();
			}
		}

		if (parameterNames != null && parameterValues != null) {

			if (parameterNames.length != parameterValues.length) {
				RunnerException.report(Messages.PARAMETERS_ANNOTATION_LENGTH_ERROR);
			}

			for (int i = 0; i < parameterNames.length; i++) {
				result.put(parameterNames[i], parameterValues[i]);
			}
		} else if (parameterNames != null || parameterValues != null) {

			RunnerException.report(Messages.MISSING_PARAMETERS_ANNOTATION);
		}

		return result;
	}

	private Set<String> constraintsNames(Annotation[] annotations) {

		for (Annotation annotation : annotations) {
			if (annotation instanceof Constraints) {
				String[] constraints = ((Constraints)annotation).value();
				return new HashSet<String>(Arrays.asList(constraints));
			}
		}
		return null;
	}

}