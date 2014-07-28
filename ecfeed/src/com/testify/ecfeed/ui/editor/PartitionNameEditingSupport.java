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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.gal.GalException;
import com.testify.ecfeed.gal.ModelOperationManager;
import com.testify.ecfeed.gal.javax.partition.PartitionAbstractionLayer;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionNameEditingSupport extends EditingSupport{

	private TextCellEditor fNameCellEditor;
	private BasicSection fSection;
	private ModelOperationManager fOperationManager;

	public PartitionNameEditingSupport(CheckboxTableViewerSection viewer, ModelOperationManager operationManager) {
		super(viewer.getTableViewer());
		fSection = viewer;
		fNameCellEditor = new TextCellEditor(viewer.getTable());
		fOperationManager = operationManager;
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
		
		if(newName.equals(partition.getName())){
			return;
		}
		PartitionAbstractionLayer al = new PartitionAbstractionLayer(fOperationManager);
		al.setTarget(partition);

		try {
			al.setName(newName);
			fSection.modelUpdated();
		} catch (GalException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
					e.getMessage());
		}
	}
}
