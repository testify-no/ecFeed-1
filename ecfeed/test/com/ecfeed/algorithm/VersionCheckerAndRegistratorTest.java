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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.net.IHttpComunicator;
import com.ecfeed.core.utils.StringHelper;


public class VersionCheckerAndRegistratorTest {

	@Test
	public void shouldReturnNullWhenHttpResponseIsNull() {

		class HttpCommunicatorStub implements IHttpComunicator {

			@Override
			public String sendGetRequest(String url, List<HttpProperty> properties) throws RuntimeException {

				return null;
			}
		}

		HttpCommunicatorStub httpCommunicatorStub = new HttpCommunicatorStub();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorStub);

		assertEquals(null, currentReleases);
	}	

	@Test
	public void shouldCheckRequestParameters() {

		class HttpCommunicatorStub implements IHttpComunicator {

			@Override
			public String sendGetRequest(String url, List<HttpProperty> properties) throws RuntimeException {

				if (!StringHelper.isEqual(url, "http://www.ecfeed.com/get_releases.php"))
					return null;

				if (properties.size() != 2) {
					return null;
				}

				HttpProperty property0 = properties.get(0);
				assertEquals("User-Agent", property0.getKey());
				assertEquals("ecFeed; EP; unknownVersion; All;", property0.getValue());

				HttpProperty property1 = properties.get(1);
				assertEquals("Ec-Id", property1.getKey());

				return createEmptyResponse();
			}
		}

		HttpCommunicatorStub httpCommunicatorStub = new HttpCommunicatorStub();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorStub);

		testEmptyResponse(currentReleases);
	}

	@Test
	public void shouldCreateEmptyResponse() {

		class HttpCommunicatorStub implements IHttpComunicator {

			@Override
			public String sendGetRequest(String url, List<HttpProperty> properties) throws RuntimeException {
				return createEmptyResponse();
			}
		}

		HttpCommunicatorStub httpCommunicatorStub = new HttpCommunicatorStub();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorStub);

		testEmptyResponse(currentReleases);
	}

	@Test
	public void shouldCreateFullResponse() {

		class HttpCommunicatorStub implements IHttpComunicator {

			@Override
			public String sendGetRequest(String url, List<HttpProperty> properties) throws RuntimeException {
				return createFullResponse();
			}
		}

		HttpCommunicatorStub httpCommunicatorStub = new HttpCommunicatorStub();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorStub);

		testFullResponse(currentReleases);
	}	

	static String createEmptyResponse() {
		StringBuilder stringBuilder = new StringBuilder();  

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuilder.append("<releases>\n");
		stringBuilder.append("</releases>\n");

		return stringBuilder.toString();		
	}

	static void testEmptyResponse(CurrentReleases currentReleases) {

		assertNotEquals(null, currentReleases);

		assertEquals(null, currentReleases.versionStandard);
		assertEquals(null, currentReleases.linkStandard);

		assertEquals(null, currentReleases.versionBeta);
		assertEquals(null, currentReleases.linkBeta);
	}

	static String createFullResponse() {
		StringBuilder stringBuilder = new StringBuilder();  

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuilder.append("<releases>\n");

		stringBuilder.append("\t<release>");
		stringBuilder.append("\t\t<type>S</type>");
		stringBuilder.append("\t\t<version> " + "versionS" + "</version>");
		stringBuilder.append("\t\t<link> " + "linkS" + "</link>");
		stringBuilder.append( "\t</release>");

		stringBuilder.append("\t<release>");
		stringBuilder.append("\t\t<type>B</type>");
		stringBuilder.append("\t\t<version> " + "versionB" + "</version>");
		stringBuilder.append("\t\t<link> " + "linkB" + "</link>");
		stringBuilder.append( "\t</release>");			

		stringBuilder.append("</releases>\n");

		return stringBuilder.toString();		
	}

	static void testFullResponse(CurrentReleases currentReleases) {

		assertNotEquals(null, currentReleases);

		assertEquals("versionS", currentReleases.versionStandard);
		assertEquals("linkS", currentReleases.linkStandard);

		assertEquals("versionB", currentReleases.versionBeta);
		assertEquals("linkB", currentReleases.linkBeta);
	}	

}
