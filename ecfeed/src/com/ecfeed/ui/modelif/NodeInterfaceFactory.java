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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class NodeInterfaceFactory{

	public static AbstractNodeInterface getNodeInterface(
			AbstractNode node, 
			IModelUpdateContext context, 
			IJavaProjectProvider javaProjectProvider) {

		try {
			return (AbstractNodeInterface)node.accept(new InterfaceProvider(context, javaProjectProvider));
		} catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}

		AbstractNodeInterface nodeIf = new AbstractNodeInterface(context, javaProjectProvider);
		nodeIf.setOwnNode(node);
		return nodeIf;
	}

	private static class InterfaceProvider  implements IModelVisitor {

		private IJavaProjectProvider fJavaProjectProvider;
		private IModelUpdateContext fContext;

		public InterfaceProvider(IModelUpdateContext context, IJavaProjectProvider javaProjectProvider) {
			fContext = context;
			fJavaProjectProvider = javaProjectProvider;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface nodeIf = new RootInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface nodeIf = new ClassInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			MethodInterface nodeIf = new MethodInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			AbstractParameterInterface nodeIf = new MethodParameterInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			AbstractParameterInterface nodeIf = new GlobalParameterInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			TestCaseInterface nodeIf = new TestCaseInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ConstraintInterface nodeIf = new ConstraintInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			ChoiceInterface nodeIf = new ChoiceInterface(fContext, fJavaProjectProvider);
			nodeIf.setOwnNode(node);
			return nodeIf;
		}
	}

}
