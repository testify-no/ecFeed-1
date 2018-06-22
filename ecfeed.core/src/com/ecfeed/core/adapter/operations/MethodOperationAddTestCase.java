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
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationAddTestCase extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCase;
	private int fIndex;
	private ITypeAdapterProvider fAdapterProvider;

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider, int index) {
		super(OperationNames.ADD_TEST_CASE);
		fMethodNode = target;
		fTestCase = testCase;
		fIndex = index;
		fAdapterProvider = adapterProvider;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, ITypeAdapterProvider adapterProvider) {
		this(target, testCase, adapterProvider, -1);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fMethodNode);

		if(fIndex == -1){
			fIndex = fMethodNode.getTestCases().size();
		}
		if(fTestCase.getName().matches(AdapterConstants.REGEX_TEST_CASE_NODE_NAME) == false){
			ModelOperationException.report(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.updateReferences(fMethodNode) == false){
			ModelOperationException.report(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		//following must be done AFTER references are updated
		fTestCase.setParent(fMethodNode);
		for(ChoiceNode choice : fTestCase.getTestData()){
			MethodParameterNode parameter = fTestCase.getMethodParameter(choice);
			if(parameter.isExpected()){
				String type = parameter.getType();
				ITypeAdapter<?> adapter = fAdapterProvider.getAdapter(type);
				String newValue = 
						adapter.convert(
								choice.getValueString(), choice.isRandomizedValue(), EConversionMode.QUIET);
				if(newValue == null){
					ModelOperationException.report(Messages.TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD);
				}
				choice.setValueString(newValue);
			}
		}

		fMethodNode.addTestCase(fTestCase, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationRemoveTestCase(fMethodNode, fTestCase);
	}

}
