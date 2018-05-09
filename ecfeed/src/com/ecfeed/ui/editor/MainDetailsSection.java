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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.application.SectionDecorationsHolder;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.dialogs.basic.TooltipController;


public class MainDetailsSection extends Section {

	TooltipController fTooltipController;

	private MainDetailsSection(Composite parent, int style, FormToolkit formToolkit) {

		super(parent, style);

		fTooltipController = new TooltipController(textLabel, "Tooltip");
	}

	@Override
	public void setText(String title) {

		final int DESCRIPTION_LENGTH = 180;

		String shortTitle = StringHelper.cutToMaxSize(title, DESCRIPTION_LENGTH);

		if (shortTitle.length() < title.length()) {
			shortTitle = shortTitle + "...";
		}

		fTooltipController.setTooltipMessage(title); 

		super.setText(shortTitle);
	}

	public static Section create(Composite parent, int style, FormToolkit formToolkit) {

		SectionDecorationsHolder sessionDecorationsHolder = ApplicationContext.getSessionDecorationsHolder();		
		Section section = new MainDetailsSection(parent, style, formToolkit);

		section.setBackground(sessionDecorationsHolder.getBackground());
		section.setForeground(sessionDecorationsHolder.getForeground());

		section.setTitleBarBackground(sessionDecorationsHolder.getTitleBarBackground());
		section.setTitleBarBorderColor(sessionDecorationsHolder.getTitleBarBorderColor());
		section.setTitleBarForeground(sessionDecorationsHolder.getTitleBarForeground());

		section.setFont(sessionDecorationsHolder.getFont());

		return section;
	}

}
