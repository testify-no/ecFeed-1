package com.ecfeed.core.utils;

import java.util.Arrays;

import com.ecfeed.core.adapter.ITypeAdapter;

public abstract class TypeAdapterForNumeric<T extends Number> implements ITypeAdapter<T>{

	private String[] NUMERIC_SPECIAL_VALUES = new String[]{
			JavaTypeHelper.VALUE_REPRESENTATION_MAX,
			JavaTypeHelper.VALUE_REPRESENTATION_MIN
	};

	public static final String DELIMITER = ":";		

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
	}

	@Override
	public String convert(String value, boolean isRandomized){
		return Arrays.asList(NUMERIC_SPECIAL_VALUES).contains(value) ? value : null;
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

	protected final int getLower(String range) {
		return Integer.parseInt(range.split(DELIMITER)[0]);
	}

	protected final int getUpper(String range) {
		return Integer.parseInt(range.split(DELIMITER)[1]);
	}

	// ADR-REF - move to a common file - ValueFieldHelper ?
	protected String generateRange(String numericValue) {
		return numericValue + ":" + numericValue;
	}

}
