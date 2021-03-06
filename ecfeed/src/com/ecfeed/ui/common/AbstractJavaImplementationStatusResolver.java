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

import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.IPrimitiveTypePredicate;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.EcException;

public abstract class AbstractJavaImplementationStatusResolver extends CachedImplementationStatusResolver{

	boolean fIsAndroidProject;

	public AbstractJavaImplementationStatusResolver(
			IPrimitiveTypePredicate primitiveTypePredicate, boolean isAndroidProject) {
		super(primitiveTypePredicate);
		fIsAndroidProject = isAndroidProject;
	}

	protected abstract boolean androidCodeImplemented(ClassNode classNode) throws EcException;

	protected EImplementationStatus implementationStatus(ClassNode classNode) throws EcException{
		if(fIsAndroidProject) {
			if (classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
				return EImplementationStatus.NOT_IMPLEMENTED;
			}			
		}
		return super.implementationStatus(classNode);
	}

	protected EImplementationStatus implementationStatus(MethodNode method) throws EcException{
		if(fIsAndroidProject) {
			ClassNode classNode = method.getClassNode();

			if (classNode.getRunOnAndroid() && !androidCodeImplemented(classNode)) {
				return EImplementationStatus.NOT_IMPLEMENTED;
			}
		}
		return super.implementationStatus(method);
	}
}
