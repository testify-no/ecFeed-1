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

import org.junit.Test;


public class ReleasesXmlParserTest {

	@Test
	public void testCreateVersionData() {

		String xmlResponse = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<releases>" +
						"    <release>" +
						"        <type>S</type>" +
						"        <version>1.11.10</version>" +
						"        <link>linkS</link>" +
						"    </release>" +
						"    <release>" +
						"        <type>B</type>" +
						"        <version>1.11.11</version>" +
						"        <link>linkB</link>" +
						"    </release>" + 
						"</releases>";


		CurrentReleases currentReleases = null;;
		
		try {
			currentReleases = ReleasesXmlParser.parseXml(xmlResponse);
		} catch (Exception e) {
			fail();
		}

		assertEquals("1.11.10", currentReleases.versionStandard);
		assertEquals("linkS", currentReleases.linkStandard);
		assertEquals("1.11.11", currentReleases.versionBeta);
		assertEquals("linkB", currentReleases.linkBeta);		
	}

}
