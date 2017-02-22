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

import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.JavaTypeHelper;

public class ConstraintNode extends AbstractNode{

	private Constraint fConstraint;

	@Override
	public int getIndex() {
		if (getMethod() == null) {
			return -1;
		}
		return getMethod().getConstraintNodes().indexOf(this);
	}

	@Override
	public String toString() {
		return getName() + ": " + getConstraint().toString();
	}

	@Override
	public ConstraintNode makeClone() {
		ConstraintNode copy = new ConstraintNode(getName(), fConstraint.getCopy());
		copy.setProperties(getProperties());
		return copy;
	}

	public ConstraintNode(String name, Constraint constraint) {
		super(name);
		fConstraint = constraint;
	}

	public Constraint getConstraint() {
		return fConstraint;
	}

	public MethodNode getMethod() {
		AbstractNode parent = getParent();
		if (parent == null) {
			return null;
		}
		if (parent instanceof MethodNode) {
			return (MethodNode)parent;
		}
		return null;
	}

	public void setMethod(MethodNode method) {
		setParent(method);
	}

	public boolean evaluate(List<ChoiceNode> values) {
		if (fConstraint != null) {
			return fConstraint.evaluate(values);
		}
		return false;
	}

	public boolean mentions(ChoiceNode choice) {
		if (fConstraint.mentions(choice)) {
			return true;
		}
		return false;
	}

	public boolean mentions(MethodParameterNode parameter) {
		return fConstraint.mentions(parameter);
	}

	public boolean mentions(AbstractParameterNode parameter) {
		if (parameter instanceof MethodParameterNode) {
			MethodParameterNode param = (MethodParameterNode)parameter;
			return fConstraint.mentions(param);
		} else if (parameter instanceof GlobalParameterNode) {
			GlobalParameterNode global = (GlobalParameterNode)parameter;
			for (MethodParameterNode methodParam: global.getLinkers()) {
				return fConstraint.mentions(methodParam);
			}
		}
		return false;
	}

	public boolean mentions(MethodParameterNode parameter, String label) {
		return fConstraint.mentions(parameter, label);
	}

	public boolean updateReferences(MethodNode method) {
		if (fConstraint.updateRefrences(method)) {
			setParent(method);
			return true;
		}
		return false;
	}

	public ConstraintNode getCopy(MethodNode method) {
		ConstraintNode copy = makeClone();
		if (copy.updateReferences(method))
			return copy;
		else
			return null;
	}

	@Override
	public boolean isMatch(AbstractNode node) {
		if (node instanceof ConstraintNode == false) {
			return false;
		}
		ConstraintNode compared = (ConstraintNode)node;
		if (getConstraint().getPremise().compare(compared.getConstraint().getPremise()) == false) {
			return false;
		}

		if (getConstraint().getConsequence().compare(compared.getConstraint().getConsequence()) == false) {
			return false;
		}

		return super.isMatch(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public boolean isConsistent() {

		if (!parametersConsistent()) {
			return false;
		}

		if (!choicesConsistent()) {
			return false;
		}

		if (!constraintsConsistent()) {
			return false;
		}

		return true;
	}

	private boolean parametersConsistent() {
		Set<AbstractParameterNode> referencedParameters = getConstraint().getReferencedParameters(); 
		for (AbstractParameterNode parameter : referencedParameters) { // wow it really does point to global instead of pointing to method param WHICH would then point to global, if linked...
			boolean isLinked = false;

			List<AbstractParameterNode> methodParameters = getMethod().getParameters(); 
			for (AbstractParameterNode param : methodParameters) {
				MethodParameterNode methodParam = (MethodParameterNode) param;
				if (methodParam.isLinked() && methodParam.getLink().equals(parameter)) {
					isLinked = true;
					break;
				}
			}

			if (!isLinked && !methodParameters.contains(parameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean choicesConsistent() {
		for (ChoiceNode choice : getConstraint().getReferencedChoices()) {
			AbstractParameterNode parameter = choice.getParameter();
			if (parameter == null || parameter.getChoice(choice.getQualifiedName()) == null) {
				if (false == (parameter instanceof MethodParameterNode && ((MethodParameterNode)parameter).isExpected())) {
					return false;
				}
			}
			//check if the choices parent parameter is still part of the method
			if (parameter.getMethods().contains(getMethod()) == false) {
				return false;
			}
			// of course 2nd parameter is linked, so... Also, above 2nd parameter passed equals 
			// because it points to same link...
			// looks like we have to check by name or something?
		}
		return true;
	}

	private boolean constraintsConsistent() {

		for (MethodParameterNode parameter : getMethod().getMethodParameters()) {
			if (!isConsistentForParameter(parameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean isConsistentForParameter(MethodParameterNode parameter) {

		String typeName = parameter.getType();

		if (isForbiddenTypeForOrderRelations(typeName)) {

			if (fConstraint.mentionsParameterAndOrderRelation(parameter)) {
				return false;
			}
		}

		if (!checkLabels(parameter)) {
			return false;
		}

		return true;
	}

	private boolean isForbiddenTypeForOrderRelations(String typeName) {

		if (JavaTypeHelper.isUserType(typeName)) {
			return true;
		}

		if (JavaTypeHelper.isBooleanTypeName(typeName)) {
			return true;
		}

		return false;
	}

	private boolean checkLabels(MethodParameterNode parameter) {

		for (String label : getConstraint().getReferencedLabels(parameter)) {
			if (!parameter.getLeafLabels().contains(label)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public int getMaxIndex() {
		if (getMethod() != null) {
			return getMethod().getConstraintNodes().size();
		}
		return -1;
	}

	boolean mentionsParameter(MethodParameterNode methodParameter) {
		return fConstraint.mentionsParameter(methodParameter);
	}
}
