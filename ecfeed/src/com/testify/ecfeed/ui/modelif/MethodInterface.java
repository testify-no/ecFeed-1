package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.JavaTestRunner;
import com.testify.ecfeed.abstraction.java.JavaUtils;
import com.testify.ecfeed.abstraction.operations.MethodOperationAddConstraint;
import com.testify.ecfeed.abstraction.operations.MethodOperationAddParameter;
import com.testify.ecfeed.abstraction.operations.MethodOperationAddTestCase;
import com.testify.ecfeed.abstraction.operations.MethodOperationAddTestSuite;
import com.testify.ecfeed.abstraction.operations.MethodOperationConvertTo;
import com.testify.ecfeed.abstraction.operations.MethodOperationRename;
import com.testify.ecfeed.abstraction.operations.MethodOperationRenameTestCases;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.AddTestCaseDialog;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;
import com.testify.ecfeed.ui.dialogs.RenameTestSuiteDialog;
import com.testify.ecfeed.ui.dialogs.SelectCompatibleMethodDialog;

public class MethodInterface extends GenericNodeInterface {

	private MethodNode fTarget;

	public MethodInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(MethodNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public MethodNode getTarget(){
		return fTarget;
	}

	public List<String> getArgTypes(MethodNode method) {
		return JavaUtils.getArgTypes(method);
	}

	public List<String> getArgNames(MethodNode method) {
		return JavaUtils.getArgNames(method);
	}

	public boolean setName(String newName) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new MethodOperationRename(fTarget, newName), Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean convertTo(MethodNode method) {
		return execute(new MethodOperationConvertTo(fTarget, method), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public CategoryNode addNewParameter() {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		String name = generateNewParameterName(fTarget);
		String type = generateNewParameterType(fTarget);
		String defaultValue = modelBuilder.getDefaultExpectedValue(type);
		CategoryNode parameter = new CategoryNode(name, type, defaultValue, false);
		List<PartitionNode> defaultPartitions = modelBuilder.defaultPartitions(type);
		parameter.addPartitions(defaultPartitions);
		if(addNewParameter(parameter, fTarget.getCategories().size())){
			return parameter;
		}
		return null;
	}
	
	public boolean addNewParameter(CategoryNode parameter, int index) {
		return execute(new MethodOperationAddParameter(fTarget, parameter, index), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public boolean removeParameters(Collection<CategoryNode> parameters, IModelUpdateContext context){
		Set<ConstraintNode> constraints = fTarget.mentioningConstraints(parameters);
		if(constraints.size() > 0 || fTarget.getTestCases().size() > 0){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_PARAMETERS_WARNING_TITLE, Messages.DIALOG_REMOVE_PARAMETERS_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return super.removeChildren(parameters, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	public ConstraintNode addNewConstraint(){
		Constraint constraint = new Constraint(new StaticStatement(true), new StaticStatement(true));
		ConstraintNode node = new ConstraintNode(Constants.DEFAULT_NEW_CONSTRAINT_NAME, constraint);
		if(addNewConstraint(node)){
			return node;
		}
		return null;
	}
	
	public boolean addNewConstraint(ConstraintNode constraint){
		IModelOperation operation = new MethodOperationAddConstraint(fTarget, constraint, fTarget.getConstraintNodes().size());
		return execute(operation, Messages.DIALOG_ADD_CONSTRAINT_PROBLEM_TITLE);
	}
	
	public boolean removeConstraints(Collection<ConstraintNode> constraints){
		return removeChildren(constraints, Messages.DIALOG_REMOVE_CONSTRAINTS_PROBLEM_TITLE);
	}
	
	public TestCaseNode addTestCase() {
		for(CategoryNode category : fTarget.getCategories()){
			if(!category.isExpected() && category.getPartitions().isEmpty()){
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE, Messages.DIALOG_TEST_CASE_WITH_EMPTY_CATEGORY_MESSAGE);
				return null;
			}
		}
		
		AddTestCaseDialog dialog = new AddTestCaseDialog(Display.getCurrent().getActiveShell(), fTarget);
		dialog.create();
		if (dialog.open() == IDialogConstants.OK_ID) {
			String testSuite = dialog.getTestSuite();
			List<PartitionNode> testData = dialog.getTestData();
			TestCaseNode testCase = new TestCaseNode(testSuite, testData);
			if(addTestCase(testCase)){
				return testCase;
			}
		}
		return null;
	}

	public boolean addTestCase(TestCaseNode testCase) {
		return execute(new MethodOperationAddTestCase(fTarget, testCase, fTarget.getTestCases().size()), Messages.DIALOG_ADD_TEST_CASE_PROBLEM_TITLE);
	}

	public boolean removeTestCases(Collection<TestCaseNode> testCases) {
		return removeChildren(testCases, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
	}
	
	public void renameSuite() {
		RenameTestSuiteDialog dialog = 
				new RenameTestSuiteDialog(Display.getDefault().getActiveShell(), fTarget.getTestSuites());
		dialog.create();
		if (dialog.open() == Window.OK) {
			String oldName = dialog.getRenamedTestSuite();
			String newName = dialog.getNewName();
			renameSuite(oldName, newName);
		}
	}

	public void renameSuite(String oldName, String newName) {
		try{
			execute(new MethodOperationRenameTestCases(fTarget.getTestCases(oldName), newName), 
					Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM);
		}
		catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_RENAME_TEST_SUITE_PROBLEM, 
					e.getMessage());
		}
	}

	public boolean generateTestSuite(){
		TestSuiteGenerationSupport testGenerationSupport = new TestSuiteGenerationSupport(fTarget);
		testGenerationSupport.proceed();
		if(testGenerationSupport.hasData() == false) return false;
		
		String testSuiteName = testGenerationSupport.getTestSuiteName();
		List<List<PartitionNode>> testData = testGenerationSupport.getGeneratedData();
		
		int dataLength = testData.size();
		if(dataLength < 0 && (testGenerationSupport.wasCancelled() == false)){
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
			Messages.DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE,
			Messages.DIALOG_EMPTY_TEST_SUITE_GENERATED_MESSAGE);
			return false;
		}
		if(testData.size() > Constants.TEST_SUITE_SIZE_WARNING_LIMIT){
			if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
					Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_TITLE,
					Messages.DIALOG_LARGE_TEST_SUITE_GENERATED_MESSAGE(dataLength)) == false){
				return false;
			}
		}
		IModelOperation operation = new MethodOperationAddTestSuite(fTarget, testSuiteName, testData);
		return execute(operation, Messages.DIALOG_ADD_TEST_SUITE_PROBLEM_TITLE);
	}
	
	public void executeOnlineTests() {
		OnlineTestRunningSupport runner = new OnlineTestRunningSupport();
		runner.setTarget(fTarget);
		runner.proceed();
	}

	//TODO progress monitor with cancel
	public void executeStaticTests(Collection<TestCaseNode> testCases) {
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
		JavaTestRunner runner = new JavaTestRunner(EclipseLoaderProvider.createLoader());
		try {
			runner.setTarget(fTarget);
			for(TestCaseNode testCase : testCases){
				runner.runTestCase(testCase.getTestData());
			}
		} catch (RunnerException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE, e.getMessage());
		}
	}
	
	public Collection<TestCaseNode> getTestCases(String testSuite){
		return fTarget.getTestCases(testSuite);
	}

	public Collection<String> getTestSuites() {
		return fTarget.getTestSuites();
	}

	public Collection<TestCaseNode> getTestCases() {
		return fTarget.getTestCases();
	}

	public void reassignTarget() {
		SelectCompatibleMethodDialog dialog = new SelectCompatibleMethodDialog(Display.getDefault().getActiveShell(), getCompatibleMethods());
		if(dialog.open() == IDialogConstants.OK_ID){
			MethodNode selectedMethod = dialog.getSelectedMethod();
			convertTo(selectedMethod);
		}
	}

	public List<MethodNode> getCompatibleMethods(){
		List<MethodNode> compatibleMethods = new ArrayList<MethodNode>();
		for(MethodNode m : ClassInterface.getOtherMethods(fTarget.getClassNode())){
			if(m.getCategoriesTypes().equals(fTarget.getCategoriesTypes())){
				compatibleMethods.add(m);
			}
		}
		return compatibleMethods;
	}
	
	public void openCoverageDialog(Object[] checkedElements, Object[] grayedElements) {
		Shell activeShell = Display.getDefault().getActiveShell();
		new CalculateCoverageDialog(activeShell, fTarget, checkedElements, grayedElements).open();
	}

	private String generateNewParameterName(MethodNode method) {
		int i = 0;
		String name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(method.getCategory(name) != null){
			name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}

	private String generateNewParameterType(MethodNode method) {
		for(String type : JavaUtils.supportedPrimitiveTypes()){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				return type;
			}
		}
		String type = Constants.DEFAULT_USER_TYPE_NAME;
		int i = 0;
		while(true){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				break;
			}
			else{
				type = Constants.DEFAULT_USER_TYPE_NAME + i++;
			}
		}
		return type;
	}
}
