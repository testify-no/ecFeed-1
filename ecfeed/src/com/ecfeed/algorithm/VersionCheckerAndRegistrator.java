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

import com.ecfeed.application.SessionContext;
import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.net.IHttpCommunicator;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.core.utils.SystemLogger;


public class VersionCheckerAndRegistrator {

	public static void registerApp(IHttpCommunicator httpCommunicator, int timeoutInSeconds) {

		List<HttpProperty> properties = createHttpProperties();

		try {
			sendRegisteringRequest(httpCommunicator, properties, timeoutInSeconds);
		} catch (Exception e) {
			SystemLogger.logCatch("Can not register application.");
		}
	}

	public static CurrentReleases registerAppAndGetCurrentReleases(IHttpCommunicator httpCommunicator, int timeoutInSeconds) {

		List<HttpProperty> properties = createHttpProperties();

		try {
			return sendAndParseRequest(httpCommunicator, properties, timeoutInSeconds);
		} catch (Exception e) {
			return null;
		}
	}

	private static List<HttpProperty> createHttpProperties() {

		List<HttpProperty> properties = new ArrayList<HttpProperty>();

		properties.add(createUserAgentProperty());
		properties.add(createEcIdProperty());

		return properties;
	}

	private static CurrentReleases sendAndParseRequest(
			IHttpCommunicator httpComunicator, 
			List<HttpProperty> properties,
			int timeoutInSeconds) throws Exception {

		String xmlResponse = sendRegisteringRequest(httpComunicator, properties, timeoutInSeconds);

		return ReleasesXmlParser.parseXml(xmlResponse);		
	}

	private static String sendRegisteringRequest(
			IHttpCommunicator httpComunicator, 
			List<HttpProperty> properties,
			int timeoutInSeconds) throws Exception {

		String url = "http://www.ecfeed.com/get_releases.php";

		return httpComunicator.sendGetRequest(url, properties, timeoutInSeconds);
	}	

	private static HttpProperty createUserAgentProperty() {

		String ecFeedVersion = SessionContext.getEcFeedVersion();
		if (ecFeedVersion == null) {
			ecFeedVersion = "unknownVersion";
		}

		String operatingSystem;
		if (SessionContext.isApplicationTypeLocalStandalone()) {
			operatingSystem = SystemHelper.getOperatingSystemType();
		} else {
			operatingSystem = "All";
		}

		String productType = "?";

		if (SessionContext.isApplicationTypeLocalStandalone()) {
			productType = "ES";
		} 

		if (SessionContext.isApplicationTypeLocalPlugin()) {
			productType = "EP";
		}

		if (SessionContext.isApplicationTypeRemoteRap()) {
			productType = "ER";
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
