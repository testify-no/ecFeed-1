/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

public class DimItem { // TODO - DimensionedItem<String> instead (add necessary methods) and delete DimItem, 
	// TODO - move tests to DimensionedItemTest   

	int fDimension; // e.g. index of method parameter
	String fItem;

	public DimItem(int dimension, String item) {
		fDimension = dimension;
		fItem = item;
	}

	int getDim(){
		return fDimension;
	}

	String getItem(){
		return fItem;
	}

	public boolean isMatch(DimItem dimItem) {

		if (fItem == dimItem.getItem() && fDimension == dimItem.getDim()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append(fDimension);
		sb.append(", ");
		sb.append(fItem);
		sb.append("}");

		return sb.toString();
	}	

}
