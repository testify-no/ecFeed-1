/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;

import com.ecfeed.core.model.AbstractNode;

public interface IModelUpdateContext {
	public IUndoContext getUndoContext();
	public void notifyUpdateListeners(List<AbstractNode> nodesToSelectAfterTheOperation);
}
