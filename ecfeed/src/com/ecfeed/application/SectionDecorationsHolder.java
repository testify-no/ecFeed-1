/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.application;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class SectionDecorationsHolder {

	Color fBackground;
	Color fForeground;
	Color fTitleBarBackground;
	Color fTitleBarBorderColor;
	Color fTitleBarForeground;
	Font fFont;

	public void setDecorations(
			Color background,
			Color foreground,
			Color titleBarBackground,
			Color titleBarBorderColor,
			Color titleBarForeground,
			Font font) {

		fBackground = background;
		fForeground = foreground;
		fTitleBarBackground = titleBarBackground;
		fTitleBarBorderColor = titleBarBorderColor;
		fTitleBarForeground = titleBarForeground;
		fFont = font;
	}

	public Color getBackground() {
		return fBackground;
	}

	public Color getForeground() {
		return fForeground;
	}

	public Color getTitleBarBackground() {
		return fTitleBarBackground;
	}

	public Color getTitleBarBorderColor() {
		return fTitleBarBorderColor;
	}

	public Color getTitleBarForeground() {
		return fTitleBarForeground;
	}

	public Font getFont() {
		return fFont;
	}

}
