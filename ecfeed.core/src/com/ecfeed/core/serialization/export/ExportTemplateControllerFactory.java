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

import com.ecfeed.core.model.MethodNode;


public class ExportTemplateControllerFactory {

	MethodNode fMethodNode;

	public ExportTemplateControllerFactory(MethodNode methodNode) {
		fMethodNode = methodNode;
	}

	public IExportTemplateController createDefaultController() {
		return createController(getDefaultFormat());
	}

	public IExportTemplateController createController(String formatName) {
		if (formatName.equals("CSV")) {
			return new CsvExportTemplateController(fMethodNode);
		}
		if (formatName.equals("XML")) {
			return new XmlExportTemplateController(fMethodNode);
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
