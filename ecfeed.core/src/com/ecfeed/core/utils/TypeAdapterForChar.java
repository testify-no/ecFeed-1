package com.ecfeed.core.utils;

import java.util.Arrays;

import nl.flotsam.xeger.Xeger;

import com.ecfeed.core.adapter.ITypeAdapter;

public class TypeAdapterForChar implements ITypeAdapter<Character>{

	private final String[] TYPES_CONVERTABLE_TO_CHAR = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_BYTE,
			JavaTypeHelper.TYPE_NAME_INT
	};

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_CHAR).contains(type);
	}

	public String convert(String value, boolean isRandomized){

		if (value.length() == 1) {

			if (isRandomized) {
				return generateRange(value);
			}

			return value;
		}

		if (isRandomized) { 
			return generateRange(getDefaultValue());
		}

		return getDefaultValue();
	}

	@Override
	public String getDefaultValue() {
		return JavaTypeHelper.DEFAULT_EXPECTED_CHAR_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return false;
	}

	@Override
	public Character generateValue(String regex) {
		return new Xeger(regex).generate().charAt(0);
	}

	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

	// ADR-REF - move to a common file - ValueFieldHelper ?
	protected String generateRange(String numericValue) {
		return numericValue + ":" + numericValue;
	}

}
