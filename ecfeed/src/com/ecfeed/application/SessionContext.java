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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.editor.ModelEditorHelper;


public class SessionContext {

	private enum ApplicationType {
		LOCAL_PLUGIN,
		LOCAL_STANDALONE,
		REMOTE_RAP
	}

	private static final String ATTRIBUTE_BUNDLE_NAME = "SDS_BUNDLE_NAME";

	private static ApplicationType fApplicationType = getInitialApplicationType();
	private static String fExportFileName;
	private static String fMainBundleName = getInitialBundleName();
	private static ObjectUndoContext fRapObjectUndoContext = null;


	private static ApplicationType getInitialApplicationType() {

		ApplicationType applicationType = ApplicationType.LOCAL_PLUGIN;
		SessionDataStore.setAttribute("SDS_APPLICATION_TYPE", applicationType);
		return applicationType;
	}

	private static ApplicationType getApplicationType() {

		fApplicationType = (ApplicationType)SessionDataStore.getAttribute("SDS_APPLICATION_TYPE");
		return fApplicationType;
	}

	private static void setApplicationType(ApplicationType applicationType) {
		fApplicationType = applicationType;
		SessionDataStore.setAttribute("SDS_APPLICATION_TYPE", applicationType);
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

	private static String getInitialBundleName() {

		String value = "com.ecfeed";
		SessionDataStore.setAttribute(ATTRIBUTE_BUNDLE_NAME, value);
		return value;
	}

	public static void setMainBundleName(String mainBundleName) {

		SessionDataStore.setAttribute(ATTRIBUTE_BUNDLE_NAME, mainBundleName);
		fMainBundleName = mainBundleName;
	}

	public static String getMainBundleName() {

		fMainBundleName = (String)SessionDataStore.getAttribute(ATTRIBUTE_BUNDLE_NAME);
		return fMainBundleName;
	}

	public static String getRapVersion() {

		Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
		Dictionary<String, String> headers = bundle.getHeaders();
		return headers.get( Constants.BUNDLE_VERSION );
	}

	public static void setExportTargetFile(String exportFileName) {

		fExportFileName = exportFileName;
	}

	public static String getExportTargetFile() {

		return fExportFileName;
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

	public static void setRapUndoContextObject(Object obj) {
		fRapObjectUndoContext = new ObjectUndoContext(obj);
	}

	public static IUndoContext getUndoContext() {

		if (isApplicationTypeLocal()) {
			return ModelEditorHelper.getActiveModelEditor().getUndoContext();
		}

		return fRapObjectUndoContext;
	}

}
