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


public interface IExportTemplateController {

	void initialize();

	String createDefaultTemplateText();
	void setTemplateText(String summaryTemplate);
	String getTemplateText();
	boolean isTemplateTextModified();

	void setHeaderTemplate(String headerTemplate);
	void setTestCaseTemplate(String testCaseTemplate);
	void setFooterTemplate(String footerTemplate);

	String getFooterTemplate();
	String getHeaderTemplate();
	String getTestCaseTemplate();
}
