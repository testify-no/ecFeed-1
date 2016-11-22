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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class MethodRunnerSection extends BasicSection  {

	MethodInterface fMethodInterface;
	private FormObjectToolkit fFormObjectToolkit;
	private Combo fRunnerCombo;
	private Button fMapBrowserCheckbox;
	private Combo fBrowserCombo;
	private Text fBrowserDriverText;
	private Button fMapStartUrlCheckbox;
	private Text fStartUrlText;
	private Composite fWebDriverPropertiesComposite;

	private final NodePropertyDefs.PropertyId fRunnerPropertyId = NodePropertyDefs.PropertyId.METHOD_RUNNER;
	private final NodePropertyDefs.PropertyId fMapBrowserPropertyId = NodePropertyDefs.PropertyId.MAP_BROWSER_TO_PARAM;
	private final NodePropertyDefs.PropertyId fBrowserPropertyId = NodePropertyDefs.PropertyId.WEB_BROWSER;
	private final NodePropertyDefs.PropertyId fBrowserDriverPropertyId = NodePropertyDefs.PropertyId.BROWSER_DRIVER;
	private final NodePropertyDefs.PropertyId fMapStartUrlPropertyId = NodePropertyDefs.PropertyId.MAP_START_URL_TO_PARAM;
	private final NodePropertyDefs.PropertyId fStartUrlPropertyId = NodePropertyDefs.PropertyId.START_URL;

	public MethodRunnerSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			MethodInterface methodIf,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fMethodInterface = methodIf;
		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Runner");

		Composite clientComposite = getClientComposite();

		Composite gridComposite1 = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite1);
		createRunnerCombo(gridComposite1);

		createWebDriverPropertiesComposite(clientComposite);
	}

	private void createWebDriverPropertiesComposite(Composite parentComposite) {
		fWebDriverPropertiesComposite = fFormObjectToolkit.createGridComposite(parentComposite, 2);
		fFormObjectToolkit.paintBorders(fWebDriverPropertiesComposite);
		createMapBrowserCheckBox(fWebDriverPropertiesComposite);
		createBrowserCombo(fWebDriverPropertiesComposite);
		createBrowserDriverPathText(fWebDriverPropertiesComposite);
		createMapStartUrlCheckBox(fWebDriverPropertiesComposite);		
		createUrlText(fWebDriverPropertiesComposite);
	}

	private void createRunnerCombo(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Runner");
		fRunnerCombo = fFormObjectToolkit.createGridCombo(gridComposite, new RunnerChangedAdapter());
		fRunnerCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fRunnerPropertyId)); 
		fRunnerCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fRunnerPropertyId));
	}

	private void createMapBrowserCheckBox(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, " ");
		fMapBrowserCheckbox = 
				fFormObjectToolkit.createGridCheckBox(
						gridComposite, "Map browser to method parameter", new SetMapBrowserListener());
	}

	private void createBrowserCombo(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Browser");
		fBrowserCombo = fFormObjectToolkit.createGridCombo(gridComposite, new BrowserChangedAdapter());
		fBrowserCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fBrowserPropertyId)); 
	}

	private void createBrowserDriverPathText(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Web driver  ");
		fBrowserDriverText = fFormObjectToolkit.createGridText(gridComposite, new BrowserDriverChangedAdapter());
	}

	private void createMapStartUrlCheckBox(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, " ");
		fMapStartUrlCheckbox = 
				fFormObjectToolkit.createGridCheckBox(
						gridComposite, "Map start URL to method parameter", new SetMapStartUrlListener());
	}

	private void createUrlText(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Start URL");
		fStartUrlText = fFormObjectToolkit.createGridText(gridComposite, new UrlChangedAdapter());
	}

	public void refresh() {
		MethodNode methodNode = fMethodInterface.getTarget();

		refreshComboByProperty(fRunnerPropertyId, fRunnerCombo, methodNode);

		String runner = methodNode.getPropertyValue(fRunnerPropertyId);

		if (NodePropertyDefs.isJavaRunnerMethod(runner)) {
			FormObjectToolkit.setVisibilityOfGridComposite(fWebDriverPropertiesComposite, false);
		} else {
			FormObjectToolkit.setVisibilityOfGridComposite(fWebDriverPropertiesComposite, true);

			refreshCheckboxByProperty(fMapBrowserPropertyId, fMapBrowserCheckbox, methodNode, new Control[]{fBrowserCombo, fBrowserDriverText});
			refreshComboByProperty(fBrowserPropertyId, fBrowserCombo, methodNode);
			refreshTextByProperty(fBrowserDriverPropertyId, fBrowserDriverText, methodNode);

			refreshCheckboxByProperty(fMapStartUrlPropertyId, fMapStartUrlCheckbox, methodNode, new Control[]{fStartUrlText});
			refreshTextByProperty(fStartUrlPropertyId, fStartUrlText, methodNode);
		}
	}

	private void refreshComboByProperty(NodePropertyDefs.PropertyId propertyId, Combo combo, MethodNode methodNode) {
		String value = methodNode.getPropertyValue(propertyId);
		if (value != null) {
			combo.setText(value);
		}		
	}

	private void refreshTextByProperty(NodePropertyDefs.PropertyId propertyId, Text text, MethodNode methodNode) {
		String value = methodNode.getPropertyValue(propertyId);
		if (value != null) {
			text.setText(value);
		}		
	}	

	private void refreshCheckboxByProperty(
			NodePropertyDefs.PropertyId propertyId, Button checkBox, MethodNode methodNode, Control[] additionalControls) {
		String mapBrowserToParam = methodNode.getPropertyValue(propertyId);
		setCheckbox(checkBox, mapBrowserToParam);

		for (Control control : additionalControls) {
			control.setEnabled(!BooleanHelper.parseBoolean(mapBrowserToParam));
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

	private class SetMapStartUrlListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean selected = fMapStartUrlCheckbox.getSelection();
			fStartUrlText.setEnabled(!selected);
			fMethodInterface.setProperty(fMapStartUrlPropertyId, BooleanHelper.toString(selected));
		}
	}	

	private class BrowserChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fBrowserPropertyId, fBrowserCombo.getText());
		}
	}

	private class BrowserDriverChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fBrowserDriverPropertyId, fBrowserDriverText.getText());
		}
	}	

	private class UrlChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fStartUrlPropertyId, fStartUrlText.getText());
		}
	}

}
