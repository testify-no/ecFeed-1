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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterDetailsPage extends AbstractParameterDetailsPage {

	private IJavaProjectProvider fJavaProjectProvider;
	private MethodParameterInterface fParameterIf;
	private Button fExpectedCheckbox;
	private Combo fDefaultValueCombo;
	private Composite fDefaultValueComboComposite;
	private Button fLinkedCheckbox;
	private Combo fLinkCombo;


	public MethodParameterDetailsPage(
			IMainTreeProvider mainTreeProvider, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {

		super(mainTreeProvider, updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
		getParameterIf();
	}

	@Override
	protected AbstractParameterInterface getParameterIf() {
		if(fParameterIf == null){
			fParameterIf = new MethodParameterInterface(this, fJavaProjectProvider);
		}
		return fParameterIf;
	}

	@Override
	protected WebParameterSection createWebParameterSection() {
		return new WebParameterSection(this, this, getParameterIf(), fJavaProjectProvider);
	}

	@Override
	protected Composite createAttributesComposite(){
		Composite attributesComposite = super.createAttributesComposite();

		EcFormToolkit formObjectToolkit = getEcFormToolkit(); 

		GridData comboGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		comboGridData.horizontalSpan = 2;

		fExpectedCheckbox = 
				formObjectToolkit.createGridCheckBox(
						attributesComposite, "Expected", new ExpectedApplier());

		SwtObjectHelper.setHorizontalSpan(fExpectedCheckbox, 3);


		getToolkit().createLabel(attributesComposite, "Default value: ", SWT.NONE);
		fDefaultValueComboComposite = getToolkit().createComposite(attributesComposite);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		fDefaultValueComboComposite.setLayout(gl);
		fDefaultValueComboComposite.setLayoutData(comboGridData);

		fLinkedCheckbox = 
				formObjectToolkit.createGridCheckBox(
						attributesComposite, "Linked", new LinkedApplier());

		SwtObjectHelper.setHorizontalSpan(fLinkedCheckbox, 3);


		getToolkit().createLabel(attributesComposite, "Parameter link: ", SWT.NONE);

		fLinkCombo = new Combo(attributesComposite,SWT.DROP_DOWN|SWT.READ_ONLY);
		fLinkCombo.setLayoutData(comboGridData);
		fLinkCombo.addSelectionListener(new SetLinkListener());

		return attributesComposite;
	}

	@Override
	public void refresh() {
		super.refresh();
		if(getSelectedElement() instanceof MethodParameterNode){
			MethodParameterNode parameter = (MethodParameterNode)getSelectedElement();
			fParameterIf.setOwnNode(parameter);

			getMainSection().setText((parameter.isExpected()?"[e]":"") + parameter.toString());

			getTypeCombo().setEnabled(typeComboEnabled());

			getMainSection().setText((parameter.isExpected()?"[e]":"") + parameter.toString());
			fExpectedCheckbox.setSelection(parameter.isExpected());

			fExpectedCheckbox.setEnabled(expectedCheckboxEnabled());

			recreateDefaultValueCombo(parameter);
			fLinkedCheckbox.setSelection(parameter.isLinked());
			fLinkedCheckbox.setEnabled(linkedCheckboxEnabled());
			fLinkCombo.setItems(availableLinks().toArray(new String[]{}));

			if(parameter.getLink() != null){
				fLinkCombo.setText(linkName(parameter.getLink()));
			}
			fLinkCombo.setEnabled(fParameterIf.isLinked());

			if (ApplicationContext.isProjectAvailable()) {
				getBrowseUserTypeButton().setEnabled(!fParameterIf.isLinked());
			}
			getChoicesViewer().setReplaceButtonEnabled(isReplaceButtonEnabled());


			if(fParameterIf.isExpected() && fParameterIf.isPrimitive()){
				getChoicesViewer().setVisible(false);
			}
			else{
				getChoicesViewer().setVisible(true);
			}
			getChoicesViewer().setEditEnabled(choicesViewerEnabled());
		}
	}

	private boolean isReplaceButtonEnabled() {

		if (fParameterIf.isLinked()) {
			return false;
		}

		String parameterType = fParameterIf.getParameter().getType();

		if (JavaTypeHelper.isJavaType(parameterType)) {
			return true;
		} 

		return isReplaceButtonEnabledForUserType();
	}

	private boolean isReplaceButtonEnabledForUserType() {

		if (ApplicationContext.isStandaloneApplication()) {
			return false;
		} 

		EImplementationStatus implementationStatus = fParameterIf.getImplementationStatus();

		if (implementationStatus == EImplementationStatus.NOT_IMPLEMENTED) {
			return false;
		}

		return true;
	}

	private boolean choicesViewerEnabled() {
		return fParameterIf.isLinked() == false;
	}

	private boolean typeComboEnabled() {
		return fParameterIf.isLinked() == false;
	}

	private boolean linkedCheckboxEnabled() {
		return availableLinks().size() > 0 && (fParameterIf.isExpected() == false);
	}

	private boolean expectedCheckboxEnabled(){
		return fParameterIf.isLinked() == false || fParameterIf.isUserType();
	}

	private void recreateDefaultValueCombo(MethodParameterNode parameter) {
		if(fDefaultValueCombo != null && fDefaultValueCombo.isDisposed() == false){
			fDefaultValueCombo.dispose();
		}
		if(fParameterIf.hasLimitedValuesSet()){
			fDefaultValueCombo = new Combo(fDefaultValueComboComposite,SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		else{
			fDefaultValueCombo = new Combo(fDefaultValueComboComposite,SWT.DROP_DOWN);
		}
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueCombo.setItems(fParameterIf.defaultValueSuggestions());
		fDefaultValueCombo.setText(parameter.getDefaultValue());
		fDefaultValueCombo.addSelectionListener(new SetDefaultValueListener());
		fDefaultValueCombo.addFocusListener(new DefaultValueFocusLostListener());

		fDefaultValueCombo.setEnabled(parameter.isExpected());

		fDefaultValueComboComposite.layout();
	}

	private List<String> availableLinks(){
		List<String> result = new ArrayList<>();
		for(GlobalParameterNode parameter : fParameterIf.getAvailableLinks()){
			result.add(linkName(parameter));
		}
		return result;
	}

	private String linkName(GlobalParameterNode parameter) {
		return parameter.getQualifiedName() + " [" + ModelHelper.convertToLocalName(parameter.getType()) + "]";
	}

	private String linkPath(String linkName){
		return linkName.substring(0, linkName.indexOf(" "));
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return MethodParameterNode.class;
	}

	@Override
	protected AbstractCommentsSection getCommentsSection(
			ISectionContext sectionContext, IModelUpdateContext updateContext) {

		if (ApplicationContext.isProjectAvailable()) {
			return new MethodParameterCommentsSection(sectionContext, updateContext, fJavaProjectProvider);
		} else {
			return new SingleTextCommentsSection(this, this, fJavaProjectProvider);
		}
	}

	private class SetDefaultValueListener extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fParameterIf.setDefaultValue(fDefaultValueCombo.getText());
			fDefaultValueCombo.setText(fParameterIf.getDefaultValue());
		}
	}

	private class DefaultValueFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			fParameterIf.setDefaultValue(fDefaultValueCombo.getText());
			fDefaultValueCombo.setText(fParameterIf.getDefaultValue());
		}
	}	

	private class ExpectedApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fParameterIf.setExpected(fExpectedCheckbox.getSelection());
			fExpectedCheckbox.setSelection(fParameterIf.isExpected());
		}
	}

	private class LinkedApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fParameterIf.setLinked(fLinkedCheckbox.getSelection());
			fLinkedCheckbox.setSelection(fParameterIf.isLinked());
		}
	}	

	private class SetLinkListener extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			String linkPath = linkPath(fLinkCombo.getText());
			GlobalParameterNode link = fParameterIf.getGlobalParameter(linkPath);
			fParameterIf.setLink(link);
			fLinkCombo.setText(linkName(fParameterIf.getLink()));
		}
	}

}
