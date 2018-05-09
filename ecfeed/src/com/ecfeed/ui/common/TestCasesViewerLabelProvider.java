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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IImplementationStatusResolver;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class TestCasesViewerLabelProvider extends LabelProvider implements IColorProvider {

	private Map<String, Integer> fExecutableTestSuites;
	private Map<TestCaseNode, Boolean> fTestCasesStatusMap;
	MethodNode fMethod;
	private IImplementationStatusResolver fStatusResolver;

	public TestCasesViewerLabelProvider(IJavaProjectProvider javaProjectProvider) {
		fExecutableTestSuites = new HashMap<String, Integer>();
		fTestCasesStatusMap = new HashMap<TestCaseNode, Boolean>();
		fStatusResolver = new EclipseImplementationStatusResolver(javaProjectProvider);
	}

	public TestCasesViewerLabelProvider(
			IImplementationStatusResolver statusResolver, 
			MethodNode method, 
			IJavaProjectProvider javaProjectProvider){
		this(javaProjectProvider);
		fMethod = method;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String suiteName = (String)element;
			int executable = 0;
			if(fExecutableTestSuites.containsKey(suiteName)){
				executable = fExecutableTestSuites.get(suiteName);
			}
			Collection<TestCaseNode> testCases = fMethod.getTestCases(suiteName);
			String plural = testCases.size() != 1 ? "s" : "";
			return suiteName + " [" + testCases.size() + " test case" + plural + ", " + executable + " executable]";   
		}
		else if(element instanceof TestCaseNode){
			return fMethod.getName() + "(" + ((TestCaseNode)element).testDataString() + ")";
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		Color executableColor = ColorManager.getColor(ColorConstants.TEST_CASE_EXECUTABLE);
		if (element instanceof TestCaseNode) {
			TestCaseNode tc = (TestCaseNode)element;
			if(fTestCasesStatusMap.containsKey(tc) && fTestCasesStatusMap.get(tc) == true){
				return executableColor;
			}
			return null;
		}
		if (element instanceof String) {
			String name = (String)element;
			if(fExecutableTestSuites.containsKey(name)){
				boolean suiteExecutable = (fExecutableTestSuites.get(name) == fMethod.getTestCases(name).size());
				return suiteExecutable ? executableColor : null;
			}
			return null;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	public void setMethod(MethodNode method){
		fMethod = method;
		refresh();
	}

	public void refresh(){
		if (ApplicationContext.isProjectAvailable()) {
			updateExecutableTable();
		}
	}

	private void updateExecutableTable() {
		Map<ChoiceNode, EImplementationStatus> choiceStatusMap = new HashMap<ChoiceNode, EImplementationStatus>();
		fExecutableTestSuites.clear();
		fTestCasesStatusMap.clear();
		for(String testSuite : fMethod.getTestCaseNames()){
			fExecutableTestSuites.put(testSuite, 0);
		}
		if(fStatusResolver.getImplementationStatus(fMethod) != EImplementationStatus.NOT_IMPLEMENTED){
			for(TestCaseNode tc : fMethod.getTestCases()){
				boolean executable = true;
				String name = tc.getName();
				if(fExecutableTestSuites.containsKey(name) == false){
					fExecutableTestSuites.put(name, 0);
				}
				for(ChoiceNode p : tc.getTestData()){
					EImplementationStatus status = choiceStatusMap.get(p);
					if(status == null){
						status = fStatusResolver.getImplementationStatus(p);
						choiceStatusMap.put(p, status);
					}
					if(status != EImplementationStatus.IMPLEMENTED){
						executable = false;
						break;
					}
				}
				if(executable){
					int current = fExecutableTestSuites.get(name);
					fExecutableTestSuites.put(name, current + 1);
				}
				fTestCasesStatusMap.put(tc, executable);
			}
		}
	}
}
