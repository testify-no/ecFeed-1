/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common.utils;

import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.RegexHelper;

public class SourceCodeTextImplementer {

	public static String correctItemsForEnumWithStringConstructor(
			String oldContent, 
			List<ChoiceNode> choiceNodes) throws EcException {

		if (oldContent == null) {
			EcException.report("Old content must not be empty.");
		}

		if (choiceNodes == null || choiceNodes.isEmpty()) {
			return oldContent;
		}

		String newContent = oldContent;

		for (ChoiceNode choiceNode : choiceNodes) {
			newContent = correctOneChoice(choiceNode, newContent);
		}

		return newContent;
	}

	private static String correctOneChoice(ChoiceNode choiceNode, String oldContent) throws EcException {

		String choiceValue = choiceNode.getValueString();
		String itemRegex = choiceValue + "\\s*[;,]";

		String oldValue = RegexHelper.getOneMatchingSubstring(oldContent, itemRegex);
		if (oldValue == null) {
			return addItemWithStringConstructor(choiceValue, oldContent);
		}

		return addStringConstructorToSimpleItem(choiceValue, oldValue, oldContent);
	}

	private static String addStringConstructorToSimpleItem(String choiceName, String oldValue, String oldContent) {

		String newValue = createItemWithStringConstructorAndSeparator(choiceName, oldValue);

		return oldContent.replace(oldValue, newValue);
	}

	private static String addItemWithStringConstructor(String choiceName, String oldContent) throws EcException {

		int indexOfSemicolon = getIndexOfEnumSemicolon(oldContent);

		if (indexOfSemicolon == -1) {
			EcException.report("Can not correct enum file.");
		}

		String oldPart1 = oldContent.substring(0, indexOfSemicolon);
		String oldPart2 = oldContent.substring(indexOfSemicolon);

		String oldValue = oldContent.substring(indexOfSemicolon, indexOfSemicolon+1);
		String newValue = ", " + createItemWithStringConstructorAndSeparator(choiceName, oldValue);

		String newPart2 = oldPart2.replace(oldValue, newValue);

		return oldPart1 + newPart2;
	}

	private static String createItemWithStringConstructorAndSeparator(
			String choiceName, String oldValue) {

		String newValue = createItemWithStringConstructor(choiceName);
		newValue = addSeparators(oldValue, newValue);

		return newValue;
	}

	private static int getIndexOfEnumSemicolon(String content) {

		int indexOfEnumTag = content.indexOf("enum");

		if (indexOfEnumTag == -1) {
			return -1;
		}

		int indexOfStartingBrace = content.indexOf("{", indexOfEnumTag);

		if (indexOfStartingBrace == -1) {
			return -1;
		}

		return content.indexOf(";", indexOfStartingBrace);
	}

	private static String createItemWithStringConstructor(String choiceName) {

		return choiceName + "(\"" + choiceName + "\")";
	}


	private static String addSeparators(String oldValue, String newValue) {

		if (oldValue.endsWith(",")) {
			return newValue + ",";
		}

		if (oldValue.endsWith(";")) {
			return newValue + ";";
		}

		return oldValue;
	}

}
