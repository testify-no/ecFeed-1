package com.ecfeed.core.utils;

import java.util.Arrays;

public abstract class TypeAdapterFloatingPoint<T extends Number> extends TypeAdapterForNumeric<T>{

	private String[] FLOATING_POINT_SPECIAL_VALUES = new String[]{
			CommonConstants.POSITIVE_INFINITY_STRING_REPRESENTATION,
			CommonConstants.NEGATIVE_INFINITY_STRING_REPRESENTATION
	};

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
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
	public String getDefaultValue(){
		return CommonConstants.DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	}
}

