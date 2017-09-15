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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.StringHelper;

public class GenericOperationAddParameter extends AbstractModelOperation {

	private ParametersParentNode fParametersParentNode;
	private AbstractParameterNode fAbstractParameterNode;
	private int fNewIndex;

	protected class ReverseOperation extends AbstractModelOperation{

		private int fOriginalIndex;
		private AbstractParameterNode fReversedParameter;
		private ParametersParentNode fReversedTarget;

		public ReverseOperation(ParametersParentNode target, AbstractParameterNode parameter) {
			super("reverse " + OperationNames.ADD_PARAMETER);
			fReversedTarget = target;
			fReversedParameter = parameter;
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fReversedParameter.getIndex();
			fReversedTarget.removeParameter(fReversedParameter);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationAddParameter(fReversedTarget, fReversedParameter, fOriginalIndex);
		}

		@Override
		public AbstractNode getNodeToBeSelectedAfterTheOperation() {
			return fParametersParentNode;
		}
	}

	public GenericOperationAddParameter(ParametersParentNode target, AbstractParameterNode parameter, int index) {
		super(OperationNames.ADD_PARAMETER);
		fParametersParentNode = target;
		fAbstractParameterNode = parameter;
		fNewIndex = (index == -1)? target.getParameters().size() : index;
	}

	public GenericOperationAddParameter(ParametersParentNode target, AbstractParameterNode parameter) {
		this(target, parameter, -1);
	}


	@Override
	public void execute() throws ModelOperationException {

		generateUniqueParameterName(fAbstractParameterNode);
		String parameterName = fAbstractParameterNode.getName();

		if(fNewIndex < 0){
			ModelOperationException.report(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fParametersParentNode.getParameters().size()){
			ModelOperationException.report(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(fParametersParentNode.getParameter(parameterName) != null){
			ModelOperationException.report(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}

		fParametersParentNode.addParameter(fAbstractParameterNode, fNewIndex);
		markModelUpdated();
	}

	private void generateUniqueParameterName(AbstractParameterNode abstractParameterNode) {

		String oldName = abstractParameterNode.getName();
		String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);
		String newName = ParametersParentNode.generateNewParameterName(fParametersParentNode, oldNameCore);
		abstractParameterNode.setName(newName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation(fParametersParentNode, fAbstractParameterNode);
	}

	@Override
	public AbstractNode getNodeToBeSelectedAfterTheOperation() {
		return fParametersParentNode;
	}

}
