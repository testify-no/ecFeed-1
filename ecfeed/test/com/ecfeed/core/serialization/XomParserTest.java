/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import static com.ecfeed.testutils.Constants.SUPPORTED_TYPES;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodeProperty;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.ect.Constants;
import com.ecfeed.core.serialization.ect.XomAnalyser;
import com.ecfeed.core.serialization.ect.XomAnalyserFactory;
import com.ecfeed.core.serialization.ect.XomBuilder;
import com.ecfeed.core.serialization.ect.XomBuilderFactory;
import com.ecfeed.testutils.ModelStringifier;
import com.ecfeed.testutils.RandomModelGenerator;

public class XomParserTest {

	private final boolean DEBUG = false;

	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	ModelStringifier fStringifier = new ModelStringifier();
	Random fRandom = new Random();

	@Test
	public void parseRootTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseRootTest(version);
		}
	}

	private void parseRootTest(int version) {
		try {
			RootNode rootNode = fModelGenerator.generateModel(3);
			addCommonProperties(version, rootNode);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
			Element rootElement = (Element)rootNode.accept(builder);
			TRACE(rootElement);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			RootNode parsedRootNode = analyser.parseRoot(rootElement);
			assertElementsEqual(rootNode, parsedRootNode);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseClassTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseClassTest(version);
		}
	}

	private void parseClassTest(int version){
		try {
			ClassNode classNode = fModelGenerator.generateClass(3);
			addCommonProperties(version, classNode);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
			Element element = (Element)classNode.accept(builder);
			TRACE(element);
			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			ClassNode parsedClass = analyser.parseClass(element, null);
			assertElementsEqual(classNode, parsedClass);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}


	@Test
	public void parseMethodTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseMethodTest(version);
		}
	}

	private void parseMethodTest(int version){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode methodNode = fModelGenerator.generateMethod(5, 5, 5);
				addCommonProperties(version, methodNode);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
				Element element = (Element)methodNode.accept(builder);
				TRACE(element);

				XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
				MethodNode parsedMethodNode = analyser.parseMethod(element, null);
				assertElementsEqual(methodNode, parsedMethodNode);
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseParameterTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseParameterTest(version);
		}
	}

	private void parseParameterTest(int version){
		for(String type : SUPPORTED_TYPES){
			try{
				for(boolean expected : new Boolean[]{true, false}){
					MethodNode methodNode = new MethodNode("method");
					MethodParameterNode methodParameterNode = fModelGenerator.generateParameter(type, expected, 3, 3, 3);
					addCommonProperties(version, methodParameterNode);
					methodNode.addParameter(methodParameterNode);

					XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
					Element element = (Element)methodParameterNode.accept(builder);
					TRACE(element);

					XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
					MethodParameterNode parsedMethodParameterNode = analyser.parseMethodParameter(element, methodNode);
					assertElementsEqual(methodParameterNode, parsedMethodParameterNode);
				}
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseTestCaseTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseTestCaseTest(version);
		}
	}

	private void parseTestCaseTest(int version){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			for(int j = 0; j < 100; j++){
				try {
					TestCaseNode testCaseNode = fModelGenerator.generateTestCase(m);
					XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
					Element element = (Element)testCaseNode.accept(builder);
					TRACE(element);

					XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
					TestCaseNode tc1 = analyser.parseTestCase(element, m);
					assertElementsEqual(testCaseNode, tc1);
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage());
				}
			}
		}
	}

	@Test
	public void parseConstraintTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseConstraintTest(version);
		}
	}

	private void parseConstraintTest(int version) {
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(3, 0, 0);
			for(int j = 0; j < 10; j++){
				try {
					ConstraintNode c = fModelGenerator.generateConstraint(m);

					XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
					Element element = (Element)c.accept(builder);
					TRACE(element);

					XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
					ConstraintNode c1 = analyser.parseConstraint(element, m);
					assertElementsEqual(c, c1);
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage() + "\nMethod\n" + new ModelStringifier().stringify(m, 0));
				}
			}
		}
	}


	@Test
	public void parseChoiceTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseChoiceTest(version);
		}
	}

	private void parseChoiceTest(int version){
		for(String type: SUPPORTED_TYPES){
			try {
				ChoiceNode p = fModelGenerator.generateChoice(3, 3, 3, type);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
				Element element = (Element)p.accept(builder);
				TRACE(element);

				XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
				ChoiceNode p1 = analyser.parseChoice(element);
				assertElementsEqual(p, p1);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}


	@Test
	public void parseStaticStatementTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseStaticStatementTest(version);
		}
	}

	private void parseStaticStatementTest(int version) {
		StaticStatement trueStatement = new StaticStatement(true);
		StaticStatement falseStatement = new StaticStatement(false);
		try{

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version);

			Element trueElement = (Element)trueStatement.accept(builder);
			Element falseElement = (Element)falseStatement.accept(builder);
			TRACE(trueElement);
			TRACE(falseElement);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			StaticStatement parsedTrue = analyser.parseStaticStatement(trueElement);
			StaticStatement parsedFalse = analyser.parseStaticStatement(falseElement);

			assertStatementsEqual(trueStatement, parsedTrue);
			assertStatementsEqual(falseStatement, parsedFalse);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}


	@Test
	public void parseChoiceStatementTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseChoiceStatementTest(version);
		}
	}

	private void parseChoiceStatementTest(int version){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
				ChoicesParentStatement s = fModelGenerator.generateChoicesParentStatement(m);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
				Element element = (Element)s.accept(builder);
				TRACE(element);
				XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);

				ChoicesParentStatement parsedS = null;
				switch(element.getLocalName()){
				case Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
					parsedS = analyser.parseLabelStatement(element, m);
					break;
				case Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
					parsedS = analyser.parseChoiceStatement(element, m);
					break;
				}

				assertStatementsEqual(s, parsedS);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseExpectedValueStatementTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseExpectedValueStatementTest(version);
		}
	}	

	private void parseExpectedValueStatementTest(int version){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
				ExpectedValueStatement s = fModelGenerator.generateExpectedValueStatement(m);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
				Element element = (Element)s.accept(builder);
				TRACE(element);

				XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
				ExpectedValueStatement parsedS = analyser.parseExpectedValueStatement(element, m);
				assertStatementsEqual(s, parsedS);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseStatementArrayTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			parseStatementArrayTest(version);
		}
	}	

	private void parseStatementArrayTest(int version) {
		try{
			MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
			StatementArray s = fModelGenerator.generateStatementArray(m, 4);
			XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
			Element element = (Element)s.accept(builder);
			TRACE(element);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			StatementArray parsedS = analyser.parseStatementArray(element, m);
			assertStatementsEqual(s, parsedS);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}


	@Test
	public void assertTypeTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentVersion(); version++) {
			assertTypeTest(version);
		}
	}

	private void assertTypeTest(int version){
		try{
			RootNode root = fModelGenerator.generateModel(3);
			ClassNode _class = fModelGenerator.generateClass(3);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version);
			Element rootElement = (Element)root.accept(builder);
			Element classElement = (Element)_class.accept(builder);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			analyser.parseRoot(rootElement);

			try {
				analyser.parseClass(classElement, null);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}

			try {
				analyser.parseClass(rootElement, null);
				fail("exception expected");
			} catch (Exception e) {
			}

			try {
				analyser.parseRoot(classElement);
				fail("exception expected");
			} catch (Exception e) {
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	private void assertStatementsEqual(AbstractStatement s1, AbstractStatement s2) {
		if(s1.compare(s2) == false){
			fail("Parsed statement\n" + fStringifier.stringify(s1, 0) + "\ndiffers from original\n" + fStringifier.stringify(s2, 0));
		}

	}

	private void assertElementsEqual(AbstractNode n, AbstractNode n1) {
		if(n.compare(n1) == false){
			fail("Parsed element differs from original\n" + fStringifier.stringify(n, 0) + "\n" + fStringifier.stringify(n1, 0));
		}
	}

	private void addCommonProperties(int version, AbstractNode targetNode) {
		if (!ModelVersionDistributor.nodesHaveCommonProperties(version)) {
			return;
		}

		int maxProperties = fRandom.nextInt(3);

		for (int propertyNum = 0; propertyNum < maxProperties; propertyNum++) {
			NodeProperty nodeProperty = new NodeProperty("String", "value" + propertyNum);
			targetNode.putProperty("key" + propertyNum, nodeProperty);			
		}
	}

	private void TRACE(Element element){
		if (!DEBUG) {
			return;
		}

		Document document = new Document(element);
		OutputStream ostream = new ByteArrayOutputStream();
		Serializer serializer = new Serializer(ostream);
		// Uncomment for pretty formatting. This however will affect
		// whitespaces in the document's ... infoset
		serializer.setIndent(4);
		try {
			serializer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(ostream);
	}

}
