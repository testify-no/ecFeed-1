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

public class SourceCodeTextImplementer {

	public static String correctItemsForEnumWithStringConstructor(
			String enumFilePath,
			String oldContent, 
			List<ChoiceNode> choiceNodes) {

		String newContent = "package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\");\nEnum1(String value){}}";

		return newContent;
	}

}
