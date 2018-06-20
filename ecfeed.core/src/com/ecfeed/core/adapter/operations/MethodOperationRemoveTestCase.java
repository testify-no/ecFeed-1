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
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationRemoveTestCase extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCase;
	private int fIndex;

	private class DummyAdapterProvider implements ITypeAdapterProvider{

		@Override
		public ITypeAdapter getAdapter(String type) {
			return new ITypeAdapter() {
				@Override
				public boolean isNullAllowed() {
					return false;
				}
				@Override
				public String getDefaultValue() {
					return null;
				}
				@Override
				public String convert(String value) {
					return value;
				}
				@Override
				public boolean isCompatible(String type) {
					return true;
				}
				@Override
				public Object generateValue(String range) {
					return null;
				}
				@Override
				public String generateValueAsString(String range) {
					return null;
				}
			};
		}

	}

	public MethodOperationRemoveTestCase(MethodNode target, TestCaseNode testCase) {
		super(OperationNames.REMOVE_TEST_CASE);
		fMethodNode = target;
		fTestCase = testCase;
		fIndex = testCase.getIndex();
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fMethodNode);
		fIndex = fTestCase.getIndex();
		fMethodNode.removeTestCase(fTestCase);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddTestCase(fMethodNode, fTestCase, new DummyAdapterProvider(), fIndex);
	}

}
