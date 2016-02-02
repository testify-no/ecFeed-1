/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.swt;

import org.eclipse.jface.dialogs.MessageDialog;

import com.testify.ecfeed.utils.EclipseHelper;

public class YesNoDialog {

	public enum Result {
		NO,
		YES,
	}

	public static Result display(String question) {
		MessageDialog fDialog = new MessageDialog(
				EclipseHelper.getActiveShell(), "Question", null, question, 
				MessageDialog.QUESTION, new String[] { "No", "Yes" }, 0);

		int result = fDialog.open();
		if (result == 0) {
			return Result.NO; 
		}
		return Result.YES;
	}
}
