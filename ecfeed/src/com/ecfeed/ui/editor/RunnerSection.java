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

import org.eclipse.swt.widgets.Composite;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class RunnerSection extends BasicSection  {

	private FormObjectToolkit fFormObjectToolkit;

	public RunnerSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		setText("Runner");
		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		Composite clientComposite = getClientComposite();

		Composite gridComposite = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite);

		fFormObjectToolkit.createLabel(gridComposite, "Runner");
		fFormObjectToolkit.createGridText(gridComposite, null);
	}

}
