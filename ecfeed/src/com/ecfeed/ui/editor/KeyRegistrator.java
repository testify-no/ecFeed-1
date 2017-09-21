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

		addKeyListener(
				ActionDescriptionProvider.INSERT.getId(), 
				ActionDescriptionProvider.INSERT.getKeyCode(),
				ActionDescriptionProvider.INSERT.getModifier());

		addKeyListener(
				ActionDescriptionProvider.DELETE.getId(), 
				ActionDescriptionProvider.DELETE.getKeyCode(),
				ActionDescriptionProvider.DELETE.getModifier());

		addKeyListener(
				ActionDescriptionProvider.MOVE_UP.getId(), 
				ActionDescriptionProvider.MOVE_UP.getKeyCode(), 
				ActionDescriptionProvider.MOVE_UP.getModifier());

		addKeyListener(
				ActionDescriptionProvider.MOVE_DOWN.getId(),
				ActionDescriptionProvider.MOVE_DOWN.getKeyCode(), 
				ActionDescriptionProvider.MOVE_DOWN.getModifier());

		if (!ApplicationContext.isProjectAvailable()) {
			addActionsForStandaloneApp();
		}
	}

	private void addActionsForStandaloneApp() {

		addKeyListener(
				ActionDescriptionProvider.COPY.getId(), 
				ActionDescriptionProvider.COPY.getKeyCode(), 
				ActionDescriptionProvider.COPY.getModifier());

		addKeyListener(
				ActionDescriptionProvider.CUT.getId(), 
				ActionDescriptionProvider.CUT.getKeyCode(), 
				ActionDescriptionProvider.CUT.getModifier());


		addKeyListener(
				ActionDescriptionProvider.PASTE.getId(), 
				ActionDescriptionProvider.PASTE.getKeyCode(), 
				ActionDescriptionProvider.PASTE.getModifier());

		addKeyListener(
				ActionDescriptionProvider.SAVE.getId(), 
				ActionDescriptionProvider.SAVE.getKeyCode(), 
				ActionDescriptionProvider.SAVE.getModifier());

		addKeyListener(
				ActionDescriptionProvider.UNDO.getId(), 
				ActionDescriptionProvider.UNDO.getKeyCode(), 
				ActionDescriptionProvider.UNDO.getModifier());

		addKeyListener(
				ActionDescriptionProvider.REDO.getId(), 
				ActionDescriptionProvider.REDO.getKeyCode(), 
				ActionDescriptionProvider.REDO.getModifier());
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

}
