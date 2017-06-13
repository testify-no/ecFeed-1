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
import com.ecfeed.core.net.IHttpCommunicator;
import com.ecfeed.core.utils.SystemHelper;


public class VersionCheckerAndRegistrator {

	public static CurrentReleases registerAndGetCurrentReleases(IHttpCommunicator httpComunicator, int timeoutInSeconds) {

		List<HttpProperty> properties = new ArrayList<HttpProperty>();

		properties.add(createUserAgentProperty());
		properties.add(createEcIdProperty());

		try {
			return sendAndParseRequest(httpComunicator, properties, timeoutInSeconds);
		} catch (Exception e) {
			return null;
		}
	}

	private static CurrentReleases sendAndParseRequest(
			IHttpCommunicator httpComunicator, 
			List<HttpProperty> properties,
			int timeoutInSeconds) throws Exception {

		String url = "http://www.ecfeed.com/get_releases.php";

		String xmlResponse = httpComunicator.sendGetRequest(url, properties, timeoutInSeconds);

		return ReleasesXmlParser.parseXml(xmlResponse);		
	}

	private static HttpProperty createUserAgentProperty() {

		String ecFeedVersion = ApplicationContext.getEcFeedVersion();
		if (ecFeedVersion == null) {
			ecFeedVersion = "unknownVersion";
		}

		String operatingSystem;
		if (ApplicationContext.isStandaloneApplication()) {
			operatingSystem = SystemHelper.getOperatingSystemType();
		} else {
			operatingSystem = "All";
		}

		String productType;
		if (ApplicationContext.isStandaloneApplication()) {
			productType = "ES";
		} else {
			productType = "EP";
		}

		return new HttpProperty(
				"User-Agent", 
				"ecFeed; " + productType + "; " + ecFeedVersion + "; " + operatingSystem + ";");
	}

	private static HttpProperty createEcIdProperty() {

		String ecId = SystemHelper.createEcId();

		if (ecId == null) {
			ecId = "Err";
		}

		return new HttpProperty("Ec-Id", ecId);
	}

}
