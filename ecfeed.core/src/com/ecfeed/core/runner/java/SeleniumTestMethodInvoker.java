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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;

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

		String browserDriver = decodeDriverPath(fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER));
		if (StringHelper.isNullOrEmpty(browserDriver)) {
			return;
		}

		String webBrowser = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER);
		if (!StringHelper.isNullOrEmpty(webBrowser)) {
			setDriver(webBrowser, browserDriver);
		}

		String startupPage = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_START_URL);
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
			processOneArgument(methodParameterNode, argument);
		}		
	}

	private void processOneArgument(
			MethodParameterNode methodParameterNode, String argument) {
		String parameterName = methodParameterNode.getName();

		if (processCmdSetDriver(parameterName, argument)) {
			return;
		}
		if (processCmdGoToPage(parameterName, argument)) {
			return;
		}
		if (processCmdSetInputById(methodParameterNode, argument)) {
			return;
		}
		if (processCmdClickButtonById(parameterName, argument)) {
			return;
		}
		if (processCmdWait(methodParameterNode, argument)) {
			return;
		}
		if (processCmdPageAddress(methodParameterNode, argument)) {
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
		if (driverName == null) {
			reportException("WebDriver name is empty.");
			return;
		}
		if (driverName.equals(NodePropertyDefs.browserNameChrome())) {
			System.setProperty("webdriver.chrome.driver", driverProperty);
			fDriver = new ChromeDriver();
			return;
		}
		if (driverName.equals(NodePropertyDefs.browserNameFirefox())) {
			System.setProperty("webdriver.firefox.driver", driverProperty);
			fDriver = new FirefoxDriver();
			return;
		}		
		if (driverName.equals(NodePropertyDefs.browserNameIExplorer())) {
			System.setProperty("webdriver.ie.driver", driverProperty);
			fDriver = new InternetExplorerDriver();
			return;
		}		
		if (driverName.equals(NodePropertyDefs.browserNameOpera())) {
			System.setProperty("webdriver.opera.driver", driverProperty);
			fDriver = new OperaDriver();
			return;
		}
		if (driverName.equals(NodePropertyDefs.browserNameSafari())) {
			System.setProperty("webdriver.safari.driver", driverProperty);
			fDriver = new SafariDriver();
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

	private boolean processCmdSetInputById(MethodParameterNode methodParameterNode, String argument) {
		String parameterType 
		= methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_PARAMETER_TYPE);
		if (!NodePropertyDefs.isElementTypePageElement(parameterType)) {
			return false;
		}

		String findByType 
		= methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT);

		String findByValue 
		= methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT);

		WebElement webElement = findWebElement(findByType, findByValue);
		if (webElement == null) {
			return false;
		}

		// TODO add various actions
		webElement.sendKeys(argument);
		return true;
	}

	private WebElement findWebElement(String findByType, String findByValue) {
		// TODO add various types of By
		checkWebDriver();
		return fDriver.findElement(By.id(findByValue));
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

	private boolean processCmdWait(MethodParameterNode methodParameterNode, String argument) {

		String parameterType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_PARAMETER_TYPE);
		if (!NodePropertyDefs.isElementTypeWaitTime(parameterType)) {
			return false;
		}

		Float timeInSeconds = Float.parseFloat(argument);
		try {

			Thread.sleep((long)(1000 * timeInSeconds));
		} catch (InterruptedException e) {
		}

		return true;
	}

	private boolean processCmdPageAddress(MethodParameterNode methodParameterNode, String argument) {	
		String parameterType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_PARAMETER_TYPE);
		if (!NodePropertyDefs.isElementTypePageUrl(parameterType)) {
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
