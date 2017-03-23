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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.StringHelper;

public class CsvExportTemplateHolderTest {

	@Test
	public void ShouldNotThrowWhenEmpty() {

		String templateText = new String();

		CsvExportTemplateHolder csvExportTemplateHolder = new CsvExportTemplateHolder(createMethodNode());

		try {
			csvExportTemplateHolder.setTemplateText(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getHeaderTemplate()));
		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getTestCaseTemplate()));
		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getFooterTemplate()));
	}

	@Test
	public void ShouldParseForTwoParamsTemplate() {

		String templateText = 
				StringHelper.appendNewline(CsvExportTemplateHolder.HEADER_MARKER)
				+ StringHelper.appendNewline("$1.name,$2.name") 
				+ StringHelper.appendNewline(CsvExportTemplateHolder.TEST_CASE_MARKER) 
				+ StringHelper.appendNewline("$1.value,$2.value")
				+ StringHelper.appendNewline(CsvExportTemplateHolder.FOOTER_MARKER);

		CsvExportTemplateHolder csvExportTemplateHolder = 
				new CsvExportTemplateHolder(createMethodNode());

		try {
			csvExportTemplateHolder.setTemplateText(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertEquals("$1.name,$2.name", csvExportTemplateHolder.getHeaderTemplate());
		assertEquals("$1.value,$2.value", csvExportTemplateHolder.getTestCaseTemplate());
		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getFooterTemplate()));
	}

	@Test
	public void ShouldParseMultiLineSectionsTemplate() {

		String templateText = 
				StringHelper.appendNewline(CsvExportTemplateHolder.HEADER_MARKER)
				+ StringHelper.appendNewline("HEADER")
				+ StringHelper.appendNewline("$1.name,$2.name") 

				+ StringHelper.appendNewline(CsvExportTemplateHolder.TEST_CASE_MARKER)
				+ StringHelper.appendNewline("TEST CASE")
				+ StringHelper.appendNewline("$1.value,$2.value")

				+ StringHelper.appendNewline(CsvExportTemplateHolder.FOOTER_MARKER)
				+ StringHelper.appendNewline("FOOTER 1")
				+ StringHelper.appendNewline("FOOTER 2");

		CsvExportTemplateHolder csvExportTemplateHolder = 
				new CsvExportTemplateHolder(createMethodNode());

		try {
			csvExportTemplateHolder.setTemplateText(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		String header = csvExportTemplateHolder.getHeaderTemplate();
		String expectedHeader = "HEADER" + StringHelper.newLine() + "$1.name,$2.name";
		assertEquals(expectedHeader, header);

		String testCase = csvExportTemplateHolder.getTestCaseTemplate(); 
		String expectedTestCase = "TEST CASE" + StringHelper.newLine() + "$1.value,$2.value";

		assertEquals(expectedTestCase, testCase);
	}	

	@Test
	public void ShouldNotThrowWhenOnlyInvalidMarker() {

		String templateText = "[xxx]";

		CsvExportTemplateHolder csvExportTemplateHolder = 
				new CsvExportTemplateHolder(createMethodNode());

		try {
			csvExportTemplateHolder.setTemplateText(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getHeaderTemplate()));
		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getTestCaseTemplate()));
		assertTrue(StringHelper.isNullOrEmpty(csvExportTemplateHolder.getFooterTemplate()));
	}

	@Test
	public void ShouldIgnoreInvalidMarker() {

		String templateText = "[Xxx]" + "\n" + "$1.name\n"
				+ CsvExportTemplateHolder.TEST_CASE_MARKER + "\n" + "$1.value\n"
				+ CsvExportTemplateHolder.FOOTER_MARKER;

		CsvExportTemplateHolder csvExportTemplateHolder = 
				new CsvExportTemplateHolder(createMethodNode());

		try {
			csvExportTemplateHolder.setTemplateText(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertEquals("$1.value", csvExportTemplateHolder.getTestCaseTemplate());
	}

	private MethodNode createMethodNode() {
		return new MethodNode("methodName");
	}

}
