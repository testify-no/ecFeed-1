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

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.IModelSerializer;

public class EctSerializer implements IModelSerializer{

	private OutputStream fOutputStream;
	private XomBuilder fXomBuilder;

	public EctSerializer(OutputStream ostream, int modelVersion){
		fOutputStream = ostream;
		fXomBuilder = XomBuilderFactory.createXomBuilder(modelVersion);
	}

	@Override
	public Object serialize(RootNode node) throws Exception{
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(ClassNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(MethodNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(MethodParameterNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(GlobalParameterNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(TestCaseNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(ConstraintNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	@Override
	public Object serialize(ChoiceNode node) throws Exception {
		Element element = (Element)node.accept(fXomBuilder);
		writeDocument(element);
		return null;
	}

	private void writeDocument(Element element) throws IOException {
		Document document = new Document(element);
		Serializer serializer = new Serializer(fOutputStream);
		// Uncomment for pretty formatting. This however will affect
		// whitespaces in the document's ... infoset
		serializer.setIndent(4);
		serializer.write(document);
	}

}
