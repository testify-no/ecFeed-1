/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.ui.forms.widgets.Section;

public class StyleDistributor {

	public static int getSectionStyle() {
		return Section.EXPANDED | Section.TITLE_BAR;
	}

	public static int getCollapsibleSectionStyle() {
		return Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;
	}	

}
