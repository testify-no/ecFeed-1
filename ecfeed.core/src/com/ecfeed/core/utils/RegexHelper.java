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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexHelper {

	public static List<String> getMatchingSubstrings(String sourceString, String regexPattern) {

		List<String> substrings = new ArrayList<String>();

		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(sourceString);

		while(matcher.find()) {
			substrings.add(matcher.group());
		}

		return substrings;
	}


	public static String getOneMatchingSubstring(String sourceString, String regexPattern) {

		List<String> substrings = getMatchingSubstrings(sourceString, regexPattern);

		if (substrings.size() == 1) {
			return substrings.get(0);
		}

		return null;
	}

}
