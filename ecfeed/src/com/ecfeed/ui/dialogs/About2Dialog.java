/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.StringHelper;

public class About2Dialog {

	public static void open() {

		String[] dialogButtonLabels;

		if (ApplicationContext.isApplicationTypeLocal()) {
			dialogButtonLabels = new String[] { "Ok", "Check for updates..." };
		} else {
			dialogButtonLabels = new String[] { "Ok" };
		}

		MessageDialog dialog = 
				new MessageDialog(
						Display.getDefault().getActiveShell(), 
						"About ecFeed", 
						null,
						createAboutInformation(), 
						MessageDialog.INFORMATION, 
						dialogButtonLabels, 
						0);

		int result = dialog.open();

		if (result == 1) {
			CheckForUpdatesDialog.openUnconditionally();
		}
	}

	public static String createAboutInformation() {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(
				"     ecFeed for Eclipse, version " + 
						ApplicationContext.getEcFeedVersion() + "\n");

		String rapVersion = ApplicationContext.getRapVersion();

		if (!StringHelper.isNullOrEmpty(rapVersion)) {
			stringBuilder.append("     "); 
			stringBuilder.append("Running on RAP version " + rapVersion);
			stringBuilder.append("\n");
		}

		stringBuilder.append("\n");

		stringBuilder.append("     Copyright (c) 2017 ecFeed AS.\n");
		stringBuilder.append("\n");
		stringBuilder.append("     For tutorials, documentation and other materials check:\n");
		stringBuilder.append("     www.ecfeed.com\n");

		return stringBuilder.toString();
	}


}
