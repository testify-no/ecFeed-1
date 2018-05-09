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

import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;

public class ReplaceChoicesOperation extends BulkOperation {

	public ReplaceChoicesOperation(
			AbstractParameterNode target, 
			List<ChoiceNode> choices, 
			ITypeAdapterProvider adapterProvider) {

		super("Replace choices", true, target, target);

		List<ChoiceNode> skipped = new ArrayList<ChoiceNode>();

		for (ChoiceNode choice : choices) {
			if (target.getChoiceNames().contains(choice.getName())) {
				skipped.add(choice);
			} else {
				addOperation(new GenericOperationAddChoice(target, choice, adapterProvider, true));
			}
		}

		addOperation(
				new GenericRemoveNodesOperation(
						target.getChoices(), adapterProvider, true, target, target));

		for(ChoiceNode choice : skipped){
			addOperation(new GenericOperationAddChoice(target, choice, adapterProvider, true));
		}
	}

}
