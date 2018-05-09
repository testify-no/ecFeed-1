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

public abstract class AbstractAlgorithm<E> implements IAlgorithm<E> {

	private int fTotalWork;
	private int fProgress;
	private int fTotalProgress;
	protected boolean fCancel;
	private IGeneratorProgressMonitor fGeneratorProgressMonitor;

	private List<List<E>> fInput;
	private Collection<IConstraint<E>> fConstraints;

	@Override
	public void initialize(List<List<E>> input,
			Collection<IConstraint<E>> constraints,
			IGeneratorProgressMonitor generatorProgressMonitor) throws GeneratorException {

		if(input == null || constraints == null){
			GeneratorException.report("input or constraints of algorithm cannot be null");
		}
		fInput = input;
		fConstraints = constraints;
		fGeneratorProgressMonitor = generatorProgressMonitor;
		reset();
	}

	@Override
	public int totalWork() {
		return fTotalWork;
	}

	@Override
	public int workProgress() {
		int progress = fProgress;
		fProgress = 0;
		return progress;
	}

	@Override
	public int totalProgress(){
		return fTotalProgress;
	}

	public IGeneratorProgressMonitor getGeneratorProgressMonitor() {
		return fGeneratorProgressMonitor;
	}

	public void reset(){
		fProgress = 0;
	}

	@Override
	public void addConstraint(IConstraint<E> constraint) {
		fConstraints.add(constraint);
	}

	@Override
	public void removeConstraint(IConstraint<E> constraint) {
		fConstraints.remove(constraint);
	}

	@Override
	public Collection<? extends IConstraint<E>> getConstraints() {
		return fConstraints;
	}

	public List<List<E>> getInput(){
		return fInput;
	}

	protected void progress(int progress){
		fProgress += progress;
		fTotalProgress += progress;
	}

	protected void setTotalWork(int totalWork){
		fTotalWork = totalWork;
	}

	protected List<E> instance(List<Integer> vector) {
		if (vector == null) return null;
		List<E> instance = new ArrayList<E>();
		for(int i = 0; i < vector.size(); i++){
			E element = fInput.get(i).get(vector.get(i));
			instance.add(element);
		}
		return instance;
	}

	protected List<Integer> representation(List<E> vector){
		if(vector == null) return null;
		List<Integer> representation = new ArrayList<Integer>();
		for(int i = 0; i < vector.size(); i++){
			E element = vector.get(i);
			int index = fInput.get(i).indexOf(element);
			if(index < 0){
				index = 0;
			}
			representation.add(index);
		}
		return representation;
	}

	protected EvaluationResult checkConstraints(List<E> test) {

		if (test == null) { 
			return EvaluationResult.TRUE;
		}

		for(IConstraint<E> constraint : fConstraints) {
			if(constraint.evaluate(test) == EvaluationResult.FALSE) {
				return EvaluationResult.FALSE;
			}
		}

		return EvaluationResult.TRUE;
	}

	protected boolean isDimensionMentionedInConstraints(int dimension) {

		for (IConstraint<E> constraint : fConstraints) {
			if (constraint.mentions(dimension)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void cancel() {
		fCancel = true;
	}

}
