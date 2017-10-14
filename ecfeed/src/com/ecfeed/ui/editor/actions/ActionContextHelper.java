/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.ui.editor.ModelEditorHelper;


public class ActionContextHelper {

	private static ObjectUndoContext fRapObjectUndoContext = null;

	public static void setRapUndoContextObject(Object obj) {
		fRapObjectUndoContext = new ObjectUndoContext(obj);
	}

	public static IUndoContext getUndoContext() {

		if (ApplicationContext.isApplicationTypeLocal()) {
			return ModelEditorHelper.getActiveModelEditor().getUndoContext();
		}

		return fRapObjectUndoContext;
	}

}

