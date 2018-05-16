package com.ecfeed.core.utils;

import java.util.Arrays;

import nl.flotsam.xeger.Xeger;

import com.ecfeed.core.adapter.ITypeAdapter;

public class TypeAdapterForString implements ITypeAdapter<String>{

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
			TypeAdapterHelper.USER_TYPE
	};

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_STRING).contains(type);
	}

	public String convert(String value){
		return value;
	}

	@Override
	public String getDefaultValue() {
		return JavaTypeHelper.DEFAULT_EXPECTED_STRING_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return true;
	}

	@Override
	public String generateValue(String regex) {
		return new Xeger(regex).generate();
	}

	@Override
	public String generateValueAsString(String range) {
		return generateValue(range);
	}
}
