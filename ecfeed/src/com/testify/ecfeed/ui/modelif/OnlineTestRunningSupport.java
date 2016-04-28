package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class OnlineTestRunningSupport extends AbstractOnlineSupport{

	public OnlineTestRunningSupport(MethodNode target,
			ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(target, testMethodInvoker, fileInfoProvider, runOnAndroid);
	}

	public OnlineTestRunningSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, boolean runOnAndroid) {
		super(testMethodInvoker, fileInfoProvider, runOnAndroid);
	}
}
