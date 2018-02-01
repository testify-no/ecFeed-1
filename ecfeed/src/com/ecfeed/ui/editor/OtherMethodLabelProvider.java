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

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;

public class OtherMethodLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		List<MethodNode> othermethods = (List<MethodNode>) element;
//		String result = othermethods.to
		for(MethodNode method: othermethods){
			for(AbstractParameterNode parameter: method.getParameters()){
//				String result = parameter.toString();
				String result = parameter.toString();
				
				
			}
		}
		return null;
	}

		
}
