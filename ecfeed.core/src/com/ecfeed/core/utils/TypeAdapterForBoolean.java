package com.ecfeed.core.utils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.adapter.ITypeAdapter;

public class TypeAdapterForBoolean implements ITypeAdapter<Boolean>{
	
	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING
	};
	
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

	@Override
	public Boolean generateValue(String range) {
		return ThreadLocalRandom.current().nextBoolean();
	}

	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

}
