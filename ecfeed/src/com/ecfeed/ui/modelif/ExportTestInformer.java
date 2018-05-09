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


public class ExportTestInformer extends AbstractTestInformer {
	
	public ExportTestInformer()
	{
		super(null, null);
	}

	@Override
	protected void setTestProgressMessage() {
		String message = "Exported: " + getExecutedTestCases();		
		fProgressMonitor.subTask(message);
	}

}
