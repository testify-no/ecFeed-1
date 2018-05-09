/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import java.util.Set;

public interface IActionGrouppingProvider {

	public Set<String> getGroups();
	public Set<DescribedAction> getActions(String groupId);
	public DescribedAction getAction(String actionId);
	public DescribedAction getAction(ActionId actionId);
	public void setEnabled(boolean enabled);
}
