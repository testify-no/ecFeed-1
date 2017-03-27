/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ecfeed.core.adapter.java.ChoiceValueParser;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;

public class TestCasesExportHelper {

	private static final String CLASS_NAME_SEQUENCE = "%class";
	private static final String PACKAGE_NAME_SEQUENCE = "%package";
	private static final String METHOD_NAME_SEQUENCE = "%method";
	private static final String TEST_SUITE_NAME_SEQUENCE = "%suite";
	private static final String TEST_CASE_INDEX_NAME_SEQUENCE = "%index";
	private static final String PARAMETER_COMMAND_NAME = "name";
	private static final String CHOICE_COMMAND_SHORT_NAME = "choice";
	private static final String CHOICE_COMMAND_FULL_NAME = "full_choice";
	private static final String CHOICE_COMMAND_VALUE = "value";
	private static final String TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN = "\\$\\w+\\.(" + CHOICE_COMMAND_SHORT_NAME + "|" + CHOICE_COMMAND_FULL_NAME + "|" + CHOICE_COMMAND_VALUE + ")";
	private static final String METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN = "\\$\\w+\\." + PARAMETER_COMMAND_NAME;
	private static final String ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN = "\\$\\(.*\\)";

	public static String generateSection(MethodNode method, String template) {

		if (template == null) {
			return new String();
		}

		String result = template.replace(CLASS_NAME_SEQUENCE, ClassNodeHelper.getLocalName(method.getClassNode()));
		result = result.replace(PACKAGE_NAME_SEQUENCE, ClassNodeHelper.getPackageName(method.getClassNode()));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());
		result = replaceParameterNameSequences(method, result);
		result = evaluateExpressions(result);

		return result;
	}

	public static String generateTestCaseString(int sequenceIndex, TestCaseNode testCase, String template) {

		MethodNode method = testCase.getMethod();

		String result = generateSection(method, template);
		result = replaceParameterSequences(testCase, result);
		result = result.replace(TEST_CASE_INDEX_NAME_SEQUENCE, String.valueOf(sequenceIndex));
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, testCase.getName());
		result = evaluateExpressions(result);

		return result;
	}	

	private static String replaceParameterNameSequences(MethodNode methodNode, String template) {

		String result = template;
		Matcher matcher = Pattern.compile(METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN).matcher(template);

		while(matcher.find()){

			String parameterCommandSequence = matcher.group();
			String parameterSubstitute = getParameterSubstitute(parameterCommandSequence, methodNode);

			result = result.replace(parameterCommandSequence, parameterSubstitute);
		}		

		return result;
	}

	private static String getParameterSubstitute(String parameterCommandSequence, MethodNode methodNode) {

		String command = getParameterCommand(parameterCommandSequence);
		int parameterNumber = getParameterNumber(parameterCommandSequence, methodNode) - 1;

		if (parameterNumber == -1) {
			return null;
		}

		MethodParameterNode parameter = methodNode.getMethodParameters().get(parameterNumber);
		String substitute = resolveParameterCommand(command, parameter);

		return substitute;
	}

	private static String resolveParameterCommand(String command, MethodParameterNode parameter) {
		String result = command;
		switch(command){
		case PARAMETER_COMMAND_NAME:
			result = parameter.getName();
			break;
		default:
			break;
		}
		return result;
	}

	private static String getParameterCommand(String parameterCommandSequence) {
		return parameterCommandSequence.substring(parameterCommandSequence.indexOf(".") + 1, parameterCommandSequence.length());
	}

	private static int getParameterNumber(String parameterSequence, MethodNode methodNode) {

		String parameterDescriptionString = parameterSequence.substring(1, parameterSequence.indexOf("."));

		try {
			return Integer.parseInt(parameterDescriptionString);
		} catch(NumberFormatException e) {
			return methodNode.getParameterIndex(parameterDescriptionString) + 1;
		}
	}

	private static String evaluateExpressions(String template) {
		String result = template;
		Matcher m = Pattern.compile(ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			String expressionSequence = m.group();
			String expressionString = expressionSequence.substring(2, expressionSequence.length() - 1); //remove initial "$(" and ending ")"
			try{
				Expression expression = new Expression(expressionString);
				String substitute = expression.eval().toPlainString();
				result = result.replace(expressionSequence, substitute);
			}catch(RuntimeException e){} //if evaluation failed, do not stop, keep the result as it is
		}		
		return result;
	}

	private static String replaceParameterSequences(TestCaseNode testCase, String template) {

		String result = replaceParameterNameSequences(testCase.getMethod(), template);

		Matcher matcher = Pattern.compile(TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN).matcher(template);

		while(matcher.find()){

			String parameterCommandSequence = matcher.group();

			String valueSubstitute = createValueSubstitute(parameterCommandSequence, testCase);
			if (valueSubstitute != null) {
				result = result.replace(parameterCommandSequence, valueSubstitute);
			}
		}

		return result;
	}

	private static String createValueSubstitute(String parameterCommandSequence, TestCaseNode testCase) {

		int parameterNumber = getParameterNumber(parameterCommandSequence, testCase.getMethod()) - 1;

		if (parameterNumber == -1) {
			return null;
		}
		if (parameterNumber >= testCase.getTestData().size()) {
			return null;
		}

		ChoiceNode choice = testCase.getTestData().get(parameterNumber);

		String command = getParameterCommand(parameterCommandSequence);
		String substitute = resolveChoiceCommand(command, choice);

		return substitute;
	}

	private static String resolveChoiceCommand(String command, ChoiceNode choice) {
		String result = command;
		switch(command){
		case CHOICE_COMMAND_SHORT_NAME:
			result = choice.getName();
			break;
		case CHOICE_COMMAND_FULL_NAME:
			result = choice.getQualifiedName();
			break;
		case CHOICE_COMMAND_VALUE:
			result = getValue(choice);
			break;
		default:
			break;
		}
		return result;
	}

	private static String getValue(ChoiceNode choice) {
		String convertedValue = convertValue(choice);
		if (convertedValue != null) {
			return convertedValue;
		}
		return choice.getValueString();
	}

	private static String convertValue(ChoiceNode choice) {
		AbstractParameterNode parameter = choice.getParameter();
		if (parameter == null) {
			return null;
		}

		String argType = choice.getParameter().getType();
		if (argType == null) {
			return null;
		}

		ChoiceValueParser choiceValueParser = new ChoiceValueParser(null, true);
		Object parsedObject = choiceValueParser.parseValue(choice.getValueString(), argType);
		if (parsedObject == null) {
			return null;
		}

		return parsedObject.toString();
	}

}