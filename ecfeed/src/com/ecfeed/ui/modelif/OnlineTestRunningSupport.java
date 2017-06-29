/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.modelif;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.android.external.ApkInstallerExt;
import com.ecfeed.android.external.DeviceCheckerExt;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.JavaTestRunner;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.local.EclipseProjectHelper;
import com.ecfeed.ui.dialogs.SetupDialogExecuteOnline;
import com.ecfeed.ui.dialogs.SetupDialogOnline;

public class OnlineTestRunningSupport extends AbstractOnlineSupport {

	private IJavaProjectProvider fJavaProjectProvider;

	public OnlineTestRunningSupport(
			MethodNode methodNode,
			ITestMethodInvoker testMethodInvoker,
			IJavaProjectProvider javaProjectProvider) {
		super(methodNode, testMethodInvoker, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
	}

	@Override
	protected void prepareRunner(MethodNode target) throws RunnerException {
		JavaTestRunner runner = getRunner();
		runner.setOwnMethodNode(target);

		if (getTestRunMode() == TestRunMode.JUNIT) {
			runner.createTestClassAndMethod(target);
		}
	}

	@Override
	protected SetupDialogOnline createSetupDialog(Shell activeShell,
			MethodNode methodNode, IJavaProjectProvider javaProjectProvider) {

		return new SetupDialogExecuteOnline(
				activeShell, methodNode, javaProjectProvider, null);
	}

	@Override
	protected void prepareRun() throws InvocationTargetException {
		if (getTestRunMode() != TestRunMode.ANDROID) {
			return;
		}
		DeviceCheckerExt.checkIfOneDeviceAttached();
		EclipseProjectHelper projectHelper = new EclipseProjectHelper(fJavaProjectProvider);
		new ApkInstallerExt(projectHelper).installApplicationsIfModified();
	}

	@Override
	protected Result run() {
		PrintStream currentOut = System.out;
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());

		Result result = Result.CANCELED;

		if (getTargetMethod().getParametersCount() > 0) {
			result = displayParametersDialogAndRunTests();
		} else {
			runNonParametrizedTest();
			result = Result.OK;
		}

		System.setOut(currentOut);
		return result;
	}	

	private void runNonParametrizedTest() {

		try {
			IRunnableWithProgress runable = new NonParametrizedTestRunnable();

			ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			progressMonitorDialog.run(true, true, runable);

			displayTestStatusDialog();

		} catch (InvocationTargetException | InterruptedException | RuntimeException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}


	}

	@Override
	protected void processTestCase(List<ChoiceNode> testData) throws RunnerException {
		getRunner().runTestCase(testData);
	}

	@Override
	protected void displayRunSummary() {
		displayTestStatusDialog();
	}	

	private class NonParametrizedTestRunnable implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

			if (getTestRunMode() == TestRunMode.ANDROID) {
				runNonParametrizedAndroidTest(monitor);
				return;
			}

			runNonParametrizedStandardTest(monitor);
		}

		private void runNonParametrizedAndroidTest(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Installing applications and running test...", 4);

			DeviceCheckerExt.checkIfOneDeviceAttached();
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}

			EclipseProjectHelper projectHelper = 
					new EclipseProjectHelper(fJavaProjectProvider);
			new ApkInstallerExt(projectHelper).installApplicationsIfModified();
			monitor.worked(3);
			if (monitor.isCanceled()) {
				return;
			}

			try {
				executeSingleTest();
			} catch (RunnerException e) {
				SystemLogger.logCatch(e.getMessage());
				ExceptionHelper.reportRuntimeException(e.getMessage());
			}
			monitor.worked(4);
			monitor.done();
		}

		private void runNonParametrizedStandardTest(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {
			progressMonitor.beginTask("Running test...", 1);
			fTestInformer.setProgressMonitor(progressMonitor);
			fTestInformer.beginTestExecution(1);

			try {
				executeSingleTest();
			} catch (RunnerException e) {
				fTestInformer.incrementTotalTestcases();
				fTestInformer.incrementFailedTestcases(e.getMessage());
			}

			progressMonitor.worked(1);
			progressMonitor.done();
		}

		private void executeSingleTest() throws RunnerException {
			getRunner().runTestCase(new ArrayList<ChoiceNode>());
		}
	}

}