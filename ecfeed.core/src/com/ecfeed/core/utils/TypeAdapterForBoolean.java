package com.ecfeed.core.utils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.adapter.ITypeAdapter;

public class TypeAdapterForBoolean implements ITypeAdapter<Boolean>{

	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING
	};

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_BOOLEAN).contains(type);
	}

	// ADR-REF - use string helper
	public String convert(String value, boolean isRandomized){
		if(value.toLowerCase().equals(JavaTypeHelper.VALUE_REPRESENTATION_TRUE.toLowerCase())){
			return JavaTypeHelper.VALUE_REPRESENTATION_TRUE;
		}
		else if(value.toLowerCase().equals(JavaTypeHelper.VALUE_REPRESENTATION_FALSE.toLowerCase())){
			return JavaTypeHelper.VALUE_REPRESENTATION_FALSE;
		};
		return null;
	}

	@Override
	public String getDefaultValue() {
		return JavaTypeHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
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
