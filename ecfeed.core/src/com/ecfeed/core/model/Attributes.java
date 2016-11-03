/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Attributes {
	private Map<String, Attribute> fMap = new HashMap<String, Attribute>();

	public void put(String key, Attribute attribute) {
		fMap.put(key, attribute);
	}

	public Attribute get(String key) {
		return fMap.get(key);
	}

	public Set<String> getKeys() {
		return fMap.keySet();
	}

	public int size() {
		return fMap.size();
	}

	public void remove(String key) {
		fMap.remove(key);
	}
}
