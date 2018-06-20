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

import com.ecfeed.core.utils.EvaluationResult;

public interface IStatementCondition {
	public Object getCondition();
	public EvaluationResult evaluate(List<ChoiceNode> values);
	public boolean adapt(List<ChoiceNode> values);
	public IStatementCondition getCopy();
	public boolean updateReferences(MethodNode methodNode);
	public boolean compare(IStatementCondition condition);
	public Object accept(IStatementVisitor visitor) throws Exception;
	public boolean mentions(MethodParameterNode methodParameterNode);
	public boolean isAmbigous(List<List<ChoiceNode>> leftSideDomain, int parameterIndex, EStatementRelation relation);
}

