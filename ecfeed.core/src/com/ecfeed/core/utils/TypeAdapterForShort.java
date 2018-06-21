package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForShort extends TypeAdapterForNumericType<Short> {

	@Override
	public String convertSingleValue(String value) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(StringHelper.convertToShort(value));
		}
		catch (NumberFormatException e) {
			return getDefaultValue();
		}
	}

	@Override
	public Short generateValue(String range) {
		return (short)ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}

	@Override
	protected String[] getSpecialValues() {
		return NUMERIC_SPECIAL_VALUES;
	}
}
