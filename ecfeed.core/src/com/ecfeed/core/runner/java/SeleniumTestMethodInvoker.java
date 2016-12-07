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
import com.ecfeed.core.model.NodePropertyDefElemType;
import com.ecfeed.core.model.NodePropertyDefFindByType;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class SeleniumTestMethodInvoker implements ITestMethodInvoker {

	private final String ATTR_VALUE = "value";
	private final String ATTR_TYPE = "type";

	private final String TYPE_CHECKBOX = "checkbox";
	private final String TYPE_RADIO = "radio";

	private final String TAG_SELECT = "select";
	private final String TAG_OPTION = "option";

	WebDriver fDriver;
	String fStartupPage = null;
	boolean fBrowserDefined = false;
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
			Object[] choiceNames,
			String argumentsDescription) throws RuntimeException {

		try {
			fArgumentsDescription = argumentsDescription;
			fBrowserDefined = false;
			processStartupProperties();
			processArguments(arguments, choiceNames, argumentsDescription);
		} finally {
			if (fDriver != null) {
				fDriver.quit();
			}
		}
	}

	private void processStartupProperties() {

		boolean mapStartUrlToParam 
		= fMethodNode.getPropertyValueBoolean(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM);

		if (!mapStartUrlToParam) {
			fStartupPage = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_START_URL);
		}

		boolean mapBrowserToParam 
		= fMethodNode.getPropertyValueBoolean(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM);

		if (!mapBrowserToParam) {
			processWebBrowserProperty();
		}
	}

	private void processWebBrowserProperty() {

		String browserDriver = decodeDriverPath(fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER));
		if (StringHelper.isNullOrEmpty(browserDriver)) {
			return;
		}

		String webBrowser = fMethodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER);
		if (!StringHelper.isNullOrEmpty(webBrowser)) {
			setDriver(webBrowser, browserDriver);
			goToPage(fStartupPage);
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

	private void processArguments(Object[] arguments, Object[] choiceNames, String argumentsDescription) {

		for (int cnt = 0; cnt < fMethodNode.getParametersCount(); ++cnt) {
			MethodParameterNode methodParameterNode = fMethodParameters.get(cnt);

			Object argument = arguments[cnt];
			if (argument == null) {
				ExceptionHelper.reportRuntimeException("Argument: " + cnt+1 + " of parameter: " + methodParameterNode.getName() + " must not be null.");
			}

			String argumentStr = arguments[cnt].toString();
			String choiceName = choiceNames[cnt].toString();
			processOneArgument(methodParameterNode, argumentStr, choiceName);
		}		
	}

	private void processOneArgument(
			MethodParameterNode methodParameterNode, String argument, String choiceName) {

		if (processPageElement(methodParameterNode, argument)) {
			return;
		}
		if (processCmdWait(methodParameterNode, argument)) {
			return;
		}
		if (processWebBrowser(methodParameterNode, argument, choiceName)) {
			return;
		}		
		if (processPageAddress(methodParameterNode, argument)) {
			return;
		}
	}

	private void setDriverIntr(String driverName, String driverProperty) {

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
			System.setProperty("webdriver.gecko.driver", driverProperty);
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

	private void setDriver(String driverName, String driverProperty) {

		setDriverIntr(driverName, driverProperty);
		fBrowserDefined = true;
	}

	private boolean processPageElement(MethodParameterNode methodParameterNode, String argument) {

		String elementType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);

		if (processTextElement(elementType, methodParameterNode, argument)) {
			return true;
		}

		if (processCheckBox(elementType, methodParameterNode, argument)) {
			return true;
		}		

		if (processSelect(elementType, methodParameterNode, argument)) {
			return true;
		}

		if (processRadioButton(elementType, methodParameterNode, argument)) {
			return true;
		}		

		if (processButton(elementType, methodParameterNode, argument)) {
			return true;
		}

		if (processGenericPageElement(methodParameterNode, argument)) {
			return true;
		}

		return false;
	}

	private boolean processTextElement(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isText(elementType)) {
			return false;
		}

		WebElement webElement = findWebElement(methodParameterNode);

		if (!methodParameterNode.isExpected()) {
			performActionClearAndSendKeys(webElement, argument);
			return true;
		}

		String currentText = webElement.getText();
		if (currentText.equals(argument)) {
			return true;
		}

		reportException("Text does not match. Expected: " + argument + " Current: " + currentText);
		return true;
	}

	private void performActionClearAndSendKeys(WebElement webElement, String argument) {
		webElement.clear();
		webElement.sendKeys(argument);
	}

	private boolean processCheckBox(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isCheckbox(elementType)) {
			return false;
		}

		WebElement webElement = findWebElement(methodParameterNode);
		String type = webElement.getAttribute(ATTR_TYPE);

		if (!StringHelper.stringsEqualWithNulls(type, TYPE_CHECKBOX)) {
			return false;
		}

		boolean isAction = BooleanHelper.parseBoolean(argument);

		if (isAction) {
			webElement.click();
		}
		return true;
	}

	private boolean processRadioButton(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isRadio(elementType)) {
			return false;
		}

		String findByValue 
		= methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT);

		List<WebElement> allOptions = fDriver.findElements(By.name(findByValue));

		for (WebElement option : allOptions) {
			if (clickRadioButton(option, argument)) {
				break;
			}
		}

		return true;
	}	

	private boolean clickRadioButton(WebElement option, String argument) {

		String type = option.getAttribute(ATTR_TYPE);
		if (!StringHelper.stringsEqualWithNulls(type, TYPE_RADIO)) {
			return false;
		}

		String value = option.getAttribute(ATTR_VALUE); 
		if (!StringHelper.stringsEqualWithNulls(value, argument)) {
			return false;
		}

		option.click();
		return true;
	}

	private boolean processSelect(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isSelect(elementType)) {
			return false;
		}

		WebElement selectWebElement = findWebElement(methodParameterNode);
		String tagName = selectWebElement.getTagName();

		if (!StringHelper.stringsEqualWithNulls(tagName, TAG_SELECT)) {
			return false;
		}

		List<WebElement> allOptions = selectWebElement.findElements(By.tagName(TAG_OPTION));
		for (WebElement option : allOptions) {
			String text = option.getText();

			if (StringHelper.stringsEqualWithNulls(text, argument)) {
				option.click();
				break;
			}
		}

		return true;
	}	

	private boolean processButton(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isButton(elementType)) {
			return false;
		}

		WebElement webElement = findWebElement(methodParameterNode);

		if (BooleanHelper.parseBoolean(argument)) {
			webElement.click();
		}

		return true;
	}

	private boolean processGenericPageElement(MethodParameterNode methodParameterNode, String argument) {

		String elementType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);

		if (!NodePropertyDefElemType.isPageElement(elementType)) {
			return false;
		}

		WebElement webElement = findWebElement(methodParameterNode);

		if (!performAction(webElement, argument, methodParameterNode)) {
			return false; 
		}

		return true;
	}

	private WebElement findWebElement(MethodParameterNode methodParameterNode, String defaultFindByType) {

		String findByType;

		if (defaultFindByType == null) {
			findByType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT);
		} else {
			findByType = defaultFindByType;
		}
		String findByValue = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT);

		WebElement webElement = findWebElementBy(findByType, findByValue);

		if (webElement == null) {
			ExceptionHelper.reportRuntimeException("Can not find web element.");
		}

		return webElement;
	}

	private WebElement findWebElement(MethodParameterNode methodParameterNode) {
		return findWebElement(methodParameterNode, null);
	}

	private WebElement findWebElementBy(String findByType, String findByValue) {

		checkWebDriver();

		if (NodePropertyDefFindByType.isId(findByType)) {
			return fDriver.findElement(By.id(findByValue));
		}

		if (NodePropertyDefFindByType.isClassName(findByType)) {
			return fDriver.findElement(By.className(findByValue));
		}

		if (NodePropertyDefFindByType.isTagName(findByType)) {
			return fDriver.findElement(By.tagName(findByValue));
		}

		if (NodePropertyDefFindByType.isTagName(findByType)) {
			return fDriver.findElement(By.tagName(findByValue));
		}		

		if (NodePropertyDefFindByType.isName(findByType)) {
			return fDriver.findElement(By.name(findByValue));
		}

		if (NodePropertyDefFindByType.isLinkText(findByType)) {
			return fDriver.findElement(By.linkText(findByValue));
		}

		if (NodePropertyDefFindByType.isPartialLinkText(findByType)) {
			return fDriver.findElement(By.partialLinkText(findByValue));
		}		

		if (NodePropertyDefFindByType.isCssSelector(findByType)) {
			return fDriver.findElement(By.cssSelector(findByValue));
		}		

		if (NodePropertyDefFindByType.isXPath(findByType)) {
			return fDriver.findElement(By.xpath(findByValue));
		}		
		return null;
	}

	private boolean performAction(WebElement webElement, String argument, MethodParameterNode methodParameterNode) {

		String action = 
				methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ACTION);

		if (action == null) {
			ExceptionHelper.reportRuntimeException("Action is undefined.");
		}

		if (NodePropertyDefs.isActionSendKeys(action)) {
			webElement.sendKeys(argument);
			return true;
		}

		if (NodePropertyDefs.isActionClick(action)) {
			webElement.click();
			return true;
		}		

		if (NodePropertyDefs.isActionSubmit(action)) {
			webElement.submit();
			return true;
		}		

		return false;
	}

	private boolean processCmdWait(MethodParameterNode methodParameterNode, String argument) {

		String parameterType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		if (!NodePropertyDefElemType.isDelay(parameterType)) {
			return false;
		}

		Float timeInSeconds = Float.parseFloat(argument);
		try {

			Thread.sleep((long)(1000 * timeInSeconds));
		} catch (InterruptedException e) {
		}

		return true;
	}

	private boolean processWebBrowser(MethodParameterNode methodParameterNode, String argument, String choiceName) {

		String elementType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		boolean isElementTypeBrowser = NodePropertyDefElemType.isBrowser(elementType);

		if (!isElementTypeBrowser) {
			return false;
		}
		if (fBrowserDefined) {
			ExceptionHelper.reportRuntimeException(
					"Web browser was already defined. Can not redefine it in parameter: " + methodParameterNode.getName());
		}
		if (!NodePropertyDefs.isValidBrowser(choiceName)) {
			ExceptionHelper.reportRuntimeException("Invalid web browser name: " + choiceName);
		}
		String driverPath = decodeDriverPath(argument);
		setDriver(choiceName, driverPath);

		if (fStartupPage != null) {
			goToPage(fStartupPage);
		}

		return true;
	}

	private void goToPage(String url) {

		if (url == null) {
			return;
		}
		checkWebDriver();
		fDriver.get(url);
	}	

	private boolean processPageAddress(MethodParameterNode methodParameterNode, String argument) {

		String parameterType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		if (!NodePropertyDefElemType.isPageUrl(parameterType)) {
			return false;
		}

		if (methodParameterNode.isExpected()) {
			processExpectedPageAddress(argument);
		} else {
			goToPage(argument);
		}

		return true;
	}

	private void processExpectedPageAddress(String argument) {

		checkWebDriver();
		String currentUrl = fDriver.getCurrentUrl();

		final String HTTP_POSTFIX = "?";
		currentUrl = StringHelper.removeFromPostfix(HTTP_POSTFIX, currentUrl);

		if (currentUrl.equals(argument)) {
			return;
		}

		reportException("Page address does not match. Expected: " + argument + " Current: " + currentUrl);
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

}
