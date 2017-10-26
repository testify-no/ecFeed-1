/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.serialization.ect.EctSerializer;

public class ImportModelAction extends DescribedAction {

	public ImportModelAction() {
		super(ActionId.IMPORT_MODEL);
	}

	@Override
	public void run() {
		System.out.println("IMPORT");
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
