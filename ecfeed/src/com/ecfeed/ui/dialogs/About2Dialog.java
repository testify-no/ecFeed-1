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
						AboutDialogHelper.createAboutInformation(), 
						MessageDialog.INFORMATION, 
						new String[] { "Ok", "Check for updates..." }, 
						0);

		int result = dialog.open();

		if (result == 1) {
			CheckForUpdatesDialog.openUnconditionally();
		}
	}

}
