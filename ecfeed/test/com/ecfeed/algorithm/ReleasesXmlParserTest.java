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

import java.util.List;

import org.junit.Test;


public class ReleasesXmlParserTest {

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
			listOfVersionData = ReleasesXmlParser.parseXml(xmlResponse);
		} catch (Exception e) {
			fail();
		}

		assertEquals(2, listOfVersionData.size());

		VersionData result = listOfVersionData.get(0); 
		assertEquals("linux", result.os);
		assertEquals("1.11.10", result.version);
	}

}
