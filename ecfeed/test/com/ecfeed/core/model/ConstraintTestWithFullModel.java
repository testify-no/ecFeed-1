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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.generators.api.GeneratorException;
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
	public void shouldBeAmbiguousForStringsAndChoiceCondition() {

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
	public void shouldBeAmbiguousForStringsAndValueCondition() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choiceAorB' value='[A,B]' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ValueStatement rightValue='A' parameter='arg' relation='='/>\n");
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
	public void shouldBeAmbiguousForStringsAndParameterCondition() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg1' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='notRandom' value='N' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Parameter name='arg2' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Comments>\n");
		sb.append("                    <TypeComments/>\n");
		sb.append("                </Comments>\n");
		sb.append("                <Choice name='random' value='R' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ParameterStatement rightParameter='arg2' parameter='arg1' relation='='/>\n");
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
	public void shouldNotBeAmbiguousForNotRandomizedIntAndChoiceCondition() {

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

	@Test
	public void shouldBeAmbiguousForRandomizedIntAndChoiceCondition2() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints2' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice1' value='1' isRandomized='false'/>\n");
		sb.append("                <Choice name='choice2' value='1:2' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <Statement choice='choice1' parameter='arg1' relation='='/>\n");
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
	public void shouldNotBeAmbiguousForNotRandomizedIntAndValueCondition() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints2' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ValueStatement rightValue='0' parameter='arg' relation='='/>\n");
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

	@Test
	public void shouldBeAmbiguousForTheSecondRandomizedIntAndValueCondition() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints2' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice1' value='1' isRandomized='false'/>\n");
		sb.append("                <Choice name='choice2' value='1:2' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ValueStatement rightValue='1' parameter='arg1' relation='='/>\n");
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
	public void shouldNotBeAmbiguousForNotRandomizedIntAndParameterCondition() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints2' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ParameterStatement rightParameter='arg2' parameter='arg1' relation='='/>\n");
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

	@Test
	public void shouldBeAmbiguousForTwoParamsAndSecondRandomizedChoice() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraints' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice1' value='1' isRandomized='false'/>\n");
		sb.append("                <Choice name='choice2' value='1:2' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='1' isRandomized='false'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <ParameterStatement rightParameter='arg2' parameter='arg1' relation='='/>\n");
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

	private void testForIsAmbiguousConstraint(String model, boolean isTrueExpected) {

		RootNode rootNode = ModelTestHelper.createModel(model);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		Constraint constraint = (Constraint)methodNode.getAllConstraints().get(0);
		List<List<ChoiceNode>> testDomain = methodNode.getTestDomain();

		if (isTrueExpected) {
			assertTrue(constraint.isAmbiguous(testDomain, new MessageStack()));
		} else {
			assertFalse(constraint.isAmbiguous(testDomain, new MessageStack()));
		}
	}

	
	@Test
	public void constraintShouldEvaluateToTrueForRandomizedStrings() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraint' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='A' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <ValueStatement rightValue='A' parameter='arg' relation='='/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");
		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		List<List<ChoiceNode>> cartesianTestResults = generateResults(xml);		
		assertEquals(1, cartesianTestResults.size());
	}
	
	@Test
	public void constraintShouldEvaluateToFalseForRandomizedStrings() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraint' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='String' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='A' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <ValueStatement rightValue='B' parameter='arg' relation='='/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");
		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		List<List<ChoiceNode>> cartesianTestResults = generateResults(xml);
		assertEquals(0, cartesianTestResults.size());
	}	
	
	@Test
	public void constraintShouldEvaluateToTrueForRandomizedIntegers() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='Constraint' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("            <Parameter name='arg' type='int' isExpected='false' expected='0' linked='false'>\n");
		sb.append("                <Choice name='choice' value='0:1' isRandomized='true'/>\n");
		sb.append("            </Parameter>\n");
		sb.append("            <Constraint name='constraint'>\n");
		sb.append("                <Premise>\n");
		sb.append("                    <StaticStatement value='true'/>\n");
		sb.append("                </Premise>\n");
		sb.append("                <Consequence>\n");
		sb.append("                    <ValueStatement rightValue='0' parameter='arg' relation='='/>\n");
		sb.append("                </Consequence>\n");
		sb.append("            </Constraint>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");
		
		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		List<List<ChoiceNode>> cartesianTestResults = generateResults(xml);		
		assertEquals(1, cartesianTestResults.size());
	}

	private List<List<ChoiceNode>> generateResults(String xml) {
		
		RootNode rootNode = ModelTestHelper.createModel(xml);
		
		MethodNode methodNode = GeneratorHelper.getMethodByName(rootNode, "testMethod");
		
		AbstractAlgorithm<ChoiceNode> cartesianAlgorithm = 
				new CartesianProductAlgorithm<ChoiceNode>();
		
		List<List<ChoiceNode>> cartesianTestResults = new ArrayList<List<ChoiceNode>>();
		
		try {
			cartesianTestResults = 
					GeneratorHelper.generateTestCasesForMethod(methodNode, cartesianAlgorithm);

		} catch (GeneratorException e) {
			fail("Exception:" + e.getMessage());
		}
		
		return cartesianTestResults;
	}
}

