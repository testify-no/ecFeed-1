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

import com.ecfeed.algorithm.CurrentReleases;
import com.ecfeed.algorithm.VersionCheckerAndRegistrator;
import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.net.HttpComunicator;
import com.ecfeed.utils.EclipseHelper;


public class AboutDialog  {

	public static void open() {
		HttpComunicator httpComunicator = new HttpComunicator();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpComunicator);

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("EcFeed is a tool that allows to design, model and execute tests for Java, Android and Web projects.\n");
		stringBuilder.append("\n");
		stringBuilder.append("Copyright (c) 2016 ecFeed AS.\n");
		stringBuilder.append("\n");
		stringBuilder.append("https://github.com/ecfeed/wiki" + "\n");
		stringBuilder.append("\n");
		stringBuilder.append("Your version: " + ApplicationContext.getEcFeedVersion() + "\n");
		stringBuilder.append("\n");


		stringBuilder.append("Latest distributed version: ");
		if (currentReleases.versionStandard != null) {
			stringBuilder.append(currentReleases.versionStandard);
		} else {
			stringBuilder.append("unavailable");
		}
		stringBuilder.append("\n");


		stringBuilder.append("Latest beta version: ");
		if (currentReleases.versionBeta != null) {
			stringBuilder.append(currentReleases.versionBeta);
		} else {
			stringBuilder.append("unavailable");
		}
		stringBuilder.append("\n");		


		MessageDialog.openInformation(
				EclipseHelper.getActiveShell(), "About ecFeed", stringBuilder.toString());
	}

}
