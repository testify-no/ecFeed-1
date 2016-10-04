/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;


public class SystemHelper {

	public static String getSystemTemporaryDir() { 
		return System.getProperty("java.io.tmpdir");
	}

	public static String getOperatingSystem() { 
		return System.getProperty("os.name");
	}	

	public static boolean isOperatingSystemLinux() { 
		String os = System.getProperty("os.name");

		if (os.equals("Linux")) {
			return true;
		}
		return false;
	}	

}
