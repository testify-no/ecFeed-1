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
import com.ecfeed.ui.editor.actions.ActionDescriptionProvider;
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

		int ctrlModifier = getCtrlModifier();

		addKeyListener(ActionDescriptionProvider.DELETE.getId(), 'd', ctrlModifier);

		addKeyListener(ActionDescriptionProvider.MOVE_UP.getId(), SWT.ARROW_UP, SWT.ALT);
		addKeyListener(ActionDescriptionProvider.MOVE_DOWN.getId(), SWT.ARROW_DOWN, SWT.ALT);

		if (!ApplicationContext.isProjectAvailable()) {
			addActionsForStandaloneApp(ctrlModifier);
		}
	}

	private void addActionsForStandaloneApp(int ctrlModifier) {

		addKeyListener(ActionDescriptionProvider.COPY.getId(), 'c', ctrlModifier);
		addKeyListener(ActionDescriptionProvider.CUT.getId(), 'x', ctrlModifier);
		addKeyListener(ActionDescriptionProvider.PASTE.getId(), 'v', ctrlModifier);

		addKeyListener(ActionDescriptionProvider.SAVE.getId(), 's', ctrlModifier);
		addKeyListener(ActionDescriptionProvider.UNDO.getId(), 'z', ctrlModifier);
		addKeyListener(ActionDescriptionProvider.REDO.getId(), 'z', ctrlModifier | SWT.SHIFT);
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
