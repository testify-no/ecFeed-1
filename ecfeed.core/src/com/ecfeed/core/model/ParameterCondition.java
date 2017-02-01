/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

import com.ecfeed.core.model.RelationStatement.ICondition;


public class ParameterCondition implements ICondition {

	private MethodParameterNode fMethodParameterNode;

	public ParameterCondition(MethodParameterNode methodParameterNode) {
		fMethodParameterNode = methodParameterNode;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		// TODO
		return false;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition getCopy() {
		return new ParameterCondition(fMethodParameterNode.makeClone());
	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {
		return true;
	}

	@Override
	public Object getCondition(){
		return fMethodParameterNode;
	}

	@Override
	public boolean compare(ICondition otherCondition) {

		if (!(otherCondition instanceof ParameterCondition)) {
			return false;
		}

		ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

		return (fMethodParameterNode == otherParamCondition.fMethodParameterNode);
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return fMethodParameterNode.getName() + "[parameter]"; 
	}

	public MethodParameterNode getMethodParameterNode() {
		return fMethodParameterNode;
	}

}	


