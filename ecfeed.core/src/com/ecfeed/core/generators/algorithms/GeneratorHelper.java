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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;

public abstract class GeneratorHelper {

	public static List<List<ChoiceNode>> generateTestCasesForMethod(
			MethodNode methodNode, 
			AbstractAlgorithm<ChoiceNode> algorithm) throws GeneratorException {

		List<List<ChoiceNode>> choicesForParameters = 
				GeneratorHelper.getPossibleChoicesForMethodParameters(methodNode);

		Collection<IConstraint<ChoiceNode>> constraints = methodNode.getAllConstraints();

		algorithm.initialize(choicesForParameters, constraints, null);

		List<List<ChoiceNode>> testCases = new ArrayList<List<ChoiceNode>>();
		List<ChoiceNode> testCase = null;

		while ((testCase = algorithm.getNext()) != null) {
			testCases.add(testCase);
		}

		return testCases;
	}

	public static SortedSet<ComparableChoiceTuple> createSortedTuplesForSubDimension(
			List<List<ChoiceNode>> fullTuples, List<Integer> subDimension) {

		SortedSet<ComparableChoiceTuple> set = new TreeSet<ComparableChoiceTuple>();

		for (List<ChoiceNode> tuple : fullTuples) {

			List<ChoiceNode> subTuple = GeneratorHelper.createSubTuple(tuple, subDimension);
			ComparableChoiceTuple comparableSubTuple = new ComparableChoiceTuple(subTuple);

			set.add(comparableSubTuple);
		}

		return set;
	}

	private static List<List<ChoiceNode>> getPossibleChoicesForMethodParameters(MethodNode methodNode) {

		List<MethodParameterNode> parameters = methodNode.getMethodParameters();
		List<List<ChoiceNode>> algorithmInput = new ArrayList<List<ChoiceNode>>();

		for (int index = 0; index < parameters.size(); index++) {

			MethodParameterNode methodParameterNode = 
					(MethodParameterNode)methodNode.getParameter(index);

			List<ChoiceNode> choices = getChoicesForParameter(methodParameterNode);

			algorithmInput.add(choices);
		}

		return algorithmInput;
	}

	private static List<ChoiceNode> getChoicesForParameter( MethodParameterNode methodParameterNode) {

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();

		if (methodParameterNode.isExpected()) {
			choices.add(expectedValueChoice(methodParameterNode));
			return choices;
		} 

		for (ChoiceNode choice : methodParameterNode.getLeafChoicesWithCopies()) {
			choices.add(choice);
		}

		return choices;
	}

	public static Set<List<Integer>> getAllDimensionCombinations(int dimensions, int N) {

		List<Integer> dimensionList = createDimensionList(dimensions);
		Tuples<Integer> tuples = new Tuples<Integer>(dimensionList, N);

		return tuples.getAll();

	}

	public static MethodNode getMethodByName(RootNode rootNode, String methodName) {
		List<ClassNode> classes = rootNode.getClasses();

		for (ClassNode classNode : classes) {

			MethodNode methodNode = getMethodFromClassByName(classNode, methodName);

			if (methodNode != null) {
				return methodNode;
			}
		}

		return null;
	}

	private static ChoiceNode expectedValueChoice(MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = new ChoiceNode("", methodParameterNode.getDefaultValue());
		choiceNode.setParent(methodParameterNode);

		return choiceNode;
	}

	private static MethodNode getMethodFromClassByName(ClassNode classNode, String methodName) {

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			if (methodName.equals(methodNode.getName())) {
				return methodNode;
			}
		}

		return null;
	}

	private static List<Integer> createDimensionList(int dimensions) {

		List<Integer> list = new ArrayList<Integer>();

		for (int dim = 0; dim < dimensions; dim++) {
			list.add(dim);
		}

		return list;
	}

	private static <T> List<T> createSubTuple(List<T> fullTuple, List<Integer> dimensions) {

		List<T> result = new ArrayList<T>();

		for (Integer dimension : dimensions) {

			T item = fullTuple.get(dimension);

			if (item == null) {
				return null;
			}

			result.add(item);
		}

		return result;
	}

}
