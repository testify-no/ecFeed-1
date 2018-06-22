/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationMakeConsistent extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(OperationNames.MAKE_CONSISTENT);
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fMethodNode);
			fMethodNode.replaceTestCases(fOriginalTestCases);
			fMethodNode.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationMakeConsistent(fMethodNode);
		}

	}

	public MethodOperationMakeConsistent(MethodNode target) {
		super(OperationNames.MAKE_CONSISTENT);
		fMethodNode = target;
		fOriginalConstraints = new ArrayList<ConstraintNode>(target.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fMethodNode);

		boolean modelUpdated = false;

		Iterator<TestCaseNode> tcIt = fMethodNode.getTestCases().iterator();
		while (tcIt.hasNext()) {
			if (tcIt.next().isConsistent() == false) {
				tcIt.remove();
				modelUpdated = true;
			}
		}

		MethodNode.ConstraintsItr constraintItr = fMethodNode.getIterator();
		while (fMethodNode.hasNextConstraint(constraintItr)) {
			if (!fMethodNode.nextConstraint(constraintItr).isConsistent()) {
				fMethodNode.removeConstraint(constraintItr);
				modelUpdated = true;
			}
		}		

		if (modelUpdated) {
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

}
