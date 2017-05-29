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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestResultsAnalysis {
	
	private static class ExtCulprit {
		
		private Culprit fCulprit;
		private int fFailureIndex;
		
		public ExtCulprit(Culprit culprit) {
			
			fCulprit = culprit;
			fFailureIndex = 0;
		}
		
		public boolean isTupleMatch(ExtCulprit other) {
			
			if (fCulprit.isTupleMatch(other.fCulprit)) {
				return true;
			}
			
			return false;
		}
		
		public void aggregateOccurencesAndFailures(ExtCulprit extCulpritToAggregate) {
			
			fCulprit.aggregateOccurencesAndFailures(extCulpritToAggregate.fCulprit);
		}
		
		@Override
		public String toString() {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("[failureIndex:");
			sb.append(fFailureIndex);
			sb.append(", culprit:");
			sb.append(fCulprit.toString());
			sb.append("]");
			
			return sb.toString();
		}
	}
	
	private List<ExtCulprit> fExtCulprits = new ArrayList<ExtCulprit>();

	public void aggregateCulprit(Culprit culpritToAggregate) {
		
		ExtCulprit extCulpritToAggregate = new ExtCulprit(culpritToAggregate);
		
		ExtCulprit extCulpritFromList = findCulpritByTuple(extCulpritToAggregate);

		if (extCulpritFromList == null) {
			fExtCulprits.add(extCulpritToAggregate);
		} else {
			extCulpritFromList.aggregateOccurencesAndFailures(extCulpritToAggregate);
		}
	}

	private ExtCulprit findCulpritByTuple(ExtCulprit culpritWithTupleToFind) {

		for (ExtCulprit extCulprit: fExtCulprits) {
			if (extCulprit.isTupleMatch(culpritWithTupleToFind)) {
				return extCulprit;
			}
		}
		return null;
	}

	public int getCulpritCount() {
		
		return fExtCulprits.size();
	}

	public Culprit getCulprit(int index) {
		
		ExtCulprit extCulprit = fExtCulprits.get(index);
		
		return extCulprit.fCulprit.makeClone();
	}

	public boolean containsCulprit(Culprit culprit) {

		int culpritCount = fExtCulprits.size();

		for (int culpritIndex = 0; culpritIndex < culpritCount; culpritIndex++) {
			ExtCulprit extCulpritFromList = fExtCulprits.get(culpritIndex);

			if (culprit.isMatch(extCulpritFromList.fCulprit)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Test results analysis:\n");
		
		for (ExtCulprit extCulprit : fExtCulprits) {
			
			sb.append("  ");
			sb.append(extCulprit.toString());
			sb.append("\n");
		}
		
		sb.append("-------\n");
		
		return sb.toString();
	}
	
	public void calculateFailureIndexes() {
		
		int total = fExtCulprits.size();
		
		for (ExtCulprit extCulprit : fExtCulprits) {
			
			int occurences = extCulprit.fCulprit.getOccurenceCount();
			int failures = extCulprit.fCulprit.getFailureCount();
			
			int failsByOccurs = 100 * failures / occurences;
			int occurencesByTotal = 100 * occurences / total;
			
			extCulprit.fFailureIndex = 100 * failsByOccurs + occurencesByTotal; 
		}

		Collections.sort(fExtCulprits, new FailureIndexComparator());
	}
	
	class FailureIndexComparator implements Comparator<ExtCulprit> {
		
	    @Override
	    public int compare(ExtCulprit extCulprit1, ExtCulprit extCulprit2) {

	        if (extCulprit1.fFailureIndex > extCulprit2.fFailureIndex) {
	        	return -1;
	        }
	        
	        if (extCulprit1.fFailureIndex < extCulprit2.fFailureIndex) {
	        	return 1;
	        }	        
	        
	        return Culprit.compareForSort(extCulprit1.fCulprit, extCulprit2.fCulprit);
	    }
	}


}

