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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;


class ActionKeyListener extends KeyAdapter {

	private int fKeyCode;
	private Action fAction;
	private int fModifier;

	public ActionKeyListener(int keyCode, int modifier, Action action) {

		fKeyCode = keyCode;
		fModifier = modifier;
		fAction = action;
	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (e.keyCode != fKeyCode) {
			return;
		}

		if (e.stateMask != fModifier) {
			return;
		}

		fAction.run();
	}
}

