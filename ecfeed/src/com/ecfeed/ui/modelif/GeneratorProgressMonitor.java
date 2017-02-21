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

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;


public class GeneratorProgressMonitor implements IGeneratorProgressMonitor{

	private IProgressMonitor fProgressMonitor;

	public GeneratorProgressMonitor(IProgressMonitor progressMonitor) {

		fProgressMonitor = progressMonitor;
	}

	@Override
	public boolean isCanceled() {

		if (fProgressMonitor.isCanceled()) {
			return true;
		}
		return false;
	}
}
