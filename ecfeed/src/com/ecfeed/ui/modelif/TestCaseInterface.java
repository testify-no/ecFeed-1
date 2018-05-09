/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Arrays;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.TestCaseOperationUpdateTestData;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class TestCaseInterface extends AbstractNodeInterface {

	private IJavaProjectProvider fJavaProjectProvider;

	public TestCaseInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
	}

	@Override
	public TestCaseNode getOwnNode() {
		return (TestCaseNode)super.getOwnNode();
	}

	public MethodNode getMethod() {

		TestCaseNode testCaseNode = getOwnNode();
		if (testCaseNode == null) {
			return null;
		}

		return (MethodNode)testCaseNode.getParent();
	}

	public boolean isExpected(ChoiceNode testValue) {
		return getOwnNode().getMethodParameter(testValue).isExpected();
	}

	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface(getOperationExecuter().getUpdateContext(), fJavaProjectProvider);
		if(tc.getMethod() == null) return false;
		mIf.setOwnNode(tc.getMethod());
		EImplementationStatus tcStatus = getImplementationStatus(tc);
		EImplementationStatus methodStatus = mIf.getImplementationStatus();
		return tcStatus == EImplementationStatus.IMPLEMENTED && methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(getOwnNode());
	}

	public void executeStaticTest() throws EcException {
		MethodInterface methodIf = new MethodInterface(getOperationExecuter().getUpdateContext(), fJavaProjectProvider);

		TestCaseNode testCaseNode = getOwnNode();
		MethodNode methodNode = (MethodNode)testCaseNode.getParent();
		methodIf.setOwnNode(methodNode);

		methodIf.executeStaticTests(
				new ArrayList<TestCaseNode>(Arrays.asList(new TestCaseNode[]{getOwnNode()})), fJavaProjectProvider);
	}

	public boolean updateTestData(int index, ChoiceNode value) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(getOwnNode(), index, value);
		return getOperationExecuter().execute(operation, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		return false;
	}
}
