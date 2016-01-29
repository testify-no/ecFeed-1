/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter;

import com.testify.ecfeed.core.utils.SystemLogger;

public class ModelOperationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2841889790004375884L;

	private ModelOperationException(String message){
		super(message);
	}

	public static void report(String message) throws ModelOperationException {
		SystemLogger.logThrow(message);
		throw new ModelOperationException(message);
	}	
}