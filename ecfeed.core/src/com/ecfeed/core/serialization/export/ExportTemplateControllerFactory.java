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

	private static String FORMAT_CSV = "CSV";
	private static String FORMAT_XML = "XML";

	MethodNode fMethodNode;

	public ExportTemplateControllerFactory(MethodNode methodNode) {
		fMethodNode = methodNode;
	}

	public IExportTemplateController createDefaultController() {
		return createController(getDefaultFormat());
	}

	public IExportTemplateController createController(String formatName) {

		IExportTemplateController exportTemplateController = createControllerIntr(formatName);
		exportTemplateController.initialize();

		return exportTemplateController;
	}

	private IExportTemplateController createControllerIntr(String formatName) {

		if (formatName.equals(FORMAT_CSV)) {
			return new CsvExportTemplateController(fMethodNode);
		}
		if (formatName.equals(FORMAT_XML)) {
			return new XmlExportTemplateController(fMethodNode);
		}
		return null;
	}

	public static String[] getAvailableExportFormats() {
		String[] formats = { FORMAT_CSV, FORMAT_XML };
		return formats;
	}

	private static String getDefaultFormat() {
		return FORMAT_CSV;
	}

}
