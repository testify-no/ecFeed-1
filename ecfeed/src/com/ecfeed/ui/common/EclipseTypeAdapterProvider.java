/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import java.util.Arrays;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;

public class EclipseTypeAdapterProvider implements ITypeAdapterProvider{

	private final String USER_TYPE = "USER_TYPE";
	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING
	};
	private final String[] TYPES_CONVERTABLE_TO_NUMBERS = new String[]{
			JavaTypeHelper.TYPE_NAME_INT, 
			JavaTypeHelper.TYPE_NAME_FLOAT, 
			JavaTypeHelper.TYPE_NAME_DOUBLE, 
			JavaTypeHelper.TYPE_NAME_LONG, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_BYTE, 
			JavaTypeHelper.TYPE_NAME_CHAR
	};
	private final String[] TYPES_CONVERTABLE_TO_STRING = new String[]{
			JavaTypeHelper.TYPE_NAME_INT, 
			JavaTypeHelper.TYPE_NAME_FLOAT, 
			JavaTypeHelper.TYPE_NAME_DOUBLE, 
			JavaTypeHelper.TYPE_NAME_LONG, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_BYTE,
			JavaTypeHelper.TYPE_NAME_CHAR,
			JavaTypeHelper.TYPE_NAME_BOOLEAN,
			USER_TYPE
	};
	private final String[] TYPES_CONVERTABLE_TO_USER_TYPE = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING 
	};
	private final String[] TYPES_CONVERTABLE_TO_CHAR = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_BYTE,
			JavaTypeHelper.TYPE_NAME_INT
	};

	private class BooleanTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_BOOLEAN).contains(type);
		}

		public String convert(String value){
			if(value.toLowerCase().equals(CommonConstants.BOOLEAN_TRUE_STRING_REPRESENTATION.toLowerCase())){
				return CommonConstants.BOOLEAN_TRUE_STRING_REPRESENTATION;
			}
			else if(value.toLowerCase().equals(CommonConstants.BOOLEAN_FALSE_STRING_REPRESENTATION.toLowerCase())){
				return CommonConstants.BOOLEAN_FALSE_STRING_REPRESENTATION;
			};
			return null;
		}

		@Override
		public String defaultValue() {
			return CommonConstants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		}

		@Override
		public boolean isNullAllowed() {
			return false;
		}
	}

	private class StringTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_STRING).contains(type);
		}

		public String convert(String value){
			return value;
		}

		@Override
		public String defaultValue() {
			return CommonConstants.DEFAULT_EXPECTED_STRING_VALUE;
		}

		@Override
		public boolean isNullAllowed() {
			return true;
		}
	}

	private class UserTypeTypeAdapter implements ITypeAdapter{

		private String fType;

		public UserTypeTypeAdapter(String type){
			fType = type;
		}

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_USER_TYPE).contains(type);
		}

		public String convert(String value){
			return JavaLanguageHelper.isValidJavaIdentifier(value) ? value : null;
		}

		@Override
		public String defaultValue() {
			return EclipseTypeHelper.getDefaultExpectedValue(fType);
		}

		@Override
		public boolean isNullAllowed() {
			return true;
		}
	}

	private class CharTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_CHAR).contains(type);
		}

		public String convert(String value){			
			if(value.length() == 1){
				return value;
			}

			String avalue = value;
			if(value.length() > 1 && value.charAt(0) == '\\'){
				avalue = value.substring(1);
			}

			try{
				int number = Integer.parseInt(avalue);
				return  String.valueOf(Character.toChars(number));
			} 
			catch(NumberFormatException e){
			}
			catch(IllegalArgumentException i){	
			}

			return null;
		}

		@Override
		public String defaultValue() {
			return CommonConstants.DEFAULT_EXPECTED_CHAR_VALUE;
		}

		@Override
		public boolean isNullAllowed() {
			return false;
		}
	}

	private abstract class NumericTypeAdapter implements ITypeAdapter{

		private String[] NUMERIC_SPECIAL_VALUES = new String[]{
				CommonConstants.MAX_VALUE_STRING_REPRESENTATION,
				CommonConstants.MIN_VALUE_STRING_REPRESENTATION
		};

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
		}

		@Override
		public String convert(String value){
			return Arrays.asList(NUMERIC_SPECIAL_VALUES).contains(value) ? value : null;
		}

		@Override
		public String defaultValue(){
			return CommonConstants.DEFAULT_EXPECTED_NUMERIC_VALUE;
		}

		@Override
		public boolean isNullAllowed() {
			return false;
		}
	}

	private abstract class FloatingPointTypeAdapter extends NumericTypeAdapter{
		private String[] FLOATING_POINT_SPECIAL_VALUES = new String[]{
				CommonConstants.POSITIVE_INFINITY_STRING_REPRESENTATION,
				CommonConstants.NEGATIVE_INFINITY_STRING_REPRESENTATION
		};

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
		}

		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				result = Arrays.asList(FLOATING_POINT_SPECIAL_VALUES).contains(value) ? value : null;
			}
			return result;
		}

		@Override
		public String defaultValue(){
			return CommonConstants.DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
		}
	}

	private class FloatTypeAdapter extends FloatingPointTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Float.parseFloat(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class DoubleTypeAdapter extends FloatingPointTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Double.parseDouble(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class ByteTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(StringHelper.convertToByte(value));
				}
				catch(NumberFormatException e){
					if(value.length() == 1){
						int charValue = (int)value.charAt(0);
						if((charValue > Byte.MAX_VALUE) == false){
							result = Integer.toString(charValue);
						}
					} else {
						result = null;
					}
				}
			}
			return result;
		}
	}

	private class IntTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(StringHelper.convertToInteger(value));
				}
				catch(NumberFormatException e){
					if(value.length() == 1){
						result = Integer.toString((int)value.charAt(0));
					} else {
						result = null;
					}
				}
			}
			return result;
		}
	}

	private class LongTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(StringHelper.convertToLong(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class ShortTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(StringHelper.convertToShort(value));
				}
				catch(NumberFormatException e){
					if(value.length() == 1){
						int charValue = (int)value.charAt(0);
						if((charValue > Short.MAX_VALUE) == false){
							result = Integer.toString(charValue);
						}
					} else {
						result = null;
					}
				}
			}
			return result;
		}
	}

	public ITypeAdapter getAdapter(String type){
		if(JavaTypeHelper.isJavaType(type) == false){
			type = USER_TYPE;
		}
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return new BooleanTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return new ByteTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return new CharTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return new DoubleTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return new FloatTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_INT:
			return new IntTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_LONG:
			return new LongTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return new ShortTypeAdapter();
		case JavaTypeHelper.TYPE_NAME_STRING:
			return new StringTypeAdapter();
		default:
			return new UserTypeTypeAdapter(type);
		}
	}
}
