/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.runner.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class SeleniumTestMethodInvoker implements ITestMethodInvoker {

	WebDriver fDriver;
	MethodNode fMethodNode;
	List<MethodParameterNode> fMethodParameters;
	ArrayList<TestCaseNode> fTestCaseNodes;
	String fArgumentsDescription;

	@Override
	public boolean isClassInstanceRequired() {
		return false;	
	}	

	public SeleniumTestMethodInvoker(MethodNode methodNode) {
		fDriver = null;
		fMethodNode = methodNode;
		fMethodParameters = fMethodNode.getMethodParameters();
		fArgumentsDescription = null;
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			String argumentsDescription) throws RuntimeException {

		try {
			fArgumentsDescription = argumentsDescription;
			processStartupProperties();
			processArguments(arguments, argumentsDescription);
		} finally {
			if (fDriver != null) {
				fDriver.quit();
			}
		}
	}

	private void processStartupProperties() {

		String browserDriver = decodeDriverPath(fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.BROWSER_DRIVER));
		if (StringHelper.isNullOrEmpty(browserDriver)) {
			return;
		}

		String webBrowser = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.WEB_BROWSER);
		if (!StringHelper.isNullOrEmpty(webBrowser)) {
			setDriver(webBrowser, browserDriver);
		}

		String startupPage = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.START_URL);
		if (!StringHelper.isNullOrEmpty(startupPage)) {
			goToPage(startupPage);
		}
	}

	private String decodeDriverPath(String driverPath) {

		String envWithBoundaries = StringHelper.getSubstringWithBoundaries(driverPath, '%');
		if (envWithBoundaries == null) {
			return driverPath;
		}

		String env = StringHelper.removeStrgAtEnd("%", StringHelper.removePrefix("%", envWithBoundaries));

		String path = System.getenv(env);
		if (path == null) {
			return null;
		}

		return driverPath.replace(envWithBoundaries, path);
	}

	private void processArguments(Object[] arguments, String argumentsDescription) {
		for (int cnt = 0; cnt < fMethodNode.getParametersCount(); ++cnt) {
			MethodParameterNode methodParameterNode = fMethodParameters.get(cnt);
			String argument = arguments[cnt].toString();
			processOneArgument(methodParameterNode.getName(), argument);
		}		
	}

	private void processOneArgument(String parameterName, String argument) {
		if (processCmdSetDriver(parameterName, argument)) {
			return;
		}
		if (processCmdGoToPage(parameterName, argument)) {
			return;
		}
		if (processCmdSetInputById(parameterName, argument)) {
			return;
		}
		if (processCmdClickButtonById(parameterName, argument)) {
			return;
		}
		if (processCmdWait(parameterName, argument)) {
			return;
		}
		if (processCmdPageAddress(parameterName, argument)) {
			return;
		}
	}

	private boolean processCmdSetDriver(String parameterName, String argument) {
		final String CMD_SET_DRIVER = "SET_DRIVER";

		if (!parameterName.equals(CMD_SET_DRIVER)) {
			return false;
		}

		if (argument.endsWith("chromedriver")) { // TODO REFACTOR
			setDriver("chromedriver", argument);
			return true;
		}

		return true;
	}

	private void setDriver(String driverName, String driverProperty) {
		if (StringHelper.stringsEqualWithNulls(driverName, "Chrome")) {
			System.setProperty("webdriver.chrome.driver", driverProperty);
			fDriver = new ChromeDriver();
			return;
		}

		reportException("WebDriver is not supported: " + driverName);

	}

	private boolean processCmdGoToPage(String parameterName, String argument) {
		final String CMD_GO_TO_PAGE = "GO_TO_PAGE";

		if (!parameterName.startsWith(CMD_GO_TO_PAGE)) {
			return false;
		}
		goToPage(argument);
		return true;
	}

	private void goToPage(String url) {
		checkWebDriver();
		fDriver.get(url);
	}

	private boolean processCmdSetInputById(String parameterName, String argument) {
		final String CMD_SET_INPUT_BY_ID = "SET_INPUT_BY_ID";

		if (!parameterName.startsWith(CMD_SET_INPUT_BY_ID)) {
			return false;
		}

		String id = getParameterLabel(parameterName, CMD_SET_INPUT_BY_ID);
		checkWebDriver();
		WebElement webElement = fDriver.findElement(By.id(id));
		webElement.sendKeys(argument);
		return true;
	}	

	private boolean processCmdClickButtonById(String parameterName, String argument) {
		final String CMD_CLICK_BUTTON_BY_ID = "CLICK_BUTTON_BY_ID";

		if (!parameterName.startsWith(CMD_CLICK_BUTTON_BY_ID)) {
			return false;
		}

		if (argument.equals("true")) {
			String id = getParameterLabel(parameterName, CMD_CLICK_BUTTON_BY_ID);
			checkWebDriver();
			WebElement webElement = fDriver.findElement(By.id(id));
			webElement.click();
		}

		return true;
	}	

	private boolean processCmdWait(String parameterName, String argument) {
		final String CMD_WAIT = "WAIT";

		if (!parameterName.startsWith(CMD_WAIT)) {
			return false;
		}

		Float timeInSeconds = Float.parseFloat(argument);
		try {

			Thread.sleep((long)(1000 * timeInSeconds));
		} catch (InterruptedException e) {
		}

		return true;
	}	

	private boolean processCmdPageAddress(String parameterName, String argument) {

		final String CMD_PAGE_ADDRESS = "PAGE_ADDRESS";

		if (!parameterName.equals(CMD_PAGE_ADDRESS)) {
			return false;
		}

		checkWebDriver();
		String currentUrl = fDriver.getCurrentUrl();

		final String HTTP_POSTFIX = "?";
		currentUrl = StringHelper.removeFromPostfix(HTTP_POSTFIX, currentUrl);

		if (currentUrl.equals(argument)) {
			return true;
		}

		reportException("Page address does not match. Expected: " + argument + " Current: " + currentUrl);
		return true;
	}	

	private void checkWebDriver() {
		if (fDriver == null) {
			reportException("Web driver not defined.");
		}
	}

	private void reportException(String message) {
		String exceptionMessage = TestMethodInvokerHelper.createErrorMessage(
				fMethodNode.getName(), fArgumentsDescription, message);
		ExceptionHelper.reportRuntimeException(exceptionMessage);
	}

	private static String getParameterLabel(String parameterName, String prefix) {
		return StringHelper.removePrefix(prefix + "_", parameterName);
	}

}
