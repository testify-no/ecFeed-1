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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.resources.ResourceHelper;
import com.ecfeed.core.serialization.export.ExportTemplateControllerFactory;
import com.ecfeed.core.serialization.export.IExportTemplateController;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.ApplyValueMode;
import com.ecfeed.ui.dialogs.basic.AskIfOverwriteFileDialog;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.dialogs.basic.FileOpenAndReadDialog;
import com.ecfeed.ui.dialogs.basic.FileSaveDialog;
import com.ecfeed.ui.dialogs.basic.InfoDialog;
import com.ecfeed.ui.dialogs.basic.YesNoDialog;
import com.ecfeed.ui.editor.IValueApplier;
import com.ecfeed.utils.EclipseHelper;

public class TestCasesExportDialog extends TitleAreaDialog {

	private static final String[] templateFileExtension = { "*.eet" };

	private String fInitialTemplate;
	private String fTemplate;
	private Text fTemplateText;
	private String fCurrentTemplateFormat;	
	private Text fTargetFileText;
	private String fTargetFile;
	private DialogObjectToolkit fDialogObjectToolkit;
	private FileCompositeVisibility fFileCompositeVisibility;
	private Combo fExportFormatCombo;
	private int fMethodParametersCount;


	public enum FileCompositeVisibility {
		VISIBLE, NOT_VISIBLE
	}

	public TestCasesExportDialog(
			FileCompositeVisibility fileCompositeVisibility,
			String initialTemplate,
			String targetFile,
			int methodParametersCount) {
		super(EclipseHelper.getActiveShell());
		setHelpAvailable(true);
		setDialogHelpAvailable(false);

		fFileCompositeVisibility = fileCompositeVisibility;
		fInitialTemplate = initialTemplate;
		fTemplate = initialTemplate;
		fTargetFile = targetFile;
		fMethodParametersCount = methodParametersCount;

		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}

	@Override
	public void create() {
		super.create();

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
				fDialogObjectToolkit.createGridComposite(dialogAreaComposite, 1);

		createTemplateTextComposite(childComposite);

		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			createTargetFileComposite(childComposite);
		}

		setFocusedControl();

		return dialogAreaComposite;
	}

	@Override
	protected void okPressed() {

		if (!canOverwriteExistingTemplate()) {
			return;

		}

		createTemplate();

		if (fFileCompositeVisibility != FileCompositeVisibility.VISIBLE) {
			fTargetFile = null;
			super.okPressed();
			return;
		}

		fTargetFile = fTargetFileText.getText();
		if (!canOverwriteFile(fTargetFile)) {
			return;
		}

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
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
				fDialogObjectToolkit.createGridComposite(parentComposite, 1);

		createTemplateLabelAndButtonsComposite(childComposite);
		fTemplateText = fDialogObjectToolkit.createGridText(childComposite, 150, fTemplate);
	}

	private void createTemplateLabelAndButtonsComposite(
			Composite parentComposite) {

		Composite composite = fDialogObjectToolkit.createGridComposite(parentComposite, 4);

		final String DEFINE_TEMPLATE = "Template: ";
		fDialogObjectToolkit.createLabel(composite, DEFINE_TEMPLATE);

		fExportFormatCombo = 
				fDialogObjectToolkit.createReadOnlyGridCombo(
						composite, new ExportFormatComboValueApplier(), ApplyValueMode.ON_SELECTION_ONLY);

		String[] exportFormats = ExportTemplateControllerFactory.getAvailableExportFormats();
		fExportFormatCombo.setItems(exportFormats);

		String defaultformat = ExportTemplateControllerFactory.getDefaultFormat();
		fExportFormatCombo.setText(defaultformat);
		fCurrentTemplateFormat = defaultformat;

		createButtonsComposite(composite);
	}

	private void createButtonsComposite(Composite parentComposite) {
		Composite buttonComposite = fDialogObjectToolkit
				.createFillComposite(parentComposite);

		fDialogObjectToolkit.createButton(buttonComposite, "Load...",
				new LoadButtonSelectionAdapter());
		fDialogObjectToolkit.createButton(buttonComposite, "Save As...",
				new SaveAsButtonSelectionAdapter());
	}

	private void createTargetFileComposite(Composite parent) {
		final String TARGET_FILE = "Target file";

		fTargetFileText = 
				fDialogObjectToolkit.createFileSelectionComposite(
						parent, TARGET_FILE, getExportFileExtensions(), new FileTextModifyListener());

		if (fTargetFile != null) {
			fTargetFileText.setText(fTargetFile);
		}
	}

	public static String[] getExportFileExtensions() {
		String[] extensionsFilter = { "*.csv", "*.txt", "*.*" };
		return extensionsFilter;
	}

	private void setFocusedControl() {
		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			fTargetFileText.setFocus();
		} else {
			fTemplateText.setFocus();
		}
	}

	private boolean isTemplateModified() {
		String currentTemplate = fTemplateText.getText();
		if (currentTemplate.equals(fInitialTemplate)) {
			return false;
		}

		return true;
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

	private void createTemplate() {
		if (fTemplateText == null) {
			fTemplate = null;
			return;
		}

		fTemplate = fTemplateText.getText();
	}

	public String getTemplate() {
		return fTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	private void updateStatus() {
		if (fTargetFileText == null || fTargetFileText.getText().isEmpty()) {
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

		if (!isTemplateModified()) {
			return true;
		}

		final String CONTINUE_WITHOUT_SAVING = "Current template is modified. Do you want to continue without saving it?";
		YesNoDialog.Result result = YesNoDialog.open(CONTINUE_WITHOUT_SAVING);

		if (result == YesNoDialog.Result.NO) {
			return false;
		}

		return true;
	}

	private class LoadButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if (!canOverwriteExistingTemplate()) {
				return;
			}

			final String LOAD_DEF_FILE = "Load template definition file"; 
			String text = FileOpenAndReadDialog.open(LOAD_DEF_FILE, templateFileExtension);

			if (text != null) {
				fTemplateText.setText(text);
				fInitialTemplate = text;
			}

		}

	}

	private class SaveAsButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final String SAVE_DEF_FILE = "Save template definition file"; 
			FileSaveDialog.Result result = FileSaveDialog.open(SAVE_DEF_FILE , fTemplateText.getText(), templateFileExtension);

			if (result == FileSaveDialog.Result.SAVED) {
				fInitialTemplate = fTemplateText.getText();
			}
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
			String exportFormat = fExportFormatCombo.getText();

			if (StringHelper.isEqual(exportFormat, fCurrentTemplateFormat)) {
				return;
			}

			if (!canOverwriteExistingTemplate()) {
				return;
			}

			setTemplateText(exportFormat);
		}

		private void setTemplateText(String exportFormat) {

			IExportTemplateController exportTemplateController = 
					ExportTemplateControllerFactory.createController(exportFormat);

			if (exportTemplateController == null) {
				return;
			}

			fInitialTemplate = exportTemplateController.createDefaultTemplate(fMethodParametersCount);
			fTemplateText.setText(fInitialTemplate);
			fCurrentTemplateFormat = exportFormat;
		}
	}

}
