package com.ecfeed.core.utils;

import java.util.Arrays;

public abstract class TypeAdapterFloatingPoint<T extends Number> extends TypeAdapterForNumericType<T>{

	protected String[] FLOATING_POINT_SPECIAL_VALUES = new String[]{
			JavaTypeHelper.VALUE_REPRESENTATION_POSITIVE_INF,
			JavaTypeHelper.VALUE_REPRESENTATION_NEGATIVE_INF
	};

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
	}

	@Override
	protected String convertSingleValue(String value) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			Float number = Float.parseFloat(value);
			return number.toString();
		}
		catch (NumberFormatException e) {
			return getDefaultValue();
		}
	}

	@Override
	public String getDefaultValue(){
		return JavaTypeHelper.DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	}

	@Override
	protected String[] getSpecialValues() {
		return FLOATING_POINT_SPECIAL_VALUES;
	}	
}

