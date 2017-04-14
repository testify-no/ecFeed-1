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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import com.ecfeed.core.utils.StringHelper;


public class ReleasesXmlParser {

	public static CurrentReleases parseXml(String xmlResponse) throws Exception {

		InputStream stream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));
		Builder fBuilder = new Builder();

		Document document = fBuilder.build(stream);
		Element rootElement = document.getRootElement();

		Elements releases = rootElement.getChildElements();

		CurrentReleases currentReleases = new CurrentReleases();

		for (int i = 0; i < releases.size(); i++) {
			Element release = releases.get(i);
			setCurrentReleaseData(release, currentReleases);
		}

		return currentReleases;
	}

	private static void setCurrentReleaseData(Element release, CurrentReleases inOutCurrentReleases) {

		Elements releaseChildren = release.getChildElements();

		String releaseType = getCurrentReleaseType(release);

		for (int i = 0; i < releaseChildren.size(); i++) {
			convertReleasePart(releaseChildren.get(i), releaseType, inOutCurrentReleases);
		}
	}

	private static String getCurrentReleaseType(Element release) {

		Elements releaseChildren = release.getChildElements();

		for (int i = 0; i < releaseChildren.size(); i++) {
			Element releaseChild = releaseChildren.get(i);

			if (StringHelper.isEqual(releaseChild.getLocalName(), "type")) {
				return releaseChild.getValue();
			}		
		}

		return null;
	}

	private static void convertReleasePart(Element releaseChild, String releaseType, CurrentReleases currentReleases) {

		if (StringHelper.isEqual(releaseChild.getLocalName(), "version")) {
			String version = releaseChild.getValue().trim();

			if (StringHelper.isEqual(releaseType, "S")) {
				currentReleases.versionStandard = version;
			}

			if (StringHelper.isEqual(releaseType, "B")) {
				currentReleases.versionBeta = version;
			}			
		}

		if (StringHelper.isEqual(releaseChild.getLocalName(), "link")) {
			String link = releaseChild.getValue().trim();

			if (StringHelper.isEqual(releaseType, "S")) {
				currentReleases.linkStandard = link;
			}

			if (StringHelper.isEqual(releaseType, "B")) {
				currentReleases.linkBeta = link;
			}			
		}		
	}

}
