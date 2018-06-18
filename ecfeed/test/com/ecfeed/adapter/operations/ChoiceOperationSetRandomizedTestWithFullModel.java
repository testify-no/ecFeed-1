package com.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.operations.ChoiceOperationSetRandomizedValue;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelTestHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;

public class ChoiceOperationSetRandomizedTestWithFullModel {

	@Test
	public void shouldSetAndResetRandomizedFlag() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='ChangeRandomized' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='1' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		ApplicationContext.setStandaloneApplication();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(0);
		ChoiceNode choiceNode = methodParameterNode.getChoices().get(0);

		ITypeAdapterProvider adapterProvider = new EclipseTypeAdapterProvider();

		ChoiceOperationSetRandomizedValue choiceOperationSetRandomized = 
				new ChoiceOperationSetRandomizedValue(choiceNode, true, adapterProvider);

		try {
			choiceOperationSetRandomized.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		assertTrue(choiceNode.isRandomizedValue());
		assertEquals("1:1", choiceNode.getValueString());


		IModelOperation reverseOperation = choiceOperationSetRandomized.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		assertFalse(choiceNode.isRandomizedValue());
		assertEquals("1", choiceNode.getValueString());

		IModelOperation reverseRreverseOperation = reverseOperation.getReverseOperation();

		try {
			reverseRreverseOperation.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		assertTrue(choiceNode.isRandomizedValue());
		assertEquals("1:1", choiceNode.getValueString());
	}

}
