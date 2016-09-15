/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.TestCaseNode;


public class SystemLoggerHelper {

	public static void printListOfChoices(String message, List<ChoiceNode> choices) {
		System.out.println("printArrayOfChoices 01 " + " - " + message + " ----------------------------------------------------------------");
		for (ChoiceNode choice : choices) {
			printChoice("", choice);
		}
	}

	public static void printChoice(String message, ChoiceNode choice) {
		if (message != null && message.length() > 0) {
			System.out.println("message: " + message);
		}
		System.out.println("hashCode: " + choice.hashCode());
		System.out.println("name: " + choice.getName());
		System.out.println("parameter: " + choice.getParameter().getName());
	}

	public static void printChoiceParentsNode(String message, ChoicesParentNode choicesParentNode) {
		if (message != null && message.length() > 0) {
			System.out.println("message: " + message);
		}
		System.out.println("hashCode: " + choicesParentNode.hashCode());
		System.out.println("class: " + choicesParentNode.getClass().getName());
		System.out.println("name: " + choicesParentNode.getName());
	}	

	public static void printChoiceWithParents(String message, ChoiceNode choice) {

		SystemLogger.logLine("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
		ChoicesParentNode current = choice;

		for(;;) {
			printChoiceParentsNode(message, current);

			AbstractNode parent = current.getParent();

			if (parent == null) {
				break;
			}

			if (!(parent instanceof ChoicesParentNode)) {
				break;
			}

			ChoicesParentNode choicesParent =	 (ChoicesParentNode)parent;
			current = choicesParent; 
		}
		SystemLogger.logLine("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}	

	private static AbstractNode findRoot(AbstractNode startNode) {

		AbstractNode node = startNode;

		for(int cnt = 0; cnt < 100; cnt++) {
			AbstractNode parent = node.getParent();
			if (parent == null) {
				return node;
			}

			node = parent;
		}

		return null;
	}

	public static void printModel(AbstractNode someNodeOfModel) {
		AbstractNode root = findRoot(someNodeOfModel);

		if (root == null) {
			System.out.println("ROOT NOT FOUND.");
		}
		System.out.println("Model BEG ---------------------------------------------------------");
		printChildren(root, 0);
		System.out.println("Model END ---------------------------------------------------------");
	}

	private static void printChildren(AbstractNode abstractNode, int indent) {

		printAbstractNode(abstractNode, indent);

		List<? extends AbstractNode> children = abstractNode.getChildren();

		if (children.size() == 0) {
			return;
		}

		for (AbstractNode child : children) {
			printChildren(child, indent + 4);
		}
	}

	private static void printAbstractNode(AbstractNode abstractNode, int indent) {

		String repeated = new String(new char[indent]).replace("\0", " ");

		System.out.println(repeated + abstractNode.getClass().getSimpleName() + ": " + abstractNode.getName());
		System.out.println(repeated + "        " + "*hashCode(): " + abstractNode.hashCode());

		if (abstractNode instanceof TestCaseNode) {
			printTestCaseNode((TestCaseNode)abstractNode, indent + 4);
		}
	}

	private static void printTestCaseNode(TestCaseNode testCaseNode, int indent) {
		List<ChoiceNode> choices = testCaseNode.getTestData();

		for (ChoiceNode choice : choices) {
			printAbstractNode(choice, indent);
		}
	}

}
