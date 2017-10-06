/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.GlobalParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterDetailsPage extends AbstractParameterDetailsPage {

	private GlobalParameterInterface fParameterIf;
	private LinkingMethodsViewer fLinkingMethodsViewer;

	public GlobalParameterDetailsPage(
			IMainTreeProvider mainTreeProvider,
			GlobalParameterInterface globalParameterInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		this(mainTreeProvider, globalParameterInterface, updateContext, javaProjectProvider, null);
	}

	public GlobalParameterDetailsPage(
			IMainTreeProvider mainTreeProvider,
			GlobalParameterInterface globalParameterInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {

		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
		fParameterIf = globalParameterInterface;
	}	

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		addForm(fLinkingMethodsViewer = 
				new LinkingMethodsViewer(this, getModelUpdateContext(), getJavaProjectProvider()));
	}


	@Override
	protected AbstractParameterInterface getParameterIf() {
		return fParameterIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof GlobalParameterNode){
			GlobalParameterNode parameter = (GlobalParameterNode)getSelectedElement();
			fParameterIf.setOwnNode(parameter);
			getMainSection().setText(parameter.getQualifiedName() + ": " + parameter.getType());
			fLinkingMethodsViewer.setInput(parameter);
			fLinkingMethodsViewer.setVisible(fParameterIf.getLinkers().size() > 0);

			getMainSection().layout();
			getChoicesViewer().setEditEnabled(true);
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return GlobalParameterNode.class;
	}

	@Override
	protected AbstractCommentsSection getCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext) {

		if (ApplicationContext.isProjectAvailable()) {
			return new GlobalParameterCommentsSection(sectionContext, updateContext, getJavaProjectProvider());
		} else {
			return new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider());
		}
	}
}
