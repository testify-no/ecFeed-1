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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;

public class ClassNodeTest extends ClassNode {
	public ClassNodeTest(){
		super("com.ecfeed.model.ClassNodeTest");
	}

	@Test
	public void getChildrenTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method1");
		MethodNode method2 = new MethodNode("method1");

		classNode.addMethod(method1);
		classNode.addMethod(method2);

		List<? extends AbstractNode> children = classNode.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(method1));
		assertTrue(children.contains(method2));
	}

	@Test
	public void getMethodTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");

		List<String> method1Types = new ArrayList<String>();
		method1Types.add("int");
		method1Types.add("double");

		List<String> method2Types = new ArrayList<String>();
		method2Types.add("int");
		method2Types.add("int");

		int inx = 0;
		for(String type : method1Types){
			method1.addParameter(new MethodParameterNode("parameter" +  inx++, type, "0",  false));
		}

		for(String type : method2Types){
			method2.addParameter(new MethodParameterNode("parameter" + inx++, type, "0",  false));
		}

		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertEquals(method1, classNode.getMethod("method", method1Types));
		assertEquals(method2, classNode.getMethod("method", method2Types));
	}

	@Test
	public void getMethodsTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");
		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertTrue(classNode.getMethods().contains(method1));
		assertTrue(classNode.getMethods().contains(method2));
	}

	@Test
	public void getTestSuitesTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName");
		MethodNode method1 = new MethodNode("method");
		MethodNode method2 = new MethodNode("method");

		method1.addTestCase(new TestCaseNode("suite 1", null));
		method1.addTestCase(new TestCaseNode("suite 2", null));
		method1.addTestCase(new TestCaseNode("suite 2", null));
		method1.addTestCase(new TestCaseNode("suite 3", null));

		method2.addTestCase(new TestCaseNode("suite 1", null));
		method2.addTestCase(new TestCaseNode("suite 4", null));
		method2.addTestCase(new TestCaseNode("suite 2", null));
		method2.addTestCase(new TestCaseNode("suite 3", null));

		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertEquals(4, classNode.getTestCaseNames().size());
		assertTrue(classNode.getTestCaseNames().contains("suite 1"));
		assertTrue(classNode.getTestCaseNames().contains("suite 2"));
		assertTrue(classNode.getTestCaseNames().contains("suite 3"));
		assertTrue(classNode.getTestCaseNames().contains("suite 4"));
		assertFalse(classNode.getTestCaseNames().contains("unused test suite"));
	}

	@Test
	public void compareTest(){
		ClassNode c1 = new ClassNode("c1");
		ClassNode c2 = new ClassNode("c2");

		assertFalse(c1.isMatch(c2));

		c2.setName("c1");
		assertTrue(c1.isMatch(c2));

		MethodNode m1 = new MethodNode("m1");
		MethodNode m2 = new MethodNode("m2");

		c1.addMethod(m1);
		assertFalse(c1.isMatch(c2));

		c2.addMethod(m2);
		assertFalse(c1.isMatch(c2));

		m2.setName("m1");
		assertTrue(c1.isMatch(c2));

		GlobalParameterNode parameter1 = new GlobalParameterNode("parameter1", "int");
		c1.addParameter(parameter1);
		assertFalse(c1.isMatch(c2));
		GlobalParameterNode parameter2 = new GlobalParameterNode("parameter1", "int");
		c2.addParameter(parameter2);
		assertTrue(c1.isMatch(c2));
		parameter1.setName("newName");
		assertFalse(c1.isMatch(c2));
		parameter2.setName("newName");
		assertTrue(c1.isMatch(c2));
		parameter1.setType("float");
		assertFalse(c1.isMatch(c2));
		parameter2.setType("float");
		assertTrue(c1.isMatch(c2));
	}
}
