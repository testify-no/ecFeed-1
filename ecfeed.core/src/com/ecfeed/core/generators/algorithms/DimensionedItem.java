/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;


public class DimensionedItem<E> {
	
	int fDimension; // e.g. index of method parameter
	E fItem;

	public DimensionedItem(int dimension, E item) {
		fDimension = dimension;
		fItem = item;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof DimensionedItem))
			return false;

		DimensionedItem<?> var = (DimensionedItem<?>) obj;

		if (var.fDimension == this.fDimension && this.fItem.equals(var.fItem)) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {

		return ("dim:" + fDimension + " item:" + fItem.toString());
	}
	

}
