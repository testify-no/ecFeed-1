/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.StringHelper;

public class ClassNode extends GlobalParametersParentNode {

	private List<MethodNode> fMethods;

	@Override
	public List<? extends AbstractNode> getChildren(){
		List<AbstractNode> children = new ArrayList<AbstractNode>(super.getChildren());
		children.addAll(fMethods);
		return children;
	}

	@Override
	public ClassNode makeClone(){
		ClassNode copy = new ClassNode(getName());

		copy.setProperties(getProperties());

		for(GlobalParameterNode parameter : getGlobalParameters()){
			copy.addParameter(parameter.makeClone());
		}

		for(MethodNode method : fMethods){
			copy.addMethod(method.makeClone());
		}

		copy.setParent(getParent());
		return copy;
	}

	@Override
	public RootNode getRoot(){
		return (RootNode) getParent();
	}

	@Override
	public int getMaxChildIndex(AbstractNode potentialChild){
		if(potentialChild instanceof GlobalParameterNode) return getParameters().size();
		if(potentialChild instanceof MethodParameterNode) return getParameters().size();
		if(potentialChild instanceof MethodNode) return getMethods().size();
		return super.getMaxChildIndex(potentialChild);
	}

	public ClassNode(String qualifiedName) {
		this(qualifiedName, false, null);
	}

	public ClassNode(String qualifiedName, boolean runOnAndroid, String androidBaseRunner) {
		super(qualifiedName);

		setRunOnAndroid(runOnAndroid);

		if (!StringHelper.isNullOrEmpty(androidBaseRunner)) {
			setAndroidRunner(androidBaseRunner);
		}

		fMethods = new ArrayList<MethodNode>();
	}

	public String getAndroidRunner() {
		return getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER);
	}

	public void setAndroidRunner(String androidRunner) {
		setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER, androidRunner);
	}	

	public boolean getRunOnAndroid() {
		String value = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID);

		if (value == null) {
			return false;
		}

		return BooleanHelper.parseBoolean(value);
	}

	public void setRunOnAndroid(boolean runOnAndroid) {
		setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID, BooleanHelper.toString(runOnAndroid));
	}	

	public boolean addMethod(MethodNode method) {
		return addMethod(method, fMethods.size());
	}

	public boolean addMethod(MethodNode method, int index) {
		if(index >= 0 && index <= fMethods.size()){
			fMethods.add(index, method);
			method.setParent(this);
			return fMethods.indexOf(method) == index;
		}
		return false;
	}

	public MethodNode getMethod(String name, List<String> argTypes) {
		for(MethodNode methodNode : getMethods()){
			List<String> args = new ArrayList<String>();
			for(AbstractParameterNode arg : methodNode.getParameters()){
				args.add(arg.getType());
			}
			if(methodNode.getName().equals(name) && args.equals(argTypes)){
				return methodNode;
			}
		}
		return null;
	}

	public List<MethodNode> getMethods() {
		return fMethods;
	}

	public boolean removeMethod(MethodNode method) {
		return fMethods.remove(method);
	}

	public Set<String> getTestCaseNames() {
		Set<String> suites = new HashSet<String>();
		
		for(MethodNode method : getMethods()){
			suites.addAll(method.getTestCaseNames());
		}
		
		return suites;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean isMatch(AbstractNode node){
		if(node instanceof ClassNode == false){
			return false;
		}
		ClassNode compared = (ClassNode) node;
		List<MethodNode> comparedMethods = compared.getMethods();

		if(getMethods().size() != comparedMethods.size()){
			return false;
		}

		for(int i = 0; i < comparedMethods.size(); i++){
			if(getMethods().get(i).isMatch(comparedMethods.get(i)) == false){
				return false;
			}
		}

		return super.isMatch(node);
	}

	@Override
	public List<MethodNode> getMethods(AbstractParameterNode parameter) {
		List<MethodNode> result = new ArrayList<MethodNode>();
		for(MethodNode method : getMethods()){
			for(MethodParameterNode methodParameter : method.getMethodParameters()){
				if(methodParameter.isLinked() && methodParameter.getLink() == parameter){
					result.add(method);
					break;
				}
			}
		}
		return result;
	}
}
