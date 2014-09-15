/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerGlobalActionFactory;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodsViewer extends TableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private TableViewerColumn fMethodsColumn;
	private ClassInterface fClassIf;
	private MethodInterface fMethodIf;
	
	public class MethodNameEditingSupport extends EditingSupport{

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
			fMethodIf.setTarget((MethodNode)element);
			return fMethodIf.getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			MethodNode method = (MethodNode)element;
			fMethodIf.setTarget(method);
			fMethodIf.setName(newName, MethodsViewer.this);
		}
	}

	private class AddNewMethodAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			MethodNode newMethod = fClassIf.addNewMethod(MethodsViewer.this);
			if(newMethod != null){
				selectElement(newMethod);
				fMethodsColumn.getViewer().editElement(newMethod, 0);
			}
		}
	}

	private class MethodsArgsLabelProvider extends NodeViewerColumnLabelProvider{
		public MethodsArgsLabelProvider() {
			super();
		}
		
		@Override
		public String getText(Object element){
			List<String> argTypes = fMethodIf.getArgTypes((MethodNode)element);
			List<String> argNames = fMethodIf.getArgNames((MethodNode)element);
			String result = "";
			for(int i = 0; i < argTypes.size(); i++){
				result += JavaUtils.getLocalName(argTypes.get(i)) + " " + argNames.get(i);
				if(i < argTypes.size() - 1){
					result += ", ";
				}
			}
			return result;
		}
	}
	
	public MethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		
		fClassIf = new ClassInterface();
		fMethodIf = new MethodInterface();

		fMethodsColumn.setEditingSupport(new MethodNameEditingSupport());

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new DeleteAction(getViewer(), this));
		setActionProvider(new ModelViewerGlobalActionFactory(getTableViewer(), this));
	}

	public void setInput(ClassNode classNode){
		fClassIf.setTarget(classNode);
		super.setInput(classNode.getMethods());
	}

	@Override
	protected void createTableColumns() {
		fMethodsColumn = addColumn("Methods", 150, new NodeNameColumnLabelProvider());
		addColumn("Arguments", 450, new MethodsArgsLabelProvider());
	}
	
	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return true;
	}
}
