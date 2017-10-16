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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.ecfeed.ui.modelif.DragAndDropNodeBuffer;
import com.ecfeed.ui.modelif.NodeSelectionUtils;

public class ModelNodeDragListener implements DragSourceListener {

	private NodeSelectionUtils fSelectionToolbox;
	private boolean fEnabled;

	public ModelNodeDragListener(ISelectionProvider selectionProvider){
		fSelectionToolbox = new NodeSelectionUtils(selectionProvider);
		fEnabled = true;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		if((fEnabled == false) || (fSelectionToolbox.isSelectionSingleType() == false)){
			event.doit = false;
		}
		else{
			DragAndDropNodeBuffer.setDraggedNodes(fSelectionToolbox.getSelectedNodes());
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		DragAndDropNodeBuffer.clear();
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}
}
