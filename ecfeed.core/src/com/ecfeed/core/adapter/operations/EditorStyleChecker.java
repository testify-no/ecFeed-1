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
import java.util.Comparator;
import java.util.List;

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
import com.ecfeed.core.utils.Pair;

public class EditorStyleChecker implements IModelVisitor{

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		DuplicatedClassesChecker duplicateChecker = new DuplicatedClassesChecker(rootNode.getClasses());

		ErrorDescription errorDescription = duplicateChecker.checkForDuplicateClasses();
		if (errorDescription != null) {
			return errorDescription;
		}

		List<ClassNode> classesInRootNode = rootNode.getClasses();
		List<ClassNode> classesInRootNodeClone = new ArrayList<ClassNode>(classesInRootNode);

		for (ClassNode classNode: classesInRootNodeClone) {
			System.out.println(classNode.getName());
			errorDescription = (ErrorDescription) classNode.accept(this);
			if (errorDescription != null) {
				System.out.println(errorDescription.toString());
				return errorDescription;
			}
		}
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {

		RootNode root = node.getRoot();

		DuplicatedClassesChecker duplicateChecker = new DuplicatedClassesChecker(root.getClasses());
		ErrorDescription errorDescription = duplicateChecker.checkForDuplicateClasses();
		if (errorDescription != null) {
			return errorDescription;
		}

		List<MethodNode> methodsInNode = node.getMethods();
		List<MethodNode> methodsInNodeClone = new ArrayList<MethodNode>(methodsInNode);
		for (MethodNode method: methodsInNodeClone) {
			errorDescription = (ErrorDescription) method.accept(this);
			System.out.println(methodsInNode.size());
			if (errorDescription != null) {
				System.out.println(errorDescription.toString());
				return errorDescription;
			}
		}
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {

		ClassNode parent = node.getClassNode();

		DuplicatedMethodsChecker duplicateChecker =
				new DuplicatedMethodsChecker(parent.getMethods());
		ErrorDescription errorDescription =
				duplicateChecker.checkForDuplicateMethods();
		if (errorDescription != null) {
			System.out.println(errorDescription.toString());
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
	
	public static ErrorDescription canSwitchToSimpleModel(RootNode rootNode) throws Exception {
		
		ErrorDescription errorDescription = (ErrorDescription) rootNode.accept(new EditorStyleChecker());
		
		return errorDescription;
	}

	public static class ErrorDescription {
		private AbstractNode fNode;
		private AbstractNode fDuplicateNode;
		private CheckErrorCode fErrorCode;

		public ErrorDescription(
				AbstractNode node, 
				AbstractNode duplicateNode,
				CheckErrorCode errorCode) {

			fNode = node;
			fDuplicateNode = duplicateNode;
			fErrorCode = errorCode;
		}
		
		public String createErrorMessage() {
			//TODO create a better error message.
			return fErrorCode + " " + fNode + " " + "is a duplicate of " + fDuplicateNode + " ";
		}

		public CheckErrorCode getErrorCode() {
			return fErrorCode;
		}

		public AbstractNode getDuplicatedNode() {
			return fDuplicateNode;
		}

		public AbstractNode getErrorNode() {
			return fNode;
		}
	}

	public enum CheckErrorCode {
		DUPLICATE_CLASS_NAME, DUPLICATE_METHOD_NAME;	
	}

	public static class DuplicatedMethodsChecker {
		private List<MethodNode> fMethods;

		public DuplicatedMethodsChecker(List<MethodNode> methods) {
			fMethods = methods;
		}

		public ErrorDescription checkForDuplicateMethods() {

			Collections.sort(fMethods, new Comparator<MethodNode>() {

				@Override
				public int compare(MethodNode node1, MethodNode node2) {
					return node1.getName().compareTo(node2.getName());
				}
			});

			for (int i = 0; i < fMethods.size() - 1; i++) {
				MethodNode methodNode1 = fMethods.get(i);
				MethodNode methodNode2 = fMethods.get(i + 1);
				List<String> method1ParameterTypes = fMethods.get(i).getParameterTypes();
				List<String> method2ParameterTypes = fMethods.get(i).getParameterTypes();

				if (areMethodNamesEqual(methodNode1, methodNode2)) {
					if (areParameterTypesEqual(method1ParameterTypes, method2ParameterTypes)) {
						return new ErrorDescription(methodNode1, methodNode2, 
								CheckErrorCode.DUPLICATE_METHOD_NAME);
					}
				}
			}
			return null;
		}

		private boolean areMethodNamesEqual(MethodNode methodNode, MethodNode methodNode2) {
			if (methodNode.getName().equals(methodNode2.getName())) {
				return true;
			}
			return false;
		}

		private boolean areParameterTypesEqual(List<String> parameterTypes, List<String> parameterTypes2) {

			if (parameterTypes.size() != parameterTypes2.size()) {
				return false;
			}

			List<String> numericList = Arrays.asList("int", "double", "float", "long", "short", "byte");			

			if (parameterTypes.size() == parameterTypes2.size()) {
				Collections.sort(parameterTypes);
				Collections.sort(parameterTypes2);
				if (parameterTypes.equals(parameterTypes2)) {
					return true;
				}

				if (!Collections.disjoint(parameterTypes, numericList) &&
						!Collections.disjoint(parameterTypes2, numericList)) {
					return true;	
				}
			}
			return false;
		}
	}
	public static class DuplicatedClassesChecker {

		private List<ClassNode> fClasses;


		public DuplicatedClassesChecker(List<ClassNode> classes) {
			fClasses = classes;
		}

		public ErrorDescription checkForDuplicateClasses() {

			Pair<ClassNode, ClassNode> duplicates = getFirstPairOfDuplicates();
			if (duplicates != null) {
				return new ErrorDescription(duplicates.getFirst(), duplicates.getSecond(), 
						CheckErrorCode.DUPLICATE_CLASS_NAME);
			}
			return null;
		}

		private Pair<ClassNode, ClassNode> getFirstPairOfDuplicates() {

			Collections.sort(fClasses, new Comparator<ClassNode>() {

				@Override
				public int compare(ClassNode node1, ClassNode node2) {
					return node1.getName().compareTo(node2.getName());
				}
			});

			for (int index = 0; index < fClasses.size() - 1; index++) {
				if (fClasses.get(index).getName().equals(fClasses.get(index + 1).getName())) {
					Pair<ClassNode, ClassNode> duplicatedpairs = 
							new Pair<ClassNode, ClassNode>(fClasses.get(index), fClasses.get(index + 1));
					return duplicatedpairs;
				}
			}
			return null;
		}
	}
}
