/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common.utils;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;


public class SwtObjectHelper {
	
	public static GridData getGridData(Control control) {
		return (GridData)control.getLayoutData();
	}
	
	public static void setHorizontalSpan(Control control, int span) {
		GridData gridData = getGridData(control);
		gridData.horizontalSpan = span;
	}

}
