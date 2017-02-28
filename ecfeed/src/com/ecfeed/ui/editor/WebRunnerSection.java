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
import com.ecfeed.ui.dialogs.basic.FileOpenDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class WebRunnerSection extends BasicSection  {

	MethodInterface fMethodInterface;
	private FormObjectToolkit fFormObjectToolkit;
	private Button fMapBrowserCheckbox;
	private Combo fBrowserCombo;
	private Text fBrowserDriverText;
	private Button fMapStartUrlCheckbox;
	private Text fStartUrlText;

	private boolean fSectionEnabled = false;

	private final NodePropertyDefs.PropertyId fMapBrowserPropertyId = NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM;
	private final NodePropertyDefs.PropertyId fBrowserPropertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER;
	private final NodePropertyDefs.PropertyId fBrowserDriverPropertyId = NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH;
	private final NodePropertyDefs.PropertyId fMapStartUrlPropertyId = NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM;
	private final NodePropertyDefs.PropertyId fStartUrlPropertyId = NodePropertyDefs.PropertyId.PROPERTY_START_URL;

	public WebRunnerSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			MethodInterface methodInterface,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fMethodInterface = methodInterface;
		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Web runner properties");

		Composite clientComposite = getClientComposite();
		fFormObjectToolkit.paintBorders(clientComposite);
		createWebDriverPropertiesComposite(clientComposite);
	}

	private void createWebDriverPropertiesComposite(Composite parentComposite) {

		Composite composite = fFormObjectToolkit.createGridComposite(parentComposite, 3);
		fFormObjectToolkit.paintBorders(composite);

		createMapBrowserCheckBox(composite);
		createBrowserCombo(composite);
		createBrowserDriverPathText(composite);
		createMapStartUrlCheckBox(composite);
		createUrlText(composite);
		fFormObjectToolkit.createEmptyLabel(composite);
	}

	private void createMapBrowserCheckBox(Composite gridComposite) {

		fFormObjectToolkit.createEmptyLabel(gridComposite);
		fMapBrowserCheckbox = 
				fFormObjectToolkit.createGridCheckBox(
						gridComposite, "Map browser to method parameter", new SetMapBrowserListener());
		fFormObjectToolkit.setHorizontalSpan(fMapBrowserCheckbox, 2);
	}

	private void createBrowserCombo(Composite gridComposite) {

		fFormObjectToolkit.createLabel(gridComposite, "Browser");
		fBrowserCombo = fFormObjectToolkit.createReadOnlyGridCombo(gridComposite, new BrowserChangedAdapter());
		fBrowserCombo.setItems(NodePropertyDefs.getValueSet(fBrowserPropertyId, null).getPossibleValues());
		fFormObjectToolkit.setHorizontalSpan(fBrowserCombo, 2);
	}

	private void createBrowserDriverPathText(Composite gridComposite) {

		fFormObjectToolkit.createLabel(gridComposite, "Web driver  ");
		fBrowserDriverText = fFormObjectToolkit.createGridText(gridComposite, new BrowserDriverApplier());
		fFormObjectToolkit.createButton(gridComposite, "Browse...", new BrowseButtonSelectionAdapter());
	}

	private void createMapStartUrlCheckBox(Composite gridComposite) {

		fFormObjectToolkit.createEmptyLabel(gridComposite);
		fMapStartUrlCheckbox = 
				fFormObjectToolkit.createGridCheckBox(
						gridComposite, "Map start URL to method parameter", new SetMapStartUrlListener());
		fFormObjectToolkit.setHorizontalSpan(fMapStartUrlCheckbox, 2);
	}

	private void createUrlText(Composite gridComposite) {

		fFormObjectToolkit.createLabel(gridComposite, "Start URL");
		fStartUrlText = fFormObjectToolkit.createGridText(gridComposite, new UrlApplier());

		fFormObjectToolkit.setHorizontalSpan(fStartUrlText, 2);
	}

	public void refresh() {

		MethodNode methodNode = fMethodInterface.getOwnNode();

		refreshCheckboxByProperty(fMapBrowserPropertyId, fMapBrowserCheckbox, methodNode, new Control[]{fBrowserCombo, fBrowserDriverText});
		refreshComboByProperty(fBrowserPropertyId, fBrowserCombo, methodNode);
		refreshTextByProperty(fBrowserDriverPropertyId, fBrowserDriverText, methodNode);

		refreshCheckboxByProperty(fMapStartUrlPropertyId, fMapStartUrlCheckbox, methodNode, new Control[]{fStartUrlText});
		refreshTextByProperty(fStartUrlPropertyId, fStartUrlText, methodNode);
	}

	private void refreshComboByProperty(NodePropertyDefs.PropertyId propertyId, Combo combo, MethodNode methodNode) {

		String value = methodNode.getPropertyValue(propertyId);
		if (value != null) {
			combo.setText(value);
			return;
		}		

		combo.setText(NodePropertyDefs.getEmptyElement());
	}

	private void refreshTextByProperty(NodePropertyDefs.PropertyId propertyId, Text text, MethodNode methodNode) {

		String value = methodNode.getPropertyValue(propertyId);
		if (value != null) {
			text.setText(value);
			return;
		}

		text.setText(NodePropertyDefs.getEmptyElement());
	}	

	private void refreshCheckboxByProperty(
			NodePropertyDefs.PropertyId propertyId, Button checkBox, MethodNode methodNode, Control[] additionalControls) {

		String mapBrowserToParam = methodNode.getPropertyValue(propertyId);
		setCheckbox(checkBox, mapBrowserToParam);

		for (Control control : additionalControls) {
			if (fSectionEnabled) {
				control.setEnabled(!BooleanHelper.parseBoolean(mapBrowserToParam));
			} else {
				control.setEnabled(false);
			}
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

	private class SetMapBrowserListener extends CheckBoxClickListener {

		@Override
		public void widgetSelected(SelectionEvent e) {

			boolean selected = fMapBrowserCheckbox.getSelection();

			if (fSectionEnabled) {
				fBrowserCombo.setEnabled(!selected);
			} else {
				fBrowserCombo.setEnabled(false);
			}

			fMethodInterface.setProperty(fMapBrowserPropertyId, BooleanHelper.toString(selected));
		}
	}	

	private class SetMapStartUrlListener extends CheckBoxClickListener {

		@Override
		public void widgetSelected(SelectionEvent e) {

			boolean selected = fMapStartUrlCheckbox.getSelection();

			if (fSectionEnabled) {
				fStartUrlText.setEnabled(!selected);
			} else {
				fStartUrlText.setEnabled(false);
			}

			fMethodInterface.setProperty(fMapStartUrlPropertyId, BooleanHelper.toString(selected));
		}
	}	

	private class BrowserChangedAdapter extends ComboSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fBrowserPropertyId, fBrowserCombo.getText());
		}
	}

	private class BrowserDriverApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fMethodInterface.setProperty(fBrowserDriverPropertyId, fBrowserDriverText.getText());
		}
	}	

	private class BrowseButtonSelectionAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {

			String path = FileOpenDialog.open();

			if (path != null) {
				fBrowserDriverText.setText(path);
				fMethodInterface.setProperty(fBrowserDriverPropertyId, fBrowserDriverText.getText());
			}
		}
	}

	private class UrlApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fMethodInterface.setProperty(fStartUrlPropertyId, fStartUrlText.getText());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		fMapBrowserCheckbox.setEnabled(enabled);
		fMapStartUrlCheckbox.setEnabled(enabled);
		fSectionEnabled = enabled;

		refresh();
	}

}
