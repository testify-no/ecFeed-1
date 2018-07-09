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

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.SessionAttributes;

public class ConsoleManager {

	private MessageConsole outputConsole;
	private MessageConsoleStream outputStream;

	public static void prepareOutput() {
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
	}

	private static ConsoleManager getSessionInstance() {

		ConsoleManager consoleManager = (ConsoleManager)SessionDataStore.get(
				SessionAttributes.SA_CONSOLE_MANAGER);

		if (consoleManager == null) {
			consoleManager = new ConsoleManager();
			SessionDataStore.set(SessionAttributes.SA_CONSOLE_MANAGER, consoleManager);
		}

		return consoleManager;
	}

	private static MessageConsole getOutputConsole() {

		ConsoleManager sessionInstance = getSessionInstance();

		if (sessionInstance.outputConsole == null) {

			ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
			IConsoleManager consoleManager = consolePlugin.getConsoleManager();
			sessionInstance.outputConsole = new MessageConsole("Test output", null);
			consoleManager.addConsoles(new IConsole[]{sessionInstance.outputConsole});
		}

		return sessionInstance.outputConsole;
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

		ConsoleManager sessionInstance = getSessionInstance();

		if (sessionInstance.outputStream == null) {
			sessionInstance.outputStream = getOutputConsole().newMessageStream();
		}
		return sessionInstance.outputStream;
	}

	private static void redirectSystemOutputToStream(OutputStream outputStream) {
		PrintStream printStream = new PrintStream(outputStream);
		System.setOut(printStream);
	}

}
