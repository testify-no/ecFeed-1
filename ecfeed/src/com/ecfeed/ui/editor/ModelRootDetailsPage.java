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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.operations.EditorStyleChecker;
import com.ecfeed.core.adapter.operations.EditorStyleChecker.ErrorDescription;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.RootInterface;

public class ModelRootDetailsPage extends BasicDetailsPage {

	private ClassViewer fClassesSection;
	private GlobalParametersViewer fParametersSection;
	private ModelNameComposite fModelNameComposite;
	private RootInterface fRootInterface;
	private SingleTextCommentsSection fCommentsSection;
	private IMainTreeProvider fmainTreeProvider;

	public ModelRootDetailsPage(
			IMainTreeProvider mainTreeProvider,
			RootInterface rootInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		super(mainTreeProvider, updateContext, javaProjectProvider);
		fmainTreeProvider = mainTreeProvider;
		fRootInterface = rootInterface;
	}

	public ModelRootDetailsPage(
			IMainTreeProvider mainTreeProvider,
			RootInterface rootInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
		fmainTreeProvider = mainTreeProvider;
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

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = new ExportableSingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		}
	}
	
	class SwitchEditModeButtonSelectionAdapter extends ButtonClickListener {
		
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
		
			try {
				RootNode rootNode = fRootInterface.getOwnNode();
				ErrorDescription errorDescription = EditorStyleChecker.canSwitchToSimpleModel(rootNode);
				
				if (errorDescription != null) {
					ApplicationContext.setSimplifiedUI(false);
					String message = errorDescription.createErrorMessage();
					ErrorDialog.open(message);
				} else {
					modifyModeView();
				}
				
			} catch (Exception e) {
				SystemLogger.logCatch(e.getMessage());
				e.printStackTrace();
			}
			
			refresh();
			fmainTreeProvider.refresh();
		}

		private void modifyModeView() {
			
			if (ApplicationContext.getSimplifiedUI()) {
				ApplicationContext.setSimplifiedUI(false);
			} else {
				ApplicationContext.setSimplifiedUI(true);
			}
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
			
			ecFormToolkit.createButton(composite, "Switch edit mode", new SwitchEditModeButtonSelectionAdapter());
			ecFormToolkit.createEmptyLabel(composite);
			
			ecFormToolkit.createLabel(composite, "Model name");
			fModelNameText = ecFormToolkit.createGridText(composite, new ModelNameApplier());
			ecFormToolkit.paintBordersFor(composite);
			ecFormToolkit.createEmptyLabel(composite);
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
