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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;
import com.ecfeed.ui.dialogs.SetupDialogGenerateTestSuite;

public class TestSuiteGenerationSupport {

	private boolean fCanceled;
	private Collection<IConstraint<ChoiceNode>> fSelectedConstraints;
	private MethodNode fTarget;
	private IJavaProjectProvider fJavaProjectProvider;
	private String fTestSuiteName;
	private List<List<ChoiceNode>> fGeneratedData;
	private boolean fHasData;

	private class ExpectedValueReplacer implements IConstraint<ChoiceNode>{
		
		@Override
		public EvaluationResult evaluate(List<ChoiceNode> values) {
			return EvaluationResult.TRUE;
		}

		@Override
		public boolean adapt(List<ChoiceNode> values) {
			for(ChoiceNode p : values){
				MethodParameterNode parameter = fTarget.getMethodParameters().get(values.indexOf(p));
				if(parameter.isExpected()){
					values.set(p.getParameter().getIndex(), p.makeClone());
				}
			}
			return true;
		}

		@Override
		public boolean mentions(int dimension) {
			return false;
		}
	}

	private class GeneratorRunnable implements IRunnableWithProgress{

		private IGenerator<ChoiceNode> fGenerator;
		private List<List<ChoiceNode>> fGeneratedData;
		private List<List<ChoiceNode>> fInput;
		private Collection<IConstraint<ChoiceNode>> fConstraints;
		private Map<String, Object> fParameters;

		GeneratorRunnable(IGenerator<ChoiceNode> generator,
				List<List<ChoiceNode>> input,
				Collection<IConstraint<ChoiceNode>> constraints,
				Map<String, Object> parameters,
				List<List<ChoiceNode>> generated){
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
			fGeneratedData = generated;
		}

		//HERE, ambigous
		//todo, mock(?) generator
		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {

			List<ChoiceNode> next;
			try {
				fGenerator.initialize(fInput, fConstraints, fParameters, new GeneratorProgressMonitor(progressMonitor));
				progressMonitor.beginTask("Generating test data", fGenerator.totalWork());
				while((progressMonitor.isCanceled() == false) && (next = fGenerator.next()) != null){
					fGeneratedData.add(next);
					progressMonitor.worked(fGenerator.workProgress());
				}
				progressMonitor.done();
			} catch (GeneratorException e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}

	}

	public TestSuiteGenerationSupport(MethodNode target, IJavaProjectProvider javaProjectProvider) {
		fTarget = target;
		fJavaProjectProvider = javaProjectProvider;
		fHasData = false;
	}

	public void proceed(){
		fHasData = generate() && !fCanceled;
	}
	
	//TODO
	//copy/base on generate method maybe
	protected boolean generate(){

		SetupDialogGenerateTestSuite dialog = 
				SetupDialogGenerateTestSuite.create(
						getActiveShell(), fTarget, null, fJavaProjectProvider);

		if (dialog == null) {
			return false;
		}

		if (dialog.open() != IDialogConstants.OK_ID) {
			return false;
		}

		IGenerator<ChoiceNode> selectedGenerator = dialog.getSelectedGenerator();
		List<List<ChoiceNode>> algorithmInput = dialog.getAlgorithmInput();
		fSelectedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		fSelectedConstraints.addAll(dialog.getConstraints());
		fSelectedConstraints.add(new ExpectedValueReplacer());
		List<IConstraint<ChoiceNode>> constraints = new ArrayList<IConstraint<ChoiceNode>>();
		constraints.addAll(fSelectedConstraints);
		fTestSuiteName = dialog.getTestSuiteName();
		Map<String, Object> parameters = dialog.getGeneratorParameters();
		fGeneratedData = generateTestData(selectedGenerator, algorithmInput, constraints, parameters);

		return true;
	}

	public List<List<ChoiceNode>> getGeneratedData(){
		return fGeneratedData;
	}

	public String getTestSuiteName(){
		return fTestSuiteName;
	}

	private List<List<ChoiceNode>> generateTestData(final IGenerator<ChoiceNode> generator,
			final List<List<ChoiceNode>> input,
			final Collection<IConstraint<ChoiceNode>> constraints,
			final Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = new GeneratorProgressMonitorDialog(getActiveShell(), generator);
		List<List<ChoiceNode>> generated = new ArrayList<List<ChoiceNode>>();
		fCanceled = false;
		try {
			GeneratorRunnable runnable = new GeneratorRunnable(generator, input, constraints, parameters, generated);
			progressDialog.open();
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getActiveShell(), "Exception", e.getMessage());
			fCanceled = true;
		}catch (InterruptedException e) {
			fCanceled = true;
			MessageDialog.openError(getActiveShell(), "Exception", e.getMessage());
			e.printStackTrace();
		}
		fCanceled |= progressDialog.getProgressMonitor().isCanceled();
		if(!fCanceled){
			return generated;
		}
		else{
			//return empty set if the operation was canceled
			//TODO add a decision dialog where user may choose whether to add generated data
			return new ArrayList<List<ChoiceNode>>();
		}
	}

	private Shell getActiveShell() {
		return Display.getCurrent().getActiveShell();
	}

	public boolean wasCancelled() {
		return fCanceled;
	}

	public boolean hasData(){
		return fHasData;
	}
}
