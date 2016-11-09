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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.core.model.NodePropertyDescriptions;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class RunnerSection extends BasicSection  {

	MethodInterface fMethodInterface;
	private FormObjectToolkit fFormObjectToolkit;
	private Combo fRunnersCombo;
	
	private static final String RUNNER_JAVA = "JavaRunner";
	private static final String RUNNER_WEB = "WebDriver";

	public RunnerSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			MethodInterface methodIf,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());
		
		fMethodInterface = methodIf;
		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Runner");
		
		Composite clientComposite = getClientComposite();

		Composite gridComposite = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite);

		fFormObjectToolkit.createLabel(gridComposite, "Runner");
		fRunnersCombo = fFormObjectToolkit.createGridCombo(gridComposite, new RunnerChangedAdapter());

		fRunnersCombo.setItems(availableRunners()); 
		fRunnersCombo.setText(RUNNER_JAVA);		
	}

	private static String[] availableRunners() {
		final String[] TYPES = new String[]{
				RUNNER_JAVA,
				RUNNER_WEB,
		};

		return TYPES;
	}	
	
	private class RunnerChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			NodePropertyDescriptions.PropertyId propertyId = NodePropertyDescriptions.PropertyId.METHOD_RUNNER;
			
			String name = NodePropertyDescriptions.getPropertyName(propertyId);
			String type = NodePropertyDescriptions.getPropertyType(propertyId);
			
			fMethodInterface.setProperty(name, type, fRunnersCombo.getText());
		}
	}

}
