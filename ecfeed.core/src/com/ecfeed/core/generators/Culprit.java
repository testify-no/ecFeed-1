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

public class Culprit {

	private List<DimItem> fTestInput;
	private int fOccurenceCount = 0;	
	private int fFailureCount = 0;


	public Culprit(List<DimItem> testInput) {
		this(testInput, 0, 0);		
	}

	public Culprit(List<DimItem> testInput, int occurenceCount, int failureCount) {
		fTestInput = testInput;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
	}

	public Culprit(DimItem[] testInput) {
		this(testInput, 0, 0);		
	}

	public Culprit(DimItem[] testInput, int occurenceCount, int failureCount) {

		List<DimItem> testInputList = new ArrayList<DimItem>();

		for (DimItem dimItem : testInput) {
			testInputList.add(dimItem);
		}

		fTestInput = testInputList;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
	}	

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("[occurences:");
		sb.append(fOccurenceCount);
		sb.append(", fails:");
		sb.append(fFailureCount);
		sb.append(", items:[");

		boolean firstTime = true;

		for (DimItem dimItem : fTestInput) {

			if (!firstTime) {
				sb.append(", ");
			}

			sb.append(dimItem.toString());
			firstTime = false;
		}

		sb.append("]");
		sb.append("]");

		return sb.toString();
	}

	public static int compareForSort(Culprit culprit1, Culprit culprit2) {

		if (culprit1.fTestInput.size() > culprit2.fTestInput.size()) {
			return -1;
		}

		if (culprit1.fTestInput.size() < culprit2.fTestInput.size()) {
			return 1;
		}    	

		int compareResult;
		int size = culprit1.fTestInput.size();

		for (int index = 0; index < size; index++) {

			DimItem dimItem1 = culprit1.fTestInput.get(index);
			DimItem dimItem2 = culprit2.fTestInput.get(index);

			compareResult = DimItem.compareForSort(dimItem1, dimItem2);

			if (compareResult != 0) {
				return compareResult;
			}
		}

		return 0;
	}

	int getOccurenceCount() {
		return fOccurenceCount;
	}

	int getFailureCount() {
		return fFailureCount;
	}

	List<DimItem> getItem() {
		return fTestInput;
	}

	public void incrementFailures(int failureCount) {
		fFailureCount += failureCount;
	}

	public void incrementOccurenceCount(int occurenceCount) {
		fOccurenceCount += occurenceCount;
	}

	public void aggregateOccurencesAndFailures(Culprit culpritToAggregate) {
		incrementOccurenceCount(culpritToAggregate.getOccurenceCount());
		incrementFailures(culpritToAggregate.getFailureCount());
	}

	public boolean isTupleMatch(Culprit other) {
		if(fTestInput.size() != other.fTestInput.size()) {
			return false;
		}

		for(int index = 0; index < fTestInput.size(); index++) {
			if(!fTestInput.get(index).isMatch(other.fTestInput.get(index))) {
				return false;
			}
		}
		return true;
	}

	public boolean isMatch(Culprit other) {

		if (other.fOccurenceCount != fOccurenceCount) {
			return false;
		}

		if (other.fFailureCount != fFailureCount) {
			return false;
		}

		if (!isTupleMatch(other)) {
			return false;
		}

		return true;
	}

	public Culprit makeClone() {

		return new Culprit(fTestInput, fOccurenceCount, fFailureCount);
	}

}

