/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.NodePropertyDefs;

public class AbstractNodeOperationSetProperty extends AbstractModelOperation {

	private NodePropertyDefs.PropertyId fPropertyId;
	private String fNewValue;
	private AbstractNode fAbstractNode;	

	private String fOriginalValue;	

	public AbstractNodeOperationSetProperty(
			NodePropertyDefs.PropertyId propertyId, 
			String value,
			AbstractNode abstractNode) {

		super(OperationNames.SET_PROPERTY);
		fPropertyId = propertyId;
		fNewValue = value;
		fAbstractNode = abstractNode;

		fOriginalValue = abstractNode.getPropertyValue(fPropertyId);
	}

	@Override
	public void execute() throws ModelOperationException {
		fAbstractNode.setPropertyValue(fPropertyId, fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new AbstractNodeOperationSetProperty(fPropertyId, fOriginalValue, fAbstractNode);
	}

}
