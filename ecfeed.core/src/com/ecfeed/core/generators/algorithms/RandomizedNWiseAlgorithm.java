/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;

public class RandomizedNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	final private int RANDOM_TEST_TRIES = 10;

	private Set<List<Integer>> allDimCombs = null;

	private Set<List<Variable<E>>> fPotentiallyRemainingTuples = null;

	// The set of all n-tuples for which none of the constraints fail (this set
	// includes both the n-tuples for which all the constraints can be evaluated
	// and pass, as well as the constraints for which at least one of
	// constraints cannot be evaluated).
	private Set<List<Variable<E>>> fRemainingTuples = null;

	private int fIgnoreCount = 0;

	final private int CONSISTENCY_LOOP_LIM = 10;

	public RandomizedNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public void reset() {
		try {
			Map<Boolean, Set<List<Variable<E>>>> nTuples = getAllNTuples();
			fPotentiallyRemainingTuples = nTuples.get(null);
			fRemainingTuples = nTuples.get(true);
			fRemainingTuples.addAll(fPotentiallyRemainingTuples);

			fIgnoreCount = fRemainingTuples.size() * (100 - getCoverage()) / 100;

		} catch (GeneratorException e) {
			throw new RuntimeErrorException(new Error(e));
		}
		super.reset();
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		IGeneratorProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

		while (true) {

			if (generatorProgressMonitor != null) {
				if (generatorProgressMonitor.isCanceled()) {
					return null;
				}
			}

			if (fRemainingTuples.size() <= fIgnoreCount)
				return null;


			List<Variable<E>> nTuple = fRemainingTuples.iterator().next();
			fRemainingTuples.remove(nTuple);

			boolean canGenerate = canGenerateTest(nTuple);

			//			String message = canGenerate ? "CAN GENERATE: " : "CAN NOT GENERATE: ";
			//			debugPrintTuple(message, nTuple);

			if (!canGenerate) {
				continue;
			}

			List<E> randomTest = generateRandomTest(nTuple);

			if (randomTest != null) {
				if (fRemainingTuples.size() <= fIgnoreCount) {
					// no need for optimization
					progress(1);
					return randomTest;
				}
				List<E> improvedTest = improveCoverageOfTest(randomTest, nTuple);
				int cov = removeCoveredNTuples(improvedTest);
				progress(cov);
				return improvedTest;
			} else {
				// GeneratorException.report("Cannot generate test for " +
				// toString(nTuple));
				System.out.println("Cannot generate test for" + toString(nTuple) + "!!! " + fRemainingTuples.size());
				if (!fPotentiallyRemainingTuples.contains(nTuple))
					fRemainingTuples.add(nTuple);
			}
		}
	}

	private boolean canGenerateTest(List<Variable<E>> nTuple) {

		List<Integer> dimensions = createDimensions(nTuple);

		IterableTestSubinput<E> inputValuesList = new IterableTestSubinput<E>(getInput(), dimensions);

		for (List<E> partialValues : inputValuesList ) {

			if (canGenerateTestForPartialValues(partialValues, dimensions, nTuple)) {
				return true;
			}
		}

		return false;
	}

	private List<Integer> createDimensions(List<Variable<E>> nTuple) {

		List<Integer> dimensionsToCheck = getDimensionsToCheck(nTuple);

		List<Integer> selectedDimensions = 
				selectDimensionsByConstraints(dimensionsToCheck);

		return selectedDimensions;
	}

	private boolean canGenerateTestForPartialValues(
			List<E> partialValues, List<Integer> selectedDimensions, List<Variable<E>> nTuple) {

		List<Variable<E>> partialTuple = createInputTuple(partialValues, selectedDimensions);

		List<E> test = createOneTest(getInput(), nTuple, partialTuple);

		if (checkConstraints(test)) {
			return true;
		}

		return false;
	}

	private List<Variable<E>> createInputTuple(List<E> input, List<Integer> dimensions) {

		int inputSize = input.size();

		if (inputSize != dimensions.size()) {
			return null;
		}

		List<Variable<E>> result = new ArrayList<Variable<E>>();

		for (int index = 0; index < inputSize; index++) {
			Variable<E> item = new Variable<E>(dimensions.get(index), input.get(index));
			result.add(item);
		}

		return result;
	}

	private List<Integer> getDimensionsToCheck(List<Variable<E>> nTuple) {

		List<Integer> result = new ArrayList<Integer>();
		int maxDimension = getInput().size();

		for (int dimension = 0; dimension < maxDimension; dimension++) {
			if (tupleContainsIndex(nTuple, dimension)) {
				continue;
			}

			result.add(dimension);
		}

		return result;
	}

	private boolean tupleContainsIndex(List<Variable<E>> nTuple, int dimension) {

		for (Variable<E> variable : nTuple) {
			if (variable.fDimension == dimension) {
				return true;
			}
		}

		return false;
	}

	private List<Integer> selectDimensionsByConstraints(List<Integer> dimensions) {
		return dimensions; // TODO
	}

	private String toString(List<Variable<E>> nTuple) {
		String str = "< ";
		for (Variable<E> var : nTuple)
			str += "(" + var.fDimension + ", " + var.fItem + ") ";
		return str + ">";
	}

	/*
	 * Removes all the nTuples that are covered by improvedTest from both
	 * fRemainingTuples and fPotentiallyRemainingTuples
	 * 
	 * @param improvedTest
	 */
	private int removeCoveredNTuples(List<E> test) {
		Set<List<Variable<E>>> coveredTuples = getCoveredNTuples(test);
		int cov = coveredTuples.size();
		fRemainingTuples.removeAll(coveredTuples);
		fPotentiallyRemainingTuples.removeAll(coveredTuples);
		return cov;
	}

	private Set<List<Variable<E>>> getCoveredNTuples(List<E> test) {
		int k = allDimCombs.size();
		Set<List<Variable<E>>> coveredTuples = new HashSet<>();

		for (List<Variable<E>> nTuple : fRemainingTuples) {
			if (k == 0)
				break;

			boolean isCovered = true;
			for (Variable<E> var : nTuple)
				if (!test.get(var.fDimension).equals(var.fItem)) {
					isCovered = false;
					break;
				}
			if (isCovered) {
				k--;
				coveredTuples.add(nTuple);
			}
		}
		return coveredTuples;
	}

	private List<E> improveCoverageOfTest(List<E> randomTest, List<Variable<E>> nTuple) {
		/*
		 * while you can improve coverage, make a random ordering of modifiable
		 * indices for each index in the list, check all available values for
		 * that index and choose the best. If the coverage is improved, use the
		 * newly generated tuple (test).
		 */
		List<E> improvedTest = randomTest;

		Map<Integer, Integer> dims = getModifiableDimensions(randomTest, nTuple);
		boolean progress;

		do {
			progress = false;

			// shuffle dims
			List<Integer> mDims = new ArrayList<>(dims.values());
			Collections.shuffle(mDims);

			for (int i = 0; i < mDims.size(); i++) {
				int coverage;
				int bestCov = getCoverage(improvedTest);
				int dim = mDims.get(i);
				List<E> input = getInput().get(dim);
				List<E> bests = new ArrayList<>();
				bests.add(improvedTest.get(dim));
				for (E feature : input) {
					improvedTest.set(dim, feature);
					if (checkConstraints(improvedTest)) {
						coverage = getCoverage(improvedTest);

						if (coverage >= bestCov) {
							if (coverage > bestCov) {
								progress = true;
								bestCov = coverage;
								bests.clear();
							}
							bests.add(feature);
						}
					}
				}
				// use the best feature
				improvedTest.set(dim, bests.get((new Random().nextInt(bests.size()))));
			}
		} while (progress);

		return improvedTest;
	}

	private Map<Integer, Integer> getModifiableDimensions(List<E> randomTest, List<Variable<E>> nTuple) {
		// make a list of dimensions that do not appear in nTuple
		Map<Integer, Integer> dims = new HashMap<>();
		for (int i = 0; i < randomTest.size(); i++)
			dims.put(i, i);

		for (int i = 0; i < nTuple.size(); i++)
			dims.remove(nTuple.get(i).fDimension);
		return dims;
	}

	/*
	 * Randomly generates a test that contains 'nTuple' and satisfies all the
	 * constraints.
	 * nTuple - list of n values selected from available choices
	 */
	private List<E> generateRandomTest(List<Variable<E>> nTuple) {

		List<E> bestTest = null;
		int bestCoverage = 1;

		for (int cnt = 0; cnt < RANDOM_TEST_TRIES; cnt++) {

			List<E> currentTest = findTestSatisfyingAllConstraints(nTuple);

			if (currentTest == null) {
				continue;
			}

			if (fRemainingTuples.size() <= fIgnoreCount) {
				return currentTest;
			}

			int currentCoverage = getCoverage(currentTest) + 1; // one extra point for the current tuple

			if (currentCoverage >= bestCoverage) {
				bestTest = currentTest;
				bestCoverage = currentCoverage;
			}
		}

		return bestTest;
	}

	private List<E> findTestSatisfyingAllConstraints(List<Variable<E>> nTuple) {

		List<List<E>> paramsWithChoices = getInput();
		List<E> test = null;
		int itr = 0;

		do {
			test = createOneTest(paramsWithChoices, nTuple);

			if (checkConstraints(test)) {
				return test;
			}

		} while (++itr < CONSISTENCY_LOOP_LIM);

		return null;
	}

	protected void debugPrintTuple(String message, List<Variable<E>> nTuple) {
		System.out.println(message);

		for (Variable<E> var : nTuple) {
			System.out.println("   " + var.toString());
		}
	}

	protected void debugPrintParametersAndChoices(String message, List<List<E>> tInput) {
		System.out.println(message);

		for (List<E> list : tInput) {
			debugPrintListOfElements("list", list);
		}
	}

	protected <T> void debugPrintListOfElements(String message, List<T> list) {
		System.out.println(message);
		for (T elem : list) {
			System.out.println("   " + elem.toString());
		}
	}

	private List<E> createOneTest(List<List<E>> tInput, List<Variable<E>> tuple) {

		List<E> result = createRandomTest(tInput);
		return plugInTupleIntoList(tuple, result);
	}

	private List<E> createOneTest(
			List<List<E>> tInput, List<Variable<E>> tuple1, List<Variable<E>> tuple2) {

		List<E> result = createRandomTest(tInput);
		result = plugInTupleIntoList(tuple1, result);
		return plugInTupleIntoList(tuple2, result);
	}	

	private List<E> createRandomTest(List<List<E>> tInput) {

		List<E> result = new ArrayList<>();

		for (int i = 0; i < tInput.size(); i++) {
			List<E> features = tInput.get(i);
			result.add(features.get((new Random()).nextInt(features.size())));
		}

		return result;
	}

	private List<E> plugInTupleIntoList(List<Variable<E>> nTuple, List<E> listOfItems) {

		for (Variable<E> var : nTuple) {
			listOfItems.set(var.fDimension, var.fItem);
		}

		return listOfItems;
	}

	private int getCoverage(List<E> test) {
		return getCoveredNTuples(test).size();
	}

	private Set<List<Integer>> getAllDimensionCombinations() {
		int dimCount = getInput().size();
		List<Integer> dimentions = new ArrayList<>();
		for (int i = 0; i < dimCount; i++)
			dimentions.add(new Integer(i));

		return (new Tuples<Integer>(dimentions, N)).getAll();
	}

	private Map<Boolean, Set<List<Variable<E>>>> getAllNTuples() throws GeneratorException {

		Set<List<Integer>> allCombs = getAllDimCombs();
		Map<Boolean, Set<List<Variable<E>>>> allNTuples = new HashMap<>();
		Set<List<Variable<E>>> validNTuple = new HashSet<>();
		Set<List<Variable<E>>> unevaluableNTuples = new HashSet<>();
		allNTuples.put(true, validNTuple);
		allNTuples.put(null, unevaluableNTuples);

		for (List<Integer> comb : allCombs) {
			List<List<Variable<E>>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++) {
				List<Variable<E>> values = new ArrayList<>();
				for (E e : getInput().get(comb.get(i)))
					values.add(new Variable<E>(comb.get(i), e));
				tempIn.add(values);
			}

			CartesianProductAlgorithm<Variable<E>> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, new HashSet<IConstraint<Variable<E>>>(), getGeneratorProgressMonitor());
			List<Variable<E>> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {
				// Generate a full tuple from this nTuple to make sure that it
				// is consistent with the constraints
				List<E> fullTuple = new ArrayList<>();
				for (int i = 0; i < getInput().size(); i++)
					fullTuple.add(null);
				for (Variable<E> var : tuple)
					fullTuple.set(var.fDimension, var.fItem);

				Boolean check = checkConstraintsOnExtendedNTuple(fullTuple);
				if (check == null)
					unevaluableNTuples.add(tuple);
				else if (check)
					validNTuple.add(tuple);
			}
		}

		return allNTuples;
	}

	@SuppressWarnings("unchecked")
	void generateNTuples(Set<List<Integer>> allCombs, Set<List<Variable<E>>> allNTuples) throws GeneratorException {
		for (List<Integer> comb : allCombs) {
			List<List<E>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++)
				tempIn.add(getInput().get(comb.get(i)));

			CartesianProductAlgorithm<E> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, (Collection<IConstraint<E>>) getConstraints(), getGeneratorProgressMonitor());
			List<E> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {
				List<Variable<E>> extendedTuple = new ArrayList<>();
				for (int i = 0; i < comb.size(); i++)
					extendedTuple.add(new Variable<>(comb.get(i), tuple.get(i)));
				allNTuples.add(extendedTuple);
			}
		}
	}

	protected static class Variable<E> {

		int fDimension; // e.g. index of method parameter
		E fItem;

		public Variable(int dimension, E item) {
			fDimension = dimension;
			fItem = item;
		}

		@Override
		public boolean equals(Object obj) {

			if (!(obj instanceof Variable))
				return false;

			Variable<?> var = (Variable<?>) obj;

			if (var.fDimension == this.fDimension && this.fItem.equals(var.fItem)) {
				return true;
			}

			return false;
		}

		@Override
		public String toString() {

			return ("dim:" + fDimension + " item:" + fItem.toString());
		}

	}

	protected Set<List<Integer>> getAllDimCombs() {

		if (allDimCombs == null)
			allDimCombs = getAllDimensionCombinations();

		return allDimCombs;
	}

	/*
	 * If the incomplete tuple (tuple with null values at some of the indices)
	 * is consistent returns true. If evaluating the constraint requires
	 * accessing some of the indices with a null value, the constraints cannot
	 * be evaluated and the method returns null; otherwise it returns false.
	 */
	protected Boolean checkConstraintsOnExtendedNTuple(List<E> vector) {
		boolean hasNull = false;
		if (vector == null)
			return true;
		for (IConstraint<E> constraint : getConstraints()) {
			boolean value = false;
			try {
				value = constraint.evaluate(vector);
				if (value == false) {
					return false;
				}
			} catch (NullPointerException e) {
				hasNull = true;
			}
		}
		if (hasNull)
			return null;
		return true;
	}
}
