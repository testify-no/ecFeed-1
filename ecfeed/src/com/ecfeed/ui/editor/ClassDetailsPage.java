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
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassDetailsPage extends BasicDetailsPage {

	private boolean fIsAndroidProject;
	private IFileInfoProvider fFileInfoProvider;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Text fPackageNameText;
	private Button fRunOnAndroidCheckbox;
	private Label fAndroidBaseRunnerLabel;
	private Combo fAndroidBaseRunnerCombo;	
	private ClassInterface fClassIf;
	private GlobalParametersViewer fGlobalParametersSection;
	private ExportableJavaDocCommentsSection fCommentsSection;


	public ClassDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fIsAndroidProject = new EclipseProjectHelper(fFileInfoProvider).isAndroidProject();
		fClassIf = new ClassInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createBasicParametersComposite(getMainComposite());

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new ExportableJavaDocCommentsSection(this, this, fFileInfoProvider));
		}
		addViewerSection(fMethodsSection = new MethodsViewer(this, this, fFileInfoProvider));
		addViewerSection(fGlobalParametersSection = new GlobalParametersViewer(this, this, fFileInfoProvider));

		if (fFileInfoProvider.isProjectAvailable()) {
			addViewerSection(fOtherMethodsSection = new OtherMethodsViewer(this, this, fFileInfoProvider));
		}

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createBasicParametersComposite(Composite parent) {

		Composite mainComposite = getToolkit().createComposite(parent);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite packageAndClassComposite = getToolkit().createComposite(mainComposite);
		initAndFillClassComposite(packageAndClassComposite);

		if (fIsAndroidProject) {
			Composite androidComposite = getToolkit().createComposite(mainComposite);
			initAndFillAndroidComposite(androidComposite);
		}
	}

	private void initAndFillClassComposite(Composite composite) {

		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		FormObjectToolkit formObjectToolkit = getFormObjectToolkit(); 


		formObjectToolkit.createLabel(composite, "Package name");
		fPackageNameText = formObjectToolkit.createGridText(composite, new PackageNameApplier());
		formObjectToolkit.createEmptyLabel(composite);


		formObjectToolkit.createLabel(composite, "Class name");
		fClassNameText = formObjectToolkit.createGridText(composite, new ClassNameApplier());
		if (fFileInfoProvider.isProjectAvailable()) {
			formObjectToolkit.createButton(composite, "Browse...", new BrowseClassesSelectionListener());
		}

		formObjectToolkit.paintBorders(composite);
	}

	private void initAndFillAndroidComposite(Composite composite) {

		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// row 1

		// col 1 and 2

		FormObjectToolkit formObjectToolkit = getFormObjectToolkit();

		fRunOnAndroidCheckbox = 
				formObjectToolkit.createGridCheckBox(
						composite, "Run on Android", new RunOnAndroidApplier());

		SwtObjectHelper.setHorizontalSpan(fRunOnAndroidCheckbox, 2);


		// row 2


		if (baseRunnerFieldsActive()) {
			// col 1 - label
			fAndroidBaseRunnerLabel = getToolkit().createLabel(composite, "Base runner");

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
			projectPath = new EclipseProjectHelper(fFileInfoProvider).getProjectPath();

			List<String> runners = fClassIf.createRunnerList(projectPath);

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
			fClassIf.setOwnNode(selectedClass);
			String title = fClassIf.getQualifiedName();
			//Remove implementation status for performance reasons
			//			String title = fClassIf.getQualifiedName() + " [" + fClassIf.getImplementationStatus() + "]";
			getMainSection().setText(title);
			fClassNameText.setText(fClassIf.getLocalName());
			fPackageNameText.setText(fClassIf.getPackageName());

			if (fIsAndroidProject) {
				refreshAndroid();
			}

			fMethodsSection.setInput(selectedClass);
			fGlobalParametersSection.setInput(selectedClass);

			refreshOtherMethodsSection(selectedClass);

			if (fFileInfoProvider.isProjectAvailable()) {
				fCommentsSection.setInput(selectedClass);
			}

			getMainSection().layout();
		}
	}

	private void refreshOtherMethodsSection(ClassNode classNode) {
		if (!fFileInfoProvider.isProjectAvailable()) {
			return;
		}

		if (fClassIf.getImplementationStatus() == EImplementationStatus.NOT_IMPLEMENTED) {
			fOtherMethodsSection.setVisible(false);
			return;
		}

		fOtherMethodsSection.setInput(classNode);
		fOtherMethodsSection.setVisible(fOtherMethodsSection.getItemsCount() > 0);
	}

	private void refreshAndroid() {
		boolean runOnAndroid = fClassIf.getRunOnAndroid(); 
		fRunOnAndroidCheckbox.setSelection(runOnAndroid);

		if (baseRunnerFieldsActive()) {
			fAndroidBaseRunnerLabel.setEnabled(runOnAndroid);
			fAndroidBaseRunnerCombo.setEnabled(runOnAndroid);

			String androidBaseRunner = fClassIf.getAndroidBaseRunner();

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
			fClassIf.reassignClass();
		}
	}

	private class AndroidBaseRunnerComboSelectionListener extends ComboSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e){

			if (baseRunnerFieldsActive()) {
				fClassIf.setAndroidBaseRunner(fAndroidBaseRunnerCombo.getText());

				String androidBaseRunner = fClassIf.getAndroidBaseRunner();
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
			fClassIf.setLocalName(fClassNameText.getText());
			fClassNameText.setText(fClassIf.getLocalName());
		}
	}	

	private class PackageNameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fClassIf.setPackageName(fPackageNameText.getText());
			fPackageNameText.setText(fClassIf.getPackageName());
		}
	}	

	private class RunOnAndroidApplier implements IValueApplier {

		@Override
		public void applyValue() {

			boolean selection = fRunOnAndroidCheckbox.getSelection();
			fClassIf.setRunOnAndroid(selection);

			if (selection) {
				adjustAndroidBaseRunner();
			}
			refresh();
		}		

		private void adjustAndroidBaseRunner() {

			final String defaultBaseAndroidBaseRunner = 
					AndroidBaseRunnerHelper.getDefaultAndroidBaseRunnerName();

			if (!baseRunnerFieldsActive()) {
				fClassIf.setAndroidBaseRunner(defaultBaseAndroidBaseRunner);
				return;
			}

			String androidBaseRunner = fClassIf.getAndroidBaseRunner();
			if (androidBaseRunner == null || androidBaseRunner.isEmpty()) {

				fClassIf.setAndroidBaseRunner(defaultBaseAndroidBaseRunner);
			}
		}

	}

}
