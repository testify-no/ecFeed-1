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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelLabelDecorator implements ILabelDecorator {

	IFileInfoProvider fFileInfoProvider;
	IModelUpdateContext fModelUpdateContext;
	Map<List<Image>, Image> fDecoratedImagesCache;

	public ModelLabelDecorator(IModelUpdateContext modelUpdateContext) {

		fModelUpdateContext = modelUpdateContext;
		fDecoratedImagesCache = new HashMap<List<Image>, Image>();
	}

	@Override
	public Image decorateImage(Image imageToDecorate, Object element) {

		if (!(element instanceof AbstractNode)) {
			return imageToDecorate;
		}

		return ModelLabelDecoratorHelper.decorateImageOfAbstractNode(
				imageToDecorate, 
				(AbstractNode)element, 
				fModelUpdateContext, 
				fFileInfoProvider, 
				fDecoratedImagesCache);
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

	public void setFileInfoProvider(IFileInfoProvider fileInfoProvider) {

		fFileInfoProvider = fileInfoProvider;
	}

}
