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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.net.HttpHelper;
import com.ecfeed.core.net.HttpProperty;


public class VersionCheckerAndRegistrator {

	public static List<VersionData> registerAndGetCurrentReleases() throws IOException {

		String url = "http://localhost/ecfeed/get_releases.php";

		List<HttpProperty> properties = new ArrayList<HttpProperty>();

		properties.add(createUserAgentProperty());

		String response = HttpHelper.sendGetRequest(url, properties);

		return createVersionData(response);
	}

	private static HttpProperty createUserAgentProperty() {
		return new HttpProperty("User-Agent", "ecFeed " + ApplicationContext.getEcFeedVersion());
	}

	private static List<VersionData> createVersionData(String xmlResponse) {

		try {
			return ReleasesXmlParser.parseXml(xmlResponse); 
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}

}
