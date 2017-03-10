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

import com.ecfeed.core.utils.ExceptionHelper;

public abstract class ParametersParentNode extends AbstractNode {

	private List<AbstractParameterNode> fParameters;

	public ParametersParentNode(String name) {
		super(name);
		fParameters = new ArrayList<AbstractParameterNode>();
	}

	public void addParameter(AbstractParameterNode parameter){
		addParameter(parameter, fParameters.size());
	}

	public void addParameter(AbstractParameterNode parameter, int index) {

		if (parameterWithNameExists(parameter)) {
			ExceptionHelper.reportRuntimeException("Parameter: " + parameter.getName() + " already exists.");
		}

		fParameters.add(index, parameter);
		parameter.setParent(this);
	}

	private boolean parameterWithNameExists(AbstractParameterNode abstractParameterNode) {

		for (AbstractParameterNode abstractParam : fParameters) {

			if (abstractParam.getName().equals(abstractParameterNode.getName())) {
				return true;
			}
		}

		return false;
	}

	public List<AbstractParameterNode> getParameters(){
		return fParameters;
	}

	public int getParametersCount(){
		return fParameters.size();
	}	

	public AbstractParameterNode getParameter(String parameterName) {
		for(AbstractParameterNode parameter : fParameters){
			if(parameter.getName().equals(parameterName)){
				return parameter;
			}
		}
		return null;
	}

	public List<String> getParametersTypes() {
		List<String> types = new ArrayList<String>();
		for(AbstractParameterNode parameter : fParameters){
			types.add(parameter.getType());
		}
		return types;
	}

	public List<String> getParametersNames() {
		List<String> names = new ArrayList<String>();
		for(AbstractParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}
		return names;
	}

	public boolean removeParameter(AbstractParameterNode parameter){
		parameter.setParent(null);
		return fParameters.remove(parameter);
	}

	public void replaceParameters(List<AbstractParameterNode> parameters) {
		fParameters.clear();
		fParameters.addAll(parameters);
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		return fParameters;
	}

	@Override
	public boolean isMatch(AbstractNode node){
		if(node instanceof ParametersParentNode == false){
			return false;
		}
		ParametersParentNode comparedParent = (ParametersParentNode)node;

		if(getParameters().size() != comparedParent.getParameters().size()){
			return false;
		}
		for(int i = 0; i < getParameters().size(); ++i){
			if(getParameters().get(i).isMatch(comparedParent.getParameters().get(i)) == false){
				return false;
			}
		}
		return super.isMatch(node);
	}

	public abstract List<MethodNode> getMethods(AbstractParameterNode parameter);
}
