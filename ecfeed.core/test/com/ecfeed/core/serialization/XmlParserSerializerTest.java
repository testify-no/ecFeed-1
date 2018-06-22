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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.adapter.java.JavaPrimitiveTypePredicate;
import com.ecfeed.core.generators.RandomGenerator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.core.serialization.ect.SerializationConstants;
import com.ecfeed.core.utils.JavaTypeHelper;

public class XmlParserSerializerTest {
	private final int TEST_RUNS = 10;

	//	private final int MAX_CLASSES = 1;
	//	private final int MAX_METHODS = 1;
	//	private final int MAX_PARAMETERS = 3;
	//	private final int MAX_EXPECTED_PARAMETERS = 3;
	//	private final int MAX_PARTITIONS = 1;
	//	private final int MAX_PARTITION_LEVELS = 1;
	//	private final int MAX_PARTITION_LABELS = 1;
	//	private final int MAX_CONSTRAINTS = 5;
	//	private final int MAX_TEST_CASES = 1;

	private final int MAX_CLASSES = 5;
	private final int MAX_METHODS = 5;
	private final int MAX_PARAMETERS = 5;
	//	private final int MAX_EXPECTED_PARAMETERS = 3;
	private final int MAX_PARTITIONS = 10;
	private final int MAX_PARTITION_LEVELS = 5;
	private final int MAX_PARTITION_LABELS = 5;
	private final int MAX_CONSTRAINTS = 5;
	private final int MAX_TEST_CASES = 50;

	Random rand = new Random();
	static int nextInt = 0;

	private final String[] CATEGORY_TYPES = new String[]{
			SerializationConstants.TYPE_NAME_BOOLEAN, SerializationConstants.TYPE_NAME_BYTE, SerializationConstants.TYPE_NAME_CHAR,
			SerializationConstants.TYPE_NAME_DOUBLE, SerializationConstants.TYPE_NAME_FLOAT, SerializationConstants.TYPE_NAME_INT,
			SerializationConstants.TYPE_NAME_LONG, SerializationConstants.TYPE_NAME_SHORT, SerializationConstants.TYPE_NAME_STRING
	};

	@Test
	public void test() {
		try {
			for(int i = 0; i < TEST_RUNS; ++i){
				RootNode model = createRootNode(rand.nextInt(MAX_CLASSES) + 1);
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				IModelSerializer serializer = 
						new EctSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());
				IModelParser parser = new EctParser();
				serializer.serialize(model);
				ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
				RootNode parsedModel = parser.parseModel(istream);
				compareModels(model, parsedModel);

			}
		} catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseChoiceTestVersion0() {
		parseChoiceTest(0);
	}

	@Test
	public void parseChoiceTestVersion1() {
		parseChoiceTest(1);
	}	

	public void parseChoiceTest(int version) {
		try{
			RootNode root = new RootNode("root", version);
			ClassNode classNode = new ClassNode("classNode");
			MethodNode method = new MethodNode("method");
			MethodParameterNode parameter = new MethodParameterNode("parameter", JavaTypeHelper.TYPE_NAME_STRING, "0", false);
			ChoiceNode choice = new ChoiceNode("choice", "A                 B");
			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(choice);
			TestCaseNode testCase = new TestCaseNode("test", testData);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(parameter);
			parameter.addChoice(choice);
			method.addTestCase(testCase);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream, version);
			IModelParser parser = new EctParser();
			serializer.serialize(root);

			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			RootNode parsedModel = parser.parseModel(istream);
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseConditionStatementTest(){
		try{
			int version = ModelVersionDistributor.getCurrentSoftwareVersion();
			RootNode root = new RootNode("root", version);
			ClassNode classNode = new ClassNode("classNode");
			MethodNode method = new MethodNode("method");
			MethodParameterNode choicesParentParameter =
					new MethodParameterNode("choicesParentParameter", JavaTypeHelper.TYPE_NAME_STRING, "0", false);
			MethodParameterNode expectedParameter =
					new MethodParameterNode("expectedParameter", JavaTypeHelper.TYPE_NAME_CHAR, "0", true);
			expectedParameter.setDefaultValueString("d");
			ChoiceNode choice1 = new ChoiceNode("choice", "p");
			choice1.setParent(choicesParentParameter);
			ChoiceNode choice2 = new ChoiceNode("expected", "s");
			choice2.setParent(expectedParameter);

			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(choice1);
			testData.add(choice2);
			TestCaseNode testCase = new TestCaseNode("test", testData);
			Constraint choiceConstraint = new Constraint(
					"constraint",
					new StaticStatement(true),
					RelationStatement.createStatementWithChoiceCondition(
							choicesParentParameter, EStatementRelation.EQUAL, choice1));

			Constraint labelConstraint = 
					new Constraint(
							"constraint",
							new StaticStatement(true),
							RelationStatement.createStatementWithLabelCondition(
									choicesParentParameter, EStatementRelation.EQUAL, "label"));

			Constraint expectedConstraint = 
					new Constraint(
							"constraint",
							new StaticStatement(true),
							new ExpectedValueStatement(expectedParameter, new ChoiceNode("expected", "n"), new JavaPrimitiveTypePredicate()));

			ConstraintNode choiceConstraintNode = new ConstraintNode("choice constraint", choiceConstraint);
			ConstraintNode labelConstraintNode = new ConstraintNode("label constraint", labelConstraint);
			ConstraintNode expectedConstraintNode = new ConstraintNode("expected constraint", expectedConstraint);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(choicesParentParameter);
			method.addParameter(expectedParameter);
			choicesParentParameter.addChoice(choice1);
			method.addTestCase(testCase);
			method.addConstraint(labelConstraintNode);
			method.addConstraint(choiceConstraintNode);
			method.addConstraint(expectedConstraintNode);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream, version);
			serializer.serialize(root);

			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			IModelParser parser = new EctParser();
			RootNode parsedModel = parser.parseModel(istream);
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	protected RootNode createRootNode(int classes) {
		RootNode root = new RootNode(randomName(), ModelVersionDistributor.getCurrentSoftwareVersion());
		for(int i = 0; i < classes; ++i){
			root.addClass(createClassNode(rand.nextInt(MAX_METHODS) + 1));
		}
		return root;
	}

	protected ClassNode createClassNode(int methods) {
		ClassNode classNode = new ClassNode("com.example." + randomName());
		for(int i = 0; i < methods; ++i){
			int numOfParameters = rand.nextInt(MAX_PARAMETERS) + 1;
			//			int numOfExpParameters = rand.nextInt(MAX_EXPECTED_PARAMETERS);
			//			if(numOfParameters + numOfExpParameters == 0){
			//				numOfParameters = 1;
			//			}
			int numOfConstraints = rand.nextInt(MAX_CONSTRAINTS) + 1;
			int numOfTestCases = rand.nextInt(MAX_TEST_CASES);
			classNode.addMethod(createMethodNode(numOfParameters, 0, numOfConstraints, numOfTestCases));
		}
		return classNode;
	}

	protected MethodNode createMethodNode(int numOfParameters,
			int numOfExpParameters, int numOfConstraints, int numOfTestCases) {
		MethodNode method = new MethodNode(randomName());
		List<MethodParameterNode> choicesParentParameters = createChoicesParentParameters(numOfParameters);
		List<MethodParameterNode> expectedParameters = createExpectedParameters(numOfExpParameters);

		for(int i = 0, j = 0; i < choicesParentParameters.size() || j < expectedParameters.size();){
			if(rand.nextBoolean() && i < choicesParentParameters.size()){
				method.addParameter(choicesParentParameters.get(i));
				++i;
			}
			else if (j < expectedParameters.size()){
				method.addParameter(expectedParameters.get(j));
				++j;
			}
		}

		List<ConstraintNode> constraints = createConstraints(choicesParentParameters, expectedParameters, numOfConstraints);
		List<TestCaseNode> testCases = createTestCases(method.getMethodParameters(), numOfTestCases);

		for(ConstraintNode constraint : constraints){
			method.addConstraint(constraint);
		}
		for(TestCaseNode testCase : testCases){
			method.addTestCase(testCase);
		}

		return method;
	}

	private List<MethodParameterNode> createChoicesParentParameters(int numOfParameters) {
		List<MethodParameterNode> parameters = new ArrayList<MethodParameterNode>();
		for(int i = 0; i < numOfParameters; i++){
			parameters.add(createChoicesParentParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)], rand.nextInt(MAX_PARTITIONS) + 1));
		}
		return parameters;
	}

	private MethodParameterNode createChoicesParentParameter(String type, int numOfChoices) {
		MethodParameterNode parameter = new MethodParameterNode(randomName(), type, "0", false);
		for(int i = 0; i < numOfChoices; i++){
			parameter.addChoice(createChoice(type, 1));
		}
		return parameter;
	}

	private List<MethodParameterNode> createExpectedParameters(int numOfExpParameters) {
		List<MethodParameterNode> parameters = new ArrayList<MethodParameterNode>();
		for(int i = 0; i < numOfExpParameters; i++){
			parameters.add(createExpectedValueParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)]));
		}
		return parameters;
	}

	private MethodParameterNode createExpectedValueParameter(String type) {
		String defaultValue = createRandomValue(type);
		MethodParameterNode parameter = new MethodParameterNode(randomName(), type, "0", true);
		parameter.setDefaultValueString(defaultValue);
		return parameter;
	}

	private String createRandomValue(String type) {
		switch(type){
		case SerializationConstants.TYPE_NAME_BOOLEAN:
			return Boolean.toString(rand.nextBoolean());
		case SerializationConstants.TYPE_NAME_BYTE:
			return Byte.toString((byte)rand.nextInt());
		case SerializationConstants.TYPE_NAME_CHAR:
			int random = rand.nextInt(255);
			if (random >= 32) {
				return new String ("\\" + String.valueOf(random));
			}
			return new String ("\\");
		case SerializationConstants.TYPE_NAME_DOUBLE:
			return Double.toString(rand.nextDouble());
		case SerializationConstants.TYPE_NAME_FLOAT:
			return Float.toString(rand.nextFloat());
		case SerializationConstants.TYPE_NAME_INT:
			return Integer.toString(rand.nextInt());
		case SerializationConstants.TYPE_NAME_LONG:
			return Long.toString(rand.nextLong());
		case SerializationConstants.TYPE_NAME_SHORT:
			return Short.toString((short)rand.nextInt());
		case SerializationConstants.TYPE_NAME_STRING:
			if(rand.nextInt(5) == 0){
				return JavaTypeHelper.SPECIAL_VALUE_NULL;
			}
			else{
				return generateRandomString(rand.nextInt(10));
			}
		default:
			fail("Unexpected parameter type");
			return null;
		}
	}

	String generateRandomString(int length) {
		String allowedChars = " 1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String result = new String();
		for(int i = 0; i < length; i++){
			int index = rand.nextInt(allowedChars.length());
			result += allowedChars.substring(index, index + 1);
		}
		return result;
	}

	private ChoiceNode createChoice(String type, int level) {
		String value = createRandomValue(type);
		ChoiceNode choice = new ChoiceNode(randomName(), value);
		for(int i = 0; i < rand.nextInt(MAX_PARTITION_LABELS); i++){
			choice.addLabel(generateRandomString(10));
		}
		boolean createChildren = rand.nextBoolean();
		int numOfChildren = rand.nextInt(MAX_PARTITIONS);
		if(createChildren && level <= MAX_PARTITION_LEVELS){
			for(int i = 0; i < numOfChildren; i++){
				choice.addChoice(createChoice(type, level + 1));
			}
		}
		return choice;
	}


	private List<ConstraintNode> createConstraints(List<MethodParameterNode> choicesParentParameters,
			List<MethodParameterNode> expectedParameters, int numOfConstraints) {
		List<ConstraintNode> constraints = new ArrayList<ConstraintNode>();
		for(int i = 0; i < numOfConstraints; ++i){
			constraints.add(new ConstraintNode(randomName(), createConstraint(choicesParentParameters, expectedParameters)));
		}
		return constraints;
	}

	private Constraint createConstraint(List<MethodParameterNode> choicesParentParameters,
			List<MethodParameterNode> expectedParameters) {
		AbstractStatement premise = createChoicesParentStatement(choicesParentParameters);
		AbstractStatement consequence = null;
		while(consequence == null){
			if(rand.nextBoolean()){
				consequence = createChoicesParentStatement(choicesParentParameters);
			}
			else{
				consequence = createExpectedStatement(expectedParameters);
			}
		}
		return new Constraint("constraint", premise, consequence);
	}

	private AbstractStatement createChoicesParentStatement(List<MethodParameterNode> parameters) {
		AbstractStatement statement = null;
		while(statement == null){
			switch(rand.nextInt(3)){
			case 0: statement = new StaticStatement(rand.nextBoolean());
			case 1: if(getChoicesParentParameters(parameters).size() > 0){
				switch(rand.nextInt(2)){
				case 0:
					statement = createChoiceStatement(parameters);
				case 1:
					statement = createLabelStatement(parameters);
				}
			}
			case 2: statement = createStatementArray(rand.nextInt(3), parameters);
			}
		}
		return statement;
	}

	private AbstractStatement createLabelStatement(List<MethodParameterNode> parameters) {
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		Set<String> labels = parameter.getLeafLabels();
		String label;
		if(labels.size() > 0){
			label = new ArrayList<String>(labels).get(rand.nextInt(labels.size()));
		}
		else{
			label = "SomeLabel";
			parameter.getChoices().get(0).addLabel(label);
		}
		EStatementRelation relation = pickRelation();
		return RelationStatement.createStatementWithLabelCondition(parameter, relation, label);
	}

	private AbstractStatement createChoiceStatement(List<MethodParameterNode> parameters) {
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		ChoiceNode choiceNode = 
				new ArrayList<ChoiceNode>(parameter.getLeafChoices()).get(rand.nextInt(parameter.getChoices().size()));

		EStatementRelation relation = pickRelation();
		return RelationStatement.createStatementWithChoiceCondition(parameter, relation, choiceNode);
	}

	private EStatementRelation pickRelation() {
		EStatementRelation relation;
		switch(rand.nextInt(2)){
		case 0: relation = EStatementRelation.EQUAL;
		case 1: relation = EStatementRelation.NOT_EQUAL;
		default: relation = EStatementRelation.EQUAL;
		}
		return relation;
	}

	private AbstractStatement createExpectedStatement(List<MethodParameterNode> parameters) {
		if(parameters.size() == 0) return null;
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		return new ExpectedValueStatement(parameter, new ChoiceNode("default", createRandomValue(parameter.getType())), new JavaPrimitiveTypePredicate());
	}

	private List<MethodParameterNode> getChoicesParentParameters(List<? extends MethodParameterNode> parameters) {
		List<MethodParameterNode> result = new ArrayList<MethodParameterNode>();
		for(MethodParameterNode parameter : parameters){
			if(parameter instanceof MethodParameterNode == false){
				result.add(parameter);
			}
		}
		return result;
	}

	private AbstractStatement createStatementArray(int levels, List<MethodParameterNode> parameters) {
		StatementArray array = new StatementArray(rand.nextBoolean()?EStatementOperator.AND:EStatementOperator.OR);
		for(int i = 0; i < rand.nextInt(3) + 1; ++i){
			if(levels > 0){
				array.addStatement(createStatementArray(levels - 1, parameters));
			}
			else{
				if(rand.nextBoolean() && getChoicesParentParameters(parameters).size() > 0){
					array.addStatement(createChoiceStatement(parameters));
				}
				else{
					array.addStatement(new StaticStatement(rand.nextBoolean()));
				}
			}
		}
		return array;
	}

	private List<TestCaseNode> createTestCases(
			List<MethodParameterNode> parameters, int numOfTestCases) {
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		try {
			List<IConstraint<ChoiceNode>> constraints = new ArrayList<IConstraint<ChoiceNode>>();
			RandomGenerator<ChoiceNode> generator = new RandomGenerator<ChoiceNode>();
			List<List<ChoiceNode>> input = getGeneratorInput(parameters);
			Map<String, Object> genParameters = new HashMap<String, Object>();
			genParameters.put("length", numOfTestCases);
			genParameters.put("duplicates", true);

			generator.initialize(input, constraints, genParameters, null);
			List<ChoiceNode> next;
			while((next = generator.next()) != null){
				result.add(new TestCaseNode(randomName(), next));
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<ChoiceNode>> getGeneratorInput(
			List<MethodParameterNode> parameters) {
		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();
		for(MethodParameterNode parameter : parameters){
			result.add(parameter.getLeafChoices());
		}
		return result;
	}

	protected String randomName(){
		return "name" + nextInt++;
	}

	private void compareModels(RootNode model1, RootNode model2) {
		compareNames(model1.getName(), model2.getName());
		compareSizes(model1.getClasses(), model2.getClasses());
		for(int i = 0; i < model1.getClasses().size(); ++i){
			compareClasses(model1.getClasses().get(i), model2.getClasses().get(i));
		}
	}

	private void compareClasses(ClassNode classNode1, ClassNode classNode2) {
		compareNames(classNode1.getName(), classNode2.getName());
		compareSizes(classNode1.getMethods(), classNode2.getMethods());

		for(int i = 0; i < classNode1.getMethods().size(); ++i){
			compareMethods(classNode1.getMethods().get(i), classNode2.getMethods().get(i));
		}
	}

	private void compareMethods(MethodNode method1, MethodNode method2) {
		compareNames(method1.getName(), method2.getName());
		compareSizes(method1.getParameters(), method2.getParameters());
		compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes());
		compareSizes(method1.getTestCases(), method2.getTestCases());

		for(int i =0; i < method1.getParameters().size(); ++i){
			compareParameters(method1.getMethodParameters().get(i), method2.getMethodParameters().get(i));
		}
		for(int i =0; i < method1.getConstraintNodes().size(); ++i){
			compareConstraintNodes(method1.getConstraintNodes().get(i), method2.getConstraintNodes().get(i));
		}
		for(int i =0; i < method1.getTestCases().size(); ++i){
			compareTestCases(method1.getTestCases().get(i), method2.getTestCases().get(i));
		}
	}

	private void compareParameters(MethodParameterNode parameter1, MethodParameterNode parameter2) {
		compareNames(parameter1.getName(), parameter2.getName());
		compareNames(parameter1.getType(), parameter2.getType());
		compareSizes(parameter1.getChoices(), parameter2.getChoices());
		if(parameter1 instanceof MethodParameterNode || parameter2 instanceof MethodParameterNode){
			if((parameter1 instanceof MethodParameterNode && parameter2 instanceof MethodParameterNode) == false){
				fail("Either both parameters must be expected value or none");
			}
		}
		for(int i = 0; i < parameter1.getChoices().size(); ++i){
			compareChoices(parameter1.getChoices().get(i), parameter2.getChoices().get(i));
		}
	}

	private void compareChoices(ChoiceNode choice1, ChoiceNode choice2) {
		compareNames(choice1.getName(), choice2.getName());
		compareValues(choice1.getValueString(),choice2.getValueString());
		compareLabels(choice1.getLabels(), choice2.getLabels());
		assertEquals(choice1.getChoices().size(), choice2.getChoices().size());
		for(int i = 0; i < choice1.getChoices().size(); i++){
			compareChoices(choice1.getChoices().get(i), choice2.getChoices().get(i));
		}
	}

	private void compareLabels(Set<String> labels, Set<String> labels2) {
		assertTrue(labels.size() == labels2.size());
		for(String label : labels){
			assertTrue(labels2.contains(label));
		}
	}

	private void compareValues(Object value1, Object value2) {
		boolean result = true;
		if(value1 == null){
			result = (value2 == null);
		}
		else{
			result = value1.equals(value2);
		}
		if(!result){
			fail("Value " + value1 + " differ from " + value2);
		}
	}

	private void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {
		compareNames(constraint1.getName(), constraint2.getName());
		compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}


	private void compareConstraints(Constraint constraint1, Constraint constraint2) {
		compareBasicStatements(constraint1.getPremise(), constraint2.getPremise());
		compareBasicStatements(constraint1.getConsequence(), constraint2.getConsequence());
	}

	private void compareBasicStatements(AbstractStatement statement1, AbstractStatement statement2) {
		if(statement1 instanceof StaticStatement && statement2 instanceof StaticStatement){
			compareStaticStatements((StaticStatement)statement1, (StaticStatement)statement2);
		}
		else if(statement1 instanceof RelationStatement && statement2 instanceof RelationStatement){
			compareRelationStatements((RelationStatement)statement1, (RelationStatement)statement2);
		}
		else if(statement1 instanceof StatementArray && statement2 instanceof StatementArray){
			compareStatementArrays((StatementArray)statement1, (StatementArray)statement2);
		}
		else if(statement1 instanceof ExpectedValueStatement && statement2 instanceof ExpectedValueStatement){
			compareExpectedValueStatements((ExpectedValueStatement)statement1, (ExpectedValueStatement)statement2);
		}
		else{
			fail("Unknown type of statement or compared statements are of didderent types");
		}
	}

	private void compareExpectedValueStatements(
			ExpectedValueStatement statement1, ExpectedValueStatement statement2) {
		compareParameters(statement1.getParameter(), statement2.getParameter());
		assertEquals(statement1.getCondition().getValueString(), statement2.getCondition().getValueString());
	}

	private void compareRelationStatements(RelationStatement statement1, RelationStatement statement2) {
		compareParameters(statement1.getLeftParameter(), statement2.getLeftParameter());
		if((statement1.getRelation() != statement2.getRelation())){
			fail("Compared statements have different relations: " +
					statement1.getRelation() + " and " + statement2.getRelation());
		}
		compareConditions(statement1.getConditionValue(), statement2.getConditionValue());
	}

	private void compareConditions(Object condition, Object condition2) {
		if(condition instanceof String && condition2 instanceof String){
			if(condition.equals(condition2) == false){
				fail("Compared labels are different: " + condition + "!=" + condition2);
			}
		}
		else if(condition instanceof ChoiceNode && condition2 instanceof ChoiceNode){
			compareChoices((ChoiceNode)condition, (ChoiceNode)condition2);
		}
		else{
			fail("Unknown or not same types of compared conditions");
		}
	}

	private void compareStatementArrays(StatementArray array1, StatementArray array2) {
		if(array1.getOperator() != array2.getOperator()){
			fail("Operator of compared statement arrays differ");
		}
		compareSizes(array1.getChildren(), array2.getChildren());
		for(int i = 0; i < array1.getChildren().size(); ++i){
			compareBasicStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	private void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			fail("Static statements different");
		}
	}

	private void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {
		compareNames(testCase1.getName(), testCase2.getName());
		compareSizes(testCase1.getTestData(), testCase2.getTestData());
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			ChoiceNode testValue1 = testCase1.getTestData().get(i);
			ChoiceNode testValue2 = testCase2.getTestData().get(i);

			if(testValue1.getParameter() instanceof MethodParameterNode){
				compareValues(testValue1.getValueString(), testValue2.getValueString());
			}
			else{
				compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	private void compareSizes(Collection<? extends Object> collection1, Collection<? extends Object> collection2) {
		if(collection1.size() != collection2.size()){
			fail("Different sizes of collections");
		}
	}

	private void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			fail("Different names: " + name + ", " + name2);
		}
	}
}
