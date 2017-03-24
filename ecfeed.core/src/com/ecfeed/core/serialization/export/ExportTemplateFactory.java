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


public class ExportTemplateFactory {

	private static String FORMAT_CSV = "CSV";
	private static String FORMAT_XML = "XML";

	MethodNode fMethodNode;

	public ExportTemplateFactory(MethodNode methodNode) {
		fMethodNode = methodNode;
	}

	public IExportTemplate createDefaultHolder() {
		return createHolder(getDefaultFormat());
	}

	public IExportTemplate createHolder(String formatName) {

		IExportTemplate exportTemplate = createHolderIntr(formatName);
		exportTemplate.initialize();

		return exportTemplate;
	}

	private IExportTemplate createHolderIntr(String formatName) {

		if (formatName.equals(FORMAT_CSV)) {
			return new CsvExportTemplate(fMethodNode);
		}
		if (formatName.equals(FORMAT_XML)) {
			return new XmlExportTemplate(fMethodNode);
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
