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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.Constants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.ui.common.ColorConstants;
import com.ecfeed.ui.common.ColorManager;
import com.ecfeed.ui.common.ITestDataEditorListener;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.TestDataValueEditingSupport;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.TestCaseInterface;

public class TestDataViewer extends TableViewerSection implements ITestDataEditorListener{

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private IFileInfoProvider fFileInfoProvider;
	private TestCaseInterface fTestCaseIf;
	private TestDataValueEditingSupport fValueEditingSupport;

	public TestDataViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);
		fFileInfoProvider = fileInfoProvider;
		getTestCaseInterface();
		getSection().setText("Test data");
	}

	@Override
	protected void createTableColumns() {
		addColumn("Parameter", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				ChoiceNode choice = (ChoiceNode)element;
				AbstractParameterNode parent = choice.getParameter();
				return parent.toString();
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});

		TableViewerColumn valueColumn = addColumn("Value", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				ChoiceNode choice = (ChoiceNode)element;
				if(isExpected(choice)){
					return choice.getValueString();
				}
				return choice.toString();
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}

			private boolean isExpected(ChoiceNode choice) {
				MethodParameterNode parameter = (MethodParameterNode)choice.getParameter();
				return parameter.isExpected();
			}
		});

		fValueEditingSupport = new TestDataValueEditingSupport(null, getTableViewer(), this);
		valueColumn.setEditingSupport(fValueEditingSupport);
	}

	protected TestCaseInterface getTestCaseInterface() {
		if(fTestCaseIf == null){
			fTestCaseIf = new TestCaseInterface(this, fFileInfoProvider);
		}
		return fTestCaseIf;
	}

	public void setInput(TestCaseNode testCase){
		List<ChoiceNode> testData = testCase.getTestData();
		MethodNode methodNode = testCase.getMethod();		
		fValueEditingSupport.setMethod(testCase.getMethod());
		fTestCaseIf.setTarget(testCase);
		//target and data support must be updated prior to calling super
		List<ChoiceNode> convertedChoices = convertChoices(testData, methodNode);
		super.setInput(convertedChoices);
	}

	private List<ChoiceNode> convertChoices(List<ChoiceNode> choices, MethodNode methodNode) {

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		List<ChoiceNode> newChoices = new ArrayList<ChoiceNode>();
		int maxIndex = choices.size();

		for(int index = 0; index < maxIndex; index++) {
			MethodParameterNode parameter = (MethodParameterNode)parameters.get(index);
			ChoiceNode choice = choices.get(index);
			ChoiceNode newChoice = choice.getQualifiedCopy(parameter);
			newChoices.add(newChoice);
		}

		return newChoices;
	}

	@Override
	public void testDataChanged(int index, ChoiceNode choiceToUpdate) {
		if (choiceToUpdate.getName().equals(Constants.EXPECTED_VALUE_CHOICE_NAME)) { 
			fTestCaseIf.updateTestData(index, choiceToUpdate);
			return;
		}
		ChoiceNode originalChoice = getOriginalChoiceByName(index, choiceToUpdate.getName());
		fTestCaseIf.updateTestData(index, originalChoice);
	}

	private ChoiceNode getOriginalChoiceByName(int index, String choiceName) {
		List<ChoiceNode> originalChoices = getOriginalChoices(index);

		for (ChoiceNode choiceOfParent : originalChoices) {
			if (choiceOfParent.getName().equals(choiceName)) {
				return choiceOfParent;
			}
		}

		ExceptionHelper.reportRuntimeException("Can not find original choice by name : " + choiceName);
		return null;
	}

	private List<ChoiceNode> getOriginalChoices(int index) {
		TestCaseNode testCase = fTestCaseIf.getTarget();
		MethodNode method = testCase.getMethod();
		List<AbstractParameterNode> parameters = method.getParameters();
		AbstractParameterNode parameter = parameters.get(index);
		return parameter.getLeafChoices();
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	private Color getColor(Object element) {
		if (!(element instanceof ChoiceNode)) {
			return null;
		}
		if (!fFileInfoProvider.isProjectAvailable()) {
			return null;
		}		
		ChoiceNode choice = (ChoiceNode)element;
		if(fTestCaseIf.getImplementationStatus(choice) == EImplementationStatus.IMPLEMENTED){
			return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
		}
		return null;
	}

}
