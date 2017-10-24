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

	// XMQX - static qualifier added
	public static class ErrorDescription {
		private AbstractNode fNode;
		private CheckErrorCode fErrorCode;

		// XMQX - we have to add another node fDuplicateNode to generate the message so that
		// duplicate nodes are easy to find
		public ErrorDescription(
				AbstractNode node, 
				// AbstractNode duplicateNode,
				CheckErrorCode errorCode) {
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

	// XMQX - this class has two responsibilities - divide into two separate classes + maybe plus a helper class
	// XMQX - static qualifier added
	public static class DuplicateChecker {

		private List<ClassNode> fClasses;
		private List<MethodNode> fMethods;

		public DuplicateChecker(List<ClassNode> classes, List<MethodNode> methods) {
			fClasses = classes;
			fMethods = methods;
		}

		public ErrorDescription checkForDuplicateClasses() {
			List<String> classNameList = makeClassNamesList();
			List<String> duplicatedNames = new ArrayList<String>();

			// XMQX - I would to sth. like this
			
			// Pair<ClassNode, ClassNode> duplicates = getFirstPairOfDuplicates(fClasses)
			// if (duplicates != null) {
			//      return new ErrorDescription(duplicates.getFirst(), duplicates.getSecond(), CheckErrorCode.DUPLICATE_CLASS_NAME);
		    // }
			
			if (hasDuplicate(classNameList)) { // XMQX - this is not necessary, 
				// it is enough to check if duplicatedNames.isEmpty()
				
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

			// XMQX - avoid nested if/for/it etc.
			// extract if (fMethods.get(i).. to a method e.g. compareTwoMethods(m1,m2);
			
			// XMQX -- nearly square complexity of this algorithm can be avoided
			// using sort + compare neighbours - but Ok this could be because 
			// there are not so many methods in the class
			for (int i = 0; i < fMethods.size()-1; i++) {
				for (int j = i+1; j < fMethods.size(); j++) {
					if (fMethods.get(i).getName().equals(fMethods.get(j).getName())) {
						// XMQX - too long line - around 100 is ok
						if (compareParametersTypes(fMethods.get(i).getParameterTypes(), fMethods.get(j).getParameterTypes())) {
							
							// XMQX - this will be: ErrorDescription(fMethods.get(i), fMethods.get(j), CheckErrorCode.DUPLICATE_METHOD_NAME);
							return new ErrorDescription(fMethods.get(i), CheckErrorCode.DUPLICATE_METHOD_NAME);
						}
					}
				}
			}
			return null;
		}

		// XMQX rename to areParameterTypesEqual - this is an exception which 
		// refers only to boolean methods - use are... or is... 
		private boolean compareParametersTypes(List<String> parameterTypes, List<String> parameterTypes2) {

			// XMQX - check obvious cases as early as possible (before creating the list)
			// if (parameterTypes.size() == parameterTypes2.size()) {
			//		return false;
			// }
			
			
			List<String> numericList = Arrays.asList("int", "double", "float", "long", "short", "byte");

			// XMQX - this should generate an exception (also move it to the first place in the method)
			if (parameterTypes == null && parameterTypes2 == null) {
				return true;
			}
			
			if (parameterTypes.size() == parameterTypes2.size()) {
				
				// MQMX - i think, we should not sort the parameters, just compare if 
				// the first parameteters match in both methods (eg. the one is int and another float etc.)
				// then the next and so on
				Collections.sort(parameterTypes);
				Collections.sort(parameterTypes2);
				if (parameterTypes.equals(parameterTypes2)) {
					return true;
				}

				// XMQX
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

		
		// XMQX - it is not necessary to get all the duplicates - just the first pair
		private List<String> getDuplicate(List<String> list) {

			final List<String> duplicatedNames = new ArrayList<String>();
			
			Set<String> set = new HashSet<String>(){

				private static final long serialVersionUID = 5714004217588411946L;

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
