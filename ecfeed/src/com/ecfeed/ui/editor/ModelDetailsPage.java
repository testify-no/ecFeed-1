///*******************************************************************************
// *
// * Copyright (c) 2016 ecFeed AS.                                                
// * All rights reserved. This program and the accompanying materials              
// * are made available under the terms of the Eclipse Public License v1.0         
// * which accompanies this distribution, and is available at                      
// * http://www.eclipse.org/legal/epl-v10.html 
// *  
// *******************************************************************************/
//
//package com.ecfeed.ui.editor;
//
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.ui.forms.IFormPart;
//
//import com.ecfeed.application.ApplicationContext;
//import com.ecfeed.core.adapter.operations.EditorStyleChecker;
//import com.ecfeed.core.adapter.operations.EditorStyleChecker.ErrorDescription;
//import com.ecfeed.core.model.AbstractNode;
//import com.ecfeed.core.model.RootNode;
//import com.ecfeed.core.utils.SystemLogger;
//import com.ecfeed.ui.common.utils.IFileInfoProvider;
//import com.ecfeed.ui.dialogs.basic.ErrorDialog;
//import com.ecfeed.ui.modelif.IModelUpdateContext;
//import com.ecfeed.ui.modelif.RootInterface;
//
//public class ModelDetailsPage extends BasicDetailsPage {
//
//	private ClassViewer fClassesSection;
//	private GlobalParametersViewer fParametersSection;
//	private Text fModelNameText;
//	private RootInterface fRootIf;
//	private SingleTextCommentsSection fCommentsSection;
//	private IFileInfoProvider fFileInfoProvider;
//	private ModelMasterSection fMasterSection;
//
//	public ModelDetailsPage(
//			ModelMasterSection masterSection, 
//			IModelUpdateContext updateContext, 
//			IFileInfoProvider fileInforProvider) {
//		super(masterSection, updateContext, fileInforProvider);
//		fMasterSection = masterSection;
//		fFileInfoProvider = fileInforProvider;
//		fRootIf = new RootInterface(this, fFileInfoProvider);
//	}
//
//	@Override
//	public void createContents(Composite parent){
//		super.createContents(parent);
//		getMainSection().setText("Model details"); 
//
//		createModelNameEdit(getMainComposite());
//
//		addCommentsSection();
//		
//		addViewerSection(fClassesSection = new ClassViewer(this, this, fFileInfoProvider));
//
//		fParametersSection = new GlobalParametersViewer(this, this, fFileInfoProvider);
//		addViewerSection(fParametersSection);
//
//		getToolkit().paintBordersFor(getMainComposite());
//	}
//
//	@Override
//	protected Composite createTextClientComposite(){
//		Composite textClient = super.createTextClientComposite();
//		return textClient;
//	}
//
//	private void addCommentsSection() {
//
//		if (fFileInfoProvider.isProjectAvailable()) {
//			addForm(fCommentsSection = new ExportableSingleTextCommentsSection(this, this, fFileInfoProvider));
//		} else {
//			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fFileInfoProvider));
//		}
//	}
//
//
//	private void createModelNameEdit(Composite parent) {
//
//		FormObjectToolkit formObjectToolkit = getFormObjectToolkit();
//		Composite composite = formObjectToolkit.createGridComposite(parent, 2);		
//		
//		formObjectToolkit.createButton(composite, "Switch edit mode",
//				new SwitchEditModeButtonSelectionAdapter());
//		formObjectToolkit.createEmptyLabel(composite);
//
//		formObjectToolkit.createLabel(composite, "Model name");
//		fModelNameText = formObjectToolkit.createGridText(composite, new ModelNameApplier());
//		formObjectToolkit.paintBorders(composite);
//		formObjectToolkit.createEmptyLabel(composite);
//
//	}
//	
//	class SwitchEditModeButtonSelectionAdapter extends ButtonClickListener {
//		
//		@Override
//		public void widgetSelected(SelectionEvent arg0)
//		{
//		
//			try {
//				RootNode rootNode = fRootIf.getOwnNode();
//				ErrorDescription errorDescription = EditorStyleChecker.canSwitchToSimpleModel(rootNode);
//				
//				if (errorDescription != null) {
//					ApplicationContext.setSimplifiedUI(false);
//					String message = errorDescription.createErrorMessage();
//					ErrorDialog.open(message);
//				} else {
//					modifyModeView();
//				}
//				
//			} catch (Exception e) {
//				SystemLogger.logCatch(e.getMessage());
//				e.printStackTrace();
//			}
//			
//			refresh();
//			fMasterSection.refresh();
//		}
//
//		private void modifyModeView() {
//			
//			if (ApplicationContext.getSimplifiedUI()) {
//				ApplicationContext.setSimplifiedUI(false);
//			} else {
//				ApplicationContext.setSimplifiedUI(true);
//			}
//		}
//	}
//	
//	@Override
//	public void refresh() {
//		super.refresh();
//		if(getSelectedElement() instanceof RootNode){
//			RootNode selectedRoot = (RootNode)getSelectedElement();
//			fRootIf.setOwnNode(selectedRoot);
//			fModelNameText.setText(selectedRoot.getName());
//			fClassesSection.setInput(selectedRoot);
//			fParametersSection.setInput(selectedRoot);
//			fCommentsSection.setInput(selectedRoot);
//		}
//	}
//
//	@Override
//	public void selectionChanged(IFormPart part, ISelection selection) {
//		super.selectionChanged(part, selection);
//		if(getSelectedElement() instanceof RootNode){
//			fRootIf.setOwnNode((RootNode)getSelectedElement());
//		}
//	}
//
//	@Override
//	protected Class<? extends AbstractNode> getNodeType() {
//		return RootNode.class;
//	}
//
//	private class ModelNameApplier implements IValueApplier{
//
//		@Override
//		public void applyValue() {
//			fRootIf.setName(fModelNameText.getText());
//			fModelNameText.setText(fRootIf.getName());
//		}
//	}	
//
//}
