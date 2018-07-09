/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;

public class ITypeHelper {

	public static String getTypePath(IType iType) {

		if (iType == null) {
			return null;
		}

		IResource resource = iType.getResource();

		if (iType.getResource() == null) {
			return null;
		}

		IPath path = resource.getLocation();

		if (path == null) {
			return null;
		}

		return path.toString();
	}

}