/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common.local;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IImplementationStatusResolver;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.IEclipseImplementationStatusResolver;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class EclipseImplementationStatusResolver 
implements IEclipseImplementationStatusResolver, IImplementationStatusResolver {

	public EclipseImplementationStatusResolver(IJavaProjectProvider javaProjectProvider) {
	}

	@Override
	public boolean androidCodeImplemented(ClassNode classNode) throws EcException {
		return false;
	}

	@Override
	public boolean classDefinitionImplemented(String qualifiedName) {
		return false;
	}

	@Override
	public boolean methodDefinitionImplemented(MethodNode method) {
		return false;
	}

	@Override
	public boolean enumDefinitionImplemented(String qualifiedName) {
		return false;
	}

	@Override
	public boolean enumValueImplemented(String qualifiedName, String value) {
		return false;
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node) {
		return null;
	}

}
