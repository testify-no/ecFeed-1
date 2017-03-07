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



public class ClassNodeHelper  {

	public static String getLocalName(ClassNode node){
		return ModelHelper.convertToLocalName(node.getName());
	}
	
	public static String getPackageName(ClassNode classNode){
		return ModelHelper.getPackageName(classNode.getName());
	}
	
	public static String getQualifiedName(ClassNode classNode){
		return classNode.getName();
	}

}
