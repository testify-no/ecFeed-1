/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import com.ecfeed.core.model.ModelOperationException;

public final class ValueFieldOperationException extends ModelOperationException {

	private ValueFieldOperationException(String message) {
		super(message);
	}
	
	public static void report(String message) throws ModelOperationException {
		throw new ValueFieldOperationException(message);
	}

	private static final long serialVersionUID = 1L;

}
