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

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;
import com.ecfeed.core.utils.EvaluationResult;

public class CartesianProductAlgorithm<E> extends AbstractAlgorithm<E>{
	private boolean fInitialized;
	protected List<Integer> fLastGenerated;

	@Override
	public List<E> getNext() throws GeneratorException{
		if(fInitialized == false){
			GeneratorException.report("Generator not initialized");
		}
		if(fCancel){
			return null;
		}
		List<E> next = getNext(instance(fLastGenerated));
		fLastGenerated = representation(next);
		return next;
	}

	@Override
	public void initialize(
			List<List<E>> input, 
			Collection<IConstraint<E>> constraints,
			IGeneratorProgressMonitor generatorProgressMonitor) throws GeneratorException{
		
		fInitialized = true;
		setTotalWork(calculateProductSize(input));
		super.initialize(input, constraints, generatorProgressMonitor);
	}
	
	public void reset(){
		fLastGenerated = null;
		super.reset();
	}

	protected List<E> getNext(List<E> last) throws GeneratorException{
		List<Integer> nextElement = representation(last);
		while((nextElement = incrementVector(nextElement)) != null){
			List<E> instance = instance(nextElement);
			if (checkConstraints(instance) == EvaluationResult.TRUE) {
				progress(1);
				return instance;
			}
		}
		return null;
	}
	
	protected List<Integer> incrementVector(List<Integer> vector) {
		if(vector == null){
			return firstVector();
		}
		for(int i = vector.size() - 1; i >= 0; i--){
			if(vector.get(i) < getInput().get(i).size() - 1){
				vector.set(i, vector.get(i) + 1);
				for(++i; i < vector.size(); i++){
					vector.set(i, 0);
				}
				return vector;
			}
		}
		return null;
	}

	protected List<Integer> firstVector() {
		List<Integer> firstVector = new ArrayList<Integer>(getInput().size());
		for(int i = 0; i < getInput().size(); i++){
			firstVector.add(0);
		}
		return firstVector;
	}

	protected int calculateProductSize(List<? extends List<E>> input) {
		int result = 1;
		for(List<E> vector : input){
			result *= vector.size();
		}
		return result;
	}
}
