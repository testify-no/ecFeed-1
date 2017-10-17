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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.SessionAttributes;

public class ColorManager {

	private static Map<RGB, Color> getSessionInstance() {

		@SuppressWarnings("unchecked")
		Map<RGB, Color> sessionInstance = (Map<RGB, Color>)SessionDataStore.get(
				SessionAttributes.SA_COLOR_MANAGER);

		if (sessionInstance == null) {
			sessionInstance = new HashMap<RGB, Color>();
			SessionDataStore.set(SessionAttributes.SA_COLOR_MANAGER, sessionInstance);
		}

		return sessionInstance;
	}

	public static void dispose() {

		Iterator<Color> e = getSessionInstance().values().iterator();

		while (e.hasNext()) {
			((Color) e.next()).dispose();
		}
	}

	public static Color getColor(RGB rgb) {

		Color color = (Color) getSessionInstance().get(rgb);

		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			getSessionInstance().put(rgb, color);
		}

		return color;
	}
}
