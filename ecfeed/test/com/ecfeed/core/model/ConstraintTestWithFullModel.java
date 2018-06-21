/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.MessageStack;

public class ConstraintTestWithFullModel {

	@Test
	public void testIsAmbiguousForRanges() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='RandomizedValues' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='1:3' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ValueStatement rightValue='2' parameter='arg' relation='='/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		testForIsAmbiguousConstraint(xml, true);
	}


	@Test
	public void testIsAmbiguousForStrings() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints01' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choiceAorB' value='[A,B]' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='equalsA'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <Statement choice='choiceAorB' parameter='arg' relation='='/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		testForIsAmbiguousConstraint(xml, true);
	}	

	@Test
	public void shouldNotBeAmbiguousForNotRandomizedInt() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints02' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <Statement choice='choice' parameter='arg' relation='='/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		testForIsAmbiguousConstraint(xml, false);
	}	


	private void testForIsAmbiguousConstraint(String model, boolean isTrueExpected) {

		RootNode rootNode = ModelTestHelper.createModel(model);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		Constraint constraint = (Constraint)methodNode.getAllConstraints().get(0);
		AbstractParameterNode abstractParameterNode = methodNode.getParameter(0);

		List<ChoiceNode> choices = abstractParameterNode.getChoices();
		List<List<ChoiceNode>> values = new ArrayList<List<ChoiceNode>>();
		values.add(choices);

		if (isTrueExpected) {
			assertTrue(constraint.isAmbiguous(values, new MessageStack()));
		} else {
			assertFalse(constraint.isAmbiguous(values, new MessageStack()));
		}
	}	

}

