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
import java.util.List;


public class TestCaseNode extends AbstractNode {
	List<ChoiceNode> fTestData;

	@Override
	public int getMyIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getTestCases().indexOf(this);
	}

	@Override
	public String toString(){
		String methodName = null;
		if (getParent() != null){
			methodName = getParent().getName();
		}
		String result = "[" + getName() + "]";

		if(methodName != null){
			result += ": " + methodName + "(";
			result += testDataString();
			result += ")";
		}

		return result;
	}

	@Override
	public TestCaseNode makeClone(){
		List<ChoiceNode> testdata = new ArrayList<>();
		for(ChoiceNode choice : fTestData){
			testdata.add(choice);
		}
		TestCaseNode copy = new TestCaseNode(this.getName(), testdata);
		copy.setProperties(getProperties());
		return copy;
	}

	public TestCaseNode(String name, List<ChoiceNode> testData) {
		super(name);
		fTestData = testData;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	public MethodParameterNode getMethodParameter(ChoiceNode choice){
		if(getTestData().contains(choice)){
			int index = getTestData().indexOf(choice);
			return getMethod().getMethodParameters().get(index);
		}
		return null;
	}

	public List<ChoiceNode> getTestData(){
		return fTestData;
	}

	public void replaceValue(int index, ChoiceNode newValue) {
		fTestData.set(index, newValue);
	}

	public boolean mentions(ChoiceNode choice) {
		for(ChoiceNode p : fTestData){
			if(p.isMatchIncludingParents(choice)){
				return true;
			}
		}
		return false;
	}

	public String testDataString(){
		String result = new String();

		for(int i = 0; i < fTestData.size(); i++){
			ChoiceNode choice = fTestData.get(i);
			if(getMethodParameter(choice).isExpected()){
				result += "[e]" + choice.getValueString();
			}
			else{
				result += choice.getQualifiedName();
			}
			if(i < fTestData.size() - 1){
				result += ", ";
			}
		}
		return result;
	}

	public static boolean validateTestSuiteName(String newName) {
		if(newName.length() < 1 || newName.length() > 64) return false;
		if(newName.matches("[ ]+.*")) return false;
		return true;
	}

	public TestCaseNode getCopy(MethodNode method){
		TestCaseNode tcase = makeClone();
		if(tcase.updateReferences(method)){
			tcase.setParent(method);
			return tcase;
		}
		else
			return null;
	}

	public boolean updateReferences(MethodNode method){
		List<MethodParameterNode> parameters = method.getMethodParameters();
		if(parameters.size() != getTestData().size())
			return false;

		for(int i = 0; i < parameters.size(); i++){
			MethodParameterNode parameter = parameters.get(i);
			if(parameter.isExpected()){
				String name = getTestData().get(i).getName();
				String value = getTestData().get(i).getValueString();
				ChoiceNode newChoice = new ChoiceNode(name, value);
				newChoice.setParent(parameter);
				getTestData().set(i, newChoice);
			} else{
				ChoiceNode original = getTestData().get(i);
				ChoiceNode newReference = parameter.getChoice(original.getQualifiedName());
				if(newReference == null){
					return false;
				}
				getTestData().set(i, newReference);
			}
		}
		return true;
	}

	@Override
	public boolean isMatch(AbstractNode node){
		if(node instanceof TestCaseNode == false){
			return false;
		}

		TestCaseNode compared = (TestCaseNode)node;

		if(getTestData().size() != compared.getTestData().size()){
			return false;
		}

		for(int i = 0; i < getTestData().size(); i++){
			if(getTestData().get(i).isMatch(compared.getTestData().get(i)) == false){
				return false;
			}
		}

		return super.isMatch(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public boolean isConsistent() {
		for(ChoiceNode choice : getTestData()){
			MethodParameterNode parameter = getMethodParameter(choice);
			if(parameter == null || (parameter.isExpected() == false && parameter.getChoice(choice.getQualifiedName()) == null)){
				return false;
			}
			if(choice.isAbstract()){
				return false;
			}
		}
		return true;
	}

	@Override
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getTestCases().size();
		}
		return -1;
	}

}
