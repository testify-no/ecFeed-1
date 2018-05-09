/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common.utils;

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleManager {

	private static MessageConsole outputConsole;
	private static MessageConsoleStream outputStream;

	public static void prepareOutput() {
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
	}

	private static MessageConsole getOutputConsole() {
		if (outputConsole == null) {
			ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
			IConsoleManager consoleManager = consolePlugin.getConsoleManager();
			outputConsole = new MessageConsole("Test output", null);
			consoleManager.addConsoles(new IConsole[]{outputConsole});
		}
		return outputConsole;
	}

	private static void displayConsole() {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IConsoleView consoleView = (IConsoleView)activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			getOutputConsole().clearConsole();
			consoleView.display(getOutputConsole());
		} catch (PartInitException e) {
		}
	}

	private static MessageConsoleStream getOutputStream() {
		if (outputStream == null) {
			outputStream = getOutputConsole().newMessageStream();
		}
		return outputStream;
	}

	private static void redirectSystemOutputToStream(OutputStream outputStream) {
		PrintStream printStream = new PrintStream(outputStream);
		System.setOut(printStream);
	}

}
