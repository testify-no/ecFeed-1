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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.ConstraintInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

public class ConstraintsListViewer extends TableViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private TableViewerColumn fNameColumn;
	private MethodInterface fMethodInterface;
	private ConstraintInterface fConstraintIf;
	private Button fRemoveSelectedButton;


	public ConstraintsListViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider){
		super(sectionContext, updateContext, javaProjectProvider, STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Constraints");

		fMethodInterface = new MethodInterface(getModelUpdateContext(), javaProjectProvider);
		fConstraintIf = new ConstraintInterface(getModelUpdateContext(), javaProjectProvider);

		fNameColumn.setEditingSupport(new ConstraintNameEditingSupport());

		addButton("Add constraint", new AddConstraintAdapter());
		fRemoveSelectedButton = 
				addButton(
						"Remove selected", 
						new ActionSelectionAdapter(
								new DeleteAction(
										getViewer(), 
										getModelUpdateContext()), 
										Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		setActionProvider(new ModelViewerActionProvider(getTableViewer(), updateContext, javaProjectProvider));

		getViewer().addDragSupport(
				DND.DROP_COPY|DND.DROP_MOVE, 
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDragListener(getViewer()));

		addSelectionChangedListener(new SelectionChangedListener());
	}

	public void setInput(MethodNode method){
		super.setInput(method.getConstraintNodes());
		fMethodInterface.setOwnNode(method);
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());

		addColumn("Definition", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getConstraint().toString();
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

	public class ConstraintNameEditingSupport extends EditingSupport{

		private CellEditor fNameCellEditor;

		public ConstraintNameEditingSupport(){
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element){
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element){
			return true;
		}

		@Override
		protected Object getValue(Object element){
			return ((ConstraintNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value){
			String newName = (String)value;
			ConstraintNode constraint = (ConstraintNode)element;
			fConstraintIf.setOwnNode(constraint);
			fConstraintIf.setName(newName);
		}
	}

	private class AddConstraintAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			ConstraintNode constraint = fMethodInterface.addNewConstraint();
			if(constraint != null){
				selectElement(constraint);
				fNameColumn.getViewer().editElement(constraint, 0);
			}
		}
	}

	private class SelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			refresh();
		}
	}

}
