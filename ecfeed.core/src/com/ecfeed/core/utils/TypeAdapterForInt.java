package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForInt extends TypeAdapterForNumeric<Integer>{

	@Override
	public String convert(String value, boolean isRandomized){

		String result = super.convert(value, isRandomized);

		if (result == null) {
			try {
				result = String.valueOf(StringHelper.convertToInteger(value));
			}
			catch (NumberFormatException e) {
				result = getDefaultValue();
			}
		}

		if (isRandomized) {
			result = generateRange(result);
		}

		return result;
	}

	@Override
	public Integer generateValue(String range) {
		return ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}
}
