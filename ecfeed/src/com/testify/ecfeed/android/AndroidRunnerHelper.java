package com.testify.ecfeed.android;

import com.testify.ecfeed.android.project.AndroidManifestAccessor;
import com.testify.ecfeed.generators.api.EcException;

public class AndroidRunnerHelper {

	public static String getDefaultBaseAndroidRunnerName() {
		return "android.test.InstrumentationTestRunner";
	}

	public static String getEcFeedTestRunnerName() {
		return "EcFeedTestRunner";
	}

	public static String getEcFeedTestRunnerPrefix() {
		return "ecFeed.android";
	}

	public static String createFullAndroidRunnerName(String projectPath) throws EcException {
		String testingAppPackage = getTestingAppPackage(projectPath);

		if (testingAppPackage == null) {
			return null;
		}

		return testingAppPackage + "/" + qualifiedRunnerName(testingAppPackage); 
	}

	public static String createAndroidRunnerName(String projectPath) throws EcException {
		String testingAppPackage = getTestingAppPackage(projectPath);

		if (testingAppPackage == null) {
			return null;
		}

		return qualifiedRunnerName(testingAppPackage);
	}

	private static String getTestingAppPackage(String projectPath) throws EcException {
		String testingAppPackage = new AndroidManifestAccessor(projectPath).getTestingAppPackage();

		if (testingAppPackage == null || testingAppPackage.isEmpty()) {
			return null;
		}

		return testingAppPackage;
	}

	private static String qualifiedRunnerName(String testingAppPackage) {
		return testingAppPackage + "." + 
				getEcFeedTestRunnerPrefix() + "." + 
				getEcFeedTestRunnerName();
	}
}
