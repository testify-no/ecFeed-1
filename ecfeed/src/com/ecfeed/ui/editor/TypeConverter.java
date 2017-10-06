/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class TypeConverter {
	
	String fItem;
	String reversed;
	
	public TypeConverter(String item) {
		fItem = item;
		reversed = item;
	}
	
	public String convertToValidJaveIdentifier() {
		
		if (!JavaLanguageHelper.isValidJavaIdentifier(fItem)) {
			
			adjustUnvalidIdentifier();
			
			return fItem;
		}	
		return fItem;	
	}
	
	public void adjustKeywordName() {
		
		if (JavaLanguageHelper.isJavaKeyword(fItem)) {
			fItem = "_" + fItem;
		}
	}
	
	public void adjustNameStartingWithDigit() {
		
		if (Character.isDigit(fItem.charAt(0))) {
			fItem = "_" + fItem;
		}
	}
	
	public void adjustNameContainingSpaces() {
		
		fItem = fItem.replaceAll(" ", "_");
	}
	
	public void adjustUnvalidIdentifier() {
		
		adjustKeywordName();
		
		if (!fItem.matches(AdapterConstants.REGEX_JAVA_IDENTIFIER)) {
			
			adjustNameContainingSpaces();
			
			adjustNameStartingWithDigit();
		}
	}
	
	public String getString(){
		return fItem;
	}
	
	public String getReversedName(){
		
		fItem = fItem.replaceAll("_", " ");
		return fItem;
	}
	
	public static void main(String[] args){
		TypeConverter tc = new TypeConverter("3");
		System.out.println(tc.convertToValidJaveIdentifier());
		
		
		
	}

}
