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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.generators.algorithms.Tuples;

public class Culprit {

	private List<DimItem> fItems;
	private int fFailureCount;
	private int fOccurenceCount;

	public Culprit(){
		fItems = new ArrayList<DimItem>();
		fFailureCount = 0;
		fOccurenceCount = 0;	
	}
	public Culprit(List<DimItem> tuple){
		this();
		this.fItems = tuple;
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
	public void incrementFailures(int FailureCount){
		fFailureCount = FailureCount + 1;
	}
	public void incrementOccurenceCount(int OccurenceCount){
		fOccurenceCount = OccurenceCount + 1;
	}
	public void aggregateOccurencesAndFailures(Culprit culpritToAggregate){
		incrementOccurenceCount(culpritToAggregate.getOccurenceCount());
		incrementFailures(culpritToAggregate.getFailureCount());
	}
	public boolean isTupleMatch(Culprit other){
		if(fItems.size() != other.fItems.size()){
			return false;
		}
		for(int index = 0; index < fItems.size(); index++){
			if(!fItems.get(index).isMatch(other.fItems.get(index))){
				return false;
			}
		}
		return true;
	}
}

