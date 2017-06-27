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
import com.ecfeed.core.utils.StringHelper;

public class XmlExportTemplate extends AbstractExportTemplate {

	public static final String HEADER_MARKER = "[Header]";
	public static final String TEST_CASE_MARKER = "[TestCase]";
	public static final String FOOTER_MARKER = "[Footer]";

	public XmlExportTemplate(MethodNode methodNode) {
		super(methodNode);
	}

	@Override
	public String createDefaultTemplateText() {

		int methodParametersCount = getMethodNode().getParametersCount();

		String defaultTemplateText =
				StringHelper.appendNewline(HEADER_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate())
				+ StringHelper.appendNewline(TEST_CASE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate(methodParametersCount))
				+ StringHelper.appendNewline(FOOTER_MARKER)
				+ StringHelper.appendNewline(createDefaultFooterTemplate());

		setDefaultTemplateText(defaultTemplateText);

		return defaultTemplateText;		
	}

	@Override
	public String getFileExtension() {
		return "xml";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {
		final String FORMAT_XML = "XML";
		return FORMAT_XML;
	}	
	private static String createDefaultHeaderTemplate() {
		return "<TestCases>";
	}

	private static String createDefaultFooterTemplate() {
		return "</TestCases>";
	}

	private static String createDefaultTestCaseTemplate(int methodParametersCount) {

		StringBuilder template = new StringBuilder();
		template.append("\t<TestCase ");
		template.append("testSuite=\"%suite\" ");
		template.append(createParametersTemplate(methodParametersCount));
		template.append("/>");

		return template.toString();
	}

	private static String createParametersTemplate(int methodParametersCount) {

		StringBuilder template = new StringBuilder();

		for (int paramIndex = 1; paramIndex <= methodParametersCount; ++paramIndex) {
			template.append(createParameterString(paramIndex));
		}

		return template.toString();
	}

	private static String createParameterString(int cnt) {
		return "arg" + cnt + "=" + "\"$" + cnt + "." + "value" + "\" ";
	}

}
