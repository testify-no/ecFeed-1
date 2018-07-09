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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;


public abstract class AbstractExportTemplate implements IExportTemplate {

	private static final String HEADER_MARKER = "[Header]";
	private static final String TEST_CASE_MARKER = "[TestCase]";
	private static final String FOOTER_MARKER = "[Footer]";

	private StringHolder fHeaderTemplate = new StringHolder();
	private StringHolder fTestCaseTemplate = new StringHolder();
	private StringHolder fFooterTemplate = new StringHolder();

	private String fDefaultTemplateText;
	private String fTemplateText;

	private MethodNode fMethodNode;

	public AbstractExportTemplate(MethodNode methodNode) {

		fMethodNode = methodNode;
	}

	@Override
	public void initialize() {

		String defaultTemplateText = createDefaultTemplateText();
		setTemplateText(defaultTemplateText);
	}

	@Override
	public void setTemplateText(String templateText) {

		if (templateText == null) {
			ExceptionHelper.reportRuntimeException("Template text must not be empty.");
		}

		fHeaderTemplate.reset();
		fTestCaseTemplate.reset();
		fFooterTemplate.reset();

		fTemplateText = templateText;
		divideIntoSubtemplates(templateText);
	}

	@Override
	public String getTemplateText() {

		return fTemplateText;
	}

	@Override
	public String getHeaderTemplate() {

		return fHeaderTemplate.get();
	}

	@Override
	public String getTestCaseTemplate() {

		return fTestCaseTemplate.get();
	}

	@Override
	public String getFooterTemplate() {

		return fFooterTemplate.get();
	}

	@Override
	public boolean isTemplateTextModified() {

		if (StringHelper.isEqual(fTemplateText, fDefaultTemplateText)) {
			return false;
		}

		return true;
	}

	@Override
	public String createPreview(Collection<TestCaseNode> selectedTestCases) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(TestCasesExportHelper.generateSection(fMethodNode, fHeaderTemplate.get()));
		stringBuilder.append("\n");

		appendPreviewOfTestCases(selectedTestCases, stringBuilder);

		stringBuilder.append(TestCasesExportHelper.generateSection(fMethodNode, fFooterTemplate.get()));
		stringBuilder.append("\n");

		String result = stringBuilder.toString();
		result = TestCasesExportHelper.evaluateMinWidthOperators(result);

		return result;
	}

	private void appendPreviewOfTestCases(
			Collection<TestCaseNode> selectedTestCases, StringBuilder inOutStringBuilder) {

		List<TestCaseNode> testCases = createPreviewTestCasesSample(selectedTestCases);
		int sequenceIndex = 0;

		for (TestCaseNode testCase : testCases) {

			inOutStringBuilder.append(
					TestCasesExportHelper.generateTestCaseString(
							sequenceIndex++, testCase, fTestCaseTemplate.get()));

			inOutStringBuilder.append("\n");
		}

	}

	private List<TestCaseNode> createPreviewTestCasesSample(Collection<TestCaseNode> selectedTestCases) {

		final int MAX_PREVIEW_TEST_CASES = 5;

		if (selectedTestCases == null) {
			return createRandomTestCasesSample(MAX_PREVIEW_TEST_CASES);
		}

		return createSampleFromSelectedTestCases(
				selectedTestCases, MAX_PREVIEW_TEST_CASES);
	}

	private List<TestCaseNode> createSampleFromSelectedTestCases(
			Collection<TestCaseNode> selectedTestCases,
			final int maxPreviewTestCases) {

		List<TestCaseNode> testCases = new ArrayList<TestCaseNode>();

		int cnt = Math.min(maxPreviewTestCases, selectedTestCases.size());
		List<TestCaseNode> selectedTestCasesList = new ArrayList<TestCaseNode>(selectedTestCases);

		for (int index = 0; index < cnt; index++) {
			testCases.add(selectedTestCasesList.get(index));
		}

		return testCases;
	}

	private List<TestCaseNode> createRandomTestCasesSample(
			final int maxPreviewTestCases) {

		List<TestCaseNode> testCases = new ArrayList<TestCaseNode>();

		for (int index = 0; index < maxPreviewTestCases; index++) {
			testCases.add(createRandomTestCaseNode(fMethodNode, index));
		}

		return testCases;
	}

	private TestCaseNode createRandomTestCaseNode(MethodNode methodNode, int testCaseNumber) {

		Random randomGenerator = new Random();
		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();
		List<String> parameterNames = methodNode.getParametersNames();

		for (String parameterName : parameterNames) {
			choiceNodes.add(getRandomChoiceNode(methodNode, parameterName, randomGenerator));
		}

		TestCaseNode testCaseNode = 
				new TestCaseNode(CommonConstants.DEFAULT_NEW_TEST_SUITE_NAME, choiceNodes);

		testCaseNode.setParent(methodNode);

		return testCaseNode;
	}

	ChoiceNode getRandomChoiceNode(MethodNode methodNode, String parameterName, Random randomGenerator) {

		MethodParameterNode methodParameterNode = (MethodParameterNode)methodNode.getParameter(parameterName);
		List<ChoiceNode> choices = methodParameterNode.getChoices();

		ChoiceNode choiceNode = choices.get(randomGenerator.nextInt(choices.size()));
		return choiceNode;
	}

	protected void setDefaultTemplateText(String defaultTemplateText) {
		fDefaultTemplateText = defaultTemplateText;
	}

	protected MethodNode getMethodNode() {
		return fMethodNode;
	}

	protected void divideIntoSubtemplates(String templateText) {

		StringHolder currentSectionMarker = new StringHolder();

		String[] lines = templateText.split("\n");

		for (String line : lines) {

			if (isCommentLine(line)) {
				continue;
			}

			if (setSectionMarker(line, currentSectionMarker)) {
				continue;
			}

			if (currentSectionMarker.isNull()) {
				continue;
			}

			updateTemplatePart(currentSectionMarker.get(), line);
		}
	}

	private void updateTemplatePart(String marker, String line) {

		StringHolder templatePart = getCurrentTemplatePart(marker);

		if (StringHelper.isNullOrEmpty(templatePart.get())) {
			templatePart.set(line);
			return;
		}

		templatePart.append(StringHelper.newLine() + line);
	}

	private StringHolder getCurrentTemplatePart(String marker) {

		if (marker.equals(HEADER_MARKER)) {
			return fHeaderTemplate;
		}
		if (marker.equals(TEST_CASE_MARKER)) {
			return fTestCaseTemplate;
		}
		if (marker.equals(FOOTER_MARKER)) {
			return fFooterTemplate;
		}
		return null;
	}

	private static boolean isCommentLine(String line) {

		final String COMMENTED_LINE_REGEX = "^\\s*#.*";

		if (line.matches(COMMENTED_LINE_REGEX)) {
			return true;
		}
		return false;
	}

	private static boolean setSectionMarker(String line, StringHolder currentMarker) {

		if (!isSectionMarker(line)) {
			return false;
		}

		currentMarker.set(getMarker(line));

		return true;
	}

	private static boolean isSectionMarker(String line) {

		String trimmedLine = line.trim();

		if (trimmedLine.equals(HEADER_MARKER)) {
			return true;
		}

		if (trimmedLine.equals(TEST_CASE_MARKER)) {
			return true;
		}

		if (trimmedLine.equals(FOOTER_MARKER)) {
			return true;
		}

		return false;
	}

	private static String getMarker(String line) {

		int sectionTitleStart = line.indexOf('[');
		int sectionTitleStop = line.indexOf(']') + 1;

		return line.substring(sectionTitleStart, sectionTitleStop);
	}

	@Override
	public void setFooterTemplate(String template) {

		fFooterTemplate.set(template);
	}

	@Override
	public void setHeaderTemplate(String template) {

		fHeaderTemplate.set(template);
	}

	@Override
	public void setTestCaseTemplate(String template) {

		fTestCaseTemplate.set(template);
	}
}
