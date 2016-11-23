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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
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
	private MethodRunnerSection fRunnerSection;
	private MethodParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;

	private MethodInterface fMethodInterface;
	private JavaDocCommentsSection fCommentsSection;

	private final NodePropertyDefs.PropertyId fRunnerPropertyId = NodePropertyDefs.PropertyId.METHOD_RUNNER;

	private class OnlineTestAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodInterface.executeOnlineTests(getFileInfoProvider());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not execute online tests.",
						e.getMessage());
			}
		}
	}

	private class OnlineExportAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				fMethodInterface.executeOnlineExport(getFileInfoProvider());
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not execute online export.",
						e.getMessage());
			}
		}
	}

	private class ReassignAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.reassignTarget();
		}
	}

	private class RenameMethodAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setName(fMethodNameText.getText());
			fMethodNameText.setText(fMethodInterface.getName());
		}
	}

	public MethodDetailsPage(ModelMasterSection masterSection,
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fMethodInterface = new MethodInterface(this, fileInfoProvider);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);

		IFileInfoProvider fileInfoProvider = getFileInfoProvider();

		createMethodNameWidgets(fileInfoProvider);
		createTestAndExportButtons(fileInfoProvider);
		createRunnerCombo();
		createRunnerSection(fileInfoProvider);

		if (fileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new JavaDocCommentsSection(this, this,
					fileInfoProvider));
		}
		addViewerSection(fParemetersSection = new MethodParametersViewer(this,
				this, fileInfoProvider));
		addViewerSection(fConstraintsSection = new ConstraintsListViewer(this,
				this, fileInfoProvider));
		addViewerSection(fTestCasesSection = new TestCasesViewer(this, this,
				fileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite() {
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createMethodNameWidgets(IFileInfoProvider fileInfoProvider) {
		int gridColumns = 2;

		if (fileInfoProvider.isProjectAvailable()) {
			++gridColumns;
		}

		Composite gridComposite = getFormObjectToolkit().createGridComposite(
				getMainComposite(), gridColumns);

		getFormObjectToolkit().createLabel(gridComposite, "Method name ");
		fMethodNameText = getFormObjectToolkit().createGridText(gridComposite,
				new RenameMethodAdapter());

		if (fileInfoProvider.isProjectAvailable()) {
			fBrowseButton = getFormObjectToolkit().createButton(gridComposite,
					"Browse...", new ReassignAdapter());
		}

		getFormObjectToolkit().paintBorders(gridComposite);
	}

	private void createTestAndExportButtons(IFileInfoProvider fileInfoProvider) {

		Composite childComposite = getFormObjectToolkit().createRowComposite(
				getMainComposite());

		fTestOnlineButton = getFormObjectToolkit().createButton(
				childComposite, "Test online...", new OnlineTestAdapter());

		fExportOnlineButton = getFormObjectToolkit().createButton(
				childComposite, "Export online...", new OnlineExportAdapter());
		getFormObjectToolkit().paintBorders(childComposite);
	}

	private void createRunnerCombo() {
		FormObjectToolkit formObjectToolkit = getFormObjectToolkit();
		Composite gridComposite = formObjectToolkit.createGridComposite(getMainComposite(), 2);

		formObjectToolkit.createLabel(gridComposite, "Runner");

		fRunnerCombo = formObjectToolkit.createGridCombo(gridComposite, new RunnerChangedAdapter());
		fRunnerCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fRunnerPropertyId)); 
		fRunnerCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fRunnerPropertyId));
	}	

	private class RunnerChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fRunnerPropertyId, fRunnerCombo.getText());
		}
	}	

	private void createRunnerSection(IFileInfoProvider fileInfoProvider) {
		fRunnerSection = new MethodRunnerSection(this, this, fMethodInterface, fileInfoProvider);
	}

	@Override
	public void refresh() {
		super.refresh();

		if (!(getSelectedElement() instanceof MethodNode)) {
			return;
		}

		MethodNode selectedMethod = (MethodNode) getSelectedElement();
		fMethodInterface.setTarget(selectedMethod);

		IFileInfoProvider fileInfoProvider = getFileInfoProvider();

		String methodRunner = selectedMethod.getPropertyValue(fRunnerPropertyId);
		if (methodRunner != null) {
			fRunnerCombo.setText(methodRunner);
		}		

		fRunnerSection.refresh();

		String runner = selectedMethod.getPropertyValue(fRunnerPropertyId);
		if (NodePropertyDefs.isJavaRunnerMethod(runner)) {
			fRunnerSection.setVisible(false);
		} else {
			fRunnerSection.setVisible(true);
		}

		EImplementationStatus methodStatus = null;
		if (fileInfoProvider.isProjectAvailable()) {
			methodStatus = fMethodInterface.getImplementationStatus();
		}
		getMainSection().setText(JavaUtils.simplifiedToString(selectedMethod));

		fTestOnlineButton.setEnabled(isTestOnlineButtonEnabled(fileInfoProvider, methodStatus));

		if (selectedMethod.getParametersCount() > 0) {
			fExportOnlineButton.setEnabled(true);
		} else {
			fExportOnlineButton.setEnabled(false);
		}

		fParemetersSection.setInput(selectedMethod);
		fConstraintsSection.setInput(selectedMethod);
		fTestCasesSection.setInput(selectedMethod);

		if (fileInfoProvider.isProjectAvailable()) {
			fCommentsSection.setInput(selectedMethod);
		}
		fMethodNameText.setText(fMethodInterface.getName());

		if (fileInfoProvider.isProjectAvailable()) {
			EImplementationStatus parentStatus = fMethodInterface
					.getImplementationStatus(selectedMethod.getClassNode());
			fBrowseButton
			.setEnabled((parentStatus == EImplementationStatus.IMPLEMENTED || parentStatus == EImplementationStatus.PARTIALLY_IMPLEMENTED)
					&& fMethodInterface.getCompatibleMethods().isEmpty() == false);
		}
	}

	private boolean isTestOnlineButtonEnabled(IFileInfoProvider fileInfoProvider, EImplementationStatus methodStatus) {

		MethodNode methodNode = fMethodInterface.getTarget();

		if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
			return true;
		}		

		if (fileInfoProvider.isProjectAvailable()) {
			return methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
		}

		return false;
	}	

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return MethodNode.class;
	}

}
