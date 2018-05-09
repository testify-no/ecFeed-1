/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IPrimitiveTypePredicate;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.SystemLogger;

public abstract class AbstractImplementationStatusResolver implements
IImplementationStatusResolver {

	private StatusResolver fStatusResolver;
	private IPrimitiveTypePredicate fPrimitiveTypeTester;

	private class StatusResolver implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implementationStatus(node);
		}
	}

	public AbstractImplementationStatusResolver(IPrimitiveTypePredicate primitiveTypeTester){
		fStatusResolver = new StatusResolver();
		fPrimitiveTypeTester = primitiveTypeTester;
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node) {

		if (fStatusResolver == null) {
			return EImplementationStatus.NOT_IMPLEMENTED;
		}

		try {
			return (EImplementationStatus)node.accept(fStatusResolver);
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());

		}

		return EImplementationStatus.NOT_IMPLEMENTED;
	}

	protected EImplementationStatus implementationStatus(RootNode project){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(project.getClasses().size() != 0){
			EImplementationStatus childrenStatus = childrenStatus(project.getClasses());
			if(childrenStatus != EImplementationStatus.IMPLEMENTED){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(ClassNode classNode) throws EcException {
		if(!classDefinitionImplemented(classNode.getName())){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}

		if(classNode.getMethods().isEmpty()){
			return EImplementationStatus.IMPLEMENTED;
		}

		EImplementationStatus childrenStatus = childrenStatus(classNode.getMethods());
		if(childrenStatus == EImplementationStatus.IMPLEMENTED){
			return EImplementationStatus.IMPLEMENTED;
		}

		return EImplementationStatus.PARTIALLY_IMPLEMENTED;
	}

	protected EImplementationStatus implementationStatus(MethodNode method) throws EcException {
		if(methodDefinitionImplemented(method) == false){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		if(method.getParameters().size() == 0){
			return EImplementationStatus.IMPLEMENTED;
		}
		EImplementationStatus childrenStatus = childrenStatus(method.getParameters());

		if(childrenStatus != EImplementationStatus.IMPLEMENTED){
			return EImplementationStatus.PARTIALLY_IMPLEMENTED;
		}

		return EImplementationStatus.IMPLEMENTED;
	}

	protected EImplementationStatus implementationStatus(MethodParameterNode parameter){
		EImplementationStatus status = implementationStatus((AbstractParameterNode)parameter);
		if(fPrimitiveTypeTester.isPrimitive(parameter.getType()) && parameter.isExpected()){
			status = EImplementationStatus.IMPLEMENTED;
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(GlobalParameterNode parameter){
		return implementationStatus((AbstractParameterNode)parameter);
	}

	protected EImplementationStatus implementationStatus(AbstractParameterNode parameter){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(fPrimitiveTypeTester.isPrimitive(parameter.getType()) == true){
			if(parameter.getChoices().size() == 0){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}else{
			if(enumDefinitionImplemented(parameter.getType()) == false){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}else{
				if(parameter.getChoices().size() == 0){
					status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
				}else{
					for(ChoiceNode choice : parameter.getChoices()){
						if(implementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
							status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
						}
					}
				}
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(TestCaseNode testCase){
		EImplementationStatus status = childrenStatus(testCase.getTestData());
		return status;
	}

	protected EImplementationStatus implementationStatus(ConstraintNode constraint){
		return EImplementationStatus.IRRELEVANT;
	}

	protected EImplementationStatus implementationStatus(ChoiceNode choice) {

		if (choice.isAbstract()) {
			return childrenStatus(choice.getChoices());
		}

		AbstractParameterNode parameter = choice.getParameter();
		if (parameter == null) {
			return EImplementationStatus.NOT_IMPLEMENTED;
		}

		String type = parameter.getType();
		if (fPrimitiveTypeTester.isPrimitive(type)) {
			return EImplementationStatus.IMPLEMENTED;
		}

		if (enumValueImplemented(type, choice.getValueString())) {
			return EImplementationStatus.IMPLEMENTED;
		}

		return EImplementationStatus.NOT_IMPLEMENTED;
	}

	protected EImplementationStatus childrenStatus(List<? extends AbstractNode> children){
		int size = children.size();
		int implementedChildren = 0;
		int notImplementedChildren = 0;

		for(AbstractNode child : children){

			EImplementationStatus status = getImplementationStatus(child);

			if(status == EImplementationStatus.IMPLEMENTED) { 
				++implementedChildren;
			}
			if(status == EImplementationStatus.NOT_IMPLEMENTED) {
				++notImplementedChildren;
			}
		}

		if(implementedChildren == size){
			return EImplementationStatus.IMPLEMENTED;
		}
		else if(notImplementedChildren == size){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		return EImplementationStatus.PARTIALLY_IMPLEMENTED;
	}

	protected abstract boolean classDefinitionImplemented(String qualifiedName);
	protected abstract boolean methodDefinitionImplemented(MethodNode method);
	protected abstract boolean enumDefinitionImplemented(String qualifiedName);
	protected abstract boolean enumValueImplemented(String qualifiedName, String value);
}
