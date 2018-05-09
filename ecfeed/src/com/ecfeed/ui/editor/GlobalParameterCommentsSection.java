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

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.GlobalParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterCommentsSection extends AbstractParameterCommentsSection {

	private IJavaProjectProvider fJavaProjectProvider;
	private GlobalParameterInterface fTargetIf;

	public GlobalParameterCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
		getExportButton().setText("Export type comments");
		getImportButton().setText("Import type comments");
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new GlobalParameterInterface(getModelUpdateContext(), fJavaProjectProvider);
		}
		return fTargetIf;
	}

	@Override
	protected ButtonClickListener createExportButtonSelectionListener(){
		return new ExportFullTypeSelectionAdapter();
	}

	@Override
	protected ButtonClickListener createImportButtonSelectionListener(){
		return new ImportFullTypeSelectionAdapter();
	}

}
