/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.net.HttpHelper;
import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.utils.SystemHelper;


public class VersionCheckerAndRegistrator {

	public static CurrentReleases registerAndGetCurrentReleases() {

		List<HttpProperty> properties = new ArrayList<HttpProperty>();

		properties.add(createUserAgentProperty());
		properties.add(createMachineIdProperty());

		try {
			return sendAndParseRequest(properties);
		} catch (Exception e) {
			return null;
		}
	}

	private static CurrentReleases sendAndParseRequest(List<HttpProperty> properties) throws Exception {
		
		String url = "http://localhost/ecfeed/get_releases.php";
		
		String xmlResponse = HttpHelper.sendGetRequest(url, properties);

		return ReleasesXmlParser.parseXml(xmlResponse);		
	}
	
	private static HttpProperty createUserAgentProperty() {
		
		return new HttpProperty(
				"User-Agent", 
				"ecFeed; " + ApplicationContext.getEcFeedVersion() + "; " + SystemHelper.getOperatingSystem() + ";");
	}

	private static HttpProperty createMachineIdProperty() {
		
		return new HttpProperty("Machine-Id", "1234567ABC"); // TODO
	}
	
}
