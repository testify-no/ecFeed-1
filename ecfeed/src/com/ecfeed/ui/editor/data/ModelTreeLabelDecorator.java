/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelTreeLabelDecorator implements ILabelDecorator {

	private IJavaProjectProvider fJavaProjectProvider;
	private Map<List<Image>, Image> fDecoratedImagesCache;
	IModelUpdateContext fUpdateContext;

	public ModelTreeLabelDecorator(
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		fUpdateContext = updateContext;
		fJavaProjectProvider = javaProjectProvider;
		fDecoratedImagesCache = new HashMap<List<Image>, Image>();
	}

	@Override
	public Image decorateImage(Image imageToDecorate, Object element) {

		if (!(element instanceof AbstractNode)) {
			return imageToDecorate;
		}

		AbstractNode abstractNode = (AbstractNode)element;

		return ModelTreeLabelDecoratorHelper.decorateImageOfAbstractNode(
				imageToDecorate, 
				abstractNode,
				fDecoratedImagesCache,
				fUpdateContext, 
				fJavaProjectProvider);
	}

	@Override
	public String decorateText(String text, Object element) {

		return text;
	}

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

}
