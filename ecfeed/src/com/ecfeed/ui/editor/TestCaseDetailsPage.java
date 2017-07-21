/*******************************************************************************
 *
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.TestCaseInterface;
import com.ecfeed.utils.SeleniumHelper;

public class TestCaseDetailsPage extends BasicDetailsPage {

	private Combo fTestSuiteNameCombo;
	private TestDataViewer fTestDataViewer;
	private Button fExecuteButton;

	private TestCaseInterface fTestCaseIf;
	private SingleTextCommentsSection fCommentsSection;


	public TestCaseDetailsPage(
			IMainTreeProvider mainTreeProvider,
			TestCaseInterface testCaseInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(mainTreeProvider, updateContext, javaProjectProvider);
		fTestCaseIf = testCaseInterface;
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		createTestSuiteEdit(getMainComposite());

		addCommentsSection();

		addViewerSection(fTestDataViewer = 
				new TestDataViewer(this, getModelUpdateContext(), getJavaProjectProvider()));
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof TestCaseNode){
			TestCaseNode testCase = (TestCaseNode)getSelectedElement();
			fTestCaseIf.setOwnNode(testCase);

			fCommentsSection.setInput(testCase);

			getMainSection().setText(testCase.toString());
			fTestSuiteNameCombo.setItems(testCase.getMethod().getTestSuites().toArray(new String[]{}));
			fTestSuiteNameCombo.setText(testCase.getName());
			fTestDataViewer.setInput(testCase);

			fExecuteButton.setEnabled(isExecuteButtonEnabled());
		}
	}

	private void addCommentsSection() {

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = 
					new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		} else {
			addForm(fCommentsSection = 
					new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		}
	}

	private boolean isExecuteButtonEnabled() {

		MethodNode methodNode = fTestCaseIf.getMethod();

		if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
			return true;
		}		

		if (!ApplicationContext.isProjectAvailable()) {
			return false;
		}

		if (fTestCaseIf.isExecutable()) {
			return true;
		}

		return false;
	}	

	private void createTestSuiteEdit(Composite parent) {
		Composite composite = getEcFormToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getEcFormToolkit().createLabel(composite, "Test suite: ");

		fTestSuiteNameCombo = new ComboViewer(composite, SWT.NONE).getCombo();
		fTestSuiteNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteNameCombo.addSelectionListener(new RenameTestCaseAdapter());

		ButtonClickListener buttonClickListener = new ButtonClickListener() {

			@Override
			public void widgetSelected(SelectionEvent ev) {
				try {
					fTestCaseIf.executeStaticTest();
				} catch (EcException e) {
					ExceptionCatchDialog.open("Can not execute static tests.", e.getMessage());
				}
			}

		};

		fExecuteButton = getEcFormToolkit().createButton(composite, "Execute", buttonClickListener);		

		getEcFormToolkit().paintBordersFor(fTestSuiteNameCombo);
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return TestCaseNode.class;
	}

	private class RenameTestCaseAdapter extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e){
			fTestCaseIf.setName(fTestSuiteNameCombo.getText());
			fTestSuiteNameCombo.setText(fTestCaseIf.getNodeName());
		}
	}	

}
