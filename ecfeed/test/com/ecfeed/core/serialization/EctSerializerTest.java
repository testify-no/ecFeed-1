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

import static com.ecfeed.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelConverter;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.testutils.RandomModelGenerator;

public class EctSerializerTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	@Test
	public void modelSerializerTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			modelSerializerTest(version);
		}
	}

	private void modelSerializerTest(int version) { 
		RootNode model = new RootNode("model", version);

		model.addClass(new ClassNode("com.example.TestClass1"));
		model.addClass(new ClassNode("com.example.TestClass2"));
		model.addParameter(new GlobalParameterNode("globalParameter1", "int"));
		model.addParameter(new GlobalParameterNode("globalParameter2", "com.example.UserType"));

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
		try {
			serializer.serialize(model);

			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			RootNode parsedModel = parser.parseModel(istream);

			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}


	@Test
	public void classSerializerTestWithAndroidBaseRunner(){
		classSerializerTest(true, "com.example.AndroidBaseRunner", 0);
	}

	@Test
	public void classSerializerTestWithoutAndroidBaseRunner(){
		classSerializerTest(false, null, 0);
	}

	@Test
	public void classSerializerTest() {
		for (int version = 0; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			classSerializerTest(false, null, version);
		}
	}

	private void classSerializerTest(boolean runOnAndroid, String androidBaseRunner, int version){
		ClassNode classNode = new ClassNode("com.example.TestClass", runOnAndroid, androidBaseRunner);
		classNode.addMethod(new MethodNode("testMethod1"));
		classNode.addMethod(new MethodNode("testMethod2"));
		classNode.addParameter(new GlobalParameterNode("parameter1", "int"));
		classNode.addParameter(new GlobalParameterNode("parameter2", "float"));
		classNode.addParameter(new GlobalParameterNode("parameter3", "com.example.UserType"));

		RootNode model = new RootNode("model", version);
		model.addClass(classNode);

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
		try {
			serializer.serialize(model);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			RootNode parsedModel = parser.parseModel(istream);

			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void wrongTypeStreamTest(){
		int version = ModelVersionDistributor.getCurrentSoftwareVersion();
		RootNode r = new RootNode("model", version);

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
		try {
			serializer.serialize(r);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			parser.parseClass(istream);
			fail("Exception expected");
		} catch (Exception e) {
			//			System.out.println("Exception caught: " + e.getMessage());
		}
	}

	//	@Test
	//	public void modelSerializerCrossTest1(){
	//		RootNode model = fGenerator.generateModel(3);
	//		OutputStream ostream = new ByteArrayOutputStream();
	//		ObsoleteXmlModelSerializer oldSerializer = new ObsoleteXmlModelSerializer(ostream);
	//		try {
	//			oldSerializer.writeXmlDocument(model);
	//			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
	//			IModelParser parser = new EctParser();
	//			RootNode parsedModel = parser.parseModel(istream);
	//			assertElementsEqual(model, parsedModel);
	//
	//		} catch (Exception e) {
	//			fail("Unexpected exception: " + e.getMessage());
	//		}
	//	}


	//	@Test
	//	public void modelSerializerCrossTest2(){
	//		for(int i = 0; i < 10; i++){
	//			RootNode model = fGenerator.generateModel(5);
	//			OutputStream ostream = new ByteArrayOutputStream();
	//			IModelSerializer serializer = new EctSerializer(ostream);
	//			try {
	//				serializer.serialize(model);
	//				InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
	//				IModelParser parser = new ObsoleteXmlModelParser();
	//				RootNode parsedModel = parser.parseModel(istream);
	//				assertElementsEqual(model, parsedModel);
	//
	//			} catch (Exception e) {
	//				fail("Unexpected exception: " + e.getMessage());
	//			}
	//		}
	//	}

	private RootNode createModel(int version) {

		ChoiceNode choice = new ChoiceNode("choice", "0");

		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "0", false);
		parameter.addChoice(choice);

		MethodNode methodNode = new MethodNode("testMethod1");
		methodNode.addParameter(parameter);

		Constraint constraint = new Constraint(
				new RelationStatement(parameter, EStatementRelation.EQUAL, choice),
				new RelationStatement(parameter, EStatementRelation.EQUAL, choice));

		ConstraintNode constraintNode = new ConstraintNode("name1", constraint);
		methodNode.addConstraint(constraintNode);

		ClassNode classNode = new ClassNode("com.example.TestClass", false, null);
		classNode.addMethod(methodNode);

		RootNode model = new RootNode("model", version);
		model.addClass(classNode);
		model.setVersion(version);

		return model;
	}

	private String getSerializedString(RootNode model) {
		OutputStream modelStream = new ByteArrayOutputStream();
		IModelSerializer serializer = 
				new EctSerializer(modelStream, ModelVersionDistributor.getCurrentSoftwareVersion());

		try {
			serializer.serialize(model);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

		return modelStream.toString();
	}

	@Test
	public void serializerTestWithModelConversion(){
		RootNode convertedModel = null;
		try {
			convertedModel = ModelConverter.convertToCurrentVersion(createModel(0));
		} catch (ModelOperationException e) {
			fail();
		}
		String convertedString = getSerializedString(convertedModel);

		RootNode currentModel = createModel(ModelVersionDistributor.getCurrentSoftwareVersion());
		String currentString = getSerializedString(currentModel);

		assertEquals(ModelVersionDistributor.getCurrentSoftwareVersion(), convertedModel.getModelVersion());
		assertEquals(ModelVersionDistributor.getCurrentSoftwareVersion(), currentModel.getModelVersion());

		assertEquals(currentString, convertedString);
	}
}
