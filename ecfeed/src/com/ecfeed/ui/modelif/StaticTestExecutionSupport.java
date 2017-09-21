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
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.android.external.ApkInstallerExt;
import com.ecfeed.android.external.DeviceCheckerExt;
import com.ecfeed.core.adapter.java.ILoaderProvider;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.JavaTestRunner;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.ui.common.EclipseLoaderProvider;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class StaticTestExecutionSupport {

	private Collection<TestCaseNode> fTestCases;
	private JavaTestRunner fRunner;
	private List<TestCaseNode> fFailedTests;
	private IFileInfoProvider fFileInfoProvider;
	private TestRunMode fTestRunMode;
	private AbstractTestInformer fTestInformer;
	private TestResultsHolder ftestResultsHolder;
	private boolean resultOk;

	private class ExecuteRunnable implements IRunnableWithProgress{

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {
			if (fTestRunMode == TestRunMode.ANDROID) {
				DeviceCheckerExt.checkIfOneDeviceAttached();
				EclipseProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider); 
				new ApkInstallerExt(projectHelper).installApplicationsIfModified();
			}			

			fTestInformer.setProgressMonitor(progressMonitor);
			fFailedTests.clear();
			fTestInformer.beginTestExecution(fTestCases.size());

			for(TestCaseNode testCase : fTestCases){
				if(progressMonitor.isCanceled() == false){
					try {
						fTestInformer.setTestProgressMessage();

						MethodNode methodNode = testCase.getMethod();
						fRunner.setOwnMethodNode(methodNode);

						if (fTestRunMode == TestRunMode.JUNIT) {
							fRunner.createTestClassAndMethod(methodNode);
						}
						
						resultOk = true;
						fRunner.runTestCase(testCase.getTestData());
					} catch (RunnerException e) {
						resultOk = false;
						fTestInformer.incrementFailedTestcases(e.getMessage());
					}
					ftestResultsHolder.addTestResult(testCase.getTestData(), resultOk);
					progressMonitor.worked(1);
					fTestInformer.incrementTotalTestcases();
				}
			}

			progressMonitor.done();
		}
	}

	public StaticTestExecutionSupport(
			Collection<TestCaseNode> testCases, 
			ITestMethodInvoker testMethodInvoker, 
			IFileInfoProvider fileInfoProvider,
			TestRunMode testRunMode){
		super();
		ftestResultsHolder = new TestResultsHolder();
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, false, testMethodInvoker);
		fTestCases = testCases;
		TestCaseNode testCase = fTestCases.iterator().next();
		fFailedTests = new ArrayList<>();
		fFileInfoProvider = fileInfoProvider;
		fTestRunMode = testRunMode;
		fTestInformer = new ExecutionTestInformer(testCase.getMethod(), ftestResultsHolder);
	}

	public void proceed(){
		PrintStream currentOut = System.out;
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());

		try{
			fFailedTests.clear();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			dialog.open();
			dialog.run(true, true, new ExecuteRunnable());
		}catch(InvocationTargetException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getTargetException().getMessage());
		} catch (InterruptedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
		if(fFailedTests.size() > 0){
			String message = "Following tests were not successfull\n\n";
			for(TestCaseNode testCase : fFailedTests){
				message += testCase.toString() + "\n";
			}
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, message);
		}
		fTestInformer.displayTestStatusDialog();

		System.setOut(currentOut);
	}

}