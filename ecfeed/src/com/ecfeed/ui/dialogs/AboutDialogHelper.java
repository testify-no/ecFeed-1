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

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.StringHelper;

public class AboutDialogHelper {

	public static String createAboutInformation() {
		return createAboutInformation("com.ecfeed", null);
	}

	public static String createAboutInformation(String mainBundleName, String rapVersionInfo) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(
				"     ecFeed for Eclipse, version " + 
						ApplicationContext.getEcFeedVersion(mainBundleName) + "\n");

		if (!StringHelper.isNullOrEmpty(rapVersionInfo)) {
			stringBuilder.append("     "); 
			stringBuilder.append(rapVersionInfo);
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
