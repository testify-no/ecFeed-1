/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.ecfeed.application.SessionContext;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.export.IExportTemplate;
import com.ecfeed.serialization.export.TestCasesExporter;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.ui.dialogs.basic.InfoDialog;
import com.testify.ecfeed.rap.application.DownloadManager;

public class TestCasesExportHelper {

	private static final String EXPORT_FINISHED = "Export finished.";

	public static void runExport(
			MethodNode methodNode,
			Collection<TestCaseNode> testCases, 
			IExportTemplate exportTemplate, 
			String targetFile) {

		OutputStream outputStream = createOutputStream(targetFile);

		try {
			runExportToStream(methodNode, testCases, exportTemplate, outputStream);

			if (SessionContext.isApplicationTypeRemoteRap()) {
				DownloadManager.downloadFile(targetFile, outputStream.toString());
			}

		} finally {
			closeOutputStream(outputStream);
		}

		InfoDialog.open(EXPORT_FINISHED);
	}

	private static void runExportToStream(
			MethodNode methodNode,
			Collection<TestCaseNode> testCases,
			IExportTemplate exportTemplate,
			OutputStream outputStream) {

		try {
			TestCasesExporter exporter = new TestCasesExporter(exportTemplate);

			exporter.runExportToStream(methodNode, testCases, outputStream);

		} catch (Exception e) {
			ErrorDialog.open(e.getMessage());
			return;
		}
	}

	private static OutputStream createOutputStream(String targetFile) {

		OutputStream outputStream;
		try {
			if (SessionContext.isApplicationTypeLocal()) {
				outputStream = new FileOutputStream(targetFile);
			} else {
				outputStream = new ByteArrayOutputStream();
			}
		} catch (FileNotFoundException e) {
			ErrorDialog.open(e.getMessage());
			return null;
		}

		return outputStream;
	}

	private static void closeOutputStream(OutputStream outputStream) {
		try {
			outputStream.close();
		} catch (IOException e) {
			ErrorDialog.open(e.getMessage());
		}
	}

}
