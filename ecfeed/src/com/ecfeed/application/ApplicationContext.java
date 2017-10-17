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



public class ApplicationContext {

	private enum ApplicationType {
		LOCAL_PLUGIN,
		LOCAL_STANDALONE,
		REMOTE_RAP
	}

	private static ApplicationType fApplicationType = ApplicationType.LOCAL_PLUGIN;

	private static ApplicationType getApplicationType() {

		return fApplicationType;
	}

	private static void setApplicationType(ApplicationType applicationType) {
		fApplicationType = applicationType;
	}

	public static boolean isApplicationTypeLocalStandalone() {

		if (getApplicationType() == ApplicationType.LOCAL_STANDALONE) {
			return true;
		}

		return false;
	}

	public static boolean isApplicationTypeLocalPlugin() {

		if (getApplicationType() == ApplicationType.LOCAL_PLUGIN) {
			return true;
		}

		return false;
	}	

	public static boolean isApplicationTypeLocal() {

		if (isApplicationTypeLocalStandalone()) {
			return true;
		}

		if (isApplicationTypeLocalPlugin()) {
			return true;
		}

		return false;
	}	


	public static boolean isApplicationTypeRemoteRap() {

		if (getApplicationType() == ApplicationType.REMOTE_RAP) {
			return true;
		}

		return false;
	}	

	public static boolean isProjectAvailable() {

		if (isApplicationTypeLocalPlugin()) {
			return true;
		}

		return false;
	}

	public static void setApplicationTypeLocalStandalone() {

		setApplicationType(ApplicationType.LOCAL_STANDALONE);
	}

	public static void setApplicationTypeRemoteRap() {

		setApplicationType(ApplicationType.REMOTE_RAP);
	}	

}
