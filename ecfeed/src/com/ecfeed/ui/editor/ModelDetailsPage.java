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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.RootInterface;

public class ModelDetailsPage extends BasicDetailsPage {

	private ClassViewer fClassesSection;
	private GlobalParametersViewer fParametersSection;
	private Text fModelNameText;
	private RootInterface fRootIf;
	private SingleTextCommentsSection fCommentsSection;
	private IJavaProjectProvider fJavaProjectProvider;

	public ModelDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(masterSection, updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
		fRootIf = new RootInterface(this, javaProjectProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		getMainSection().setText("Model details"); 

		createModelNameEdit(getMainComposite());

		addCommentsSection();

		addViewerSection(fClassesSection = new ClassViewer(this, this, fJavaProjectProvider));

		fParametersSection = new GlobalParametersViewer(this, this, fJavaProjectProvider);
		addViewerSection(fParametersSection);

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void addCommentsSection() {

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = new ExportableSingleTextCommentsSection(this, this, fJavaProjectProvider));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fJavaProjectProvider));
		}
	}


	private void createModelNameEdit(Composite parent) {

		EcFormToolkit formObjectToolkit = getFormObjectToolkit();
		Composite composite = formObjectToolkit.createGridComposite(parent, 2);		

		formObjectToolkit.createLabel(composite, "Model name");
		fModelNameText = formObjectToolkit.createGridText(composite, new ModelNameApplier());
		formObjectToolkit.paintBorders(composite);
	}

	@Override
	public void refresh() {
		super.refresh();
		if(getSelectedElement() instanceof RootNode){
			RootNode selectedRoot = (RootNode)getSelectedElement();
			fRootIf.setOwnNode(selectedRoot);
			fModelNameText.setText(selectedRoot.getName());
			fClassesSection.setInput(selectedRoot);
			fParametersSection.setInput(selectedRoot);
			fCommentsSection.setInput(selectedRoot);
		}
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof RootNode){
			fRootIf.setOwnNode((RootNode)getSelectedElement());
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return RootNode.class;
	}

	private class ModelNameApplier implements IValueApplier{

		@Override
		public void applyValue() {
			fRootIf.setName(fModelNameText.getText());
			fModelNameText.setText(fRootIf.getName());
		}
	}	

}
