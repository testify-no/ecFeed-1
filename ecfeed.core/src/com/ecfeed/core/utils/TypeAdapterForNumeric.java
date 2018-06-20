package com.ecfeed.core.utils;

import java.util.Arrays;

import com.ecfeed.core.adapter.ITypeAdapter;

public abstract class TypeAdapterForNumeric<T extends Number> implements ITypeAdapter<T>{

	private String[] NUMERIC_SPECIAL_VALUES = new String[]{
			CommonConstants.MAX_VALUE_STRING_REPRESENTATION,
			CommonConstants.MIN_VALUE_STRING_REPRESENTATION
	};

	public static final String DELIMITER = ":";		

	@Override
	public boolean compatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
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
	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

	protected final int getLower(String range) {
		return Integer.parseInt(range.split(DELIMITER)[0]);
	}

	protected final int getUpper(String range) {
		return Integer.parseInt(range.split(DELIMITER)[1]);
	}

}
