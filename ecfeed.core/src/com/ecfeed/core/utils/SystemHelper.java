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

import java.net.InetAddress;
import java.net.UnknownHostException;


public class SystemHelper {

	static String fOs = null;

	public static String getSystemTemporaryDir() {

		return System.getProperty("java.io.tmpdir");
	}

	public static String getOperatingSystem() {

		if (fOs == null) {
			fOs = System.getProperty("os.name");
		}

		return fOs;
	}	

	public static String getOperatingSystemArchitecture() {

		return System.getProperty("os.arch");
	}	

	public static String getUserName() {

		return System.getProperty("user.name");
	}	

	public static boolean isOperatingSystemLinux() {

		String os = getOperatingSystem();

		if (os.equals("Linux")) {
			return true;
		}
		return false;
	}	

	public static boolean isOperatingSystemMacOs() {

		String os = getOperatingSystem();

		if (os.equals("Mac OS X")) {
			return true;
		}
		return false;
	}	

	public static String getLocalHostName() {

		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static String getLocalHostIpAddress() {

		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}

	}

}
