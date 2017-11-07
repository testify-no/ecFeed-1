/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.context;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.ApplicationContext;
import com.ecfeed.core.utils.SessionAttributes;
import com.ecfeed.ui.editor.ModelEditorHelper;


public class UndoContextHelper {

	public static void setOnceRapUndoContextObject(Object obj) {

		ObjectUndoContext objectUndoContext = getRapContextFromDataStore();

		if (objectUndoContext != null) {
			return;
		}

		ObjectUndoContext rapObjectUndoContext = new ObjectUndoContext(obj);
		SessionDataStore.set(SessionAttributes.SA_RAP_UNDO_CONTEXT, rapObjectUndoContext);
	}

	public static IUndoContext getUndoContext() {

		if (ApplicationContext.isApplicationTypeLocal()) {
			return ModelEditorHelper.getActiveModelEditor().getUndoContext();
		}

		return getRapContextFromDataStore();
	}

	private static ObjectUndoContext getRapContextFromDataStore() {

		return (ObjectUndoContext)SessionDataStore.get(SessionAttributes.SA_RAP_UNDO_CONTEXT);
	}
}

