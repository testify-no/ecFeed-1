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
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.ChoiceInterface;
import com.ecfeed.ui.modelif.ChoicesParentInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;

public class ChoicesViewer extends TableViewerSection {

	private IJavaProjectProvider fJavaProjectProvider;

	private ChoicesParentInterface fParentIf;
	private ChoiceInterface fTableItemIf;

	private boolean fChoiceViewerEnabled;

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fValueColumn;

	private ChoiceNameEditingSupport fNameEditingSupport;
	private ChoiceValueEditingSupport fValueEditingSupport;

	private Button fRemoveSelectedButton;

	private Button fAddChoicesButton;

	private ModelNodeDropListener fDropListener;
	private ModelNodeDragListener fDragListener;

	private IActionProvider fActionProvider;

	private Button fReplaceWithDefaultButton;

	private ChoicesParentNode fSelectedParent;


	private class ChoiceNameEditingSupport extends EditingSupport{

		private TextCellEditor fNameCellEditor;
		private boolean fEnabled;

		public ChoiceNameEditingSupport() {
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
			fEnabled = true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return fEnabled;
		}

		@Override
		protected Object getValue(Object element) {
			return ((ChoiceNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			ChoiceNode choice = (ChoiceNode)element;

			if(newName.equals(choice.getName()) == false){
				fTableItemIf.setOwnNode(choice);
				fTableItemIf.setName(newName);
			}
		}

		public void setEnabled(boolean enabled){
			fEnabled = enabled;
		}
	}

	private class ChoiceValueEditingSupport extends EditingSupport {
		private ComboBoxViewerCellEditor fCellEditor;
		private boolean fEnabled;

		public ChoiceValueEditingSupport(TableViewerSection viewer) {
			super(viewer.getTableViewer());
			fEnabled = true;
			fCellEditor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.TRAIL);
			fCellEditor.setLabelProvider(new LabelProvider());
			fCellEditor.setContentProvider(new ArrayContentProvider());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			ChoiceNode node = (ChoiceNode)element;
			AbstractParameterNode parameter = node.getParameter();
			if(AbstractParameterInterface.hasLimitedValuesSet(node.getParameter())){
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
			} else {
				fCellEditor.setActivationStyle(SWT.NONE);
			}
			List<String> items = AbstractParameterInterface.getSpecialValues(node.getParameter().getType());
			if(JavaTypeHelper.isUserType(parameter.getType())){
				Set<String> usedValues = parameter.getLeafChoiceValues();
				usedValues.removeAll(items);
				items.addAll(usedValues);
			}
			if(items.contains(node.getValueString()) == false){
				items.add(node.getValueString());
			}
			fCellEditor.setInput(items);
			fCellEditor.getViewer().getCCombo().setEditable(AbstractParameterInterface.isBoolean(node.getParameter().getType()) == false);
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return fEnabled && (((ChoiceNode)element).isAbstract() == false);
		}

		@Override
		protected Object getValue(Object element) {
			return ((ChoiceNode)element).getValueString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String valueString = null;
			if(value instanceof String){
				valueString = (String)value;
			} else if(value == null){
				valueString = fCellEditor.getViewer().getCCombo().getText();
			}
			fTableItemIf.setOwnNode((ChoiceNode)element);
			fTableItemIf.setValue(valueString);
		}

		public void setEnabled(boolean enabled){
			fEnabled = enabled;
		}
	}

	private class ChoiceValueLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element){
			if(element instanceof ChoiceNode){
				ChoiceNode choice = (ChoiceNode)element;
				return choice.isAbstract() ? ChoiceNode.ABSTRACT_CHOICE_MARKER : choice.getValueString();
			}
			return "";
		}
	}

	private class AddChoiceAdapter extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				ChoiceNode added = fParentIf.addNewChoice();
				if(added != null){
					getTable().setSelection(added.getIndex());
				}

				setRemoveSelectedStatus();
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add choice.", e.getMessage());
			}
		}
	}

	private class ReplaceWithDefaultAdapter extends ButtonClickListener {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				if(fSelectedParent == fSelectedParent.getParameter()){
					AbstractParameterInterface parameterIf = 
							(AbstractParameterInterface)NodeInterfaceFactory.getNodeInterface(
									fSelectedParent, ChoicesViewer.this, fJavaProjectProvider);
					parameterIf.resetChoicesToDefault();

					setRemoveSelectedStatus();
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not reset with default.", e.getMessage());
			}
		}
	}

	public ChoicesViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider, StyleDistributor.getSectionStyle());

		fJavaProjectProvider = javaProjectProvider;

		fParentIf = new ChoicesParentInterface(this, fJavaProjectProvider);
		fTableItemIf = new ChoiceInterface(this, fJavaProjectProvider);

		fNameEditingSupport = new ChoiceNameEditingSupport();
		fValueEditingSupport = new ChoiceValueEditingSupport(this);

		fNameColumn.setEditingSupport(fNameEditingSupport);
		fValueColumn.setEditingSupport(fValueEditingSupport);

		getSection().setText("Choices");
		fAddChoicesButton = addButton("Add choice", new AddChoiceAdapter());
		fRemoveSelectedButton = 
				addButton("Remove selected", 
						new ActionSelectionAdapter(
								new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		fReplaceWithDefaultButton = addButton("Reset with default", new ReplaceWithDefaultAdapter());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		fActionProvider = new ModelViewerActionProvider(getTableViewer(), this, javaProjectProvider);
		setActionProvider(fActionProvider);
		fDragListener = new ModelNodeDragListener(getViewer());
		fDropListener = new ModelNodeDropListener(getViewer(), this, javaProjectProvider);
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, fDragListener);
		getViewer().addDropSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, fDropListener);

		addSelectionChangedListener(new SelectionChangedListener());
	}

	private class SelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			setRemoveSelectedStatus();
		}
	}


	public void setInput(ChoicesParentNode parent){
		super.setInput(parent.getChoices());
		fSelectedParent = parent;
		fParentIf.setOwnNode(parent);
		if(parent == parent.getParameter()){
			fReplaceWithDefaultButton.setVisible(true);
		}else{
			fReplaceWithDefaultButton.setVisible(false);
		}
	}

	@Override
	public void setVisible(boolean visible){
		this.getSection().setVisible(visible);
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		fValueColumn = addColumn("Value", 150, new ChoiceValueLabelProvider());
	}

	public void setEditEnabled(boolean enabled) {
		fChoiceViewerEnabled = enabled;

		fNameEditingSupport.setEnabled(enabled);
		fValueEditingSupport.setEnabled(enabled);
		fAddChoicesButton.setEnabled(enabled);
		setRemoveSelectedStatus();
		fDragListener.setEnabled(enabled);
		fDropListener.setEnabled(enabled);
		if(enabled){
			setActionProvider(fActionProvider);
		}else{
			setActionProvider(null);
		}
	}

	public void setReplaceButtonEnabled(boolean isEnabled){
		fReplaceWithDefaultButton.setEnabled(isEnabled);
	}

	private void setRemoveSelectedStatus() {

		if (!fChoiceViewerEnabled) {
			fRemoveSelectedButton.setEnabled(false);
			return;
		}

		ISelectionProvider selectionProvider = getViewer();

		if (selectionProvider.getSelection().isEmpty()) {
			fRemoveSelectedButton.setEnabled(false);
		} else {
			fRemoveSelectedButton.setEnabled(true);
		}
	}

}
