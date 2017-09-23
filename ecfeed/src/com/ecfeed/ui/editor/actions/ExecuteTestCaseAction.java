/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.TestCaseInterface;

public class ExecuteTestCaseAction extends ModelSelectionAction {

	private TestCaseInterface fTestCaseInterface;

	public ExecuteTestCaseAction(ISelectionProvider selectionProvider, TestCaseInterface TestCaseInterface) {
		super(ActionId.EXECUTE_TEST_CASE, selectionProvider);
		fTestCaseInterface = TestCaseInterface;
	}

	@Override
	public void run() {
		try {
			fTestCaseInterface.executeStaticTest();
		} catch (EcException e) {
			final String MSG = "Can not execute test.";
			ExceptionCatchDialog.open(MSG, e.getMessage());
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

}
