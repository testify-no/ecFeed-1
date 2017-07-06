/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.utils;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class EclipseHelper {
	
	public static Shell getActiveShell() {
		return Display.getDefault().getActiveShell();
	}

	public static IWorkbenchPage getActiveWorkBenchPage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	public static IAction getGlobalAction(String actionId) {
		IActionBars actionBars = EclipseHelper.getActionBarsForActiveEditor();
		return actionBars.getGlobalActionHandler(actionId);
	}

	public static IActionBars getActionBarsForActiveEditor() {
		IEditorPart editorPart = ModelEditorPlatformAdapter.getActiveEditor();

		if (editorPart == null) {
			return null;
		}
		return editorPart.getEditorSite().getActionBars();
	}


}
