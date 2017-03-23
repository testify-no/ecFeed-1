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


public class ExportTemplateHolderFactory {

	private static String FORMAT_CSV = "CSV";
	private static String FORMAT_XML = "XML";

	MethodNode fMethodNode;

	public ExportTemplateHolderFactory(MethodNode methodNode) {
		fMethodNode = methodNode;
	}

	public IExportTemplateHolder createDefaultHolder() {
		return createHolder(getDefaultFormat());
	}

	public IExportTemplateHolder createHolder(String formatName) {

		IExportTemplateHolder exportTemplateHolder = createHolderIntr(formatName);
		exportTemplateHolder.initialize();

		return exportTemplateHolder;
	}

	private IExportTemplateHolder createHolderIntr(String formatName) {

		if (formatName.equals(FORMAT_CSV)) {
			return new CsvExportTemplateHolder(fMethodNode);
		}
		if (formatName.equals(FORMAT_XML)) {
			return new XmlExportTemplateHolder(fMethodNode);
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
