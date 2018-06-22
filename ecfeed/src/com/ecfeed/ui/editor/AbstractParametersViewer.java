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
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.MainActionGrouppingProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ParametersParentInterface;

public abstract class AbstractParametersViewer extends TableViewerSection {

	private final String BROWSE_PARAMETER_TYPE_STRING = "Browse...";

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fTypeColumn;
	private ParametersParentInterface fParentIf;
	private Button fRemoveSelectedButton;

	protected abstract ParametersParentInterface getParametersParentInterface();
	protected abstract AbstractParameterInterface getParameterInterface();


	public AbstractParametersViewer(
			ISectionContext sectionContext,
			IMainTreeProvider mainTreeProvider,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider,
			int STYLE) {
		super(sectionContext, updateContext, javaProjectProvider, STYLE);
		fParentIf = getParametersParentInterface();

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		addButton("Add parameter", new AddNewParameterAdapter());
		fRemoveSelectedButton = addButton("Remove selected", 
				new ActionSelectionAdapter(
						new DeleteAction(
								getViewer(), getModelUpdateContext()), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		fNameColumn.setEditingSupport(new ParameterNameEditingSupport());
		fTypeColumn.setEditingSupport(getParameterTypeEditingSupport());

		addDoubleClickListener(new SelectNodeDoubleClickListener(mainTreeProvider));

		setActionGrouppingProvider(new MainActionGrouppingProvider(getTableViewer(), getModelUpdateContext(), javaProjectProvider));
		addSelectionChangedListener(new SelectionChangedListener());
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 100, new NodeNameColumnLabelProvider());
		fTypeColumn = addColumn("Type", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((AbstractParameterNode)element).getType();
			}
		});
	}

	@Override
	public void refresh() {

		Object selectedElement = getSelectedElement();

		if (selectedElement == null) {
			fRemoveSelectedButton.setEnabled(false);
		} else {
			fRemoveSelectedButton.setEnabled(true);
		}
	}	

	public void setInput(ParametersParentNode parent){
		fParentIf.setOwnNode(parent);
		super.setInput(parent.getParameters());
	}

	protected EditingSupport getParameterTypeEditingSupport() {
		return new ParameterTypeEditingSupport();
	}

	private class SelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			refresh();
		}
	}

	protected class ParameterTypeEditingSupport extends EditingSupport {

		private ComboBoxCellEditor fCellEditor;

		public ParameterTypeEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				List<String> items = 
						new ArrayList<String>(Arrays.asList(JavaTypeHelper.getSupportedJavaTypes()));

				items.add(BROWSE_PARAMETER_TYPE_STRING);
				fCellEditor = new ComboBoxCellEditor(getTable(), items.toArray(new String[]{}));
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String [] items = fCellEditor.getItems();
			ArrayList<String> newItems = new ArrayList<String>();

			for (int i = 0; i < items.length; ++i) {
				newItems.add(items[i]);
				if (items[i].equals(node.getType())) {
					return i;
				}
			}

			newItems.add(newItems.size() - 1, node.getType());
			fCellEditor.setItems(newItems.toArray(items));
			return (newItems.size() - 2);
		}

		@Override
		protected void setValue(Object element, Object value) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String newType = null;
			int index = (int)value;

			if (index >= 0) {
				newType = fCellEditor.getItems()[index];
			} else {
				newType = ((CCombo)fCellEditor.getControl()).getText();
			}
			if(newType.equals(BROWSE_PARAMETER_TYPE_STRING)){
				getParameterInterface().setOwnNode(node);
				getParameterInterface().importType();
			}
			else{
				getParameterInterface().setOwnNode(node);
				getParameterInterface().setType(newType);
			}

			fCellEditor.setFocus();
		}
	}

	private class ParameterNameEditingSupport extends EditingSupport {

		private TextCellEditor fNameCellEditor;

		public ParameterNameEditingSupport() {
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((AbstractParameterNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			getParameterInterface().setOwnNode((AbstractParameterNode)element);
			getParameterInterface().setName((String)value);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				AbstractParameterNode addedParameter = fParentIf.addNewParameter();
				if(addedParameter != null){
					selectElement(addedParameter);
					startEditingNewParameter(addedParameter);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not create parameter.", e.getMessage());
			}
		}

		private void startEditingNewParameter(AbstractParameterNode addedParameter) {

			if (fNameColumn == null) {
				return;
			}

			ColumnViewer columnViewer = fNameColumn.getViewer();

			if (columnViewer == null) {
				return;
			}

			if (columnViewer.getControl().isDisposed()) {
				return;
			}

			columnViewer.editElement(addedParameter, 0);
		}
	}

}
