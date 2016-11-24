/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization.ect;

import static com.ecfeed.core.model.Constants.EXPECTED_VALUE_CHOICE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.ANDROID_RUNNER_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CLASS_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.METHOD_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.ROOT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.RUN_ON_ANDROID_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.TYPE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.VALUE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import com.ecfeed.core.adapter.java.JavaPrimitiveTypePredicate;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.WhiteCharConverter;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

public abstract class XomAnalyser {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract int getModelVersion();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();

	public RootNode parseRoot(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME);
		String name = getElementName(element);

		RootNode targetRootNode = new RootNode(name, getModelVersion());

		targetRootNode.setDescription(parseComments(element));

		//parameters must be parsed before classes
		for(Element child : getIterableChildren(element, getParameterNodeName())){
			try{
				targetRootNode.addParameter(parseGlobalParameter(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}
		for(Element child : getIterableChildren(element, Constants.CLASS_NODE_NAME)){
			try{
				targetRootNode.addClass(parseClass(child, targetRootNode));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		return targetRootNode;
	}

	public ClassNode parseClass(Element classElement, RootNode parent) throws ParserException{
		assertNodeTag(classElement.getQualifiedName(), CLASS_NODE_NAME);

		String name = getElementName(classElement);

		BooleanHolder runOnAndroidHolder = new BooleanHolder(false);
		StringHolder androidBaseRunnerHolder = new StringHolder(); 
		parseAndroidValues(classElement, runOnAndroidHolder, androidBaseRunnerHolder);
		ClassNode targetClassNode = new ClassNode(name, runOnAndroidHolder.get(), androidBaseRunnerHolder.get());

		targetClassNode.setDescription(parseComments(classElement));
		//we need to do it here, so the backward search for global parameters will work
		targetClassNode.setParent(parent);

		//parameters must be parsed before classes
		for(Element child : getIterableChildren(classElement, getParameterNodeName())){
			try{
				targetClassNode.addParameter(parseGlobalParameter(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		for(Element child : getIterableChildren(classElement, Constants.METHOD_NODE_NAME)){
			try{
				targetClassNode.addMethod(parseMethod(child, targetClassNode));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		return targetClassNode;
	}

	private void parseAndroidValues(
			Element classElement, 
			BooleanHolder runOnAndroidHolder, 
			StringHolder androidBaseRunnerHolder) {

		if (ModelVersionDistributor.isAndroidAttributeInTheClass(getModelVersion())) {
			parseAndroidAttributes(classElement, runOnAndroidHolder, androidBaseRunnerHolder);
		} else {
			parseAndroidProperties(classElement, runOnAndroidHolder, androidBaseRunnerHolder);
		}
	}

	private static void parseAndroidAttributes(
			Element classElement, BooleanHolder runOnAndroidHolder, StringHolder androidBaseRunnerHolder) {

		String runOnAndroidStr = classElement.getAttributeValue(RUN_ON_ANDROID_ATTRIBUTE_NAME);
		runOnAndroidHolder.set(BooleanHelper.parseBoolean(runOnAndroidStr));

		String androidBaseRunnerStr = classElement.getAttributeValue(ANDROID_RUNNER_ATTRIBUTE_NAME);

		if (StringHelper.isNullOrEmpty(androidBaseRunnerStr)) {
			return;
		}

		androidBaseRunnerHolder.set(androidBaseRunnerStr);
	}

	private static void parseAndroidProperties(
			Element classElement, 
			BooleanHolder runOnAndroidHolder, 
			StringHolder androidBaseRunnerHolder) {

		String runOnAndroidStr = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID, classElement);
		runOnAndroidHolder.set(BooleanHelper.parseBoolean(runOnAndroidStr));

		String androidBaseRunnerStr = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER, classElement);
		androidBaseRunnerHolder.set(androidBaseRunnerStr);		
	}

	public MethodNode parseMethod(Element methodElement, ClassNode parent) throws ParserException{
		assertNodeTag(methodElement.getQualifiedName(), METHOD_NODE_NAME);
		String name = getElementName(methodElement);

		MethodNode targetMethodNode = new MethodNode(name);
		targetMethodNode.setParent(parent);

		parseMethodProperties(methodElement, targetMethodNode);

		for(Element child : getIterableChildren(methodElement, getParameterNodeName())){
			try{
				targetMethodNode.addParameter(parseMethodParameter(child, targetMethodNode));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		for(Element child : getIterableChildren(methodElement, Constants.TEST_CASE_NODE_NAME)){
			try{
				targetMethodNode.addTestCase(parseTestCase(child, targetMethodNode));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		for(Element child : getIterableChildren(methodElement, Constants.CONSTRAINT_NODE_NAME)){
			try{
				targetMethodNode.addConstraint(parseConstraint(child, targetMethodNode));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		targetMethodNode.setDescription(parseComments(methodElement));

		return targetMethodNode;
	}

	private void parseMethodProperties(Element methodElement, MethodNode targetMethodNode) {
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodElement, targetMethodNode);
	}

	private void parseMethodProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			MethodNode targetMethodNode) {
		String value = getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetMethodNode.setPropertyValue(propertyId, value);		
	}

	public MethodParameterNode parseMethodParameter(Element parameterElement, MethodNode method) throws ParserException{
		assertNodeTag(parameterElement.getQualifiedName(), getParameterNodeName());
		String name = getElementName(parameterElement);
		String type = getAttributeValue(parameterElement, TYPE_NAME_ATTRIBUTE);
		String defaultValue = null;
		String expected = String.valueOf(false);

		if(parameterElement.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null){
			expected = getAttributeValue(parameterElement, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME);
			defaultValue = getAttributeValue(parameterElement, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		}

		MethodParameterNode targetMethodParameterNode = new MethodParameterNode(name, type, defaultValue, Boolean.parseBoolean(expected));

		parseParameterProperties(parameterElement, targetMethodParameterNode);

		if(parameterElement.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null){
			boolean linked = Boolean.parseBoolean(getAttributeValue(parameterElement, PARAMETER_IS_LINKED_ATTRIBUTE_NAME));
			targetMethodParameterNode.setLinked(linked);
		}

		if(parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method != null && method.getClassNode() != null){
			String linkPath = getAttributeValue(parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME);
			GlobalParameterNode link = method.getClassNode().findGlobalParameter(linkPath);
			if(link != null){
				targetMethodParameterNode.setLink(link);
			}
			else{
				targetMethodParameterNode.setLinked(false);
			}
		}else{
			targetMethodParameterNode.setLinked(false);
		}

		for(Element child : getIterableChildren(parameterElement, getChoiceNodeName())){
			try{
				targetMethodParameterNode.addChoice(parseChoice(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		targetMethodParameterNode.setDescription(parseComments(parameterElement));
		if(targetMethodParameterNode.isLinked() == false){
			targetMethodParameterNode.setTypeComments(parseTypeComments(parameterElement));
		}

		return targetMethodParameterNode;
	}

	private void parseParameterProperties(Element parameterElement, AbstractParameterNode targetAbstractParameterNode) {
		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_PARAMETER_TYPE, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_ACTION, 
				parameterElement, 
				targetAbstractParameterNode);
	}

	private void parseParameterProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			AbstractParameterNode targetAbstractParameterNode) {
		String value = getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetAbstractParameterNode.setPropertyValue(propertyId, value);		
	}


	public GlobalParameterNode parseGlobalParameter(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), getParameterNodeName());
		String name = getElementName(element);
		String type = getAttributeValue(element, TYPE_NAME_ATTRIBUTE);
		GlobalParameterNode targetGlobalParameterNode = new GlobalParameterNode(name, type);

		parseParameterProperties(element, targetGlobalParameterNode);

		for(Element child : getIterableChildren(element, getChoiceNodeName())){
			try{
				targetGlobalParameterNode.addChoice(parseChoice(child));
			}catch(ParserException e){
				System.err.println("Exception: " + e.getMessage());
			}
		}

		targetGlobalParameterNode.setDescription(parseComments(element));
		targetGlobalParameterNode.setTypeComments(parseTypeComments(element));

		return targetGlobalParameterNode;
	}

	public TestCaseNode parseTestCase(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), TEST_CASE_NODE_NAME);
		String name = getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE);

		List<Element> parameterElements = getIterableChildren(element);
		List<MethodParameterNode> parameters = method.getMethodParameters();

		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();

		if(parameters.size() != parameterElements.size()){
			ParserException.report(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(name));
		}

		for(int i = 0; i < parameterElements.size(); i++){
			Element testParameterElement = parameterElements.get(i);
			MethodParameterNode parameter = parameters.get(i);
			ChoiceNode testValue = null;

			if(testParameterElement.getLocalName().equals(Constants.TEST_PARAMETER_NODE_NAME)){
				String choiceName = getAttributeValue(testParameterElement, getChoiceAttributeName());
				testValue = parameter.getChoice(choiceName);
				if(testValue == null){
					ParserException.report(Messages.PARTITION_DOES_NOT_EXIST(parameter.getName(), choiceName));
				}
			}
			else if(testParameterElement.getLocalName().equals(Constants.EXPECTED_PARAMETER_NODE_NAME)){
				String valueString = getAttributeValue(testParameterElement, Constants.VALUE_ATTRIBUTE_NAME);
				if(valueString == null){
					ParserException.report(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
				}
				testValue = new ChoiceNode(EXPECTED_VALUE_CHOICE_NAME, valueString);
				testValue.setParent(parameter);
			}
			testData.add(testValue);
		}

		TestCaseNode targetTestCaseNode = new TestCaseNode(name, testData);
		targetTestCaseNode.setDescription(parseComments(element));
		return targetTestCaseNode;
	}

	public ConstraintNode parseConstraint(Element element, MethodNode method) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_NODE_NAME);
		String name = getElementName(element);

		AbstractStatement premise = null;
		AbstractStatement consequence = null;

		if((getIterableChildren(element, Constants.CONSTRAINT_PREMISE_NODE_NAME).size() != 1) ||
				(getIterableChildren(element, Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME).size() != 1)){
			ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
		}
		for(Element child : getIterableChildren(element, Constants.CONSTRAINT_PREMISE_NODE_NAME)){
			if(child.getLocalName().equals(Constants.CONSTRAINT_PREMISE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					//there is only one statement per premise or consequence that is either
					//a single statement or statement array
					premise = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
		}
		for(Element child : getIterableChildren(element, Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
			if(child.getLocalName().equals(Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME)){
				if(getIterableChildren(child).size() == 1){
					consequence = parseStatement(child.getChildElements().get(0), method);
				}
				else{
					ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				}
			}
			else{
				ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			}
		}
		if(premise == null || consequence == null){
			ParserException.report(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
		}

		ConstraintNode targetConstraint = new ConstraintNode(name, new Constraint(premise, consequence));

		targetConstraint.setDescription(parseComments(element));

		return targetConstraint;
	}

	public AbstractStatement parseStatement(Element element, MethodNode method) throws ParserException {
		switch(element.getLocalName()){
		case Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
			return parseChoiceStatement(element, method);
		case Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
			return parseLabelStatement(element, method);
		case Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME:
			return parseStatementArray(element, method);
		case Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME:
			return parseStaticStatement(element);
		case Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME:
			return parseExpectedValueStatement(element, method);
		default: return null;
		}
	}

	public StatementArray parseStatementArray(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATEMENT_ARRAY_NODE_NAME);

		StatementArray statementArray = null;
		String operatorValue = getAttributeValue(element, Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME);
		switch(operatorValue){
		case Constants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(EStatementOperator.OR);
			break;
		case Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(EStatementOperator.AND);
			break;
		default:
			ParserException.report(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(method.getName(), operatorValue));
		}
		for(Element child : getIterableChildren(element)){
			AbstractStatement childStatement = parseStatement(child, method);
			if(childStatement != null){
				statementArray.addStatement(childStatement);
			}
		}
		return statementArray;
	}

	public StaticStatement parseStaticStatement(Element element) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATIC_STATEMENT_NODE_NAME);

		String valueString = getAttributeValue(element, Constants.STATIC_VALUE_ATTRIBUTE_NAME);
		switch(valueString){
		case Constants.STATIC_STATEMENT_TRUE_VALUE:
			return new StaticStatement(true);
		case Constants.STATIC_STATEMENT_FALSE_VALUE:
			return new StaticStatement(false);
		default:
			ParserException.report(Messages.WRONG_STATIC_STATEMENT_VALUE(valueString));
			return new StaticStatement(false);
		}
	}

	public ChoicesParentStatement parseChoiceStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		MethodParameterNode parameter = (MethodParameterNode)method.getParameter(parameterName);
		if(parameter == null || parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		String choiceName = getAttributeValue(element, getStatementChoiceAttributeName());
		ChoiceNode choice = parameter.getChoice(choiceName);
		if(choice == null){
			ParserException.report(Messages.WRONG_PARTITION_NAME(choiceName, parameterName, method.getName()));
		}

		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);
		EStatementRelation relation = getRelation(relationName);

		return new ChoicesParentStatement(parameter, relation, choice);
	}

	public ChoicesParentStatement parseLabelStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		String label = getAttributeValue(element, Constants.STATEMENT_LABEL_ATTRIBUTE_NAME);
		String relationName = getAttributeValue(element, Constants.STATEMENT_RELATION_ATTRIBUTE_NAME);

		MethodParameterNode parameter = method.getMethodParameter(parameterName);
		if(parameter == null || parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		EStatementRelation relation = getRelation(relationName);

		return new ChoicesParentStatement(parameter, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(Element element, MethodNode method) throws ParserException {
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName());
		String valueString = getAttributeValue(element, Constants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME);
		MethodParameterNode parameter = method.getMethodParameter(parameterName);
		if(parameter == null || !parameter.isExpected()){
			ParserException.report(Messages.WRONG_CATEGORY_NAME(parameterName, method.getName()));
		}
		ChoiceNode condition = new ChoiceNode("expected", valueString);
		condition.setParent(parameter);

		return new ExpectedValueStatement(parameter, condition, new JavaPrimitiveTypePredicate());
	}

	public ChoiceNode parseChoice(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), getChoiceNodeName());
		String name = getElementName(element);
		String value = getAttributeValue(element, VALUE_ATTRIBUTE);

		ChoiceNode choice = new ChoiceNode(name, value);
		choice.setDescription(parseComments(element));

		for(Element child : getIterableChildren(element)){
			if(child.getLocalName() == getChoiceNodeName()){
				try{
					choice.addChoice(parseChoice(child));
				}catch(ParserException e){
					System.err.println("Exception: " + e.getMessage());
				}

			}
			if(child.getLocalName() == Constants.LABEL_NODE_NAME){
				choice.addLabel(fWhiteCharConverter.decode(child.getAttributeValue(Constants.LABEL_ATTRIBUTE_NAME)));
			}
		}

		return choice;
	}

	private static void assertNodeTag(String qualifiedName, String expectedName) throws ParserException {
		if(qualifiedName.equals(expectedName) == false){
			ParserException.report("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
		}
	}

	protected static List<Element> getIterableChildren(Element element){
		ArrayList<Element> list = new ArrayList<Element>();
		Elements children = element.getChildElements();
		for(int i = 0; i < children.size(); i++){
			Node node = children.get(i);
			if(node instanceof Element){
				list.add((Element)node);
			}
		}
		return list;
	}

	protected static List<Element> getIterableChildren(Element element, String name){
		List<Element> elements = getIterableChildren(element);
		Iterator<Element> it = elements.iterator();
		while(it.hasNext()){
			if(it.next().getLocalName().equals(name) == false){
				it.remove();
			}
		}
		return elements;
	}

	protected String getElementName(Element element) throws ParserException {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null){
			ParserException.report(Messages.MISSING_ATTRIBUTE(element, Constants.NODE_NAME_ATTRIBUTE));
		}
		return fWhiteCharConverter.decode(name);
	}

	protected String getAttributeValue(Element element, String attributeName) throws ParserException{
		String value = element.getAttributeValue(attributeName);
		if(value == null){
			ParserException.report(Messages.MISSING_ATTRIBUTE(element, attributeName));
		}
		return fWhiteCharConverter.decode(value);
	}

	protected EStatementRelation getRelation(String relationName) throws ParserException{
		EStatementRelation relation = null;
		switch(relationName){
		case Constants.RELATION_EQUAL:
			relation = EStatementRelation.EQUAL;
			break;
		case Constants.RELATION_NOT:
		case Constants.RELATION_NOT_ASCII:
			relation = EStatementRelation.NOT;
			break;
		default:
			ParserException.report(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
		}
		return relation;
	}

	protected String parseComments(Element element) {
		if(element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).size() > 0){
			Element comments = element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if(comments.getChildElements(Constants.BASIC_COMMENTS_BLOCK_TAG_NAME).size() > 0){
				Element basicComments = comments.getChildElements(Constants.BASIC_COMMENTS_BLOCK_TAG_NAME).get(0);
				return fWhiteCharConverter.decode(basicComments.getValue());
			}
		}
		return null;
	}

	protected String parseTypeComments(Element element){
		if(element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).size() > 0){
			Element comments = element.getChildElements(Constants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if(comments.getChildElements(Constants.TYPE_COMMENTS_BLOCK_TAG_NAME).size() > 0){
				Element typeComments = comments.getChildElements(Constants.TYPE_COMMENTS_BLOCK_TAG_NAME).get(0);
				return fWhiteCharConverter.decode(typeComments.getValue());
			}
		}
		return null;
	}

	private static String getPropertyValue(NodePropertyDefs.PropertyId propertyId, Element classElement) {
		String propertyName = NodePropertyDefs.getPropertyName(propertyId);

		Elements propertyElements = getPropertyElements(classElement);
		if (propertyElements == null) {
			return null;
		}

		int propertiesSize = propertyElements.size();

		for (int cnt = 0; cnt < propertiesSize; cnt++) {
			Element propertyElement = propertyElements.get(cnt);

			String name = getNameFromPropertyElem(propertyElement);

			if (name.equals(propertyName)) {
				return getValueFromPropertyElem(propertyElement);
			}
		}

		return null;		
	}	

	private static Elements getPropertyElements(Element parentElement) {
		Elements propertyBlockElements = parentElement.getChildElements(Constants.PROPERTIES_BLOCK_TAG_NAME);
		if (propertyBlockElements.size() == 0) {
			return null;
		}

		Element firstBlockElement = propertyBlockElements.get(0);
		return firstBlockElement.getChildElements(Constants.PROPERTY_TAG_NAME);
	}

	private static String getNameFromPropertyElem(Element property) {
		return property.getAttributeValue(Constants.PROPERTY_ATTRIBUTE_NAME);
	}

	private static String getValueFromPropertyElem(Element property) {
		return property.getAttributeValue(Constants.PROPERTY_ATTRIBUTE_VALUE);
	}	

}
