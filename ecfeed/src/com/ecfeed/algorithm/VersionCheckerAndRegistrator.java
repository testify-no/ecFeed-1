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
import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.net.IHttpComunicator;
import com.ecfeed.core.utils.SystemHelper;


public class VersionCheckerAndRegistrator {

	public static CurrentReleases registerAndGetCurrentReleases(IHttpComunicator httpComunicator) {

		List<HttpProperty> properties = new ArrayList<HttpProperty>();

		properties.add(createUserAgentProperty());
		properties.add(createEcIdProperty());

		try {
			return sendAndParseRequest(httpComunicator, properties);
		} catch (Exception e) {
			return null;
		}
	}

	private static CurrentReleases sendAndParseRequest(IHttpComunicator httpComunicator, List<HttpProperty> properties) throws Exception {

		String url = "http://localhost/ecfeed/get_releases.php";

		String xmlResponse = httpComunicator.sendGetRequest(url, properties);

		return ReleasesXmlParser.parseXml(xmlResponse);		
	}

	private static HttpProperty createUserAgentProperty() {

		String ecFeedVersion = ApplicationContext.getEcFeedVersion();
		if (ecFeedVersion == null) {
			ecFeedVersion = "unknownVersion";
		}

		return new HttpProperty(
				"User-Agent", 
				"ecFeed; " + ecFeedVersion + "; " + SystemHelper.getOperatingSystem() + ";");
	}

	private static HttpProperty createEcIdProperty() {

		String ecId = SystemHelper.createEcId();

		if (ecId == null) {
			ecId = "Err";
		}

		return new HttpProperty("Ec-Id", ecId);
	}

}
