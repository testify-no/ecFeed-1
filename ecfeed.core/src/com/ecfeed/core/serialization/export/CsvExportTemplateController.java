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

import com.ecfeed.core.utils.StringHelper;

public class CsvExportTemplateController extends BasicExportTemplateController {

	public static final String HEADER_MARKER = "[Header]";
	public static final String TEST_CASE_MARKER = "[TestCase]";
	public static final String FOOTER_MARKER = "[Footer]";

	public CsvExportTemplateController() {
	}

	@Override
	public String createDefaultTemplate(int methodParametersCount) {

		return StringHelper.appendNewline(HEADER_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate(methodParametersCount))
				+ StringHelper.appendNewline(TEST_CASE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate(methodParametersCount))
				+ StringHelper.appendNewline(FOOTER_MARKER);
	}

	private static String createDefaultHeaderTemplate(int methodParametersCount) {

		final String NAME_TAG = "name";
		return createParameterTemplate(NAME_TAG, methodParametersCount);
	}

	private static String createDefaultTestCaseTemplate(int methodParametersCount) {

		final String VALUE_TAG = "value";
		return createParameterTemplate(VALUE_TAG, methodParametersCount);
	}

	private static String createParameterTemplate( String parameterTag, int methodParametersCount) {

		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "$" + cnt + "." + parameterTag;
			template = template + paramDescription;
		}

		return template;
	}

}
