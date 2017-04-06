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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.junit.Test;

import com.ecfeed.core.utils.StringHelper;


public class ReleasesXmlParser {

	public static List<VersionData> parseXml(String xmlResponse) throws Exception {

		InputStream stream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));
		Builder fBuilder = new Builder();

		Document document = fBuilder.build(stream);
		Element rootElement = document.getRootElement();
		System.out.println("RootElement: " + rootElement);

		Elements releases = rootElement.getChildElements();

		List<VersionData> listOfVersionData = new ArrayList<VersionData>();

		for (int i = 0; i < releases.size(); i++) {
			Element release = releases.get(i);
			VersionData versionData = getDataForRelease(release);
			listOfVersionData.add(versionData);
		}

		return listOfVersionData;
	}

	private static VersionData getDataForRelease(Element release) {

		VersionData versionData = new VersionData();

		Elements children = release.getChildElements();

		for (int i = 0; i < children.size(); i++) {
			convertReleasePart(children.get(i), versionData);
		}

		return versionData;
	}

	private static void convertReleasePart(Element releaseChild, VersionData inOutVersionData) {

		if (StringHelper.isEqual(releaseChild.getLocalName(), "os")) {
			inOutVersionData.os = releaseChild.getValue();
		}
		if (StringHelper.isEqual(releaseChild.getLocalName(), "version")) {
			inOutVersionData.version = releaseChild.getValue();
		}		
	}

	@Test
	public void testCreateVersionData() {

		String xmlResponse = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<releases>" +
						"    <release>" +
						"        <os>linux</os>" +
						"        <version>1.11.10</version>" +
						"    </release>" +
						"    <release>" +
						"        <os>linux</os>" +
						"        <version>1.11.11</version>" +
						"    </release>" + 
						"</releases>";


		List<VersionData> listOfVersionData = null;

		try {
			listOfVersionData = parseXml(xmlResponse);
		} catch (Exception e) {
			fail();
		}

		assertEquals(2, listOfVersionData.size());

		VersionData result = listOfVersionData.get(0); 
		assertEquals("linux", result.os);
		assertEquals("1.11.10", result.version);
	}

}
