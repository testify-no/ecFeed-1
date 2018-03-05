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

import static com.ecfeed.core.serialization.ect.SerializationConstants.ANDROID_RUNNER_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.CLASS_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.CONSTRAINT_CONSEQUENCE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.CONSTRAINT_PREMISE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.EXPECTED_PARAMETER_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.LABEL_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.LABEL_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.METHOD_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.NODE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PROPERTIES_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PROPERTY_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PROPERTY_ATTRIBUTE_TYPE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PROPERTY_ATTRIBUTE_VALUE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.PROPERTY_TAG_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.ROOT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.RUN_ON_ANDROID_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.TEST_PARAMETER_NODE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.TYPE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.VALUE_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.SerializationConstants.VERSION_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.SerializationConstants.NODE_IS_RADOMIZED_ATTRIBUTE;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.WhiteCharConverter;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class XomBuilder implements IModelVisitor {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract int getModelVersion();

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		Element targetRootElement = createAbstractElement(ROOT_NODE_NAME, rootNode);

		String versionStr = Integer.toString(rootNode.getModelVersion());
		Attribute versionAttr = new Attribute(VERSION_ATTRIBUTE, versionStr);
		targetRootElement.addAttribute(versionAttr);

		for (ClassNode classNode : rootNode.getClasses()) {
			targetRootElement.appendChild((Element)visit(classNode));
		}

		for (GlobalParameterNode parameterNode : rootNode.getGlobalParameters()) {
			targetRootElement.appendChild((Element)visit(parameterNode));
		}

		return targetRootElement;
	}

	@Override
	public Object visit(ClassNode classNode) throws Exception {

		Element targetClassElement = createAbstractElement(CLASS_NODE_NAME, classNode);

		addAndroidValues(classNode, targetClassElement);

		for (MethodNode methodNode : classNode.getMethods()) {
			targetClassElement.appendChild((Element)visit(methodNode));
		}

		for (GlobalParameterNode parameterNode : classNode.getGlobalParameters()) {
			targetClassElement.appendChild((Element)visit(parameterNode));
		}

		return targetClassElement;
	}

	private void addAndroidValues(ClassNode classNode, Element targetClassElement) {

		if (ModelVersionDistributor.isAndroidAttributeInTheClass(getModelVersion())) {
			addAndroidValuesAsAttributes(classNode, targetClassElement);
		} else {
			addAndroidValuesAsProperties(classNode, targetClassElement);
		}
	}

	private void addAndroidValuesAsAttributes(ClassNode classNode, Element classElement) {

		boolean runOnAndroid = classNode.getRunOnAndroid();

		classElement.addAttribute(
				new Attribute(
						RUN_ON_ANDROID_ATTRIBUTE_NAME,  
						Boolean.toString(runOnAndroid)));

		String androidBaseRunner = classNode.getAndroidRunner();

		if (!runOnAndroid && StringHelper.isNullOrEmpty(androidBaseRunner)) {
			return;
		}

		if (androidBaseRunner == null) {
			androidBaseRunner = "";
		}

		classElement.addAttribute(new Attribute(ANDROID_RUNNER_ATTRIBUTE_NAME, androidBaseRunner));
	}

	private void addAndroidValuesAsProperties(ClassNode classNode, Element targetElement) {

		boolean runOnAndroid = classNode.getRunOnAndroid();

		appendProperty(
				getPropertyName(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID),
				getPropertyType(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID), 
				BooleanHelper.toString(runOnAndroid), targetElement);

		String androidBaseRunner = classNode.getAndroidRunner();
		if (androidBaseRunner == null) {
			return;
		}

		appendProperty(
				getPropertyName(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER), 
				getPropertyType(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER),  
				androidBaseRunner, targetElement);
	}

	@Override
	public Object visit(MethodNode methodNode) throws Exception {

		Element targetMethodElement = createAbstractElement(METHOD_NODE_NAME, methodNode);

		addMethodProperties(methodNode, targetMethodElement);

		for (MethodParameterNode parameter : methodNode.getMethodParameters()) {
			targetMethodElement.appendChild((Element)parameter.accept(this));
		}

		for (ConstraintNode constraint : methodNode.getConstraintNodes()) {
			targetMethodElement.appendChild((Element)constraint.accept(this));
		}

		for (TestCaseNode testCase : methodNode.getTestCases()) {
			targetMethodElement.appendChild((Element)testCase.accept(this));
		}

		return targetMethodElement;
	}

	private void addMethodProperties(MethodNode methodNode, Element targetElement) {

		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM,  methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodNode, targetElement);
	}

	private void addMethodProperty(NodePropertyDefs.PropertyId propertyId,  MethodNode methodNode, Element targetElement) {

		String value = methodNode.getPropertyValue(propertyId);

		if (value == null) {
			return;
		}

		appendProperty(getPropertyName(propertyId), getPropertyType(propertyId), value, targetElement);
	}

	@Override
	public Object visit(MethodParameterNode node)  throws Exception {

		Element targetParameterElement = createAbstractElement(getParameterNodeName(), node);

		addParameterProperties(node, targetParameterElement);
		appendTypeComments(targetParameterElement, node);

		encodeAndAddAttribute(
				targetParameterElement, new Attribute(TYPE_NAME_ATTRIBUTE, node.getRealType()), 
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueForSerialization()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME, Boolean.toString(node.isLinked())),
				fWhiteCharConverter);

		if (node.getLink() != null) {
			encodeAndAddAttribute(
					targetParameterElement, 
					new Attribute(PARAMETER_LINK_ATTRIBUTE_NAME, node.getLink().getQualifiedName()), 
					fWhiteCharConverter);
		}

		for (ChoiceNode child : node.getRealChoices()) {
			targetParameterElement.appendChild((Element)child.accept(this));
		}

		return targetParameterElement;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {

		Element targetGlobalParamElement = createAbstractElement(getParameterNodeName(), node);

		addParameterProperties(node, targetGlobalParamElement);
		appendTypeComments(targetGlobalParamElement, node);

		encodeAndAddAttribute(
				targetGlobalParamElement, 
				new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()), 
				fWhiteCharConverter);

		for (ChoiceNode child : node.getChoices()) {
			targetGlobalParamElement.appendChild((Element)child.accept(this));
		}
		return targetGlobalParamElement;
	}

	private void addParameterProperties(AbstractParameterNode abstractParameterNode, Element targetElement) {

		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_ACTION, abstractParameterNode, targetElement);
	}

	private void addParameterProperty(
			NodePropertyDefs.PropertyId propertyId, 
			AbstractParameterNode abstractParameterNode, 
			Element targetElement) {

		String value = abstractParameterNode.getPropertyValue(propertyId);
		if (value == null) {
			return;
		}
		appendProperty(getPropertyName(propertyId), getPropertyType(propertyId), value, targetElement);
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {

		Element targetTestCaseElement = new Element(TEST_CASE_NODE_NAME);

		encodeAndAddAttribute(
				targetTestCaseElement, 
				new Attribute(TEST_SUITE_NAME_ATTRIBUTE, node.getName()), 
				fWhiteCharConverter);

		appendComments(targetTestCaseElement, node);

		for (ChoiceNode testParameter : node.getTestData()) {
			if (testParameter.getParameter() != null && node.getMethodParameter(testParameter).isExpected()) {

				Element expectedParameterElement = new Element(EXPECTED_PARAMETER_NODE_NAME);
				Attribute expectedValueAttribute = new Attribute(VALUE_ATTRIBUTE_NAME, testParameter.getValueString());
				encodeAndAddAttribute(expectedParameterElement, expectedValueAttribute, fWhiteCharConverter);

				targetTestCaseElement.appendChild(expectedParameterElement);
			}
			else{
				Element testParameterElement = new Element(TEST_PARAMETER_NODE_NAME);
				Attribute choiceNameAttribute = new Attribute(getChoiceAttributeName(), testParameter.getQualifiedName());

				encodeAndAddAttribute(testParameterElement, choiceNameAttribute, fWhiteCharConverter);
				targetTestCaseElement.appendChild(testParameterElement);
			}
		}

		return targetTestCaseElement;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception{

		Element targetConstraintElement = createAbstractElement(CONSTRAINT_NODE_NAME, node);

		AbstractStatement premise = node.getConstraint().getPremise();
		AbstractStatement consequence = node.getConstraint().getConsequence();

		Element premiseElement = new Element(CONSTRAINT_PREMISE_NODE_NAME);
		premiseElement.appendChild((Element)premise.accept(
				new XomStatementBuilder(
						getStatementParameterAttributeName(),
						getStatementChoiceAttributeName())));

		Element consequenceElement = new Element(CONSTRAINT_CONSEQUENCE_NODE_NAME);
		consequenceElement.appendChild((Element)consequence.accept(
				new XomStatementBuilder(
						getStatementParameterAttributeName(),
						getStatementChoiceAttributeName())));

		targetConstraintElement.appendChild(premiseElement);
		targetConstraintElement.appendChild(consequenceElement);

		return targetConstraintElement;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {

		Element targetChoiceElement = createAbstractElement(getChoiceNodeName(), node);

		String value = node.getValueString();
		//remove disallowed XML characters
		String xml10pattern = "[^"
				+ "\u0009\r\n"
				+ "\u0020-\uD7FF"
				+ "\uE000-\uFFFD"
				+ "\ud800\udc00-\udbff\udfff"
				+ "]";
		String legalValue = value.replaceAll(xml10pattern, "");

		encodeAndAddAttribute(targetChoiceElement, new Attribute(VALUE_ATTRIBUTE, legalValue), fWhiteCharConverter);

		boolean isRandomizedValue = ((ChoiceNode)node).isRandomizeValue();
		targetChoiceElement.addAttribute(new Attribute(NODE_IS_RADOMIZED_ATTRIBUTE, String.valueOf(isRandomizedValue)));

		
		for (String label : node.getLabels()) {
			Element labelElement = new Element(LABEL_NODE_NAME);
			encodeAndAddAttribute(labelElement, new Attribute(LABEL_ATTRIBUTE_NAME, label), fWhiteCharConverter);
			targetChoiceElement.appendChild(labelElement);
		}

		for (ChoiceNode child : node.getChoices()) {
			targetChoiceElement.appendChild((Element)child.accept(this));
		}

		return targetChoiceElement;
	}
	
	private Element createAbstractElement(String nodeTag, AbstractNode node) {

		Element targetAbstractElement = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		encodeAndAddAttribute(targetAbstractElement, nameAttr, fWhiteCharConverter);
		appendComments(targetAbstractElement, node);
		
		return targetAbstractElement;
	}

	private Element appendComments(Element element, AbstractNode node) {
		if (node.getDescription() != null) {
			Element commentsBlock = new Element(COMMENTS_BLOCK_TAG_NAME);
			Element basicComments = new Element(BASIC_COMMENTS_BLOCK_TAG_NAME);

			basicComments.appendChild(fWhiteCharConverter.encode(node.getDescription()));
			commentsBlock.appendChild(basicComments);
			element.appendChild(commentsBlock);
			return commentsBlock;
		}
		return null;
	}

	private void appendTypeComments(Element element, MethodParameterNode node) {

		if (node.isLinked() == false) {
			appendTypeComments(element, (AbstractParameterNode)node);
		}
	}

	private void appendTypeComments(Element element, AbstractParameterNode node) {

		Elements commentElements = element.getChildElements(COMMENTS_BLOCK_TAG_NAME);
		Element commentElement;

		if (commentElements.size() > 0) {
			commentElement = commentElements.get(0);
		}else{
			commentElement = new Element(COMMENTS_BLOCK_TAG_NAME);
			element.appendChild(commentElement);
		}

		Element typeComments = new Element(TYPE_COMMENTS_BLOCK_TAG_NAME);

		typeComments.appendChild(fWhiteCharConverter.encode(node.getTypeComments()));
		commentElement.appendChild(typeComments);
	}

	public static void encodeAndAddAttribute(
			Element element, Attribute attribute, WhiteCharConverter whiteCharConverter) {

		attribute.setValue(whiteCharConverter.encode(attribute.getValue()));
		element.addAttribute(attribute);
	}
	
	public static void addIsRandomizedValue(Element element, Attribute attribute) {
		attribute.setValue(Boolean.FALSE.toString());
		element.addAttribute(attribute);
	}

	private String getPropertyName(NodePropertyDefs.PropertyId propertyId) {

		return NodePropertyDefs.getPropertyName(propertyId);
	}

	private String getPropertyType(NodePropertyDefs.PropertyId propertyId) {

		return NodePropertyDefs.getPropertyType(propertyId);
	}	

	private void appendProperty(String key, String type, String value, Element targetElement) {

		Element propertiesBlock = getPropertiesBlock(targetElement);
		Element propertyElement = createCommonPropertyElement(key, type, value);

		propertiesBlock.appendChild(propertyElement);
	}	

	private Element getPropertiesBlock(Element parentElement) {

		Elements propiertiesBlocks = parentElement.getChildElements(PROPERTIES_BLOCK_TAG_NAME);

		if (propiertiesBlocks.size() == 0) {
			Element propertiesBlock = new Element(PROPERTIES_BLOCK_TAG_NAME);
			parentElement.appendChild(propertiesBlock);
			return propertiesBlock;
		}

		return propiertiesBlocks.get(0);
	}

	private Element createCommonPropertyElement(String name, String type, String value) {

		Element targetPropertyElement = new Element(PROPERTY_TAG_NAME);

		Attribute attributeName = new Attribute(PROPERTY_ATTRIBUTE_NAME, name);
		targetPropertyElement.addAttribute(attributeName);

		Attribute attributeType = new Attribute(PROPERTY_ATTRIBUTE_TYPE, type);
		targetPropertyElement.addAttribute(attributeType);

		Attribute attributeValue = new Attribute(PROPERTY_ATTRIBUTE_VALUE, value);
		targetPropertyElement.addAttribute(attributeValue);

		return targetPropertyElement;
	}

}
