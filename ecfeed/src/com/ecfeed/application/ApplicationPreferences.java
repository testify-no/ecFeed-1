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

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;


public class ApplicationPreferences {

	private static String AUTOMATICALLY_CHECK_FOR_UPDATES = "updates.automaticalCheck";
	private static String CHECK_BETA_VERSIONS = "updates.checkBetaVersions";
	private static String IGNORE_UP_TO_VERSION = "updates.ignoreUpToVersion";

	private static String INITIAL_VERSION = "1.0.0";


	public static void setPreferenceValue(String key, String value) {

		Preferences preferences = getPreferenceNode();
		preferences.put(key, value);

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			ErrorDialog.open("Can not write preferences.");
		}
	}

	public static String getPreferenceValue(String key, String defaultValue) {

		return getPreferenceNode().get(key, defaultValue);
	}

	private static Preferences getPreferenceNode() {

		final String PREFERENCES_KEY = "com.ecfeed.application.preferences";

		return InstanceScope.INSTANCE.getNode(PREFERENCES_KEY);
	}

	public static void setBooleanPreferenceValue(String key, boolean value) {

		setPreferenceValue(key, BooleanHelper.toString(value));
	}	

	public static boolean getBooleanPreferenceValue(String key) {

		String value = getPreferenceNode().get(key, "false");
		return BooleanHelper.parseBoolean(value);
	}

	public static boolean getPreferenceAutomaticallyCheckForUpdates() {

		return getBooleanPreferenceValue(AUTOMATICALLY_CHECK_FOR_UPDATES);
	}

	public static void setPreferenceAutomaticallyCheckForUpdates(boolean value) {

		setBooleanPreferenceValue(AUTOMATICALLY_CHECK_FOR_UPDATES, value);
	}	

	public static boolean getPreferenceCheckBetaVersions() {

		return getBooleanPreferenceValue(CHECK_BETA_VERSIONS);
	}

	public static void setPreferenceCheckBetaVersions(boolean value) {

		setBooleanPreferenceValue(CHECK_BETA_VERSIONS, value);
	}	

	public static String getPreferenceIgnoreUpToVersion() {

		return getPreferenceValue(IGNORE_UP_TO_VERSION, INITIAL_VERSION);
	}

	public static void setPreferenceIgnoreUpToVersion(String value) {

		setPreferenceValue(IGNORE_UP_TO_VERSION, value);
	}	

	public static void setPreferenceIgnoreUpToVersionInitial() {

		setPreferenceValue(IGNORE_UP_TO_VERSION, INITIAL_VERSION);
	}

}
