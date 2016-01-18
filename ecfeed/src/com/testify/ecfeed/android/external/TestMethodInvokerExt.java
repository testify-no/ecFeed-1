/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.external;

import com.testify.ecfeed.core.runner.ITestMethodInvoker;

public class TestMethodInvokerExt {

	public static ITestMethodInvoker createInvoker(final String androidRunner) {
		final IAndroidFactoryExt androidFactory = AndroidFactoryDistributorExt.getFactory();
		return androidFactory.createTestMethodInvoker(androidRunner);
	}
}
