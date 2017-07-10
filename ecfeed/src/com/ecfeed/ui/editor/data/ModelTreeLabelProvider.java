/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.data;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.ImageManager;


public class ModelTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element){
		if(element instanceof AbstractNode){
			try {
				return (String)((AbstractNode)element).accept(new TextProvider());
			} catch(Exception e) { 
				SystemLogger.logCatch(e.getMessage());
			}
		}
		return null;
	}

	@Override
	public Image getImage(Object element){
		if(element instanceof AbstractNode){
			try {
				return (Image)((AbstractNode)element).accept(new ImageProvider());
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
		}
		return getImageFromFile("sample.png");
	}

	private Image getImageFromFile(String file) {
		return ImageManager.getInstance().getImage(file);
	}
	
	private class TextProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return node.toString();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return node.toString();
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return MethodNodeHelper.simplifiedToString(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			String result = ModelHelper.convertParameterToSimplifiedString(node);
			if(node.isLinked()){
				result += "[LINKED]->" + node.getLink().getQualifiedName();
			}
			return result;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return ModelHelper.convertParameterToSimplifiedString(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return node.toString();
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return node.toString();
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return node.toString();
		}
	}
	
	private class ImageProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return getImageFromFile("root_node.png");
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return getImageFromFile("class_node.png");
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return getImageFromFile("method_node.png");
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return getImageFromFile("parameter_node.png");
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return getImageFromFile("parameter_node.png");
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return getImageFromFile("test_case_node.png");
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return getImageFromFile("constraint_node.png");
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return getImageFromFile("choice_node.png");
		}

	}

}
