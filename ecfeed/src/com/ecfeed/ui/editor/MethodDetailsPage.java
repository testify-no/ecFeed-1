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
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.utils.SeleniumHelper;

public class MethodDetailsPage extends BasicDetailsPage {

	private Text fMethodNameText;
	private Button fTestOnlineButton;
	private Button fExportOnlineButton;
	private Button fBrowseButton;
	private Combo fRunnerCombo;
	private WebRunnerSection fRunnerSection;
	private MethodParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;

	private MethodInterface fMethodInterface;
	private AbstractCommentsSection fCommentsSection;

	private final NodePropertyDefs.PropertyId fRunnerPropertyId = 
			NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER;


	public MethodDetailsPage(
			IMainTreeProvider mainTreeProvider,
			MethodInterface methodInterface,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecForToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecForToolkit);
		fMethodInterface = methodInterface;
	}

	public MethodDetailsPage(
			IMainTreeProvider mainTreeProvider,
			MethodInterface methodInterface,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {

		this(mainTreeProvider, methodInterface, updateContext, javaProjectProvider, null);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);

		createMethodNameWidgets();
		createTestAndExportButtons();

		if (ApplicationContext.isApplicationTypeLocal()) {
			createRunnerCombo();
			createRunnerSection();
		}

		addCommentsSection();

		IJavaProjectProvider javaProjectProvider = getJavaProjectProvider();

		addViewerSection(fParemetersSection = 
				new MethodParametersViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), javaProjectProvider));

		addViewerSection(fConstraintsSection = 
				new ConstraintsListViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), javaProjectProvider));

		addViewerSection(fTestCasesSection = 
				new TestCasesViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), javaProjectProvider));

		getEcFormToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite() {
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void addCommentsSection() {

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = 
					new ExportableJavaDocCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		} else {
			addForm(fCommentsSection = 
					new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		}
	}

	private void createMethodNameWidgets() {
		int gridColumns = 2;

		if (ApplicationContext.isProjectAvailable()) {
			++gridColumns;
		}

		Composite gridComposite = getEcFormToolkit().createGridComposite(
				getMainComposite(), gridColumns);

		getEcFormToolkit().createLabel(gridComposite, "Method name ");
		fMethodNameText = getEcFormToolkit().createGridText(gridComposite,
				new MethodNameApplier());

		if (ApplicationContext.isProjectAvailable()) {
			fBrowseButton = getEcFormToolkit().createButton(gridComposite,
					"Browse...", new ReassignAdapter());
		}

		getEcFormToolkit().paintBordersFor(gridComposite);
	}

	private void createTestAndExportButtons() {

		Composite childComposite = getEcFormToolkit().createRowComposite(
				getMainComposite());

		EcFormToolkit formObjectToolkit = getEcFormToolkit();

		if (ApplicationContext.isApplicationTypeLocal()) {
			fTestOnlineButton = 
					formObjectToolkit.createButton(
							childComposite, "Test online...", new OnlineTestAdapter());
		}

		fExportOnlineButton = formObjectToolkit.createButton(
				childComposite, "Export online...", new OnlineExportAdapter());

		formObjectToolkit.paintBordersFor(childComposite);
	}

	private void createRunnerCombo() {
		EcFormToolkit formObjectToolkit = getEcFormToolkit();
		Composite gridComposite = formObjectToolkit.createGridComposite(getMainComposite(), 2);

		formObjectToolkit.createLabel(gridComposite, "Runner");

		fRunnerCombo = formObjectToolkit.createReadOnlyGridCombo(gridComposite, new RunnerApplier());
		fRunnerCombo.setItems(NodePropertyDefs.getValueSet(fRunnerPropertyId, null).getPossibleValues()); 
		fRunnerCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fRunnerPropertyId, null));
	}	

	private class RunnerApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fMethodInterface.setProperty(fRunnerPropertyId, fRunnerCombo.getText());
		}
	}	

	private void createRunnerSection() {
		fRunnerSection = 
				new WebRunnerSection(
						this, getModelUpdateContext(), fMethodInterface, getJavaProjectProvider());

		fRunnerSection.initialize(getManagedForm());
	}

	@Override
	public void refresh() {
		super.refresh();

		if (!(getSelectedElement() instanceof MethodNode)) {
			return;
		}

		MethodNode methodNode = (MethodNode)getSelectedElement();
		fMethodInterface.setOwnNode(methodNode);

		refreshMethodNameAndSignature(methodNode);

		refreshRunnerCombo(methodNode);		
		refreshRunnerSection(methodNode);

		refrestTestOnlineButton();
		refreshExportOnlineButton(methodNode);

		setInputForOtherSections(methodNode);

		refreshBrowseButton(methodNode);
	}

	private void refreshMethodNameAndSignature(MethodNode methodNode) {
		getMainSection().setText(MethodNodeHelper.simplifiedToString(methodNode));
//<<<<<<< HEAD
//		TypeConverter tc = new TypeConverter(fMethodInterface.getName());
		fMethodNameText.setText(fMethodInterface.getNodeName());
//		fMethodNameText.setText(tc.getReversedName());
//=======
//		fMethodNameText.setText(fMethodInterface.getNodeName());
//>>>>>>> master
	}

	private void refreshRunnerCombo(MethodNode methodNode) {

		if (fRunnerCombo == null) {
			return;
		}

		String methodRunner = methodNode.getPropertyValue(fRunnerPropertyId);
		if (methodRunner != null) {
			fRunnerCombo.setText(methodRunner);
		}		
	}

	private void refreshRunnerSection(MethodNode methodNode) {

		if (fRunnerSection == null) {
			return;
		}

		fRunnerSection.refresh();

		String runner = methodNode.getPropertyValue(fRunnerPropertyId);
		if (NodePropertyDefs.isJavaRunnerMethod(runner)) {
			fRunnerSection.setEnabled(false);
		} else {
			fRunnerSection.setEnabled(true);
		}		
	}

	private void refrestTestOnlineButton() {

		if (fTestOnlineButton == null) {
			return;
		}

		EImplementationStatus methodImplementationStatus = null;
		if (ApplicationContext.isProjectAvailable()) {
			methodImplementationStatus = fMethodInterface.getImplementationStatus();
		}

		fTestOnlineButton.setEnabled(isTestOnlineButtonEnabled(methodImplementationStatus));
	}

	private void refreshExportOnlineButton(MethodNode methodNode) {
		if (methodNode.getParametersCount() > 0) {
			fExportOnlineButton.setEnabled(true);
		} else {
			fExportOnlineButton.setEnabled(false);
		}
	}

	private void setInputForOtherSections(MethodNode methodNode) {
		fParemetersSection.setInput(methodNode);
		fConstraintsSection.setInput(methodNode);
		fTestCasesSection.setInput(methodNode);
		fCommentsSection.setInput(methodNode);
	}

	private void refreshBrowseButton(MethodNode methodNode) {
		if (!ApplicationContext.isProjectAvailable()) {
			return;
		}

		EImplementationStatus parentStatus = fMethodInterface.getImplementationStatus(methodNode.getClassNode());

		boolean parentImplemented = 
				parentStatus == EImplementationStatus.IMPLEMENTED
				|| parentStatus == EImplementationStatus.PARTIALLY_IMPLEMENTED; 

		boolean isEnabled = parentImplemented && (!fMethodInterface.getCompatibleMethods().isEmpty());

		fBrowseButton.setEnabled(isEnabled);
	}

	private boolean isTestOnlineButtonEnabled(EImplementationStatus methodStatus) {

		MethodNode methodNode = fMethodInterface.getOwnNode();

		if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
			return true;
		}		

		if (ApplicationContext.isProjectAvailable()) {
			return methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
		}

		return false;
	}	

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return MethodNode.class;
	}

	private class OnlineTestAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodInterface.executeOnlineTests(getJavaProjectProvider());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not execute online tests.",
						e.getMessage());
			}
		}
	}

	private class OnlineExportAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodInterface.executeOnlineExport(getJavaProjectProvider());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not execute online export.",
						e.getMessage());
			}
		}
	}

	private class ReassignAdapter extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.reassignTarget();
		}
	}

	private class MethodNameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fMethodInterface.setName(fMethodNameText.getText());
//<<<<<<< HEAD
//			TypeConverter name = new TypeConverter(fMethodInterface.getName());
//			fMethodInterface.setName(name.getReversedName());
			fMethodNameText.setText(fMethodInterface.getNodeName());
//			fMethodNameText.setText(name.getReversedName());
//=======
//			fMethodNameText.setText(fMethodInterface.getNodeName());
//>>>>>>> master
		}
	}	

}
