/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.operations.AbstractParameterOperationSetType;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelTestHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;

public class AbstractParameterOperationSetTypeTestWithFullModel{


	@Test
	public void shouldPreserveChoiceWhenChangingToUserType() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='SetUserType' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		ApplicationContext.setApplicationTypeLocalStandalone();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(0);

		ITypeAdapterProvider adapterProvider = new EclipseTypeAdapterProvider();

		AbstractParameterOperationSetType abstractParameterOperationSetType = 
				new AbstractParameterOperationSetType(
						methodParameterNode, "com.test.MyEnum", adapterProvider);
		try {
			abstractParameterOperationSetType.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		List<ChoiceNode> choices = methodParameterNode.getChoices();
		assertEquals(1, choices.size());
	}

	@Test
	public void shouldNotDeleteAbstractChoice() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='SetUserType' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='abstract' value='0' isRandomized='false'>\n");
		sb.append("                    <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("                </Choice>\n");
		sb.append("            </Parameter>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		ApplicationContext.setApplicationTypeLocalStandalone();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(0);

		ITypeAdapterProvider adapterProvider = new EclipseTypeAdapterProvider();

		AbstractParameterOperationSetType abstractParameterOperationSetType = 
				new AbstractParameterOperationSetType(
						methodParameterNode, "com.test.MyEnum", adapterProvider);
		try {
			abstractParameterOperationSetType.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		ChoiceNode abstractchoiceNode = methodParameterNode.getChoices().get(0);
		assertTrue(abstractchoiceNode.isAbstract());
		assertEquals("abstract", abstractchoiceNode.getName());

		ChoiceNode childChoiceNode = abstractchoiceNode.getChoices().get(0);
		assertFalse(childChoiceNode.isAbstract());
		assertEquals("choice", childChoiceNode.getName());
	}

}

