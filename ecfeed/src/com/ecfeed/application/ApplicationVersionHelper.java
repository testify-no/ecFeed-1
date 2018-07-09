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

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.ecfeed.core.utils.StringHelper;


public class ApplicationVersionHelper {

	private static String fMainBundleName = "com.ecfeed";

	public static void setMainBundleName(String mainBundleName) {

		fMainBundleName = mainBundleName;
	}

	public static String getMainBundleName() {

		return fMainBundleName;
	}

	public static String getRapVersion() {

		Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
		Dictionary<String, String> headers = bundle.getHeaders();
		return headers.get( Constants.BUNDLE_VERSION );
	}

	public static String getEcFeedVersion() {

		Bundle bundle = Platform.getBundle(getMainBundleName());

		if (bundle == null) {
			return null;
		}

		String version = bundle.getVersion().toString();
		version = StringHelper.removeFromPostfix(".qualifier", version);

		return version;
	}

	public static boolean isThisNewerVersion(String thisVersion, String versionToCompare) {

		String formattedThisVersion = formatVersion(thisVersion);
		String formattedVersionToCompare = formatVersion(versionToCompare);

		if (formattedThisVersion == null) {
			return false;
		}

		int result = formattedThisVersion.compareTo(formattedVersionToCompare);
		if (result >= 1) {
			return true;
		}

		return false;
	}

	public static String formatVersion(String version) {

		String[] subVersions = StringHelper.splitIntoTokens(version,"\\.");

		if (subVersions.length < 3) {
			return null;
		}

		for (int index = 0; index < 3; index++) {
			subVersions[index] = formatSubversion(subVersions[index]);
		}

		return subVersions[0] + "." + subVersions[1] + "." + subVersions[2];
	}

	private static String formatSubversion(String subVersion) {
		return StringHelper.insertZerosToLength(subVersion, 3);
	}

}
