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

import java.util.HashMap;
import java.util.Map;


public class SessionDataStore {

		private static Map<String, Object> fAttributes = new HashMap<String, Object>();
		
		public static Object get(String name) {
			return fAttributes.get(name);
		}
		
		public static void set(String name, Object value) {
			fAttributes.put(name, value);
		}
		
}
