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

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterDetailsPage extends BasicDetailsPage {

	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private ChoicesViewer fChoicesViewer;
	private Button fBrowseUserTypeButton;
	private WebParameterSection fWebParameterSection;
	private AbstractCommentsSection fCommentsSection;

	private class NameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			getParameterIf().setName(fNameText.getText());
			fNameText.setText(getParameterIf().getNodeName());
		}
	}	

	private class TypeApplier implements IValueApplier {

		@Override
		public void applyValue() {
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

	public AbstractParameterDetailsPage(
			IMainTreeProvider mainTreeProvider,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		this(mainTreeProvider, updateContext, javaProjectProvider, null);
	}

	public AbstractParameterDetailsPage(
			IMainTreeProvider mainTreeProvider,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
	}	

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createAttributesComposite();

		addForm(fCommentsSection = getCommentsSection(this, getModelUpdateContext()));

		if (ApplicationContext.isApplicationTypeLocal()) {
			fWebParameterSection = createWebParameterSection();
		}

		addForm(fChoicesViewer = 
				new ChoicesViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider()));

		getEcFormToolkit().paintBordersFor(getMainComposite());
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
			getParameterIf().setOwnNode(parameter);

			getMainSection().setText(parameter.toString());
			fNameText.setText(parameter.getName());
			fTypeCombo.setItems(AbstractParameterInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(parameter.getType());

			refreshWebParameterSection();

			fCommentsSection.setInput(parameter);

			fChoicesViewer.setInput(parameter);
		}
	}

	private void refreshWebParameterSection() {
		if (fWebParameterSection == null) {
			return;
		}

		MethodParameterNode methodParameterNode = (MethodParameterNode)getParameterIf().getOwnNode();
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
		EcFormToolkit formObjectToolkit = getEcFormToolkit();
		fAttributesComposite = formObjectToolkit.createGridComposite(getMainComposite(), 3);


		formObjectToolkit.createLabel(fAttributesComposite, "Parameter name: ");
		fNameText = formObjectToolkit.createGridText(fAttributesComposite, new NameApplier());
		SwtObjectHelper.setHorizontalSpan(fNameText, 2);


		formObjectToolkit.createLabel(fAttributesComposite, "Parameter type: ");

		fTypeCombo = formObjectToolkit.createReadWriteGridCombo(fAttributesComposite, new TypeApplier());

		if (ApplicationContext.isProjectAvailable()) {
			fBrowseUserTypeButton = formObjectToolkit.createButton(
					fAttributesComposite, "Import...", new BrowseTypeSelectionListener());
		}

		formObjectToolkit.paintBordersFor(fAttributesComposite);
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

	protected abstract AbstractCommentsSection getCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext);

	protected Button getBrowseUserTypeButton() {
		if (!ApplicationContext.isProjectAvailable()) {
			return null;
		}
		return fBrowseUserTypeButton;
	}
}
