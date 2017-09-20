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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.ui.editor.actions.GlobalActions;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.editor.actions.NamedAction;

public class KeyRegistrator {

	private Control fControl;
	private IActionProvider fActionProvider;
	private Set<KeyListener> fKeyListeners;


	public KeyRegistrator(Control control, IActionProvider actionProvider) {
		fControl = control;
		fActionProvider = actionProvider;
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

		addKeyListener(GlobalActions.DELETE.getId(), SWT.DEL, SWT.NONE);

		addKeyListener(GlobalActions.MOVE_UP.getId(), SWT.ARROW_UP, SWT.ALT);
		addKeyListener(GlobalActions.MOVE_DOWN.getId(), SWT.ARROW_DOWN, SWT.ALT);

		if (!ApplicationContext.isProjectAvailable()) {
			addActionsForStandaloneApp();
		}
	}

	private void addActionsForStandaloneApp() {

		int ctrlModifier = getCtrlModifier();

		addKeyListener(GlobalActions.COPY.getId(), 'c', ctrlModifier);
		addKeyListener(GlobalActions.CUT.getId(), 'x', ctrlModifier);
		addKeyListener(GlobalActions.PASTE.getId(), 'v', ctrlModifier);

		addKeyListener(GlobalActions.SAVE.getId(), 's', ctrlModifier);
		addKeyListener(GlobalActions.UNDO.getId(), 'z', ctrlModifier);
		addKeyListener(GlobalActions.REDO.getId(), 'z', ctrlModifier | SWT.SHIFT);
	}

	private void addKeyListener(String actionId, int keyCode, int modifier) {

		if (fActionProvider == null) {
			return;
		}

		NamedAction action = fActionProvider.getAction(actionId);
		if (action == null) {
			return;
		}

		KeyListener keyListener = new ActionKeyListener(keyCode, modifier, action);
		fControl.addKeyListener(keyListener);

		fKeyListeners.add(keyListener);
	}


	private int getCtrlModifier() {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return SWT.COMMAND;
		} else {
			return SWT.CTRL;
		}
	}

}
