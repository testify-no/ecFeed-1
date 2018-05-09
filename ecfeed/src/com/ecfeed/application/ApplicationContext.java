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


public class ApplicationContext {

	private enum ApplicationType {
		LOCAL_PLUGIN,
		LOCAL_STANDALONE,
		REMOTE_RAP
	}

	static ApplicationType fApplicationType = ApplicationType.LOCAL_PLUGIN;
	static String fExportFileName;
//<<<<<<< HEAD
//	static boolean fSimplifiedUI;
//	
//	public static void setSimplifiedUI(boolean value){
//		fSimplifiedUI = value;
//	}
//
//	public static boolean getSimplifiedUI(){
//		return fSimplifiedUI;
//	}
//	public static boolean isStandaloneApplication() {
//=======
	static String fMainBundleName = "com.ecfeed";
	static private ObjectUndoContext fRapObjectUndoContext = null;
	static SectionDecorationsHolder fSectionDecorationsHolder = new SectionDecorationsHolder();
	static boolean fSimplifiedUI;
	
	public static void setSimplifiedUI(boolean value){
		fSimplifiedUI = value;
	}

	public static boolean getSimplifiedUI(){
		return fSimplifiedUI;
	}

	public static boolean isApplicationTypeLocalStandalone() {
//>>>>>>> master

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

	public static SectionDecorationsHolder getSessionDecorationsHolder() {
		return fSectionDecorationsHolder;
	}

}
