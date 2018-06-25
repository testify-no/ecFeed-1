/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.export.ExportTemplateFactory;
import com.ecfeed.core.serialization.export.IExportTemplate;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.resources.ResourceHelper;
import com.ecfeed.ui.common.ApplyValueMode;
import com.ecfeed.ui.dialogs.basic.AskIfOverwriteFileDialog;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit.GridButton;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.dialogs.basic.FileOpenAndReadDialog;
import com.ecfeed.ui.dialogs.basic.FileSaveDialog;
import com.ecfeed.ui.dialogs.basic.InfoDialog;
import com.ecfeed.ui.dialogs.basic.YesNoDialog;
import com.ecfeed.utils.EclipseHelper;

public class TestCasesExportDialog extends TitleAreaDialog {

	private static final String[] templateFileExtension = { "*.eet" };

	IExportTemplate fExportTemplate;
	private Text fTemplateTextField;
	private Text fPreviewTextField;
	private String fCurrentTemplateFormat;
	DialogObjectToolkit.FileSelectionComposite fExportFileSelectionComposite;
	private Text fTargetFileTextField;
	private String fTargetFile;
	private MethodNode fMethodNode;
	private FileCompositeVisibility fFileCompositeVisibility;
	private Combo fExportFormatCombo;
	private ExportTemplateFactory fExportTemplateFactory;
	private Collection<TestCaseNode> fTestCaseNodes;

	public enum FileCompositeVisibility {
		VISIBLE, NOT_VISIBLE
	}

	public TestCasesExportDialog(
			FileCompositeVisibility fileCompositeVisibility,
			ExportTemplateFactory exportTemplateFactory,
			IExportTemplate exportTemplate,
			String targetFile,
			MethodNode methodNode,
			Collection<TestCaseNode> testCaseNodes) {

		super(EclipseHelper.getActiveShell());

		setHelpAvailable(true);
		setDialogHelpAvailable(false);

		fFileCompositeVisibility = fileCompositeVisibility;
		fExportTemplateFactory = exportTemplateFactory;
		fExportTemplate = exportTemplate;
		fTargetFile = targetFile;
		fMethodNode = methodNode;
		fTestCaseNodes = testCaseNodes;
	}

	@Override
	public void create() {
		super.create();

		if (ApplicationContext.isApplicationTypeRemoteRap()) {
			setOkEnabled(true);
			return;
		}

		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {

			if (fTargetFile == null) {
				setOkEnabled(false);
			} else {
				setOkEnabled(true);
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parentComposite) {
		setDialogTitle(this);
		setDialogMessage(this);

		Composite dialogAreaComposite = 
				(Composite) super.createDialogArea(parentComposite);

		dialogAreaComposite.addHelpListener(new HelpListener() {

			@Override
			public void helpRequested(HelpEvent e) {
				InfoDialog.open(readHelpFromResource());
			}
		});

		Composite childComposite = 
				DialogObjectToolkit.createGridComposite(dialogAreaComposite, 1);

		createTemplateTextComposite(childComposite);

		createPreviewTextComposite(childComposite);

		if (ApplicationContext.isApplicationTypeLocal() 
				&& (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE)) {
			createTargetFileComposite(childComposite);
		}

		setFocusedControl();

		return dialogAreaComposite;
	}

	@Override
	protected void okPressed() {

		fExportTemplate.setTemplateText(fTemplateTextField.getText());

		if (!canOverwriteExistingTemplate()) {
			return;

		}

		if (fFileCompositeVisibility != FileCompositeVisibility.VISIBLE) {
			fTargetFile = null;
			super.okPressed();
			return;
		}

		if (fTargetFileTextField != null) {

			fTargetFile = fTargetFileTextField.getText();
			if (!canOverwriteFile(fTargetFile)) {
				return;
			}
		}

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}	

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export template definition";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
		setMessage(EXPORT_TEST_DATA_MESSAGE);
	}

	private String readHelpFromResource() {
		final String DEFAULT_TEMPLATE_TEXT_FILE = "res/TestCasesExportTemplate.txt";
		String templateText = null;

		try {
			templateText = ResourceHelper.readTextFromResource(this.getClass(),
					DEFAULT_TEMPLATE_TEXT_FILE);
		} catch (Exception e) {
			ExceptionCatchDialog.open("Can not read template", e.getMessage());
		}

		return templateText;
	}

	private void createTemplateTextComposite(Composite parentComposite) {
		Composite childComposite = 
				DialogObjectToolkit.createGridComposite(parentComposite, 1);

		createTemplateLabelAndButtonsComposite(childComposite);
		fTemplateTextField = 
				DialogObjectToolkit.createGridText(
						childComposite, 150, 
						fExportTemplate.getTemplateText());

		DialogObjectToolkit.setMonospaceFont(fTemplateTextField);
	}

	private void createTemplateLabelAndButtonsComposite(Composite parentComposite) {

		Composite composite = DialogObjectToolkit.createGridComposite(parentComposite, 4);

		createExportTemplateCombo(composite);

		createTeplateRdWrButton(composite, "Load...", new LoadButtonSelectionAdapter());
		createTeplateRdWrButton(composite, "Save As...", new SaveAsButtonSelectionAdapter());
	}

	private void createTeplateRdWrButton(Composite composite, String label, SelectionAdapter selectionAdapter) {

		GridButton loadButton = DialogObjectToolkit.createGridButton(composite, label, selectionAdapter);

		GridData gd1 = loadButton.getLayoutData();
		gd1.widthHint = 80;
	}

	private void createExportTemplateCombo(Composite composite) {
		final String DEFINE_TEMPLATE = "Template: ";
		DialogObjectToolkit.createLabel(composite, DEFINE_TEMPLATE);

		fExportFormatCombo = 
				DialogObjectToolkit.createReadOnlyGridCombo(
						composite, new ExportFormatComboValueApplier(), ApplyValueMode.ON_SELECTION_ONLY);

		String[] exportFormats = ExportTemplateFactory.getAvailableExportFormats();
		fExportFormatCombo.setItems(exportFormats);

		String format = fExportTemplate.getTemplateFormat();
		fExportFormatCombo.setText(format);
		fCurrentTemplateFormat = format;
	}

	private void createPreviewTextComposite(Composite parentComposite) {

		Composite childComposite = 
				DialogObjectToolkit.createGridComposite(parentComposite, 1);

		createPreviewLabelAndButtonsComposite(childComposite);
		fPreviewTextField = 
				DialogObjectToolkit.createGridText(
						childComposite, 150, 
						fExportTemplate.createPreview(fTestCaseNodes));

		DialogObjectToolkit.setMonospaceFont(fPreviewTextField);
	}

	private void createPreviewLabelAndButtonsComposite(
			Composite parentComposite) {

		Composite composite = DialogObjectToolkit.createGridComposite(parentComposite, 2);

		createPreviewButtonsComposite(composite);
	}

	private void createPreviewButtonsComposite(Composite parentComposite) {

		Composite buttonComposite = 
				DialogObjectToolkit.createGridComposite(parentComposite, 2);

		DialogObjectToolkit.createLabel(buttonComposite, "Sample preview: ");

		GridButton gridButton = 
				DialogObjectToolkit.createGridButton(
						buttonComposite, "Update preview", new UpdatePreviewButtonSelectionAdapter());

		GridData gridData = gridButton.getLayoutData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.grabExcessHorizontalSpace = true;
	}

	private void createTargetFileComposite(Composite parent) {
		final String TARGET_FILE = "Target file";

		fExportFileSelectionComposite =
				DialogObjectToolkit.createFileSelectionComposite(
						parent, TARGET_FILE, getExportFileExtensions(), new FileTextModifyListener());

		fTargetFileTextField = fExportFileSelectionComposite.getTextField();

		if (fTargetFile != null) {
			fTargetFileTextField.setText(fTargetFile);
		}
	}

	public String[] getExportFileExtensions() {

		String fileExtension = fExportTemplate.getFileExtension();
		String[] extensionsFilter = { "*." + fileExtension, "*.*" };

		return extensionsFilter;
	}

	private void setFocusedControl() {
		if (ApplicationContext.isApplicationTypeLocal() && 
				fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			fTargetFileTextField.setFocus();
		} else {
			fTemplateTextField.setFocus();
		}
	}

	public static boolean canOverwriteFile(String targetFile) {
		if (!DiskFileHelper.fileExists(targetFile)) {
			return true;
		}

		AskIfOverwriteFileDialog.Result result = AskIfOverwriteFileDialog.open(targetFile);

		if (result == AskIfOverwriteFileDialog.Result.NO) {
			return false;
		}		

		return true;
	}

	public IExportTemplate getExportTemplate() {
		return fExportTemplate;
	}

	public String getTargetFile() {

		if (ApplicationContext.isApplicationTypeRemoteRap()) {
			return fMethodNode.getName();
		}

		return fTargetFile;
	}

	private void updateStatus() {

		if (ApplicationContext.isApplicationTypeRemoteRap()) {
			setMessage(null);
			setOkEnabled(true);
			return;
		}

		if (fTargetFileTextField == null || fTargetFileTextField.getText().isEmpty()) {
			setDialogMessageSelectFile();

			if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
				setOkEnabled(false);
			}
		} else {
			setMessage(null);
			setOkEnabled(true);
		}
	}

	private void setDialogMessageSelectFile() {
		final String SELECT_TARGET = "Select target export file";
		setMessage(SELECT_TARGET);
	}

	private void setOkEnabled(boolean enabled) {

		Button okButton = getButton(IDialogConstants.OK_ID);

		if (okButton == null) {
			return;
		}

		okButton.setEnabled(enabled);
	}

	private boolean canOverwriteExistingTemplate() {

		if (!fExportTemplate.isTemplateTextModified()) {
			return true;
		}

		final String CONTINUE_WITHOUT_SAVING = "Current template is modified. Do you want to continue without saving it?";
		YesNoDialog.Result result = YesNoDialog.open(CONTINUE_WITHOUT_SAVING);

		if (result == YesNoDialog.Result.NO) {
			return false;
		}

		return true;
	}

	private void updatePreview() {
		fExportTemplate.setTemplateText(fTemplateTextField.getText());
		fPreviewTextField.setText(fExportTemplate.createPreview(fTestCaseNodes));
	}

	private class LoadButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			fExportTemplate.setTemplateText(fTemplateTextField.getText());

			if (!canOverwriteExistingTemplate()) {
				return;
			}

			final String LOAD_DEF_FILE = "Load template definition file"; 
			String text = FileOpenAndReadDialog.open(LOAD_DEF_FILE, templateFileExtension);

			if (text != null) {
				fExportTemplate.setTemplateText(text);
				fTemplateTextField.setText(text);
			}
		}

	}

	private class SaveAsButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final String SAVE_DEF_FILE = "Save template definition file"; 
			FileSaveDialog.Result result = FileSaveDialog.open(SAVE_DEF_FILE , fTemplateTextField.getText(), templateFileExtension);

			if (result == FileSaveDialog.Result.SAVED) {
				fExportTemplate.setTemplateText(fTemplateTextField.getText());
			}
		}
	}

	private class UpdatePreviewButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			updatePreview();
		}
	}	

	private class FileTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateStatus();
		}
	}

	private class ExportFormatComboValueApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fExportTemplate.setTemplateText(fTemplateTextField.getText());

			String exportFormat = fExportFormatCombo.getText();

			if (StringHelper.isEqual(exportFormat, fCurrentTemplateFormat)) {
				return;
			}

			if (!canOverwriteExistingTemplate()) {
				return;
			}

			fExportTemplate = fExportTemplateFactory.createTemplate(exportFormat);
			String templateDefaultText = fExportTemplate.createDefaultTemplateText();
			fExportTemplate.setTemplateText(templateDefaultText);
			fTemplateTextField.setText(templateDefaultText);

			if (fExportFileSelectionComposite != null) {
				fExportFileSelectionComposite.setFileExtensionsFilter(getExportFileExtensions());
			}

			updatePreview();

			fCurrentTemplateFormat = exportFormat;
		}

	}

}
