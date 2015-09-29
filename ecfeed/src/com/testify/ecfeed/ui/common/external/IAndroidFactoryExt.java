/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;


public interface IAndroidFactoryExt {

	public final String INTERFACE_NAME = "ANDROID_FACTORY";
	public final String INTERFACE_VERSION = "1.0";

	public IDeviceCheckerExt createDeviceChecker();
	public IApkInstallerExt createApkInstaller();
	public IImplementerExt createImplementer(
			String baseRunner, String projectPath, IClassImplementHelper classImplementHelper);
	public ITestMethodInvokerExt createTestMethodInvoker(String androidRunner);
}
