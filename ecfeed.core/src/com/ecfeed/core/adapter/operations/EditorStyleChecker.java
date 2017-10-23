/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;

public class EditorStyleChecker implements IModelVisitor{

	@Override
	public Object visit(RootNode rootNode) throws Exception {
		
		DuplicateChecker duplicateChecker = new DuplicateChecker(rootNode.getClasses(), null);
		
		ErrorDescription errorDescription = duplicateChecker.checkForDuplicateClasses();
		if (errorDescription != null) {
			return errorDescription;
		}

		List<ClassNode> classesInRootNode = rootNode.getClasses();
		for (ClassNode classNode: classesInRootNode) {
			errorDescription = (ErrorDescription) classNode.accept(this);
			if (errorDescription != null) {
				return errorDescription;
			}
		}
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {

		RootNode root = node.getRoot();
		DuplicateChecker duplicateChecker = new DuplicateChecker(root.getClasses(), null);
		ErrorDescription errorDescription = duplicateChecker.checkForDuplicateClasses();
		if (errorDescription != null) {
			return errorDescription;
		}

		List<MethodNode> methodsInNode = node.getMethods();
		for (MethodNode method: methodsInNode) {
			errorDescription = (ErrorDescription) method.accept(this);
			if (errorDescription != null) {
				return errorDescription;
			}
		}
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		
		ClassNode parent = node.getClassNode();
		DuplicateChecker duplicateChecker = new DuplicateChecker(null, parent.getMethods());
		ErrorDescription errorDescription = duplicateChecker.checkForDuplicateMethods();
		if (errorDescription != null) {
			return errorDescription;
		}		
		return null;
	}

	@Override
	public Object visit(MethodParameterNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		return null;
	}

	public class ErrorDescription {
		private AbstractNode fNode;
		private CheckErrorCode fErrorCode;

		public ErrorDescription(AbstractNode node, CheckErrorCode errorCode) {
			fNode = node;
			fErrorCode = errorCode;
		}

		public CheckErrorCode getErrorCode() {
			return fErrorCode;
		}

		public AbstractNode getErrorNode() {
			return fNode;
		}
	}

	public enum CheckErrorCode {
		DUPLICATE_CLASS_NAME, DUPLICATE_METHOD_NAME;	
	}

	public class DuplicateChecker {

		private List<ClassNode> fClasses;
		private List<MethodNode> fMethods;

		public DuplicateChecker(List<ClassNode> classes, List<MethodNode> methods) {
			fClasses = classes;
			fMethods = methods;
		}

		public ErrorDescription checkForDuplicateClasses() {
			List<String> classNameList = makeClassNamesList();
			List<String> duplicatedNames = new ArrayList<String>();

			if (hasDuplicate(classNameList)) {
				duplicatedNames = getDuplicate(classNameList);

				for (ClassNode cl: fClasses) {
					if (cl.getName().equals(duplicatedNames.get(0))) {
						return new ErrorDescription(cl, CheckErrorCode.DUPLICATE_CLASS_NAME);
					}
				}
			}
			return null;
		}

		public ErrorDescription checkForDuplicateMethods() {

			for (int i = 0; i < fMethods.size()-1; i++) {
				for (int j = i+1; j < fMethods.size(); j++) {
					if (fMethods.get(i).getName().equals(fMethods.get(j).getName())) {
						if (compareParametersTypes(fMethods.get(i).getParameterTypes(), fMethods.get(j).getParameterTypes())) {
							return new ErrorDescription(fMethods.get(i), CheckErrorCode.DUPLICATE_METHOD_NAME);
						}
					}
				}
			}
			return null;
		}

		private boolean compareParametersTypes(List<String> parameterTypes, List<String> parameterTypes2) {

			List<String> numericList = Arrays.asList("int", "double", "float", "long", "short", "byte");

			if (parameterTypes == null && parameterTypes2 == null) {
				return true;
			}
			
			if (parameterTypes.size() == parameterTypes2.size()) {
				
				Collections.sort(parameterTypes);
				Collections.sort(parameterTypes2);
				if (parameterTypes.equals(parameterTypes2)) {
					return true;
				}

				if (!Collections.disjoint(parameterTypes, numericList) && !Collections.disjoint(parameterTypes2, numericList)) {
					return true;	
				}
			}
			return false;
		}

		private List<String> makeClassNamesList() {
			
			List<String> classNames = new ArrayList<String>();
			for (ClassNode cl: fClasses) {
				classNames.add(cl.getName());
			}
			return classNames;
		}

		private List<String> getDuplicate(List<String> list) {

			final List<String> duplicatedNames = new ArrayList<String>();
			Set<String> set = new HashSet<String>(){

				@Override
				public boolean add(String name){
					if (contains(name)) {
						duplicatedNames.add(name);
					}
					return super.add(name);
				}
			};

			for (String name: list) {
				set.add(name);
			}

			return duplicatedNames;
		}

		private boolean hasDuplicate(List<String> list) {

			if (getDuplicate(list).isEmpty()) {
				return false;	
			}
			return true;	
		}
	}
}
