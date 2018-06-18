/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.generators.algorithms.Tuples;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.IntegerHolder;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;

public class CoverageCalculator {

	private int fNMax;
	private int[] fTuplesCovered;
	private int[] fTotalWork;
	private double[] fResults;
	private List<MethodParameterNode> fParameters;

	private List<List<ChoiceNode>> fInput;
	// The map of expected parameters default values. Said values are used to replace unique values in algorithm.
	private Map<Integer, ChoiceNode> fExpectedChoices;
	// The main map of covered tuples
	private List<Map<List<OrderedChoice>, Integer>> fTuples;
	// Test cases and suites (de)selected recently;
	private List<List<ChoiceNode>> fCurrentlyChangedCases;
	// If user added test cases = true; else we are substracting tuples;
	private boolean fAddingFlag;


	public CoverageCalculator(List<MethodParameterNode> parameters, int NMax) throws InterruptedException {

		fParameters = parameters;
		initialize(NMax);
	}

	public void initialize(int NMax) throws InterruptedException {
		fNMax = NMax;
		initalizeWithProgressDialog();
	}

	public void setCurrentChangedCases(Collection<TestCaseNode> testCases, boolean isAdding) {

		fAddingFlag = isAdding;
		if (testCases == null)
			fCurrentlyChangedCases = null;
		else
			fCurrentlyChangedCases = prepareCasesToAdd(testCases);
	}

	public boolean calculateCoverage() {

		// CurrentlyChangedCases are null if deselection left no test cases selected, 
		// hence we can just clear tuple map and set results to 0
		if (fCurrentlyChangedCases == null) {
			for (Map<List<OrderedChoice>, Integer> tupleMap : fTuples) {
				tupleMap.clear();
			}
			// set results to zero
			resetCoverageResults();
			fCurrentlyChangedCases = new ArrayList<>();
			return true;
		} 

		ProgressMonitorDialog progressDialog = 
				new ProgressMonitorDialog(Display.getDefault().getActiveShell());

		try {
			CalculatorRunnable runnable = new CalculatorRunnable();

			progressDialog.open();
			progressDialog.run(true, true, runnable);

			if (runnable.getIsCancelled()) {
				return false;
			} else {
				fCurrentlyChangedCases.clear();
				return true;
			}

		} catch (InvocationTargetException e) {
			ErrorDialog.open(
					"Exception", 
					"Invocation: " + e.getCause());
			return false;
		} catch (InterruptedException e) {
			ErrorDialog.open("Exception", "Interrupted: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public void resetCoverageResults() {
		for (int i = 0; i < fResults.length; i++) {
			fResults[i] = 0;
		}
	}	

	public double[] getCoverage() {
		return fResults;
	}

	private void initalizeWithProgressDialog() throws InterruptedException {

		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		CalculatorInitRunnable runnable = new CalculatorInitRunnable();

		progressDialog.open();
		try {
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			ErrorDialog.open(
					"Exception", 
					"Invocation: " + e.getCause());
		}

		if (runnable.isCanceled()) {
			throw new InterruptedException("Operation cancelled.");
		}
	}

	private static void addTuplesToMap(Map<List<OrderedChoice>, Integer> map, List<OrderedChoice> tuple) {
		if (!map.containsKey(tuple)) {
			map.put(tuple, 1);
		} else {
			map.put(tuple, map.get(tuple) + 1);
		}
	}

	private static void mergeOccurrenceMaps(Map<List<OrderedChoice>, Integer> targetMap, Map<List<OrderedChoice>, Integer> sourceMap,
			boolean isAdding) {
		if (isAdding) {
			for (List<OrderedChoice> key : sourceMap.keySet()) {
				if (!targetMap.containsKey(key)) {
					targetMap.put(key, sourceMap.get(key));
				} else {
					targetMap.put(key, sourceMap.get(key) + targetMap.get(key));
				}
			}
		} else {
			for (List<OrderedChoice> key : sourceMap.keySet()) {
				if (!targetMap.containsKey(key)) {
					System.err.println("Negative occurences...");
				} else {
					int i = targetMap.get(key) - sourceMap.get(key);
					if (i <= 0)
						targetMap.remove(key);
					else
						targetMap.put(key, i);
				}
			}
		}
	}

	private List<List<ChoiceNode>> prepareInput() {

		List<List<ChoiceNode>> input = new ArrayList<List<ChoiceNode>>();
		for (MethodParameterNode cnode : fParameters) {
			List<ChoiceNode> parameter = new ArrayList<ChoiceNode>();
			if(cnode.isExpected()){
				parameter.add(new ChoiceNode("expected", cnode.getDefaultValue()));
			} else {
				for (ChoiceNode pnode : cnode.getLeafChoices()) {
					parameter.add(pnode);
				}
			}
			input.add(parameter);
		}
		return input;
	}

	private Map<Integer, ChoiceNode> prepareExpectedChoices() {
		int n = 0;
		Map<Integer, ChoiceNode> expected = new HashMap<>();
		for (MethodParameterNode cnode : fParameters) {
			if (cnode.isExpected()) {
				ChoiceNode p = new ChoiceNode("", cnode.getDefaultValue());
				p.setParent(cnode);
				expected.put(n, p);
				//				expected.put(n, cnode.getDefaultValueChoice());
			}
			n++;
		}
		return expected;
	}

	private List<List<ChoiceNode>> prepareCasesToAdd(Collection<TestCaseNode> TestCases) {
		List<List<ChoiceNode>> cases = new ArrayList<>();
		if (fExpectedChoices.isEmpty()) {
			for (TestCaseNode tcnode : TestCases) {
				List<ChoiceNode> choices = new ArrayList<>();
				for (ChoiceNode pnode : tcnode.getTestData()) {
					choices.add(pnode);
				}
				cases.add(choices);
			}
		} else {
			for (TestCaseNode tcnode : TestCases) {
				List<ChoiceNode> choices = new ArrayList<>();
				int n = 0;
				for (ChoiceNode pnode : tcnode.getTestData()) {
					if (fExpectedChoices.containsKey(n)) {
						choices.add(fExpectedChoices.get(n));
					} else {
						choices.add(pnode);
					}
					n++;
				}
				cases.add(choices);
			}
		}
		return cases;
	}

	private int calculateTotalTuples(
			List<List<ChoiceNode>> input, 
			int n, 
			int total,
			int coverage, 
			IProgressMonitor monitor,
			BooleanHolder isSubCancelled) {

		int totalWork = 0;
		int cnt = 0;

		Tuples<List<ChoiceNode>> tuples = new Tuples<List<ChoiceNode>>(input, n);

		while (tuples.hasNext()) {

			cnt++;
			if (cnt%64 == 0) {
				monitor.subTask("Calculating totals - pass:" + n + " of:" + total + ", item:" + cnt);

				if (monitor.isCanceled()) {
					isSubCancelled.set(true);
					return 0;
				}
			}

			long combinations = 1;
			List<List<ChoiceNode>> tuple = tuples.next();
			for (List<ChoiceNode> parameter : tuple) {
				combinations *= parameter.size();
			}
			totalWork += combinations;
		}

		return (int) Math.ceil(((double) (coverage * totalWork)) / 100);
	}

	private List<OrderedChoice> convertToOrdered(List<ChoiceNode> choices){
		ArrayList<OrderedChoice> ordered = new ArrayList<>();
		int i = 0;
		for(ChoiceNode choice: choices){
			ordered.add(new OrderedChoice(i, choice));
			i++;
		}
		return ordered;
	}


	private class CalculatorInitRunnable implements IRunnableWithProgress {

		private boolean fIsCanceled = false;

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			fInput = prepareInput();
			fTuplesCovered = new int[fNMax];
			fTotalWork = new int[fNMax];
			fResults = new double[fNMax];
			fCurrentlyChangedCases = new ArrayList<>();

			fTuples = new ArrayList<Map<List<OrderedChoice>, Integer>>();
			fExpectedChoices = prepareExpectedChoices();

			createDummyProgress(monitor);

			BooleanHolder isSubCancelled = new BooleanHolder(false);

			for (int index = 0; index < fTotalWork.length; index++) {

				fTotalWork[index] = 
						calculateTotalTuples(
								fInput, index + 1, fTotalWork.length, 100, monitor, isSubCancelled);

				if (isSubCancelled.get()) {
					fIsCanceled = true;
					break;
				}

				fTuples.add(new HashMap<List<OrderedChoice>, Integer>());

				if (monitor.isCanceled()) {
					fIsCanceled = true;
					break;
				}

			}

			monitor.done();
		}

		private void createDummyProgress(IProgressMonitor monitor) {
			monitor.beginTask("Initializing Coverage calculator", 5);
			monitor.worked(1);
		}

		public boolean isCanceled() {
			return fIsCanceled;
		}

	}

	private class CalculatorRunnable implements IRunnableWithProgress {

		private boolean fIsCanceled;
		// if true - add occurences, else substract them

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			int n = 0;
			IntegerHolder cnt = new IntegerHolder(0);
			BooleanHolder isSubCancelled = new BooleanHolder(false);

			List<Map<List<OrderedChoice>, Integer>> coveredTuples = new ArrayList<>();

			monitor.beginTask("Calculating Coverage", fCurrentlyChangedCases.size() * fNMax);

			while (!monitor.isCanceled() && n < fNMax) {

				Map<List<OrderedChoice>, Integer> mapForN = new HashMap<>();

				ArrayList<List<OrderedChoice>> convertedCases = new ArrayList<>();

				for (List<ChoiceNode> tcase: fCurrentlyChangedCases) {

					displaySubTaskMessage(n, cnt, isSubCancelled, monitor);
					if (isSubCancelled.get()) {
						fIsCanceled = true;
						break;
					}

					convertedCases.add(convertToOrdered(tcase));
				}

				for (List<OrderedChoice> converted: convertedCases) {

					displaySubTaskMessage(n, cnt, isSubCancelled, monitor);
					if (isSubCancelled.get()) {
						fIsCanceled = true;
						break;
					}

					Tuples<OrderedChoice> tuples = new Tuples<OrderedChoice>(converted, n + 1);

					for (List<OrderedChoice> pnode : tuples.getAll()) {

						displaySubTaskMessage(n, cnt, isSubCancelled, monitor);
						if (isSubCancelled.get()) {
							fIsCanceled = true;
							break;
						}

						addTuplesToMap(mapForN, pnode);
					}

					monitor.worked(1);
				}

				if (!monitor.isCanceled()) {

					displaySubTaskMessage(n, cnt, isSubCancelled, monitor);
					if (isSubCancelled.get()) {
						fIsCanceled = true;
						break;
					}

					coveredTuples.add(mapForN);
					n++;
				}
			}

			n = 0;
			if (!fIsCanceled) {
				for (Map<List<OrderedChoice>, Integer> map : coveredTuples) {

					displaySubTaskMessage(n, cnt, isSubCancelled, monitor);
					if (isSubCancelled.get()) {
						fIsCanceled = true;
						break;
					}

					mergeOccurrenceMaps(fTuples.get(n), map, fAddingFlag);
					fTuplesCovered[n] = fTuples.get(n).size();
					fResults[n] = (((double) fTuplesCovered[n]) / ((double) fTotalWork[n])) * 100;
					n++;
				}
			}

			monitor.done();
		}

		public boolean getIsCancelled() {
			return fIsCanceled;
		}

		private void displaySubTaskMessage(
				int pass, IntegerHolder cnt, BooleanHolder isSubCancelled, IProgressMonitor monitor) {

			cnt.increment();

			if (cnt.get()%64 != 0) {
				return;
			}

			monitor.subTask("Calculating coverage - pass:" + pass + " , item:" + cnt.get());

			if (monitor.isCanceled()) {
				isSubCancelled.set(true);
				return;
			}
		}

	}

	/*
	 * Introducing OrderedChoice to differentiate between equal choices in different parameters
	 * (Occuring when two parameters link the same global parameter)
	 */
	private class OrderedChoice{
		int fIndex;
		ChoiceNode fChoice;

		public OrderedChoice(int index, ChoiceNode choice){
			fIndex = index;
			fChoice = choice;
		}

		@Override
		public int hashCode(){
			int hash = 7;
			hash = 31 * hash + fIndex;
			hash = 31 * hash + (fChoice == null ? 0 : fChoice.hashCode());
			return hash;
		}

		@Override
		public boolean equals(Object obj){
			if((obj == null) || (obj.getClass() != this.getClass())) return false;
			OrderedChoice choice = (OrderedChoice)obj;
			return ((choice.fIndex == this.fIndex) && choice.fChoice.equals(this.fChoice));
		}
	}

}
