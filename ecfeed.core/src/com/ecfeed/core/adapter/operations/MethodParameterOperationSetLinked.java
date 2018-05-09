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
import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.ConstraintNode;
//import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.Messages;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;

public class MethodParameterOperationSetLinked extends BulkOperation{

	private class SetLinkedOperation extends AbstractModelOperation {

		private MethodParameterNode fTarget;
		private boolean fLinked;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;

		private class ReverseSetLinkedOperation extends AbstractReverseOperation{

			public ReverseSetLinkedOperation() {
				super(SetLinkedOperation.this);
			}

			@Override
			public void execute() throws ModelOperationException {

				setOneNodeToSelect(fTarget);
				MethodNode methodNode = fTarget.getMethod();

				methodNode.replaceTestCases(fOriginalTestCases);
				methodNode.replaceConstraints(fOriginalConstraints);
				reparentConstraints(methodNode);
				fTarget.setLinked(!fLinked);
			}

			private void reparentConstraints(MethodNode methodNode) {

				for (ConstraintNode constraint : fOriginalConstraints) {
					constraint.setParent(methodNode);
				}
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetLinkedOperation(fTarget, fLinked);
			}

		}

		public SetLinkedOperation(MethodParameterNode target, boolean linked) {
			super(OperationNames.SET_LINKED);
			fTarget = target;
			fLinked = linked;
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);

			MethodNode method = fTarget.getMethod();
			String newType;
			if(fLinked){
				if(fTarget.getLink() == null){
					ModelOperationException.report(Messages.LINK_NOT_SET_PROBLEM);
				}
				newType = fTarget.getLink().getType();
			}
			else{
				newType = fTarget.getRealType();
			}

			if(method.checkDuplicate(fTarget.getIndex(), newType)){
				ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			}
			//			if(fLinked){
			//				GlobalParameterNode link = fTarget.getLink();
			//				if(link == null || method.checkDuplicate(fTarget.getIndex(), link.getType())){
			//					GlobalParameterNode newLink = setNewLink();
			//					if(newLink == null){
			//						ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			//					}
			//					fTarget.setLink(newLink);
			//				}
			//			}
			//
			//
			//				//check if the link is still part of the model
			//				GlobalParametersParentNode parent = link.getParametersParent();
			//				if(link == null || parent == null || parent.getParameters().contains(link) == false){
			//					GlobalParameterNode newLink = null;
			//					for(GlobalParameterNode newLinkCandidate : fTarget.getMethod().getAvailableGlobalParameters()){
			//						if(checkSignatureConflict(method, fTarget.getIndex(), newLinkCandidate.getType()) == false){
			//							newLink = newLinkCandidate;
			//							break;
			//						}
			//					}
			//					System.out.println("Dupa");
			//				}
			//				newType = link.getType();
			//			}else{
			//				newType = fTarget.getType();
			//			}
			//
			//			List<String> types = method.getParametersTypes();
			//			types.set(fTarget.getIndex(), newType);
			//			if(method.getClassNode().getMethod(method.getName(), types) != null && method.getClassNode().getMethod(method.getName(), types) != method){
			//				ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(method.getClassNode().getName(), method.getName()));
			//			}

			fTarget.setLinked(fLinked);
			fOriginalTestCases = new ArrayList<>(method.getTestCases());
			fOriginalConstraints = new ArrayList<>(method.getConstraintNodes());

			method.removeTestCases();
			method.removeConstraintsWithParameter(fTarget);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseSetLinkedOperation();
		}

	}

	public MethodParameterOperationSetLinked(MethodParameterNode target, boolean linked) {
		super(OperationNames.SET_LINKED, true, target, target);
		addOperation(new SetLinkedOperation(target, linked));
		addOperation(new MethodOperationMakeConsistent(target.getMethod())); 
	}

	public void addOperation(int index, IModelOperation operation){
		operations().add(index, operation);
	}

}
