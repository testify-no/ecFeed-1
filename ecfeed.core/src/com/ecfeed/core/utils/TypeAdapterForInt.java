package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForInt extends TypeAdapterForNumericType<Integer>{

	@Override
	protected String convertSingleValue(String value) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			Integer integer = StringHelper.convertToInteger(value);
			return String.valueOf(integer);
		}
		catch (NumberFormatException e) {
			return getDefaultValue();
		}
	}

	@Override
	public Integer generateValue(String range) {
		return ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}

	@Override
	protected String[] getSpecialValues() {
		return NUMERIC_SPECIAL_VALUES;
	}
}
