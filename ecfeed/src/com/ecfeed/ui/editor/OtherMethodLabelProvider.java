/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.LabelProvider;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;

public class OtherMethodLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof MethodNode) {
			MethodNode othermethods = (MethodNode) element;
			if (ApplicationContext.getSimplifiedUI()) {
				return MethodNodeHelper.simplifiedToSimpleModeString(othermethods);
			} else {
				return othermethods.toString();
			}
		}
		return null;
	}
}
