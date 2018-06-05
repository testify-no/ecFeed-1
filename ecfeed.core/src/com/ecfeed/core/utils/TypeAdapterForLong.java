package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForLong extends TypeAdapterForNumericType<Long>{

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try{
			return String.valueOf(StringHelper.convertToLong(value));
		}
		catch(NumberFormatException e){
			return getDefaultValue();
		}
	}

	@Override
	public Long generateValue(String range) {
		return ThreadLocalRandom.current().nextLong(getLower(range),getUpper(range));
	}

	@Override
	protected String[] getSpecialValues() {
		return NUMERIC_SPECIAL_VALUES;
	}
}
