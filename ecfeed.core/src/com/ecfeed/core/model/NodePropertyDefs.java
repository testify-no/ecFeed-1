/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.JavaTypeHelper;


public class NodePropertyDefs {

	static class PropertyDef {
		private String fName;
		private String fType;
		private String fDefaultValue;
		private String[] fPossibleValues;

		PropertyDef(String name, String type, String defaultValue, String[] possibleValues) {
			fName = name;
			fType = type;
			fDefaultValue = defaultValue;
			fPossibleValues = possibleValues;
		}

		String getName() {
			return fName;
		}

		String getType() {
			return fType;
		}

		String getDefaultValue() {
			return fDefaultValue;
		}	

		String[] getPossibleValues() {
			return fPossibleValues;
		}
	}

	private static final String JAVA_RUNNER = "Java Runner";
	private static final String WEB_DRIVER = "Web Runner";

	private static final String FALSE_VALUE = "false";
	private static final String TRUE_VALUE = "true";

	private static final String BROWSER_CHROME = "Chrome";
	private static final String BROWSER_FIREFOX = "Firefox";
	private static final String BROWSER_IEXPLORER = "IExplorer";
	private static final String BROWSER_OPERA = "Opera";
	private static final String BROWSER_SAFARI = "Safari";

	private static final String PAGE_ELEMENT = "Page element";	

	private static String[] falseTrueArray = new String[]{FALSE_VALUE, TRUE_VALUE};

	static PropertyDef runOnAndroid = 
			new PropertyDef("runOnAndroid", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static PropertyDef androidRunner = 
			new PropertyDef("androidRunner", JavaTypeHelper.TYPE_NAME_STRING, null, null);	

	static PropertyDef methodRunner = 
			new PropertyDef(
					"methodRunner", JavaTypeHelper.TYPE_NAME_STRING, JAVA_RUNNER,
					new String[]{JAVA_RUNNER, WEB_DRIVER});

	static PropertyDef mapBrowserToParam =  
			new PropertyDef("mapBrowserToParam", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static PropertyDef webBrowser = 
			new PropertyDef(
					"webBrowser", JavaTypeHelper.TYPE_NAME_STRING, null, 
					new String[]{BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_IEXPLORER, BROWSER_OPERA, BROWSER_SAFARI});

	static PropertyDef browserDriver = new PropertyDef("browserDriver", JavaTypeHelper.TYPE_NAME_STRING, null, null);	

	static PropertyDef mapStartUrlToParam = 
			new PropertyDef("mapStartUrlToParam", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static PropertyDef startUrl = new PropertyDef("startUrl", JavaTypeHelper.TYPE_NAME_STRING, null, null);

	static PropertyDef parameterType = 
			new PropertyDef(
					"parameterType", JavaTypeHelper.TYPE_NAME_STRING, PAGE_ELEMENT,
					new String[]{PAGE_ELEMENT, "Browser" });

	static PropertyDef findByTypeOfElement = 
			new PropertyDef(
					"findByTypeOfElement", JavaTypeHelper.TYPE_NAME_STRING, null,
					new String[]{ "Id", "Class name", "Tag name", "Name", "Link text", "Partial link text", "CSS selector", "Xpath" });

	static PropertyDef findByValueOfElement = new PropertyDef("findByValueOfElement", JavaTypeHelper.TYPE_NAME_STRING, null, null);	


	public enum PropertyId {
		PROPERTY_RUN_ON_ANDROID(0),
		PROPERTY_ANDROID_RUNNER(1),

		PROPERTY_METHOD_RUNNER(2),
		PROPERTY_MAP_BROWSER_TO_PARAM(3),
		PROPERTY_WEB_BROWSER(4),
		PROPERTY_BROWSER_DRIVER(5),
		PROPERTY_MAP_START_URL_TO_PARAM(6),
		PROPERTY_START_URL(7),

		PROPERTY_PARAMETER_TYPE(8),
		PROPERTY_FIND_BY_TYPE_OF_ELEMENT(9),
		PROPERTY_FIND_BY_VALUE_OF_ELEMENT(10);

		private final int fIndex; 

		private PropertyId(int index) {
			fIndex = index;
		}

		private final int getIndex() {
			return fIndex;
		}
	}

	static PropertyDef[] fPropertyDefs = 
		{
		runOnAndroid,
		androidRunner,
		methodRunner,
		mapBrowserToParam,
		webBrowser,
		browserDriver,
		mapStartUrlToParam,
		startUrl,
		parameterType,
		findByTypeOfElement,
		findByValueOfElement
		};

	public static String getPropertyName(PropertyId propertyId) {
		return getDefinition(propertyId).getName();
	}

	public static String getPropertyType(PropertyId propertyId) {
		return getDefinition(propertyId).getType();
	}	

	public static String getPropertyDefaultValue(PropertyId propertyId) {
		return getDefinition(propertyId).getDefaultValue();
	}	

	public static String[] getPropertyPossibleValues(PropertyId propertyId) {
		return getDefinition(propertyId).getPossibleValues();
	}	

	private static PropertyDef getDefinition(PropertyId propertyId) {
		return fPropertyDefs[propertyId.getIndex()];
	}

	public static boolean isSeleniumRunnerMethod(String value) {
		if (value.equals(WEB_DRIVER)) {
			return true;
		}
		return false;
	}

	public static boolean isJavaRunnerMethod(String value) {
		if (value.equals(JAVA_RUNNER)) {
			return true;
		}
		return false;
	}	

	public static String browserNameChrome() {
		return BROWSER_CHROME;
	}

	public static String browserNameFirefox() {
		return BROWSER_FIREFOX;
	}

	public static String browserNameIExplorer() {
		return BROWSER_IEXPLORER;
	}

	public static String browserNameOpera() {
		return BROWSER_OPERA;
	}	

	public static String browserNameSafari() {
		return BROWSER_SAFARI;
	}	

}
