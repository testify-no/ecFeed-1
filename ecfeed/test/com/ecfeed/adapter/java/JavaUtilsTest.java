/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.junit.CollectiveOnlineRunner;
import com.ecfeed.junit.annotations.Constraints;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.Generator;

@RunWith(CollectiveOnlineRunner.class)
@EcModel("test/com/ecfeed/adapter/java/JavaUtilsTest.ect")
@Generator(CartesianProductGenerator.class)
@Constraints(Constraints.ALL)
public class JavaUtilsTest {

	@Test
	public void isValidTypeNameTest(String packageName, String className, boolean valid){
//		packageName = "";
//		className = "int";
//		valid = false;
//		System.out.println("isValidTypeNameTest(" + packageName + ", " + className + ", " + valid + ")");
		String typeName;
		if(packageName == null || className == null){
			typeName = null;
		}
		else if(packageName.length() > 0){
			typeName = packageName + "." + className;
		}
		else{
			typeName = className;
		}
		boolean result = JavaLanguageHelper.isValidTypeName(typeName);
		assertEquals(valid, result);
	}

}
