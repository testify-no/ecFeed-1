package com.ecfeed.core.serialization.export;

/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.serialization.export.TestCasesExporter;

public class TestCasesExporterTest {

	@Test
	public void shouldExportAllSections() {
		IExportTemplateController exportTemplateController = 
				new CsvExportTemplateController(createMethodNode());

		exportTemplateController.setHeaderTemplate("$1.name, $2.name");
		exportTemplateController.setTestCaseTemplate("$1.value, $2.value");
		exportTemplateController.setFooterTemplate("end");

		String expectedResult = "par0, par1" + StringHelper.newLine() + "0, 1"
				+ StringHelper.newLine() + "end" + StringHelper.newLine();

		performTwoParamsTest(exportTemplateController, expectedResult);
	}

	public void shouldExportHeaderOnly() {
		IExportTemplateController exportTemplateController = 
				new CsvExportTemplateController(createMethodNode());

		exportTemplateController.setHeaderTemplate("$1.name, $2.name");

		String expectedResult = "par0, par1";

		performTwoParamsTest(exportTemplateController, expectedResult);
	}

	public void shouldExportTestCasesOnly() {
		IExportTemplateController exportTemplateController = 
				new CsvExportTemplateController(createMethodNode());

		exportTemplateController.setTestCaseTemplate("$1.value, $2.value");

		String expectedResult = "0, 1";
		performTwoParamsTest(exportTemplateController, expectedResult);
	}

	public void shouldExportFooterOnly() {
		IExportTemplateController exportTemplateController = 
				new CsvExportTemplateController(createMethodNode());

		exportTemplateController.setFooterTemplate("end");

		String expectedResult = "end";

		performTwoParamsTest(exportTemplateController, expectedResult);
	}

	private void performTwoParamsTest(
			IExportTemplateController exportTemplateController,
			String expectedResult) {
		ClassNode theClass = new ClassNode("Test");

		MethodNode method = new MethodNode("testMethod");
		theClass.addMethod(method);

		MethodParameterNode parameter0 = new MethodParameterNode("par0", "int",
				"0", false);
		ChoiceNode choiceNode00 = new ChoiceNode("value00", "0");
		parameter0.addChoice(choiceNode00);
		method.addParameter(parameter0);

		MethodParameterNode parameter1 = new MethodParameterNode("par1", "int",
				"0", false);
		ChoiceNode choiceNode11 = new ChoiceNode("value11", "1");
		parameter1.addChoice(choiceNode11);
		method.addParameter(parameter1);

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(choiceNode00);
		choices.add(choiceNode11);

		TestCaseNode testCase = new TestCaseNode("default", choices);
		method.addTestCase(testCase);

		Collection<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
		testCases.add(testCase);

		TestCasesExporter exporter = new TestCasesExporter(exportTemplateController);

		OutputStream stream = new ByteArrayOutputStream();

		try {
			exporter.runExportWithProgress(method, testCases, stream, false);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		String result = stream.toString();

		assertEquals(expectedResult, result);
	}

	private MethodNode createMethodNode() {
		return new MethodNode("methodName");
	}


}
