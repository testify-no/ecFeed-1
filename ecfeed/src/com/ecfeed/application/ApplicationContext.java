/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.application;

import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

public class ApplicationContext {

	static boolean fIsStandaloneApplication = false;
	static String fExportFileName;

	private static UndoActionHandler fUndoActionHandler;
	private static RedoActionHandler fRedoActionHandler;
	
	
	public static boolean isStandaloneApplication() {
		return fIsStandaloneApplication;
	}

	public static void setStandaloneApplication() {
		fIsStandaloneApplication = true;
	}

	public static void setExportTargetFile(String exportFileName) {
		fExportFileName = exportFileName;
	}

	public static String getExportTargetFile() {
		return fExportFileName;
	}	
	
	public static void setUndoHandler(UndoActionHandler undoActionHandler) {
		fUndoActionHandler = undoActionHandler;
	}
	
	public static UndoActionHandler getUndoHandler() {
		return fUndoActionHandler;
	}	
	
	public static void setRedoHandler(RedoActionHandler undoActionHandler) {
		fRedoActionHandler = undoActionHandler;
	}
	
	public static RedoActionHandler getRedoHandler() {
		return fRedoActionHandler;
	}	

}
