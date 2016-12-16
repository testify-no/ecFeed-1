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

			if (StringHelper.isNullOrEmpty(fStartupPage)) {
				reportException("Start URL not defined in properties of the method.", null);
			}
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
			setDriver(webBrowser, browserDriver, null);
			goToPage(fStartupPage, null);
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
				reportException("Argument: " + cnt+1 + " of parameter: " + methodParameterNode.getName() + " must not be null.", null);
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

	private void setDriverIntr(String driverName, String driverProperty, MethodParameterNode methodParameterNode) {

		if (driverName == null) {
			reportException("WebDriver name is empty.", methodParameterNode);
			return;
		}
		if (StringHelper.isEqualIgnoreCase(driverName, NodePropertyDefs.browserNameChrome())) {
			System.setProperty("webdriver.chrome.driver", driverProperty);
			fDriver = new ChromeDriver();
			return;
		}
		if (StringHelper.isEqualIgnoreCase(driverName, NodePropertyDefs.browserNameFirefox())) {
			System.setProperty("webdriver.gecko.driver", driverProperty);
			fDriver = new FirefoxDriver();
			return;
		}		
		if (StringHelper.isEqualIgnoreCase(driverName, NodePropertyDefs.browserNameIExplorer())) {
			System.setProperty("webdriver.ie.driver", driverProperty);
			fDriver = new InternetExplorerDriver();
			return;
		}		
		if (StringHelper.isEqualIgnoreCase(driverName, NodePropertyDefs.browserNameOpera())) {
			System.setProperty("webdriver.opera.driver", driverProperty);
			fDriver = new OperaDriver();
			return;
		}
		if (StringHelper.isEqualIgnoreCase(driverName, NodePropertyDefs.browserNameSafari())) {
			System.setProperty("webdriver.safari.driver", driverProperty);
			fDriver = new SafariDriver();
			return;
		}		

		reportException("WebDriver is not supported: " + driverName + ".", methodParameterNode);
	}

	private void setDriver(String driverName, String driverProperty, MethodParameterNode methodParameterNode) {

		setDriverIntr(driverName, driverProperty, methodParameterNode);
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

		if (methodParameterNode.isExpected()) {
			processExpectedText(methodParameterNode, webElement, argument);
			return true;
		}

		processNormalText(webElement, argument, methodParameterNode);
		return true;
	}

	private void processExpectedText(MethodParameterNode methodParameterNode, WebElement webElement, String argument) {

		if (webElement == null) {
			if (isOptional(methodParameterNode)) {
				return;
			} else {
				reportExceptionEmptyWebElement(methodParameterNode);
			}
		}

		String currentText = webElement.getText();
		if (currentText.equals(argument)) {
			return;
		}

		reportException("Text does not match. Expected: " + argument + " Current: " + currentText + ".", methodParameterNode);
	}

	private void processNormalText(WebElement webElement, String argument, MethodParameterNode methodParameterNode) {

		if (webElement == null) {
			reportExceptionEmptyWebElement(methodParameterNode);
		}

		performActionClearAndSendKeys(webElement, argument);
	}

	private void reportExceptionEmptyWebElement(MethodParameterNode methodParameterNode) {
		reportException("Web element not found.", methodParameterNode);
	}

	private boolean isOptional(MethodParameterNode methodParameterNode) {
		String optionalStr = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL);
		return BooleanHelper.parseBoolean(optionalStr);
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

		if (webElement == null) {
			reportExceptionEmptyWebElement(methodParameterNode);
		}
		String type = webElement.getAttribute(ATTR_TYPE);

		if (!StringHelper.isEqual(type, TYPE_CHECKBOX)) {
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
		if (!StringHelper.isEqual(type, TYPE_RADIO)) {
			return false;
		}

		String value = option.getAttribute(ATTR_VALUE); 
		if (!StringHelper.isEqual(value, argument)) {
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

		if (selectWebElement == null) {
			reportExceptionEmptyWebElement(methodParameterNode);
		}
		String tagName = selectWebElement.getTagName();

		if (!StringHelper.isEqual(tagName, TAG_SELECT)) {
			return false;
		}

		List<WebElement> allOptions = selectWebElement.findElements(By.tagName(TAG_OPTION));
		for (WebElement option : allOptions) {
			String text = option.getText();

			if (StringHelper.isEqual(text, argument)) {
				option.click();
				return true;
			}
		}

		reportException("Option with text: '" + argument + "' not found.", methodParameterNode);
		return true;
	}	

	private boolean processButton(String elementType, MethodParameterNode methodParameterNode, String argument) {

		if (!NodePropertyDefElemType.isButton(elementType)) {
			return false;
		}

		WebElement webElement = findWebElement(methodParameterNode);

		if (webElement == null) {
			reportExceptionEmptyWebElement(methodParameterNode);
		}

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

		if (webElement == null) {
			reportExceptionEmptyWebElement(methodParameterNode);
		}

		if (!performAction(webElement, argument, methodParameterNode)) {
			return false; 
		}

		return true;
	}

	private WebElement findWebElement(MethodParameterNode methodParameterNode) {
		return findWebElement(methodParameterNode, null);
	}

	private WebElement findWebElement(MethodParameterNode methodParameterNode, String defaultFindByType) {

		WebElement webElement = null;
		try {
			webElement = findWebElementThrow(methodParameterNode, defaultFindByType);
		} catch (Exception e) {

		}

		return webElement;
	}

	private WebElement findWebElementThrow(MethodParameterNode methodParameterNode, String defaultFindByType) {

		String findByType;

		if (defaultFindByType == null) {
			findByType = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT);
		} else {
			findByType = defaultFindByType;
		}
		String findByValue = methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT);

		return findWebElementBy(findByType, findByValue);
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
			reportException("Action is undefined.", methodParameterNode);
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
			reportException("Web browser was already defined. Can not redefine.", methodParameterNode);

		}
		if (!NodePropertyDefs.isValidBrowser(choiceName)) {
			reportException("Invalid web browser name: " + choiceName + ".", methodParameterNode);
		}
		String driverPath = decodeDriverPath(argument);
		setDriver(choiceName, driverPath, methodParameterNode);

		if (fStartupPage != null) {
			goToPage(fStartupPage, null);
		}

		return true;
	}

	private void goToPage(String url, MethodParameterNode methodParameterNode) {

		if (url == null) {
			reportException("Url must not be empty.", methodParameterNode);
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
			processExpectedPageAddress(argument, methodParameterNode);
		} else {
			goToPage(argument, methodParameterNode);
		}

		return true;
	}

	private void processExpectedPageAddress(String argument, MethodParameterNode methodParameterNode) {

		checkWebDriver();
		String currentUrl = fDriver.getCurrentUrl();

		final String HTTP_POSTFIX = "?";
		currentUrl = StringHelper.removeFromPostfix(HTTP_POSTFIX, currentUrl);

		if (currentUrl.equals(argument)) {
			return;
		}

		reportException("Page address does not match. Expected: " + argument + " Current: " + currentUrl + ".", methodParameterNode);
	}

	private void checkWebDriver() {

		if (fDriver == null) {
			reportException("Web driver not defined.", null);
		}
	}

	private void reportException(String message, MethodParameterNode methodParameterNode) {

		if (methodParameterNode != null) {
			message = message + " Method parameter: " + methodParameterNode.getName();
		}

		String exceptionMessage = TestMethodInvokerHelper.createErrorMessage(
				fMethodNode.getName(), fArgumentsDescription, message);
		ExceptionHelper.reportRuntimeException(exceptionMessage);
	}

}
