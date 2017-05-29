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

	private List<Culprit> fCulprits = new ArrayList<Culprit>();

	public void aggregateCulprit(Culprit culpritToAggregate) {

		Culprit extCulpritFromList = findCulpritByTuple(culpritToAggregate);

		if (extCulpritFromList == null) {
			fCulprits.add(culpritToAggregate);
		} else {
			extCulpritFromList.aggregateOccurencesAndFailures(culpritToAggregate);
		}
	}

	public Culprit findCulpritByTuple(Culprit culpritWithTupleToFind) {

		for (Culprit extCulprit: fCulprits) {
			if (extCulprit.isTupleMatch(culpritWithTupleToFind)) {
				return extCulprit;
			}
		}
		return null;
	}

	public int getCulpritCount() {

		return fCulprits.size();
	}

	public Culprit getCulprit(int index) {

		Culprit culprit = fCulprits.get(index);

		return culprit.makeClone();
	}

	public boolean containsCulprit(Culprit culprit) {

		int culpritCount = fCulprits.size();

		for (int culpritIndex = 0; culpritIndex < culpritCount; culpritIndex++) {
			Culprit culpritFromList = fCulprits.get(culpritIndex);

			if (culprit.isBasicMatch(culpritFromList)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("Test results analysis:\n");

		for (Culprit extCulprit : fCulprits) {

			sb.append("  ");
			sb.append(extCulprit.toString());
			sb.append("\n");
		}

		sb.append("-------\n");

		return sb.toString();
	}

	public void calculateFailureIndexes() {

		int total = fCulprits.size();

		for (Culprit culprit : fCulprits) {

			int occurences = culprit.getOccurenceCount();
			int failures = culprit.getFailureCount();

			int failsByOccurs = 100 * failures / occurences;
			int occurencesByTotal = 100 * occurences / total;

			culprit.setFailureIndex(100 * failsByOccurs + occurencesByTotal); 
		}

		Collections.sort(fCulprits, new FailureIndexComparator());
	}

	class FailureIndexComparator implements Comparator<Culprit> {

		@Override
		public int compare(Culprit culprit1, Culprit culprit2) {

			return Culprit.compareForSort(culprit1, culprit2);
		}
	}


}

