package com.ecfeed.core.utils;

import java.util.Arrays;

public abstract class TypeAdapterForNumericType<T extends Number> extends TypeAdapterForTypeWithRange<T> {

	public static final String DELIMITER = ":";		

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
	}

	protected String convertSpecialValue(String value){
		return Arrays.asList(getSpecialValues()).contains(value) ? value : null;
	}

	@Override
	public String getDefaultValue(){
		return JavaTypeHelper.DEFAULT_EXPECTED_NUMERIC_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return false;
	}

	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

}
