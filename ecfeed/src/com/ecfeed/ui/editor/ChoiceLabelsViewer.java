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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.ui.common.ColorConstants;
import com.ecfeed.ui.common.ColorManager;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.ActionProvider;
import com.ecfeed.ui.editor.actions.CutAction;
import com.ecfeed.ui.editor.actions.GlobalActions;
import com.ecfeed.ui.editor.actions.ModelModifyingAction;
import com.ecfeed.ui.editor.actions.NamedAction;
import com.ecfeed.ui.editor.actions.SelectAllAction;
import com.ecfeed.ui.modelif.ChoiceInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceLabelsViewer extends TableViewerSection {

	private static final int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private ChoiceInterface fChoiceIf;
	private Text fSelectedLabelText;
	private String fSelectedLabel;

	private static class LabelClipboard{
		private static List<String> fLabels = new ArrayList<>();

		public static List<String> getContent(){
			return fLabels;
		}

		public static List<String> getContentCopy(){
			List<String> copy = new ArrayList<>();
			for(String label : fLabels){
				copy.add(new String(label));
			}
			return copy;
		}

		public static void setContent(List<String> labels){
			fLabels.clear();
			for(String label : labels){
				fLabels.add(new String(label));
			}
		}
	}

	private class LabelsViewerActionProvider extends ActionProvider{

		public LabelsViewerActionProvider(){
			super();
			addAction("edit", new LabelCopyAction());
			addAction("edit", 
					new CutAction(
							new LabelCopyAction(), 
							new LabelDeleteAction(getModelUpdateContext())));


			addAction("edit", new LabelPasteAction(getModelUpdateContext()));
			addAction("edit", new LabelDeleteAction(getModelUpdateContext()));
			addAction("selection", new SelectAllAction(getTableViewer()));
		}
	}

	private class LabelCopyAction extends NamedAction{
		public LabelCopyAction() {
			super(GlobalActions.COPY.getId(), GlobalActions.COPY.getDescription());
		}

		@Override
		public boolean isEnabled(){
			return getSelectedLabels().size() > 0;
		}

		@Override
		public void run(){
			LabelClipboard.setContent(getSelectedLabels());
		}
	}

	private class LabelPasteAction extends ModelModifyingAction{
		public LabelPasteAction(IModelUpdateContext updateContext) {
			super(GlobalActions.PASTE.getId(), GlobalActions.PASTE.getDescription(), getViewer(), updateContext);
		}

		@Override
		public boolean isEnabled(){
			return LabelClipboard.getContent().size() > 0;
		}

		@Override
		public void run(){
			fChoiceIf.addLabels(LabelClipboard.getContentCopy());
		}
	}

	private class LabelDeleteAction extends ModelModifyingAction{
		public LabelDeleteAction(IModelUpdateContext updateContext) {
			super(GlobalActions.DELETE.getId(), GlobalActions.DELETE.getDescription(), 
					getTableViewer(), updateContext);
		}

		@Override
		public boolean isEnabled(){
			for(String label : getSelectedLabels()){
				if(fChoiceIf.isLabelInherited(label) == false){
					return true;
				}
			}
			return false;
		}

		@Override
		public void run(){
			fChoiceIf.removeLabels(getSelectedLabels());
		}
	}

	private class AddLabelAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				String newLabel = fChoiceIf.addNewLabel();
				if(newLabel != null){
					getTableViewer().editElement(newLabel, 0);
				}
			}
			catch (Exception e) {
				ExceptionCatchDialog.open("Can not add label.", e.getMessage());
			}
		}
	}

	public class LabelEditingSupport extends EditingSupport{
		private TextCellEditor fLabelCellEditor;

		public LabelEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fLabelCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fLabelCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return fChoiceIf.isLabelInherited((String)element) == false;
		}

		@Override
		protected Object getValue(Object element) {
			return (String)element;
		}

		@Override
		protected void setValue(Object element, Object value) {
			String strValue = (String)value;
			if (StringHelper.isNullOrEmpty(strValue)) {
				final String INVALID_LABEL = "Invalid label"; 
				final String LABEL_NOT_EMPTY = "Label must not be empty.";
				ErrorDialog.open(INVALID_LABEL, LABEL_NOT_EMPTY);
				return;
			}
			fChoiceIf.renameLabel((String)element, (String)value);
		}
	}

	private class LabelColumnLabelProvider extends ColumnLabelProvider{
		@Override
		public String getText(Object element){
			return (String)element;
		}

		@Override
		public Color getForeground(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fChoiceIf.isLabelInherited(label)){
					return ColorManager.getColor(ColorConstants.INHERITED_LABEL_FOREGROUND);
				}
			}
			return null;
		}

		@Override
		public Font getFont(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fChoiceIf.isLabelInherited(label)){
					Font font = getTable().getFont();
					FontData currentFontData = font.getFontData()[0];
					FontData fd = new FontData();
					fd.setHeight(currentFontData.getHeight());
					fd.setStyle(fd.getStyle() | SWT.ITALIC);
					Device device = font.getDevice();
					return new Font(device, fd);
				}
			}
			return null;
		}
	}

	private class LabelTextSelectionAdapter extends AbstractSelectionAdapter { 
		@Override
		public void widgetSelected(SelectionEvent e){
			if (fSelectedLabel == null) {
				return;
			}
			fChoiceIf.renameLabel(fSelectedLabel, fSelectedLabelText.getText());
			fSelectedLabel = null;
			fSelectedLabelText.setText("");
			fSelectedLabelText.setEnabled(false);
		}
	}

	private class LabelSelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();

			IStructuredSelection strSelection = getStructuredSelection(selection);
			if (strSelection == null) {
				fSelectedLabel = null;
				return;
			}

			fSelectedLabel = strSelection.getFirstElement().toString();
			fSelectedLabelText.setText(fSelectedLabel);
			fSelectedLabelText.setEnabled(true);
		}

		private IStructuredSelection getStructuredSelection(ISelection selection) {
			if (selection == null) {
				return null;
			}			
			if (selection.isEmpty()) {
				return null;
			}
			if (!(selection instanceof IStructuredSelection)) {
				return null;
			}
			return (IStructuredSelection)selection;
		}
	}

	public ChoiceLabelsViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider, STYLE);

		fChoiceIf = new ChoiceInterface(getModelUpdateContext(), javaProjectProvider);
		getSection().setText("Labels");

		addButton("Add label", new AddLabelAdapter());
		addButton("Remove selected", 
				new ActionSelectionAdapter(
						new LabelDeleteAction(updateContext), 
						Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		if (isLabelEditionInTextField()) {
			AbstractSelectionAdapter adapter = new LabelTextSelectionAdapter();
			fSelectedLabelText = addText("", adapter);
			fSelectedLabelText.setEnabled(false);
			addSelectionChangedListener(new LabelSelectionChangedListener());
		}

		setActionProvider(new LabelsViewerActionProvider());
	}

	private boolean isLabelEditionInTextField() {
		if (SystemHelper.isOperatingSystemLinux()) {
			if (ApplicationContext.isProjectAvailable()) {
				return false;
			}
			return true;			
		} else {
			return false;
		}
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn labelColumn = addColumn("Label", 150, new LabelColumnLabelProvider());
		labelColumn.setEditingSupport(new LabelEditingSupport(getTableViewer()));
	}

	public void setInput(ChoiceNode	choice){
		fChoiceIf.setOwnNode(choice);
		super.setInput(choice.getAllLabels());
	}

	@SuppressWarnings("unchecked")
	private List<String> getSelectedLabels(){
		return getSelection().toList();
	}
}
