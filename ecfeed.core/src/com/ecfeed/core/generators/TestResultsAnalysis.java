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

public class TestResultsAnalysis {

	private List<Culprit> fCulprits = new ArrayList<Culprit>();

	public void aggregateCulprit(Culprit culpritToAggregate) {

		Culprit culpritFromList = findCulpritByTuple(culpritToAggregate);

		if (culpritFromList == null) {
			fCulprits.add(culpritToAggregate);
		} else {
			culpritFromList.aggregateOccurencesAndFailures(culpritToAggregate);
		}
	}

	private Culprit findCulpritByTuple(Culprit culpritWithTupleToFind) {

		for (Culprit culprit: fCulprits) {
			if (culprit.isTupleMatch(culpritWithTupleToFind)) {
				return culprit;
			}
		}
		return null;
	}

	public int getCulpritCount() {
		return fCulprits.size();
	}

	public Culprit getCulprit(int index) {
		return fCulprits.get(index).makeClone();
	}

	public boolean containsCulprit(Culprit culprit) {

		int culpritCount = fCulprits.size();

		for (int culpritIndex = 0; culpritIndex < culpritCount; culpritIndex++) {
			Culprit culpritFromList = fCulprits.get(culpritIndex);

			if (culprit.isMatch(culpritFromList)) {
				return true;
			}
		}

		return false;
	}

}

