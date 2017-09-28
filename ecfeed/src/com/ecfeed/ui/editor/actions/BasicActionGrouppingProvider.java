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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class BasicActionGrouppingProvider implements IActionGrouppingProvider {

	private boolean fEnabled;
	private Set<GrouppedAction> fActions;

	public BasicActionGrouppingProvider() {
		fActions = new LinkedHashSet<>();
		fEnabled = true;
	}

	@Override
	public Set<String> getGroups() {
		Set<String> result = new LinkedHashSet<>();

		if (fEnabled) {
			for(GrouppedAction record : fActions) {
				result.add(record.fGroupId);
			}
		}

		return result;
	}

	@Override
	public Set<DescribedAction> getActions(String groupId) {

		Set<DescribedAction> result = new LinkedHashSet<>();

		for (GrouppedAction record : fActions) {
			if (record.fGroupId.equals(groupId)) {
				result.add(record.fAction);
			}
		}

		return result;
	}

	public DescribedAction getAction(String actionId) {

		for (GrouppedAction record : fActions) {
			if (record.fAction.getId().equals(actionId)) {
				return record.fAction;
			}
		}

		return null;
	}

	public DescribedAction getAction(ActionId actionId) {

		for (GrouppedAction record : fActions) {
			if (record.fAction.getActionId().equals(actionId)) {
				return record.fAction;
			}
		}

		return null;
	}


	public void setEnabled(boolean enabled) {
		fEnabled = enabled;
	}

	protected void addAction(String group, DescribedAction action) {

		Iterator<GrouppedAction> iterator = fActions.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().fAction.getId().equals(action.getId())) {
				iterator.remove();
			}
		}

		fActions.add(new GrouppedAction(group, action));
	}

	private class GrouppedAction {
		public GrouppedAction(String group, DescribedAction action) {
			fGroupId = group;
			fAction = action;
		}

		private String fGroupId;
		private DescribedAction fAction;
	}

}
