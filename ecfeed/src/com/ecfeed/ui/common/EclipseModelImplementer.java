/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import com.ecfeed.android.external.AndroidMethodImplementerExt;
import com.ecfeed.android.external.AndroidUserClassImplementerExt;
import com.ecfeed.android.external.IClassImplementHelper;
import com.ecfeed.android.external.IImplementerExt;
import com.ecfeed.android.external.IInstallationDirFileHelper;
import com.ecfeed.android.external.IMethodImplementHelper;
import com.ecfeed.android.external.IProjectHelper;
import com.ecfeed.android.external.ImplementerExt;
import com.ecfeed.core.adapter.AbstractJavaModelImplementer;
import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.PackageClassHelper;
import com.ecfeed.core.utils.SleepHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.core.utils.TextFileHelper;
import com.ecfeed.ui.common.utils.EclipsePackageFragmentGetter;
import com.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.common.utils.JavaUserClassImplementer;
import com.ecfeed.ui.common.utils.SourceCodeTextImplementer;

public class EclipseModelImplementer extends AbstractJavaModelImplementer {

	private final IFileInfoProvider fFileInfoProvider;

	public EclipseModelImplementer(IFileInfoProvider fileInfoProvider) {
		super(new EclipseImplementationStatusResolver(fileInfoProvider));
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public boolean implement(AbstractNode node) throws Exception{
		refreshWorkspace();
		boolean result = super.implement(node);
		CachedImplementationStatusResolver.clearCache(node);
		refreshWorkspace();
		return result;
	}

	@Override
	protected boolean implement(AbstractParameterNode parameterNode) throws CoreException, EcException{
		validateIfUserType(parameterNode);

		if(parameterDefinitionImplemented(parameterNode) == false){
			implementParameterDefinition(parameterNode, parameterNode.getLeafChoiceValues());
		}
		else{
			List<ChoiceNode> unimplemented = unimplementedChoices(parameterNode.getLeafChoices());
			implementChoiceNodes(unimplemented);
			for(ChoiceNode choice : unimplemented){
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	private void validateIfUserType(AbstractParameterNode parameterNode) throws EcException {
		String type = parameterNode.getType();
		if (!JavaTypeHelper.isUserType(type)) {
			return;
		}

		String thePackage = PackageClassHelper.getPackage(type);
		if (thePackage != null) {
			return;
		}

		AbstractNode parentNode = parameterNode.getParent();
		if (!(parentNode instanceof MethodNode)) {
			final String PACKAGE_NAME_REQUIRED_1 = "Package name is required for user type: %s (parameter: %s).";
			EcException.report(String.format(PACKAGE_NAME_REQUIRED_1, type, parameterNode.getName()));
		}

		AbstractNode grandParentNode = parentNode.getParent();
		if (!(grandParentNode instanceof ClassNode)) {
			final String PACKAGE_NAME_REQUIRED_2 = "Package name is required for user type: %s (method: %s, parameter: %s).";
			EcException.report(String.format(PACKAGE_NAME_REQUIRED_2, type, parentNode.getName(), parameterNode.getName()));
		}

		final String PACKAGE_NAME_REQUIRED_3 = "Package name is required for user type: %s (class: %s, method: %s, parameter: %s).";
		EcException.report(String.format(PACKAGE_NAME_REQUIRED_3, type, grandParentNode.getName(), parentNode.getName(), parameterNode.getName()));
	}

	@Override
	protected boolean implement(ChoiceNode choiceNode) throws CoreException, EcException {

		AbstractParameterNode parameter = choiceNode.getParameter();

		if (!parameterDefinitionImplemented(parameter)) {
			return implementParameterWithChildChoice(parameter, choiceNode);
		}

		implementChoiceWithChildren(choiceNode);
		return true;
	}

	private boolean implementParameterWithChildChoice(
			AbstractParameterNode parameter,
			ChoiceNode choiceNode) throws CoreException, EcException {

		if (parameterDefinitionImplementable(parameter)) {
			implementParameterDefinition(
					parameter, 
					new HashSet<String>(Arrays.asList(new String[]{choiceNode.getValueString()})));

			return true;
		}

		return false;
	}

	private void implementChoiceWithChildren(ChoiceNode choiceNode) throws CoreException, EcException {

		if (choiceNode.isAbstract()) {
			implementChoiceNodes(unimplementedChoices(choiceNode.getLeafChoices()));
			return;
		}

		if (isImplementableNode(choiceNode) 
				&& getImplementationStatus(choiceNode) != EImplementationStatus.IMPLEMENTED) {

			implementChoiceNodes(Arrays.asList(new ChoiceNode[]{choiceNode}));
		}
	}

	@Override
	protected void implementAndroidCode(ClassNode classNode) throws EcException {
		ImplementerExt implementer = createImplementer(classNode);
		implementer.implementContent();
	}

	@Override
	protected void implementClassDefinition(ClassNode classNode) throws CoreException, EcException {
		String projectPath = new EclipseProjectHelper(fFileInfoProvider).getProjectPath();
		IClassImplementHelper implementHelper = new EclipseClassImplementHelper(fFileInfoProvider);

		String thePackage = ModelHelper.getPackageName(classNode.getName());
		String classNameWithoutExtension = ModelHelper.convertToLocalName(classNode.getName());

		if (classNode.getRunOnAndroid()) {
			AndroidUserClassImplementerExt.implementContent(
					projectPath, thePackage, classNameWithoutExtension, implementHelper);
		} else {
			JavaUserClassImplementer implementer = 
					new JavaUserClassImplementer(
							projectPath, thePackage, classNameWithoutExtension, implementHelper);
			implementer.implementContent();
		}
	}

	@Override
	protected void implementMethodDefinition(MethodNode methodNode) throws CoreException, EcException {
		if(!classDefinitionImplemented(methodNode.getClassNode())){
			implementClassDefinition(methodNode.getClassNode());
		}
		IImplementerExt methodImplementer = createMethodImplementer(methodNode);
		methodImplementer.implementContent();
	}

	@Override
	protected void implementParameterDefinition(AbstractParameterNode node) throws CoreException, EcException {
		implementParameterDefinition(node, null);
	}

	protected void implementParameterDefinition(AbstractParameterNode node, Set<String> fields) throws CoreException, EcException {
		String typeName = node.getType();
		if(JavaTypeHelper.isJavaType(typeName)){
			return;
		}
		if(JavaLanguageHelper.isValidTypeName(typeName) == false){
			return;
		}
		String packageName = ModelHelper.getPackageName(typeName);
		String localName = ModelHelper.convertToLocalName(typeName);
		String unitName = localName + ".java";
		//		IPackageFragment packageFragment = getPackageFragment(packageName);
		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(packageName, fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		unit.createType(enumDefinitionContent(node, fields), null, false, null);
		setWorkingCopy(unit);
	}

	@Override
	protected void implementChoiceDefinition(ChoiceNode node) throws CoreException, EcException {

		if (isImplementableNode(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED) {
			implementChoiceNodes(Arrays.asList(new ChoiceNode[]{node}));
		}
	}

	private void implementChoiceNodes(List<ChoiceNode> choiceNodes) throws CoreException, EcException {

		refreshWorkspace();

		AbstractParameterNode parent = getParameter(choiceNodes);
		if (parent == null) {
			return;
		}

		if (!parameterDefinitionImplemented(parent)) {
			implementParameterDefinition(parent);
		}

		String enumTypeName = parent.getType();
		IType enumType = getJavaProject().findType(enumTypeName);
		ICompilationUnit compilationUnit = enumType.getCompilationUnit();

		implementEnumItemsFromChoiceNodes(choiceNodes, enumType, compilationUnit);
	}

	private void implementEnumItemsFromChoiceNodes(
			List<ChoiceNode> choiceNodes,
			IType enumType,
			ICompilationUnit compilationUnit) 
					throws CoreException, JavaModelException, EcException {

		addEnumItemsFromChoiceNodes(choiceNodes, enumType);
		refresh(enumType, compilationUnit);

		if (enumHasConstructorWithStringParam(enumType)) {

			final int TIME_TO_FINISH_WRITING_OF_PROJECT = 1;
			SleepHelper.sleep(TIME_TO_FINISH_WRITING_OF_PROJECT);

			correctEnumFile(choiceNodes, enumType);
			refreshWorkspace();
		}
	}

	private void correctEnumFile(List<ChoiceNode> choiceNodes, IType enumType) throws EcException {

		String enumFilePath = enumType.getResource().getLocation().toString();
		String oldFileContent = TextFileHelper.readContent(enumFilePath);

		String newFileContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						oldFileContent, choiceNodes);

		TextFileHelper.writeContent(enumFilePath, newFileContent);
	}

	private void refresh(IType enumType, ICompilationUnit iUnit) throws CoreException, JavaModelException {

		enumType.getResource().refreshLocal(IResource.DEPTH_ONE, null);
		setWorkingCopy(iUnit);
		refreshWorkspace();
	}

	private void setWorkingCopy(ICompilationUnit compilationUnit) throws JavaModelException {

		compilationUnit.becomeWorkingCopy(null);
		compilationUnit.commitWorkingCopy(true, null);
	}

	private void addEnumItemsFromChoiceNodes(
			List<ChoiceNode> choiceNodes,
			IType enumType) throws CoreException {

		CompilationUnit compilationUnit = getCompilationUnit(enumType);

		EnumDeclaration enumDeclaration = 
				getEnumDeclaration(compilationUnit, enumType.getFullyQualifiedName());

		if (enumDeclaration == null) {
			return;
		}

		addChoicesToEnum(choiceNodes, enumDeclaration, compilationUnit);

		saveChanges(compilationUnit, enumType.getResource().getLocation());
	}

	@SuppressWarnings("unchecked")
	private void addChoicesToEnum(
			List<ChoiceNode> nodes,
			EnumDeclaration enumDeclaration,
			CompilationUnit compilationUnit) {

		EnumConstantDeclaration enumConstant = compilationUnit.getAST().newEnumConstantDeclaration();
		List<String> enumItemNames = new ArrayList<String>();

		for (ChoiceNode node : nodes) {

			String enumItemName = node.getValueString();

			if (enumItemNames.contains(enumItemName)) {
				continue;
			}

			enumConstant.setName(compilationUnit.getAST().newSimpleName(enumItemName));
			//			enumConstant.setProperty(propertyName, data);
			enumDeclaration.enumConstants().add(enumConstant);
			enumItemNames.add(enumItemName);
		}
	}

	@Override
	protected boolean isImplementableNode(ClassNode node) throws EcException{
		if(!androidCodeImplemented(node)) {
			return true;
		}
		if(classDefinitionImplemented(node)){
			return hasImplementableNode(node.getMethods());
		}
		return classDefinitionImplementable(node);
	}

	@Override
	protected boolean isImplementableNode(MethodNode node) throws EcException{
		ClassNode classNode = node.getClassNode();
		if(!androidCodeImplemented(classNode)) {
			return true;
		}		
		if(methodDefinitionImplemented(node)){
			return hasImplementableNode(node.getParameters()) || hasImplementableNode(node.getTestCases());
		}
		return methodDefinitionImplementable(node);
	}

	@Override
	protected boolean isImplementableNode(MethodParameterNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getChoices());
		}
		return parameterDefinitionImplementable(node);
	}

	@Override
	protected boolean isImplementableNode(GlobalParameterNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getChoices());
		}
		return parameterDefinitionImplementable(node);
	}

	@Override
	protected boolean isImplementableNode(ChoiceNode choiceNode) {

		if (choiceNode.isAbstract()) {
			return hasImplementableNode(choiceNode.getChoices());
		}

		if (parameterDefinitionImplemented(choiceNode.getParameter())) {
			return isChoiceImplementable(choiceNode);
		}

		if (!parameterDefinitionImplementable(choiceNode.getParameter())) {
			return false;
		}

		return JavaLanguageHelper.isValidJavaIdentifier(choiceNode.getValueString());
	}

	private boolean isChoiceImplementable(ChoiceNode node) {

		try{
			String stParameterType = node.getParameter().getType();
			IType parameterType = getJavaProject().findType(stParameterType);

			if (!parameterType.isEnum()) {
				return false;
			}

			if (!enumHasImplementableConstructor(parameterType)) {
				return false;
			}

			return JavaLanguageHelper.isValidJavaIdentifier(node.getValueString());

		} catch(CoreException e) {

			return false;
		}
	}

	private static boolean enumHasImplementableConstructor(IType parameterType) throws JavaModelException {

		JavaModelAnalyser.ClassConstructorsType classConstructorsType = 
				JavaModelAnalyser.analyzeConstructors(parameterType);

		if (classConstructorsType == JavaModelAnalyser.ClassConstructorsType.NO_CONSTRUCTOR
				|| classConstructorsType == JavaModelAnalyser.ClassConstructorsType.CONSTRUCTOR_WITHOUT_PARAMETERS
				|| classConstructorsType == JavaModelAnalyser.ClassConstructorsType.CONSTRUCTOR_WITH_STRING_ONLY) {

			return true;
		}

		return false;
	}


	private static boolean enumHasConstructorWithStringParam(IType parameterType) throws JavaModelException {

		JavaModelAnalyser.ClassConstructorsType classConstructorsType = 
				JavaModelAnalyser.analyzeConstructors(parameterType);

		if (classConstructorsType == JavaModelAnalyser.ClassConstructorsType.CONSTRUCTOR_WITH_STRING_ONLY) {
			return true;
		}

		return false;
	}	

	@Override
	protected boolean androidCodeImplemented(ClassNode classNode) throws EcException {

		if (!classNode.getRunOnAndroid()) {
			return true;
		}

		if (!new EclipseProjectHelper(fFileInfoProvider).isAndroidProject()) {
			return true;
		}

		ImplementerExt implementer = createImplementer(classNode);
		return implementer.contentImplemented();
	}

	@Override
	protected boolean classDefinitionImplemented(ClassNode node) {
		try{
			IType type = getJavaProject().findType(node.getName());
			return (type != null) && type.isClass();
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode methodNode) {
		IImplementerExt methodImplementer = createMethodImplementer(methodNode);
		return methodImplementer.contentImplemented();
	}

	@Override
	protected boolean parameterDefinitionImplemented(AbstractParameterNode node) {
		try{
			IType type = getJavaProject().findType(node.getType());
			return (type != null) && type.isEnum();
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private IImplementerExt createMethodImplementer(MethodNode methodNode) {
		final String className = ClassNodeHelper.getQualifiedName(methodNode.getClassNode());

		IMethodImplementHelper fMethodImplementHelper = 
				new EclipseMethodImplementHelper(fFileInfoProvider, className, methodNode);

		if (methodNode.getRunOnAndroid()) {
			return AndroidMethodImplementerExt.createImplementer(methodNode, fMethodImplementHelper);
		} else {
			return new JavaMethodImplementer(methodNode, fMethodImplementHelper);
		}
	}

	protected String methodDefinitionContent(MethodNode node){
		String methodSignature = "public void " + node.getName() + "(" + getMethodArgs(node) +")"; 

		String methodBody =	
				" {\n"+ 
						"\t" + "// TODO Auto-generated method stub" + "\n" + 
						"\t" + createLoggingInstruction(node) + "\n"+ 
						"}";

		return methodSignature + methodBody;
	}

	private String createLoggingInstruction(MethodNode methodNode) {
		String result = "";

		if (methodNode.getRunOnAndroid()) {
			result = "android.util.Log.d(\"ecFeed\", \"" + methodNode.getName() + "(";
		} else {
			result = "System.out.println(\"" + methodNode.getName() + "(";
		}

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		if(parameters.size() == 0) {
			return result + ")\");";
		}

		result +=  "\" + ";
		for(int index = 0; index < parameters.size(); ++index) {
			result += parameters.get(index).getName();
			if(index != parameters.size() - 1) {
				result += " + \", \"";
			}
			result += " + ";
		}

		return result + "\")\");"; 
	}

	private String getMethodArgs(MethodNode node) {
		List<AbstractParameterNode> parameters = node.getParameters();

		if(parameters.size() == 0) {
			return new String();
		}

		String args = "";

		for(int i = 0; i < parameters.size(); ++i) {
			AbstractParameterNode param = parameters.get(i);
			args += ModelHelper.convertToLocalName(param.getType()) + " " + param.getName();
			if(i != parameters.size() - 1){
				args += ", ";
			}
		}
		return args;
	}

	protected String enumDefinitionContent(AbstractParameterNode node, Set<String> fields){
		String fieldsDefinition = "";
		if(fields != null && fields.size() > 0){
			for(String field: fields){
				fieldsDefinition += field + ", ";
			}
			fieldsDefinition = fieldsDefinition.substring(0, fieldsDefinition.length() - 2);
		}
		String result = "public enum " + ModelHelper.convertToLocalName(node.getType()) + "{\n\t" + fieldsDefinition + "\n}";
		return result;
	}

	private boolean methodDefinitionImplementable(MethodNode node) {
		if(classDefinitionImplemented(node.getClassNode()) == false){
			if(classDefinitionImplementable(node.getClassNode()) == false){
				return false;
			}
		}
		try{
			IType type = getJavaProject().findType(node.getClassNode().getName());
			EclipseModelBuilder builder = new EclipseModelBuilder();
			if(type != null){
				for(IMethod method : type.getMethods()){
					MethodNode model = builder.buildMethodModel(method);
					if(model.getName().equals(node.getName()) && model.getParameterTypes().equals(node.getParameterTypes())){
						return hasImplementableNode(node.getChildren());
					}
				}
			}
			return true;
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private boolean classDefinitionImplementable(ClassNode node) {
		try{
			return getJavaProject().findType(node.getName()) == null;
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private boolean parameterDefinitionImplementable(AbstractParameterNode parameter) {
		try {
			String type = parameter.getType();
			if(JavaTypeHelper.isJavaType(type)){
				return false;
			}
			else{
				return getJavaProject().findType(type) == null;
			}
		}catch (CoreException e) {
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private CompilationUnit getCompilationUnit(IType type) throws CoreException{
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(type.getCompilationUnit());
		CompilationUnit unit = (CompilationUnit)parser.createAST(null);
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = type.getResource().getLocation();
		bufferManager.connect(path, LocationKind.LOCATION, null);
		unit.recordModifications();
		return unit;
	}

	private EnumDeclaration getEnumDeclaration(CompilationUnit unit, String typeName) {
		String className = ModelHelper.convertToLocalName(typeName);
		for (Object object : unit.types()) {
			AbstractTypeDeclaration declaration = (AbstractTypeDeclaration)object;
			if (declaration.getName().toString().equals(className) && declaration instanceof EnumDeclaration) {
				return (EnumDeclaration)declaration;
			}
		}
		return null;
	}

	private void saveChanges(CompilationUnit unit, IPath location) throws CoreException {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(location, LocationKind.LOCATION);
		IDocument document = textFileBuffer.getDocument();
		TextEdit edits = unit.rewrite(document, null);
		try {
			edits.apply(document);
			textFileBuffer.commit(null, false);
			bufferManager.disconnect(location, LocationKind.LOCATION, null);
			refreshWorkspace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private IJavaProject getJavaProject() throws CoreException{
		if(fFileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			return JavaCore.create(fFileInfoProvider.getProject());
		}
		return null;
	}

	private List<ChoiceNode> unimplementedChoices(List<ChoiceNode> choices){
		List<ChoiceNode> unimplemented = new ArrayList<>();
		for(ChoiceNode choice : choices){
			if(isImplementableNode(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				unimplemented.add(choice);
			}
		}
		return unimplemented;
	}

	private AbstractParameterNode getParameter(List<ChoiceNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		AbstractParameterNode parameter = nodes.get(0).getParameter();
		for(ChoiceNode node : nodes){
			if(node.getParameter() != parameter){
				return null;
			}
		}
		return parameter;
	}

	private void refreshWorkspace() {
		try {
			getJavaProject().getProject().getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private ImplementerExt createImplementer(ClassNode classNode) {
		String baseRunner = classNode.getAndroidRunner(); 

		IProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider);
		IClassImplementHelper classImplementHelper = new EclipseClassImplementHelper(fFileInfoProvider);
		IInstallationDirFileHelper installationDirFileHelper = new EclipseInstallationDirFileHelper(); 

		return new ImplementerExt(baseRunner, projectHelper, classImplementHelper, installationDirFileHelper); 
	}
}
