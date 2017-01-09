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

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private ChoicesViewer fChoicesViewer;
	private Button fBrowseUserTypeButton;
	private WebParameterSection fWebParameterSection;
	private AbstractParameterCommentsSection fCommentsSection;

	private class NameFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			getParameterIf().setName(fNameText.getText());
			fNameText.setText(getParameterIf().getName());
		}
	}	

	private class SetTypeListener extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().setType(fTypeCombo.getText());
			fTypeCombo.setText(getParameterIf().getType());
		}
	}

	private class BrowseTypeSelectionListener extends ButtonClickListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().importType();
			fTypeCombo.setText(getParameterIf().getType());
		}

	}

	public AbstractParameterDetailsPage(ModelMasterSection masterSection,
			IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createAttributesComposite();

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = getParameterCommentsSection(this, this));
		}

		fWebParameterSection = createWebParameterSection();

		addForm(fChoicesViewer = new ChoicesViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	protected WebParameterSection createWebParameterSection() { 
		return null;
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof AbstractParameterNode){
			AbstractParameterNode parameter = (AbstractParameterNode)getSelectedElement();
			getParameterIf().setTarget(parameter);

			getMainSection().setText(parameter.toString());
			fNameText.setText(parameter.getName());
			fTypeCombo.setItems(AbstractParameterInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(parameter.getType());

			refreshWebParameterSection();

			if (fFileInfoProvider.isProjectAvailable()) {
				fCommentsSection.setInput(parameter);
			}

			fChoicesViewer.setInput(parameter);
		}
	}

	private void refreshWebParameterSection() {
		if (fWebParameterSection == null) {
			return;
		}

		MethodParameterNode methodParameterNode = (MethodParameterNode)getParameterIf().getTarget();
		MethodNode methodNode = (MethodNode)methodParameterNode.getParent();
		String runner = methodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER);

		if (NodePropertyDefs.isWebRunnerMethod(runner)) {
			fWebParameterSection.setVisible(true);
			fWebParameterSection.refresh();
		} else {
			fWebParameterSection.setVisible(false);
		}
	}

	protected Composite createAttributesComposite(){
		FormObjectToolkit formObjectToolkit = getFormObjectToolkit();
		fAttributesComposite = formObjectToolkit.createGridComposite(getMainComposite(), 3);


		formObjectToolkit.createLabel(fAttributesComposite, "Parameter name: ");
		fNameText = formObjectToolkit.createGridText(fAttributesComposite, new NameFocusLostListener());
		formObjectToolkit.setHorizontalSpan(fNameText, 2);


		formObjectToolkit.createLabel(fAttributesComposite, "Parameter type: ");
		fTypeCombo = formObjectToolkit.createReadWriteGridCombo(fAttributesComposite, new SetTypeListener());
		if (fFileInfoProvider.isProjectAvailable()) {
			fBrowseUserTypeButton = formObjectToolkit.createButton(fAttributesComposite, "Import...", new BrowseTypeSelectionListener());
		}

		formObjectToolkit.paintBorders(fAttributesComposite);
		return fAttributesComposite;
	}

	protected Composite getAttributesComposite(){
		return fAttributesComposite;
	}

	protected ChoicesViewer getChoicesViewer(){
		return fChoicesViewer;
	}

	protected Combo getTypeCombo(){
		return fTypeCombo;
	}

	protected abstract AbstractParameterInterface getParameterIf();

	protected abstract AbstractParameterCommentsSection getParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext);

	protected Button getBrowseUserTypeButton() {
		if (!fFileInfoProvider.isProjectAvailable()) {
			return null;
		}
		return fBrowseUserTypeButton;
	}
}
