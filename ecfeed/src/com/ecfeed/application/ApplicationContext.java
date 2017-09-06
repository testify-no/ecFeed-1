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

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.ecfeed.core.utils.StringHelper;


public class ApplicationContext {

	private enum ApplicationType {
		LOCAL_PLUGIN,
		LOCAL_STANDALONE,
		REMOTE_RAP
	}

	static ApplicationType fApplicationType = ApplicationType.LOCAL_PLUGIN;
	static String fExportFileName;

	public static boolean isApplicationTypeLocalStandalone() {

		if (fApplicationType == ApplicationType.LOCAL_STANDALONE) {
			return true;
		}

		return false;
	}

	public static boolean isApplicationTypeLocalPlugin() {

		if (fApplicationType == ApplicationType.LOCAL_PLUGIN) {
			return true;
		}

		return false;
	}	

	public static boolean isApplicationTypeRemoteRap() {

		if (fApplicationType == ApplicationType.REMOTE_RAP) {
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

		fApplicationType = ApplicationType.LOCAL_STANDALONE;
	}

	public static void setApplicationTypeRemoteRap() {

		fApplicationType = ApplicationType.REMOTE_RAP;
	}	

	public static void setExportTargetFile(String exportFileName) {

		fExportFileName = exportFileName;
	}

	public static String getExportTargetFile() {

		return fExportFileName;
	}

	public static String getEcFeedVersion() {
		return getEcFeedVersion("com.ecfeed");	
	}

	public static String getEcFeedVersion(String mainBundleName) {

		Bundle bundle = Platform.getBundle(mainBundleName);

		if (bundle == null) {
			return null;
		}

		String version = bundle.getVersion().toString();
		version = StringHelper.removeFromPostfix(".qualifier", version);

		return version;
	}

}
