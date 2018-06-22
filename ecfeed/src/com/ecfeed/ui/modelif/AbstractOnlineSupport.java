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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.adapter.java.ILoaderProvider;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.JavaTestRunner;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.ui.common.EclipseLoaderProvider;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;
import com.ecfeed.ui.dialogs.SetupDialogOnline;
import com.ecfeed.ui.dialogs.TestResultsHolder;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;

public abstract class AbstractOnlineSupport {


	public enum Result {
		OK, CANCELED
	}

	private MethodNode fMethodNode;
	private TestResultsHolder ftestResultsHolder;
	private JavaTestRunner fRunner;
	private IJavaProjectProvider fJavaProjectProvider;
	private String fTargetFile;
	private String fExportTemplate;
	private TestRunMode fTestRunMode;
	protected AbstractTestInformer fTestInformer;

	public AbstractOnlineSupport(
			MethodNode methodNode, ITestMethodInvoker testMethodInvoker, 

			IJavaProjectProvider javaProjectProvider) {
		
		this(methodNode, testMethodInvoker, javaProjectProvider, false);

	}

	public AbstractOnlineSupport(
			MethodNode methodNode, 
			ITestMethodInvoker testMethodInvoker,
			IJavaProjectProvider javaProjectProvider,
			boolean isExport) {
		
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, isExport, testMethodInvoker);
		fJavaProjectProvider = javaProjectProvider;
		fTestRunMode = TestRunModeHelper.getTestRunMode(methodNode);
		fMethodNode = methodNode;
		ftestResultsHolder = new TestResultsHolder();
		fTestInformer = createTestInformer(isExport);

		setOwnMethodNode(methodNode);
	}

	private AbstractTestInformer createTestInformer(boolean isExport) {

		if (isExport) {
			return new ExportTestInformer();
		}

		return new ExecutionTestInformer(fMethodNode, ftestResultsHolder);
	}

	protected TestRunMode getTestRunMode() {
		return fTestRunMode;
	}

	protected abstract void prepareRunner(MethodNode target) throws RunnerException;

	protected abstract SetupDialogOnline createSetupDialog(
			Shell activeShell, MethodNode methodNode,
			IJavaProjectProvider javaProjectProvider);

	protected abstract void prepareRun() throws InvocationTargetException;

	protected abstract Result run();

	protected abstract void processTestCase(List<ChoiceNode> testData) throws RunnerException;	

	protected abstract void displayRunSummary();

	public Result proceed() {
		return run();
	}

	private void setOwnMethodNode(MethodNode methodNode) {
		try {
			prepareRunner(methodNode);
			fMethodNode = methodNode;
		} catch (RunnerException e) {
			ErrorDialog.open(Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	protected MethodNode getTargetMethod() {
		return fMethodNode;
	}

	protected JavaTestRunner getRunner() {
		return fRunner;
	}

	public String getExportTemplate() {
		return fExportTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	protected Result displayParametersDialogAndRunTests() {

		SetupDialogOnline dialog = 
				createSetupDialog(
						Display.getCurrent().getActiveShell(), fMethodNode, fJavaProjectProvider);

		if (dialog == null) {
			return Result.CANCELED;
		}


		if (dialog.open() != IDialogConstants.OK_ID) {
			return Result.CANCELED;
		}

		IGenerator<ChoiceNode> selectedGenerator = dialog.getSelectedGenerator();
		List<List<ChoiceNode>> algorithmInput = dialog.getAlgorithmInput();
		Collection<IConstraint<ChoiceNode>> constraintList = new ArrayList<IConstraint<ChoiceNode>>();
		constraintList.addAll(dialog.getConstraints());
		Map<String, Object> parameters = dialog.getGeneratorParameters();

		Result result = runParametrizedTests(selectedGenerator, algorithmInput, constraintList, parameters);
		displayRunSummary();

		fTargetFile = dialog.getTargetFile();
		fExportTemplate = dialog.getExportTemplateText();

		return result;
	}

	private Result runParametrizedTests(IGenerator<ChoiceNode> generator,
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints,
			Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = new GeneratorProgressMonitorDialog(
				Display.getCurrent().getActiveShell(), generator);
		
		ParametrizedTestRunnable runnable = new ParametrizedTestRunnable(
				generator, input, constraints, parameters);
		progressDialog.open();
		try {
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			ErrorDialog.open(
					Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
		
		if (progressDialog.wasCancel()) {
			return Result.CANCELED;
		}
		
		return Result.OK;
	}

	protected void displayTestStatusDialog() {
		fTestInformer.displayTestStatusDialog();
	}

	protected boolean anyTestFailed() {
		return fTestInformer.anyTestFailed();
	}

	private class ParametrizedTestRunnable implements IRunnableWithProgress {

		private IGenerator<ChoiceNode> fGenerator;
		private List<List<ChoiceNode>> fInput;
		private Collection<IConstraint<ChoiceNode>> fConstraints;
		private Map<String, Object> fParameters;
		private boolean resultOk;

		ParametrizedTestRunnable(IGenerator<ChoiceNode> generator,
				List<List<ChoiceNode>> input,
				Collection<IConstraint<ChoiceNode>> constraints,
				Map<String, Object> parameters) {
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
		}

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {

			try {
				prepareRun();
				fTestInformer.setProgressMonitor(progressMonitor);

				List<ChoiceNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters, new GeneratorProgressMonitor(progressMonitor));
				fTestInformer.beginTestExecution(fGenerator.totalWork());

				while ((next = fGenerator.next()) != null
						&& progressMonitor.isCanceled() == false) {
					try {
						fTestInformer.setTestProgressMessage();
						resultOk = true;
						processTestCase(next);
					} catch (RunnerException e) {
						resultOk = false;
						fTestInformer.incrementFailedTestcases(e.getMessage());
					}
					ftestResultsHolder.addTestResult(next, resultOk);
					progressMonitor.worked(fGenerator.workProgress());
					fTestInformer.incrementTotalTestcases();
				}
				progressMonitor.done();
			} catch (Throwable e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}

	}
}