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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

public class MethodsViewer extends TableViewerSection {

	private TableViewerColumn fMethodsColumn;
	private ClassInterface fClassIf;
	private MethodInterface fMethodIf;
	private Button fRemoveSelectedButton;


	public MethodsViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fClassIf = new ClassInterface(this, fileInfoProvider);
		fMethodIf = new MethodInterface(this, fileInfoProvider);

		fMethodsColumn.setEditingSupport(new MethodNameEditingSupport());

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		fRemoveSelectedButton = 
				addButton("Remove selected", 
						new ActionSelectionAdapter(
								new DeleteAction(getViewer(), this), 
								Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		addSelectionChangedListener(new SelectionChangedListener());
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this, fileInfoProvider));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected void createTableColumns() {
		fMethodsColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		addColumn("Parameters", 450, new MethodsArgsLabelProvider());
	}

	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return true;
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

	public void setInput(ClassNode classNode){
		fClassIf.setOwnNode(classNode);
		super.setInput(classNode.getMethods());
	}

	public class MethodNameEditingSupport extends EditingSupport {

		private TextCellEditor fNameCellEditor;

		public MethodNameEditingSupport() {
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
			fMethodIf.setOwnNode((MethodNode)element);
			return fMethodIf.getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			MethodNode method = (MethodNode)element;
			fMethodIf.setOwnNode(method);
			fMethodIf.setName(newName);
		}
	}

	private class AddNewMethodAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				MethodNode newMethod = fClassIf.addNewMethod();
				if(newMethod != null){
					selectElement(newMethod);
					fMethodsColumn.getViewer().editElement(newMethod, 0);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add new method", e.getMessage());
			}
		}
	}

	private class MethodsArgsLabelProvider extends NodeViewerColumnLabelProvider{
		@Override
		public String getText(Object element){
			List<String> argTypes = fMethodIf.getArgTypes((MethodNode)element);
			List<String> argNames = fMethodIf.getArgNames((MethodNode)element);
			List<MethodParameterNode> parameters = ((MethodNode)element).getMethodParameters();
			String result = "";
			for(int i = 0; i < argTypes.size(); i++){
				result += (parameters.get(i).isExpected()?"[e]":"") + ModelHelper.convertToLocalName(argTypes.get(i)) + " " + argNames.get(i);
				if(i < argTypes.size() - 1){
					result += ", ";
				}
			}
			return result;
		}
	}

	private class SelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			refresh();
		}
	}	

}
