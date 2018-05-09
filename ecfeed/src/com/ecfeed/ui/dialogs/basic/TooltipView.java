/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.utils.INotifier;

public class TooltipView {

	private Shell fShell;

	public TooltipView(
			Shell parentShell,
			String tooltipTitle,
			String tooltipText,
			Control sourceControl,
			INotifier mouseEnterNotifier,
			INotifier mouseExitNotifier) {

		fShell = createShell(parentShell, sourceControl, tooltipTitle);
		createTextWidget(fShell, tooltipText, mouseEnterNotifier, mouseExitNotifier);
	}

	public void open() {
		fShell.open();
	}

	public void close() {

		if (fShell.isDisposed()) {
			return;
		}

		fShell.close();
	}

	private static Shell createShell(
			Shell parentShell, 
			Control sourceControl,
			String tooltipTitle) {

		Shell shell = new Shell(parentShell, SWT.RESIZE);

		shell.setText(tooltipTitle);
		shell.setLayout(new GridLayout(1, true));

		positionShell(shell, sourceControl);

		return shell;
	}

	private static void positionShell(Shell shell, Control sourceControl) {

		Rectangle sourceBounds = sourceControl.getBounds(); 
		Point sourceLocation = sourceControl.toDisplay(sourceBounds.x,	sourceBounds.y);

		final int xShift = -10;
		final int yShift = 10;

		shell.setLocation(
				sourceLocation.x + xShift,
				sourceLocation.y + sourceBounds.height + yShift);

		shell.setSize(450, 300);
	}

	private static void createTextWidget(
			Shell shell, 
			String tooltipText,
			final INotifier mouseEnterNotifier,
			final INotifier mouseExitNotifier) {

		Text textWidget = DialogObjectToolkit.createTooltipText(shell, tooltipText);

		textWidget.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				mouseEnterNotifier.doNotify();
			}
		});

		textWidget.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				mouseExitNotifier.doNotify();
			}
		});
	}

}
