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

import com.ecfeed.application.SessionContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.RootInterface;

public class ModelRootDetailsPage extends BasicDetailsPage {

	private ClassViewer fClassesSection;
	private GlobalParametersViewer fParametersSection;
	private ModelNameComposite fModelNameComposite;
	private RootInterface fRootInterface;
	private SingleTextCommentsSection fCommentsSection;

	public ModelRootDetailsPage(
			IMainTreeProvider mainTreeProvider,
			RootInterface rootInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		super(mainTreeProvider, updateContext, javaProjectProvider);
		fRootInterface = rootInterface;
	}

	public ModelRootDetailsPage(
			IMainTreeProvider mainTreeProvider,
			RootInterface rootInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
		fRootInterface = rootInterface;
	}

	@Override
	public void createContents(Composite parent) {

		super.createContents(parent);

		getMainSection().setText("Model details"); 

		fModelNameComposite = new ModelNameComposite(getMainComposite(), getEcFormToolkit(), fRootInterface);

		addCommentsSection();

		addViewerSection(fClassesSection = 
				new ClassViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider()));

		fParametersSection = 
				new GlobalParametersViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider());

		addViewerSection(fParametersSection);

		getEcFormToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void addCommentsSection() {

		if (SessionContext.isProjectAvailable()) {
			addForm(fCommentsSection = new ExportableSingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		if(getSelectedElement() instanceof RootNode){
			RootNode selectedRoot = (RootNode)getSelectedElement();
			fRootInterface.setOwnNode(selectedRoot);

			fModelNameComposite.refresh();

			fClassesSection.setInput(selectedRoot);
			fParametersSection.setInput(selectedRoot);
			fCommentsSection.setInput(selectedRoot);
		}
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof RootNode){
			fRootInterface.setOwnNode((RootNode)getSelectedElement());
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return RootNode.class;
	}

	private class ModelNameComposite {

		private Text fModelNameText;
		private RootInterface fRootIf;


		public ModelNameComposite(Composite parent, EcFormToolkit ecFormToolkit, RootInterface rootIf) {

			fRootIf = rootIf;

			Composite composite = ecFormToolkit.createGridComposite(parent, 2);		

			ecFormToolkit.createLabel(composite, "Model name");
			fModelNameText = ecFormToolkit.createGridText(composite, new ModelNameApplier());
			ecFormToolkit.paintBordersFor(composite);
		}

		public void refresh() {

			String name = fRootIf.getNodeName();
			fModelNameText.setText(name);
		}

		private class ModelNameApplier implements IValueApplier{

			@Override
			public void applyValue() {

				fRootIf.setName(fModelNameText.getText());
				fModelNameText.setText(fRootIf.getNodeName());
			}
		}	

	}

}
