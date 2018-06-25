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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.ui.editor.actions.ActionId;
import com.ecfeed.ui.editor.actions.DescribedAction;
import com.ecfeed.ui.editor.actions.IActionGrouppingProvider;

public class KeyRegistrator {

	private Control fControl;
	private IActionGrouppingProvider fActionGroupingProvider;
	private Set<KeyListener> fKeyListeners;
	private boolean fIsExpandableTreeControl;

	public KeyRegistrator(Control control,
			boolean isExpandableTreeControl,
			IActionGrouppingProvider actionProvider) {
		
		fControl = control;
		fIsExpandableTreeControl = isExpandableTreeControl;
		fActionGroupingProvider = actionProvider;
		fKeyListeners = new HashSet<KeyListener>();
	}

	public void unregisterKeyListeners() {

		Iterator<KeyListener> it = fKeyListeners.iterator();

		while (it.hasNext()) {
			fControl.removeKeyListener(it.next());
			it.remove();
		}
	}

	public void registerKeyListeners() {

		addKeyListener(ActionId.INSERT);
		addKeyListener(ActionId.DELETE);

		addKeyListener(ActionId.MOVE_UP);
		addKeyListener(ActionId.MOVE_DOWN);

		if (!ApplicationContext.isProjectAvailable()) {
			addActionsForStandaloneApp();
		}
	}

	private void addActionsForStandaloneApp() {

		addKeyListener(ActionId.COPY);
		addKeyListener(ActionId.CUT);
		addKeyListener(ActionId.PASTE);

		addKeyListener(ActionId.SAVE);

		addKeyListener(ActionId.UNDO);
		addKeyListener(ActionId.REDO);

		if (fIsExpandableTreeControl) {
			addKeyListener(ActionId.EXPAND_COLLAPSE);
		}

	}

	private void addKeyListener(ActionId actionId) {

		if (fActionGroupingProvider == null) {
			return;
		}

		DescribedAction action = fActionGroupingProvider.getAction(actionId);

		if (action == null) {
			return;
		}

		if (!action.isKeyActionType()) {
			return;
		}

		int keyCode = action.getKeyCode();
		int modifier = action.getKeyModifier();

		KeyListener keyListener = new ActionKeyListener(keyCode, modifier, action);

		fControl.addKeyListener(keyListener);

		fKeyListeners.add(keyListener);
	}

}
