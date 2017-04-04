/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.Arrays;

import com.ecfeed.core.adapter.java.AdapterConstants;

public class JavaTypeHelper {

	public static final String TYPE_NAME_BOOLEAN = "boolean";
	public static final String TYPE_NAME_BYTE = "byte";
	public static final String TYPE_NAME_CHAR = "char";
	public static final String TYPE_NAME_DOUBLE = "double";
	public static final String TYPE_NAME_FLOAT = "float";
	public static final String TYPE_NAME_INT = "int";
	public static final String TYPE_NAME_LONG = "long";
	public static final String TYPE_NAME_SHORT = "short";
	public static final String TYPE_NAME_STRING = "String";

	private static final String VALUE_REPRESENTATION_TRUE = "true";
	private static final String VALUE_REPRESENTATION_FALSE = "false";
	private static final String VALUE_REPRESENTATION_MAX = "MAX_VALUE";
	private static final String VALUE_REPRESENTATION_MIN = "MIN_VALUE";
	public static final String VALUE_REPRESENTATION_POSITIVE_INF = "POSITIVE_INFINITY";
	public static final String VALUE_REPRESENTATION_NEGATIVE_INF = "NEGATIVE_INFINITY";


	private static final String[] SUPPORTED_PRIMITIVE_TYPES = new String[]{
		TYPE_NAME_INT,
		TYPE_NAME_BYTE,
		TYPE_NAME_SHORT,
		TYPE_NAME_LONG,
		TYPE_NAME_FLOAT,
		TYPE_NAME_DOUBLE,
		TYPE_NAME_STRING,
		TYPE_NAME_CHAR,
		TYPE_NAME_BOOLEAN
	};

	public static String getTypeName(String cannonicalName) {

		if (cannonicalName.equals(boolean.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_BOOLEAN;
		}
		if (cannonicalName.equals(byte.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_BYTE;
		}
		if (cannonicalName.equals(char.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_CHAR;
		}
		if (cannonicalName.equals(double.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_DOUBLE;
		}
		if (cannonicalName.equals(float.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_FLOAT;
		}
		if (cannonicalName.equals(int.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_INT;
		}
		if (cannonicalName.equals(long.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_LONG;
		}
		if (cannonicalName.equals(short.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_SHORT;
		}
		if (cannonicalName.equals(String.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_STRING;
		}

		return cannonicalName;
	}

	public static boolean isJavaType(String typeName) {

		return Arrays.asList(SUPPORTED_PRIMITIVE_TYPES).contains(typeName);
	}

	public static boolean isUserType(String typeName) {

		if (isJavaType(typeName)) {
			return false;
		}
		return true;
	}

	public static String[] getSupportedJavaTypes() {

		return SUPPORTED_PRIMITIVE_TYPES;
	}

	public static String getStringTypeName() {
		return TYPE_NAME_STRING;
	}

	public static boolean hasLimitedValuesSet(String type){

		return isJavaType(type) == false || type.equals(getBooleanTypeName());
	}

	public static String getBooleanTypeName(){

		return JavaTypeHelper.TYPE_NAME_BOOLEAN;
	}

	public static boolean isStringTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_STRING)) {
			return true;
		}
		return false;
	}

	public static boolean isCharTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_CHAR)) {
			return true;
		}
		return false;
	}	

	public static boolean isBooleanTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_BOOLEAN)) {
			return true;
		}
		return false;
	}	

	public static boolean isByteTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_BYTE)) {
			return true;
		}
		return false;
	}	

	public static boolean isIntTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_INT)) {
			return true;
		}
		return false;
	}	

	public static boolean isShortTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_SHORT)) {
			return true;
		}
		return false;
	}	

	public static boolean isLongTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_LONG)) {
			return true;
		}
		return false;
	}	

	public static boolean isFloatTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_FLOAT)) {
			return true;
		}
		return false;
	}	

	public static boolean isDoubleTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_DOUBLE)) {
			return true;
		}
		return false;
	}	

	public static boolean isFloatingPointTypeName(String typeName) {

		if (isFloatTypeName(typeName)) {
			return true;
		}

		if (isDoubleTypeName(typeName)) {
			return true;
		}

		return false;
	}

	public static boolean isExtendedIntTypeName(String typeName) {

		if (isByteTypeName(typeName)) {
			return true;
		}
		if (isIntTypeName(typeName)) {
			return true;
		}
		if (isLongTypeName(typeName)) {
			return true;
		}		
		if (isShortTypeName(typeName)) {
			return true;
		}
		return false;
	}

	public static boolean isNumericTypeName(String typeName) {

		if (isExtendedIntTypeName(typeName)) {
			return true;
		}
		if (isFloatTypeName(typeName)) {
			return true;
		}
		if (isDoubleTypeName(typeName)) {
			return true;
		}
		return false;
	}

	public static boolean isTypeWithChars(String typeName) {

		if (isCharTypeName(typeName)) {
			return true;
		}
		if (isStringTypeName(typeName)) {
			return true;
		}

		return false;
	}

	public static boolean isTypeComparableForLessGreater(String typeName) {

		if (isNumericTypeName(typeName)) {
			return true;
		}
		if (isTypeWithChars(typeName)) {
			return true;
		}
		return false;
	}

	public static double convertNumericToDouble(String typeName, String value) {

		if (isByteTypeName(typeName)) {
			return Byte.parseByte(value);
		}
		if (isIntTypeName(typeName)) {
			return Integer.parseInt(value);
		}
		if (isShortTypeName(typeName)) {
			return Short.parseShort(value);
		}
		if (isLongTypeName(typeName)) {
			return Long.parseLong(value);
		}		
		if (isFloatTypeName(typeName)) {
			return Float.parseFloat(value);
		}
		if (isDoubleTypeName(typeName)) {
			return Double.parseDouble(value);
		}

		ExceptionHelper.reportRuntimeException("Invalid type in numeric conversion");
		return 0;
	}

	public static Object parseJavaType(String valueString, String typeName) {

		if(typeName == null || valueString == null){
			return null;
		}

		switch(typeName){
		case TYPE_NAME_BOOLEAN:
			return parseBooleanValue(valueString);
		case TYPE_NAME_BYTE:
			return parseByteValue(valueString);
		case TYPE_NAME_CHAR:
			return parseCharValue(valueString);
		case TYPE_NAME_DOUBLE:
			return parseDoubleValue(valueString);
		case TYPE_NAME_FLOAT:
			return parseFloatValue(valueString);
		case TYPE_NAME_INT:
			return parseIntValue(valueString);
		case TYPE_NAME_LONG:
			return parseLongValue(valueString);
		case TYPE_NAME_SHORT:
			return parseShortValue(valueString);
		case TYPE_NAME_STRING:
			return parseStringValue(valueString);
		default:
			return null;
		}
	}

	public static Object parseBooleanValue(String valueString) {

		if(valueString.toLowerCase().equals(VALUE_REPRESENTATION_TRUE.toLowerCase())){
			return true;
		}
		if(valueString.toLowerCase().equals(VALUE_REPRESENTATION_FALSE.toLowerCase())){
			return false;
		}
		return null;
	}	

	private static Object parseByteValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Byte.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Byte.MIN_VALUE;
		}
		try{
			return Byte.parseByte(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseCharValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Character.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Character.MIN_VALUE;
		}
		if (valueString.charAt(0) == '\\') {
			return new Character((char)Integer.parseInt(valueString.substring(1)));
		} else if (valueString.length() == 1) {
			return valueString.charAt(0);
		}
		return null;

	}

	private static Object parseDoubleValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Double.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Double.MIN_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_POSITIVE_INF)){
			return Double.POSITIVE_INFINITY;
		}
		if(valueString.equals(VALUE_REPRESENTATION_NEGATIVE_INF)){
			return Double.NEGATIVE_INFINITY;
		}
		try{
			return Double.parseDouble(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseFloatValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Float.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Float.MIN_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_POSITIVE_INF)){
			return Float.POSITIVE_INFINITY;
		}
		if(valueString.equals(VALUE_REPRESENTATION_NEGATIVE_INF)){
			return Float.NEGATIVE_INFINITY;
		}
		try{
			return Float.parseFloat(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseIntValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Integer.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Integer.MIN_VALUE;
		}
		try{
			return Integer.parseInt(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseLongValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Long.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Long.MIN_VALUE;
		}
		try{
			return Long.parseLong(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseShortValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_MAX)){
			return Short.MAX_VALUE;
		}
		if(valueString.equals(VALUE_REPRESENTATION_MIN)){
			return Short.MIN_VALUE;
		}
		try{
			return Short.parseShort(valueString);
		}
		catch(NumberFormatException e){
			return null;
		}
	}

	private static Object parseStringValue(String valueString) {

		if(valueString.equals(AdapterConstants.VALUE_REPRESENTATION_NULL)){
			return null;
		}
		return valueString;
	}

	public static String convertValueString(String valueString, String typeName) {
		return parseJavaType(valueString, typeName).toString();
	}

	public static String getSubstituteType(String typeName1, String typeName2) {

		if (typeName1 == null || typeName2 == null) {
			return null;
		}

		if (JavaTypeHelper.isBooleanTypeName(typeName1) || JavaTypeHelper.isBooleanTypeName(typeName2)) {
			return TYPE_NAME_BOOLEAN;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName1) && JavaTypeHelper.isTypeWithChars(typeName2)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaTypeHelper.isFloatingPointTypeName(typeName1) || JavaTypeHelper.isFloatingPointTypeName(typeName2)) {
			return TYPE_NAME_DOUBLE;
		}

		return TYPE_NAME_LONG;
	}

	public static String getSubstituteType(String typeName1) {

		if (typeName1 == null) {
			return null;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName1)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaTypeHelper.isFloatingPointTypeName(typeName1)) {
			return TYPE_NAME_DOUBLE;
		}

		if (JavaTypeHelper.isExtendedIntTypeName(typeName1)) {
			return TYPE_NAME_LONG;
		}

		return typeName1;
	}

	public static JustifyType getJustifyType(String typeName) {

		if (!isJavaType(typeName)) {
			return JustifyType.LEFT;
		}

		if (isNumericTypeName(typeName)) {
			return JustifyType.RIGHT;
		}

		return JustifyType.LEFT;
	}


}
