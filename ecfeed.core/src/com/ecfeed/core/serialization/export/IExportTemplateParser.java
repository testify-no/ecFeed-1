/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.serialization.export;

import java.util.Map;


public interface IExportTemplateParser {
	
	String createInitialTemplate();
	void createSubTemplates(String template);
	String getFooterTemplate();
	String getHeaderTemplate();
	String getTestCaseTemplate();
	Map<String, String> divideIntoSubtemplates(String templateText);
	
}
