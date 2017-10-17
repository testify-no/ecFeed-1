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

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.android.utils.AndroidBaseRunnerHelper;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.utils.ApplicationContext;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassDetailsPage extends BasicDetailsPage {

	private boolean fIsAndroidProject;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Text fPackageNameText;
	private Button fRunOnAndroidCheckbox;
	private Label fAndroidBaseRunnerLabel;
	private Combo fAndroidBaseRunnerCombo;	
	private ClassInterface fClassInterface;
	private GlobalParametersViewer fGlobalParametersSection;
	private AbstractCommentsSection fCommentsSection;

	public ClassDetailsPage(
			IMainTreeProvider mainTreeProvider,
			ClassInterface classInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecForToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecForToolkit);
		fIsAndroidProject = new EclipseProjectHelper(javaProjectProvider).isAndroidProject();
		fClassInterface = classInterface;
	}

	public ClassDetailsPage(
			IMainTreeProvider mainTreeProvider,
			ClassInterface classInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		this(mainTreeProvider, classInterface, updateContext, javaProjectProvider, null);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createBasicParametersComposite(getMainComposite());

		addCommentsSection();

		addViewerSection(fMethodsSection = 
				new MethodsViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider()));

		addViewerSection(fGlobalParametersSection = 
				new GlobalParametersViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider()));

		if (ApplicationContext.isProjectAvailable()) {
			addViewerSection(fOtherMethodsSection = 
					new OtherMethodsViewer(this, getModelUpdateContext(), getJavaProjectProvider()));
		}

		getEcFormToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
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

	private void createBasicParametersComposite(Composite parent) {

		Composite mainComposite = getEcFormToolkit().createGridComposite(parent, 1);

		Composite packageAndClassComposite = getEcFormToolkit().createComposite(mainComposite);
		initAndFillClassComposite(packageAndClassComposite);

		if (fIsAndroidProject) {
			Composite androidComposite = getEcFormToolkit().createComposite(mainComposite);

			initAndFillAndroidComposite(androidComposite);
		}
	}

	private void initAndFillClassComposite(Composite composite) {

		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		EcFormToolkit formObjectToolkit = getEcFormToolkit(); 


		formObjectToolkit.createLabel(composite, "Package name");
		fPackageNameText = formObjectToolkit.createGridText(composite, new PackageNameApplier());
		formObjectToolkit.createEmptyLabel(composite);


		formObjectToolkit.createLabel(composite, "Class name");
		fClassNameText = formObjectToolkit.createGridText(composite, new ClassNameApplier());
		if (ApplicationContext.isProjectAvailable()) {
			formObjectToolkit.createButton(composite, "Browse...", new BrowseClassesSelectionListener());
		}

		formObjectToolkit.paintBordersFor(composite);
	}

	private void initAndFillAndroidComposite(Composite composite) {

		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// row 1

		// col 1 and 2

		EcFormToolkit formObjectToolkit = getEcFormToolkit();

		fRunOnAndroidCheckbox = 
				formObjectToolkit.createGridCheckBox(
						composite, "Run on Android", new RunOnAndroidApplier());

		SwtObjectHelper.setHorizontalSpan(fRunOnAndroidCheckbox, 2);


		// row 2


		if (baseRunnerFieldsActive()) {
			// col 1 - label
			fAndroidBaseRunnerLabel = getEcFormToolkit().createLabel(composite, "Base runner");

			// col 2 - runner combo
			fAndroidBaseRunnerCombo = new ComboViewer(composite, SWT.NONE).getCombo();
			fAndroidBaseRunnerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			fAndroidBaseRunnerCombo.addFocusListener(new AndroidBaseRunnerComboFocusListener());
			fAndroidBaseRunnerCombo.addSelectionListener(new AndroidBaseRunnerComboSelectionListener());

			fillAndroidBaseRunnerCombo();
		}
	}

	private void refreshAndroidBaseRunnerCombo() {

		if (baseRunnerFieldsActive()) {
			String currentRunner = fAndroidBaseRunnerCombo.getText();

			fAndroidBaseRunnerCombo.removeAll();
			fillAndroidBaseRunnerCombo();

			fAndroidBaseRunnerCombo.setText(currentRunner);
		}
	}

	private void fillAndroidBaseRunnerCombo() {
		String projectPath = null;
		try {
			projectPath = new EclipseProjectHelper(getJavaProjectProvider()).getProjectPath();

			List<String> runners = fClassInterface.createRunnerList(projectPath);

			for(String runner : runners) {
				fAndroidBaseRunnerCombo.add(runner);
			}			
		} catch (EcException e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof ClassNode){
			ClassNode selectedClass = (ClassNode)getSelectedElement();
			fClassInterface.setOwnNode(selectedClass);
			String title = fClassInterface.getQualifiedName();
			//Remove implementation status for performance reasons
			//			String title = fClassIf.getQualifiedName() + " [" + fClassIf.getImplementationStatus() + "]";
			getMainSection().setText(title);
			fClassNameText.setText(fClassInterface.getLocalName());
			fPackageNameText.setText(fClassInterface.getPackageName());

			if (fIsAndroidProject) {
				refreshAndroid();
			}

			fMethodsSection.setInput(selectedClass);
			fGlobalParametersSection.setInput(selectedClass);

			refreshOtherMethodsSection(selectedClass);

			fCommentsSection.setInput(selectedClass);

			getMainSection().layout();
		}
	}

	private void refreshOtherMethodsSection(ClassNode classNode) {
		if (!ApplicationContext.isProjectAvailable()) {
			return;
		}

		if (fClassInterface.getImplementationStatus() == EImplementationStatus.NOT_IMPLEMENTED) {
			fOtherMethodsSection.setVisible(false);
			return;
		}

		fOtherMethodsSection.setInput(classNode);
		fOtherMethodsSection.setVisible(fOtherMethodsSection.getItemsCount() > 0);
	}

	private void refreshAndroid() {
		boolean runOnAndroid = fClassInterface.getRunOnAndroid(); 
		fRunOnAndroidCheckbox.setSelection(runOnAndroid);

		if (baseRunnerFieldsActive()) {
			fAndroidBaseRunnerLabel.setEnabled(runOnAndroid);
			fAndroidBaseRunnerCombo.setEnabled(runOnAndroid);

			String androidBaseRunner = fClassInterface.getAndroidBaseRunner();

			if (androidBaseRunner == null) {
				androidBaseRunner = "";
			}

			fAndroidBaseRunnerCombo.setText(androidBaseRunner);
		}
	}

	private boolean baseRunnerFieldsActive() {
		return false;
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ClassNode.class;
	}


	private class BrowseClassesSelectionListener extends ButtonClickListener {
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassInterface.reassignImplementedClass();
		}
	}

	private class AndroidBaseRunnerComboSelectionListener extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e){

			if (baseRunnerFieldsActive()) {
				fClassInterface.setAndroidBaseRunner(fAndroidBaseRunnerCombo.getText());

				String androidBaseRunner = fClassInterface.getAndroidBaseRunner();
				fAndroidBaseRunnerCombo.setText(androidBaseRunner);
			}
		}
	}	

	private class AndroidBaseRunnerComboFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			refreshAndroidBaseRunnerCombo();
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	}	

	private class ClassNameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fClassInterface.setLocalName(fClassNameText.getText());
			fClassNameText.setText(fClassInterface.getLocalName());
		}
	}	

	private class PackageNameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fClassInterface.setPackageName(fPackageNameText.getText());
			fPackageNameText.setText(fClassInterface.getPackageName());
		}
	}	

	private class RunOnAndroidApplier implements IValueApplier {

		@Override
		public void applyValue() {

			boolean selection = fRunOnAndroidCheckbox.getSelection();
			fClassInterface.setRunOnAndroid(selection);

			if (selection) {
				adjustAndroidBaseRunner();
			}
			refresh();
		}		

		private void adjustAndroidBaseRunner() {

			final String defaultBaseAndroidBaseRunner = 
					AndroidBaseRunnerHelper.getDefaultAndroidBaseRunnerName();

			if (!baseRunnerFieldsActive()) {
				fClassInterface.setAndroidBaseRunner(defaultBaseAndroidBaseRunner);
				return;
			}

			String androidBaseRunner = fClassInterface.getAndroidBaseRunner();
			if (androidBaseRunner == null || androidBaseRunner.isEmpty()) {

				fClassInterface.setAndroidBaseRunner(defaultBaseAndroidBaseRunner);
			}
		}

	}

}
