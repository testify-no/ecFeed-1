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
			return null;
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

		String choiceName = choiceNode.getName();

		String itemRegex = choiceName + "\\s*[;,]";

		String oldValue = RegexHelper.getOneMatchingSubstring(oldContent, itemRegex);
		if (oldValue == null) {
			EcException.report("Can not find choice: " + choiceName);
		}

		String newValue = choiceNode.getName() + "(\"" + choiceName + "\")";

		if (oldValue.endsWith(",")) {
			newValue = newValue + ",";
		}

		if (oldValue.endsWith(";")) {
			newValue = newValue + ";";
		}

		String newContent = oldContent.replace(oldValue, newValue);
		return newContent;
	}

}
