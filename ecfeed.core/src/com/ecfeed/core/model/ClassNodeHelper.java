/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

import com.ecfeed.core.adapter.java.Messages;



public class ClassNodeHelper {

	public static String getLocalName(ClassNode node) {

		return ModelHelper.convertToLocalName(node.getName());
	}

	public static String getPackageName(ClassNode classNode) {

		return ModelHelper.getPackageName(classNode.getName());
	}

	public static String getQualifiedName(ClassNode classNode) {

		return classNode.getName();
	}

	public static boolean validateNewMethodSignature(ClassNode parent, String methodName, List<String> argTypes) {

		return validateNewMethodSignature(parent, methodName, argTypes, null);
	}

	public static boolean validateNewMethodSignature(
			ClassNode parent, String methodName, List<String> argTypes, List<String> problems) {

		boolean valid = MethodNodeHelper.validateMethodName(methodName, problems);

		if (parent.getMethod(methodName, argTypes) != null) {

			valid = false;

			if (problems != null) {
				problems.add(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(parent.getName(), methodName));
			}
		}
		return valid;
	}

	public static String generateNewMethodName(ClassNode classNode, String startMethodName, List<String> argTypes) {

		if (validateNewMethodSignature(classNode, startMethodName, argTypes)) {
			return startMethodName;
		}

		for (int i = 0;   ; i++) {

			String newMethodName = startMethodName + String.valueOf(i);

			if (validateNewMethodSignature(classNode, newMethodName, argTypes)) {
				return newMethodName;
			}
		}
	}

}
