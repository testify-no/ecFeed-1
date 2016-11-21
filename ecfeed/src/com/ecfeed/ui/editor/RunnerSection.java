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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class RunnerSection extends BasicSection  {

	MethodInterface fMethodInterface;
	private FormObjectToolkit fFormObjectToolkit;
	private Combo fRunnerCombo;
	private Button fMapBrowserCheckbox;
	private Combo fBrowserCombo;
	private Text fStartUrlText;

	private final NodePropertyDefs.PropertyId fRunnerPropertyId = NodePropertyDefs.PropertyId.METHOD_RUNNER;
	private final NodePropertyDefs.PropertyId fMapBrowserPropertyId = NodePropertyDefs.PropertyId.MAP_BROWSER_TO_PARAM;
	private final NodePropertyDefs.PropertyId fBrowserPropertyId = NodePropertyDefs.PropertyId.WEB_BROWSER;
	private final NodePropertyDefs.PropertyId fStartUrlPropertyId = NodePropertyDefs.PropertyId.START_URL;

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
		fRunnerCombo = fFormObjectToolkit.createGridCombo(gridComposite, new RunnerChangedAdapter());
		fRunnerCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fRunnerPropertyId)); 
		fRunnerCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fRunnerPropertyId));

		fFormObjectToolkit.createLabel(gridComposite, " ");
		fMapBrowserCheckbox = 
				fFormObjectToolkit.createGridCheckBox(
						gridComposite, "Map map browser to method parameter", new SetMapBrowserListener());

		fFormObjectToolkit.createLabel(gridComposite, "Browser");
		fBrowserCombo = fFormObjectToolkit.createGridCombo(gridComposite, new BrowserChangedAdapter());
		fBrowserCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fBrowserPropertyId)); 

		fFormObjectToolkit.createLabel(gridComposite, "URL");
		fStartUrlText = fFormObjectToolkit.createGridText(gridComposite, new UrlChangedAdapter());
	}

	public void refresh() {
		MethodNode methodNode = fMethodInterface.getTarget();

		String runner = methodNode.getPropertyValue(fRunnerPropertyId);
		if (runner != null) {
			fRunnerCombo.setText(runner);
		}

		String mapBrowserToParam = methodNode.getPropertyValue(fMapBrowserPropertyId);
		setCheckbox(fMapBrowserCheckbox, mapBrowserToParam);
		fBrowserCombo.setEnabled(!BooleanHelper.parseBoolean(mapBrowserToParam));

		String browser = methodNode.getPropertyValue(fBrowserPropertyId);
		if (browser != null) {
			fBrowserCombo.setText(browser);
		}

		String url = methodNode.getPropertyValue(fStartUrlPropertyId);
		if (url!= null) {
			fStartUrlText.setText(url);
		}
	}

	private void setCheckbox(Button checkBox, String value) {
		if (value == null) {
			checkBox.setSelection(false);
			return;
		}

		boolean isSelected = BooleanHelper.parseBoolean(value);
		checkBox.setSelection(isSelected);
	}

	private class RunnerChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fRunnerPropertyId, fRunnerCombo.getText());
		}
	}

	private class SetMapBrowserListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean selected = fMapBrowserCheckbox.getSelection();
			fBrowserCombo.setEnabled(!selected);
			fMethodInterface.setProperty(fMapBrowserPropertyId, BooleanHelper.toString(selected));
		}
	}	

	private class BrowserChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fBrowserPropertyId, fBrowserCombo.getText());
		}
	}


	private class UrlChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fStartUrlPropertyId, fStartUrlText.getText());
		}
	}

}
