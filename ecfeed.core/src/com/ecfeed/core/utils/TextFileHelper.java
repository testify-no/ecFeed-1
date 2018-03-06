/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TextFileHelper {

	public static void append(String filename, String message) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
		writer.write(message);
		writer.close();
	}

	public static void appendLine(String filename, String message) throws IOException {
		String msg = StringHelper.appendNewline(message);
		append(filename, msg);
	}

	public static String readContent(String filePath) throws EcException {

		FileInputStream inputStream = StreamHelper.openInputStream(filePath);
		String content = inputStream.toString();
		StreamHelper.closeInputStream(inputStream);

		return content;
	}

	public static void writeContent(String filePath, String newContent) throws EcException {

		PrintWriter printWriter = null;
		try {

			printWriter = new PrintWriter(filePath);
			printWriter.println(newContent);
			printWriter.flush();

		} catch (FileNotFoundException e) {

			EcException.report(e.getMessage());

		} finally {

			printWriter.close();
		}
	}	
}
