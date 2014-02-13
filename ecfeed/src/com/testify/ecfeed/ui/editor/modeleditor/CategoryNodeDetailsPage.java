/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.layout.RowLayout;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ModelUtils;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class CategoryNodeDetailsPage extends GenericNodeDetailsPage implements IModelUpdateListener{

	private CategoryNode fSelectedCategory;
	private Section fMainSection;
	private CheckboxTableViewer fPartitionsViewer;
	private Table fPartitionsTable;
	private ColorManager fColorManager;
	
	public class PartitionNameEditingSupport extends EditingSupport{
		private TextCellEditor fNameCellEditor;

		public PartitionNameEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fNameCellEditor = new TextCellEditor(fPartitionsTable);
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
			return ((PartitionNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			PartitionNode partition = (PartitionNode)element;
			if(!fSelectedCategory.validatePartitionName(newName) || 
					partition.hasSibling(newName)){
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE);
			}
			else{
				((PartitionNode)element).setName((String)value);
				updateModel((RootNode)((PartitionNode)element).getRoot());
			}
		}
	}

	public class PartitionValueEditingSupport extends EditingSupport{
		private TextCellEditor fValueCellEditor;
		
		public PartitionValueEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fValueCellEditor = new TextCellEditor(fPartitionsTable);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fValueCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((PartitionNode)element).getValueString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String valueString = (String)value;
			if(!fSelectedCategory.validatePartitionStringValue(valueString)){
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
			}
			else{
				Object newValue = fSelectedCategory.getPartitionValueFromString(valueString);
				((PartitionNode)element).setValue(newValue);
				updateModel((RootNode)fSelectedCategory.getRoot());
			}
		}
	}

	/**
	 * Create the details page.
	 */
	public CategoryNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
		fColorManager = new ColorManager();
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		fMainSection.setText("Empty Section");
		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);
		mainComposite.setLayout(new GridLayout(1, false));

		fToolkit.createLabel(mainComposite, "Partitions");
		
		createPartitionViewer(mainComposite);
		
		createBottomButtons(mainComposite);

	}

	private void createPartitionViewer(Composite composite) {
		fPartitionsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fPartitionsViewer.setContentProvider(new ArrayContentProvider());
		fPartitionsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fPartitionsTable = fPartitionsViewer.getTable();
		fPartitionsTable.setLinesVisible(true);
		fPartitionsTable.setHeaderVisible(true);
		fPartitionsTable.setLayoutData(VIEWERS_GRID_DATA);
		
		TableViewerColumn nameViewerColumn = createTableViewerColumn(fPartitionsViewer, "Partition name", 
				190, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
			
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}

		});
		nameViewerColumn.setEditingSupport(new PartitionNameEditingSupport(fPartitionsViewer));
		
		TableViewerColumn valueViewerColumn = createTableViewerColumn(fPartitionsViewer, "Value", 
				100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode partition = (PartitionNode)element;
				if(partition.isAbstract()){
					return "ABSTRACT";
				}
				Object partitionValue = partition.getValueString();
				if(partitionValue != null){
					return partitionValue.toString();
				}
				return com.testify.ecfeed.parsers.Constants.NULL_VALUE_STRING_REPRESENTATION;
			}
			
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		valueViewerColumn.setEditingSupport(new PartitionValueEditingSupport(fPartitionsViewer));
	}

	private Color getColor(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			if(partition.isAbstract()){
				return fColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
			}
		}
		return null;
	}

	private void createBottomButtons(Composite composite) {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		fToolkit.adapt(buttonsComposite);
		fToolkit.paintBordersFor(buttonsComposite);
		buttonsComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
	
		createButton(buttonsComposite, "Add Partition...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME;
				int i = 1;
				while(fSelectedCategory.getPartition(newPartitionName) != null){
					newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME + "_" + i;
					i++;
				}
				Object value = ModelUtils.getDefaultExpectedValue(fSelectedCategory.getType());
				PartitionNode newPartition = new PartitionNode(newPartitionName, value);
				fSelectedCategory.addPartition(newPartition);
				updateModel(fSelectedCategory);
				fPartitionsTable.setSelection(fSelectedCategory.getPartitions().size() - 1);
			}
		});
	
		createButton(buttonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (MessageDialog.openConfirm(getActiveShell(), 
						Messages.DIALOG_REMOVE_PARTITIONS_TITLE, 
						Messages.DIALOG_REMOVE_PARTITIONS_MESSAGE)) {
					for(Object partition : fPartitionsViewer.getCheckedElements()){
						if(fSelectedCategory.getPartitions().size() > 1){
							fSelectedCategory.removePartition((PartitionNode)partition);
						}
						else{
							MessageDialog.openInformation(getActiveShell(), 
									Messages.DIALOG_REMOVE_LAST_PARTITION_TITLE, 
									Messages.DIALOG_REMOVE_LAST_PARTITION_MESSAGE);
						}
						updateModel((RootNode)fSelectedCategory.getRoot());
					}
				}
			}
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedCategory = (CategoryNode)fSelectedNode;
		refresh();
	}
	
	public void refresh() {
		if(fSelectedCategory == null){
			return;
		}
		fMainSection.setText(fSelectedCategory.toString());
		fPartitionsViewer.setInput(fSelectedCategory.getPartitions());
	}
}
