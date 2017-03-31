/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.List;

public class Culprit {

	private List<DimItem> fItems;
	private int fFailureCount;
	private int fOccurenceCount;
	
	public Culprit(List<DimItem> items, int failureCount, int occurenceCount){
		fItems = items;
		fFailureCount = failureCount;
		fOccurenceCount = occurenceCount;	
	}
	
	int getOccurenceCount(){
		return fOccurenceCount;
	}
	int getFailureCount(){
		return fFailureCount;
	}
	List<DimItem> getItem(){
		return fItems;
	}
	public int incrementFailures(){
		fFailureCount =+ 1;
		return fFailureCount;
	}
	public int incrementOccurenceCount(){
		fOccurenceCount =+ 1;
		return fOccurenceCount;
	}
}

