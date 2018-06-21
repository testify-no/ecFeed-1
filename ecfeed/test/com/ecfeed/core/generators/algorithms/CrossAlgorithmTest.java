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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelTestHelper;
import com.ecfeed.core.model.RootNode;

public class CrossAlgorithmTest {

	@Test
	public void test01() {

		compareAlgorithms(ModelXmlVeilarbPerson.getXml(), ModelXmlVeilarbPerson.getMethodName(), 2);
	}

	private void compareAlgorithms(String modelXml, String methodName, int N) {

		RootNode rootNode = ModelTestHelper.createModel(modelXml);

		MethodNode methodNode = GeneratorHelper.getMethodByName(rootNode, methodName);

		AbstractAlgorithm<ChoiceNode> randomizedAlgorithm = 
				new RandomizedNWiseAlgorithm<ChoiceNode>(N, 100);

		AbstractAlgorithm<ChoiceNode> cartesianAlgorithm = 
				new CartesianProductAlgorithm<ChoiceNode>();

		generateAndCompareTestCases(methodNode, N, randomizedAlgorithm, cartesianAlgorithm);
	}

	private static void generateAndCompareTestCases(MethodNode methodNode, int N, 
			AbstractAlgorithm<ChoiceNode> randomizedAlgorithm,
			AbstractAlgorithm<ChoiceNode> cartesianAlgorithm) {

		List<List<ChoiceNode>> randomizedNWiseTuples = new ArrayList<List<ChoiceNode>>();
		List<List<ChoiceNode>> cartesianTuples = new ArrayList<List<ChoiceNode>>();

		try {
			randomizedNWiseTuples = 
					GeneratorHelper.generateTestCasesForMethod(methodNode, randomizedAlgorithm);

			cartesianTuples = 
					GeneratorHelper.generateTestCasesForMethod(methodNode, cartesianAlgorithm);

		} catch (GeneratorException e) {
			fail("Exception:" + e.getMessage());
		}

		compareCoverage(N, cartesianTuples, randomizedNWiseTuples);
	}

	private static void compareCoverage(
			int N,
			List<List<ChoiceNode>> cartesianTuples, 
			List<List<ChoiceNode>> randomizedNWiseTuples) {

		List<ChoiceNode> tuple = cartesianTuples.get(0);

		Set<List<Integer>> allDimensionCombinations = 
				GeneratorHelper.getAllDimensionCombinations(tuple.size(), N);

		for (List<Integer> subDimensions : allDimensionCombinations) {

			SortedSet<ComparableChoiceTuple> subTuplesCartesian = 
					GeneratorHelper.createSortedTuplesForSubDimension(cartesianTuples, subDimensions);

			SortedSet<ComparableChoiceTuple> subTuplesNWise = 
					GeneratorHelper.createSortedTuplesForSubDimension(randomizedNWiseTuples, subDimensions);

			assertTrue(subTuplesCartesian.equals(subTuplesNWise));
		}
	}

}
