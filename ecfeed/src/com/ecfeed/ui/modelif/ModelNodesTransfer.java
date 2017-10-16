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

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class ModelNodesTransfer extends ByteArrayTransfer {

	public static final String TRANSFER_TYPE = "ecFeedNodeTransfer";
	public static final int TRANSFER_ID = registerType(TRANSFER_TYPE);
	private static final  ModelNodesTransfer fInstance = new ModelNodesTransfer();

	public static ModelNodesTransfer getInstance(){
		return fInstance;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[]{TRANSFER_TYPE};
	}

	@Override
	protected int[] getTypeIds() {
		return new int[]{TRANSFER_ID};
	}

	public void javaToNative (Object object, TransferData transferData) {
	}

	public Object nativeToJava(TransferData transferData) {
		return null;
	}
}
