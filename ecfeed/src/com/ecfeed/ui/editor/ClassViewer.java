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
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.ColorConstants;
import com.ecfeed.ui.common.ColorManager;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.MainActionGrouppingProvider;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.RootInterface;

public class ClassViewer extends TableViewerSection {

	private TableViewerColumn fNameColumn;
	private RootInterface fRootIf;
	private ClassInterface fClassIf;

	private TableViewerColumn fPackageNameColumn;
	private IJavaProjectProvider fJavaProjectProvider;


	public ClassViewer(
			ISectionContext parent, 
			IMainTreeProvider mainTreeProvider,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(parent, updateContext, javaProjectProvider, StyleDistributor.getSectionStyle());

		fJavaProjectProvider = javaProjectProvider; 
		fNameColumn.setEditingSupport(new LocalNameEditingSupport());
		fPackageNameColumn.setEditingSupport(new PackageNameEditingSupport());

		fRootIf = new RootInterface(getModelUpdateContext(), javaProjectProvider);
		fClassIf = new ClassInterface(getModelUpdateContext(), javaProjectProvider);

		setText("Classes");

		if (ApplicationContext.isProjectAvailable()) {
			addButton("Add implemented class", new AddImplementedClassAdapter());
		}

		addButton("Add class", new AddNewClassAdapter());
		addButton("Remove selected", 
				new ActionSelectionAdapter(
						new DeleteAction(getViewer(), getModelUpdateContext()), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		addDoubleClickListener(new SelectNodeDoubleClickListener(mainTreeProvider));

		setActionGrouppingProvider(new MainActionGrouppingProvider(getTableViewer(), getModelUpdateContext(), javaProjectProvider));

		getViewer().addDragSupport(
				DND.DROP_COPY|DND.DROP_MOVE, 
				new Transfer[]{ModelNodesTransfer.getInstance()}, 
				new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected void createTableColumns(){
		fNameColumn = addColumn("Class", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ClassInterface.getLocalName((ClassNode)element);
			}
		});

		fPackageNameColumn = addColumn("Package", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ClassNodeHelper.getPackageName((ClassNode)element);
			}
		});
		refresh();
	}
	
	@Override
	public void refresh(){
		super.refresh();
		int width = fNameColumn.getColumn().getWidth();
		if(ApplicationContext.getSimplifiedUI()){
			fPackageNameColumn.getColumn().setWidth(0);
		} else {
			fPackageNameColumn.getColumn().setWidth(width);
		}
	}

	public void setInput(RootNode model){
		super.setInput(model.getClasses());
		fRootIf.setOwnNode(model);
	}

	private ClassInterface classIf(){
		if(fClassIf == null){
			fClassIf = new ClassInterface(getModelUpdateContext(), fJavaProjectProvider);
		}
		return fClassIf;
	}

	private void startEditingAddedClass(ClassNode addedClass) {

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

		columnViewer.editElement(addedClass, 0);
	}

	private abstract class ClassNameEditingSupport extends EditingSupport{

		private TextCellEditor fNameCellEditor;

		public ClassNameEditingSupport() {
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

		protected void renameClass(ClassNode target, String qualifiedName){
			classIf().setOwnNode(target);
			classIf().setQualifiedName(qualifiedName);
		}
	}

	private class LocalNameEditingSupport extends ClassNameEditingSupport {

		@Override
		protected Object getValue(Object element) {
			return ClassInterface.getLocalName((ClassNode)element);
		}

		@Override
		protected void setValue(Object element, Object value) {
			ClassNode target = (ClassNode)element;
			String packageName = ClassInterface.getPackageName(target);
			String localName = (String)value;
			renameClass(target, ClassInterface.getQualifiedName(packageName, localName));
		}

	}


	private class PackageNameEditingSupport extends ClassNameEditingSupport{

		@Override
		protected Object getValue(Object element) {
			return ClassInterface.getPackageName((ClassNode)element);
		}

		@Override
		protected void setValue(Object element, Object value) {

			ClassNode target = (ClassNode)element;
			String localName = ClassInterface.getLocalName(target);
			String packageName = (String)value;
			renameClass(target, ClassInterface.getQualifiedName(packageName, localName));
		}

	}

	private class AddImplementedClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				ClassNode addedClass = fRootIf.addImplementedClass();
				if(addedClass != null){
					selectElement(addedClass);
					startEditingAddedClass(addedClass);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add implemented class.", e.getMessage());
			}
		}
	}


	private class AddNewClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				ClassNode addedClass = fRootIf.addNewClass();
				if(addedClass != null){
					selectElement(addedClass);
					startEditingAddedClass(addedClass);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not create new test class.", e.getMessage());
			}
		}

	}

	private class ClassViewerColumnLabelProvider extends ColumnLabelProvider {
		@Override
		public Color getForeground(Object element) {
			if (!(element instanceof ClassNode)) {
				return null;
			}
			if (!ApplicationContext.isProjectAvailable()) {
				return null;
			}
			if(fRootIf.getImplementationStatus((ClassNode)element) == EImplementationStatus.IMPLEMENTED){
				return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
			return null;
		}
	}

//<<<<<<< HEAD
//	public ClassViewer(
//			ISectionContext parent, 
//			IModelUpdateContext updateContext, 
//			IFileInfoProvider fileInfoProvider) {
//		super(parent, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());
//
//		fFileInfoProvider = fileInfoProvider; 
//		fNameColumn.setEditingSupport(new LocalNameEditingSupport());
//		fPackageNameColumn.setEditingSupport(new PackageNameEditingSupport());
//
//		fRootIf = new RootInterface(this, fileInfoProvider);
//		fClassIf = new ClassInterface(this, fileInfoProvider);
//
//		setText("Classes");
//
//		if (fFileInfoProvider.isProjectAvailable()) {
//			addButton("Add implemented class", new AddImplementedClassAdapter());
//		}
//
//		addButton("New test class", new AddNewClassAdapter());
//		addButton("Remove selected", 
//				new ActionSelectionAdapter(
//						new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));
//
//		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
//		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this, fileInfoProvider));
//		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
//	}
//
//	@Override
//	protected void createTableColumns(){
//		fNameColumn = addColumn("Class", 150, new ClassViewerColumnLabelProvider(){
//			@Override
//			public String getText(Object element){
//				return ClassInterface.getLocalName((ClassNode)element);
//			}
//		});
//
//		fPackageNameColumn = addColumn("Package", 150, new ClassViewerColumnLabelProvider(){
//			@Override
//			public String getText(Object element){
//				return ClassNodeHelper.getPackageName((ClassNode)element);
//			}
//		});
//		refresh();
//
//	}
//
//	@Override
//	public void refresh(){
//		super.refresh();
//		int width = fNameColumn.getColumn().getWidth();
//		if(ApplicationContext.getSimplifiedUI()){
//			fPackageNameColumn.getColumn().setWidth(0);
//		} else {
//			fPackageNameColumn.getColumn().setWidth(width);
//		}
//	}
//
//	public void setInput(RootNode model){
//		super.setInput(model.getClasses());
//		fRootIf.setOwnNode(model);
//		refresh();
//	}
//
//	private ClassInterface classIf(){
//		if(fClassIf == null){
//			fClassIf = new ClassInterface(this, fFileInfoProvider);
//		}
//		return fClassIf;
//	}
//=======
//>>>>>>> master
}
