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

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.StringHelper;

public class GherkinExportTemplate extends AbstractExportTemplate {

	public static final String HEADER_MARKER = "[Header]";
	public static final String TEST_CASE_MARKER = "[TestCase]";
	public static final String FOOTER_MARKER = "[Footer]";

	public GherkinExportTemplate(MethodNode methodNode) {
		super(methodNode);
	}

	@Override
	public String createDefaultTemplateText() {

		String defaultTemplateText = 
				StringHelper.appendNewline(HEADER_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate())
				+ StringHelper.appendNewline(TEST_CASE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate())
				+ StringHelper.appendNewline(FOOTER_MARKER);

		setDefaultTemplateText(defaultTemplateText);

		return defaultTemplateText;
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}

	@Override 
	public String getTemplateFormat() {
		return "CSV";
	}

	private String createDefaultHeaderTemplate() {

		StringBuilder stringBuilder = new StringBuilder();

		MethodNode methodNode = getMethodNode();
		stringBuilder.append("Scenario: executing " + methodNode.getName() + "\n");
		stringBuilder.append(createInputParametersSection(methodNode));
		stringBuilder.append(createExecuteSection(methodNode));
		stringBuilder.append(createExpectedParametersSection(methodNode));
		stringBuilder.append("\n");
		stringBuilder.append(createExamplesSection(methodNode));

		return StringHelper.removeNewlineAtEnd(stringBuilder.toString());
	}

	private static String createInputParametersSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();

		int methodParametersCount = methodNode.getParametersCount();

		int counter = 0;
		for (int parameterIndex = 0; 
				parameterIndex < methodParametersCount; 
				++parameterIndex) {

			MethodParameterNode methodParameterNode = 
					(MethodParameterNode) methodNode.getParameter(parameterIndex);

			if (methodParameterNode.isExpected()) {
				continue;
			}

			String parameterName = methodNode.getParameter(parameterIndex).getName();
			String line = getInputParameterPrefix(counter) + parameterName + " is <" + parameterName + ">" + "\n";
			stringBuilder.append(line);

			counter++;
		}

		return stringBuilder.toString();
	}

	private static String getInputParameterPrefix(int counter) {
		if (counter == 0) {
			return "\tGiven the value of ";
		}
		return "\tAnd the value of ";
	}

	private static String createExpectedParametersSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();

		int methodParametersCount = methodNode.getParametersCount();

		int counter = 0;

		for (int parameterIndex = 0; parameterIndex < methodParametersCount; ++parameterIndex) {

			MethodParameterNode methodParameterNode = 
					(MethodParameterNode) methodNode.getParameter(parameterIndex);

			if (!methodParameterNode.isExpected()) {
				continue;
			}

			String parameterName = methodNode.getParameter(parameterIndex).getName();
			String line = getExpectedParameterPrefix(counter) + parameterName + " is <" + parameterName + ">" + "\n";
			stringBuilder.append(line);
			counter++;
		}

		return stringBuilder.toString();
	}

	private static String getExpectedParameterPrefix(int counter) {
		if (counter == 0) {
			return "\tThen the value of ";
		}
		return "\tAnd the value of ";
	}

	private static String createExamplesSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Examples:\n");
		stringBuilder.append("| ");


		int methodParametersCount = methodNode.getParametersCount();

		for (int parameterIndex = 0; parameterIndex < methodParametersCount; ++parameterIndex) {

			String parameterName = methodNode.getParameter(parameterIndex).getName();
			stringBuilder.append(parameterName + " | ");
		}

		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	private static String createExecuteSection(MethodNode methodNode) {
		return "\tWhen " + methodNode.getName() + " is executed\n";
	}

	private String createDefaultTestCaseTemplate() {

		MethodNode methodNode = getMethodNode();

		int methodParametersCount = methodNode.getParametersCount();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("| ");

		for (int index = 0; index < methodParametersCount; ++index) {

			String parameterName = methodNode.getParameter(index).getName(); 
			String paramDescription = "$" + parameterName + "." + "value";
			stringBuilder.append(paramDescription);
			stringBuilder.append(" | ");
		}

		return stringBuilder.toString();
	}

}
