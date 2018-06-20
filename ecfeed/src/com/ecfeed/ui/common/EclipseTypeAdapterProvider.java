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

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.utils.TypeAdapterProvider;

public class EclipseTypeAdapterProvider extends TypeAdapterProvider{

	@SuppressWarnings("rawtypes")
	@Override
	protected ITypeAdapter<?> getTypeAdapterBaseForUserType(String type) {
		return new EclipseTypeAdapterForUserType(type);
	}
}
