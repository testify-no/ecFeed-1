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

public class About2Dialog {

	public static void open() {
		MessageDialog dialog = 
				new MessageDialog(
						Display.getDefault().getActiveShell(), 
						"About ecFeed", 
						null,
						createAboutInformation(), 
						MessageDialog.INFORMATION, 
						new String[] { "Ok", "Check for updates..." }, 
						0);

		int result = dialog.open();
		System.out.println(result);		
	}


	private static String createAboutInformation() {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("     EcFeed is a tool that allows to design, model and execute tests for Java\n");
		stringBuilder.append("     Android and Web projects.\n");
		stringBuilder.append("\n");
		stringBuilder.append("     Copyright (c) 2017 ecFeed AS.\n");
		stringBuilder.append("\n");
		stringBuilder.append("     For tutorials, documentation and other materials check:\n");
		stringBuilder.append("     www.ecfeed.com\n");

		return stringBuilder.toString();
	}

}
