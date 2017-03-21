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


public class ExportTemplateControllerFactory {

	public static IExportTemplateController createDefaultController() {
		return createController(getDefaultFormat());
	}

	public static IExportTemplateController createController(String formatName) {
		if (formatName.equals("CSV")) {
			return new CsvExportTemplateController();
		}
		if (formatName.equals("XML")) {
			return new XmlExportTemplateController();
		}
		return null;
	}

	public static String[] getAvailableExportFormats() {
		String[] formats = { "CSV", "XML" };
		return formats;
	}

	public static String getDefaultFormat() {
		return "CSV";
	}

}
