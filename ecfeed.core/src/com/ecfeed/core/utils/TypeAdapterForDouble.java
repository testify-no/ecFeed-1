package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForDouble extends TypeAdapterFloatingPoint<Double>{
	@Override
	public String convertSingleValue(String value) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			return getDefaultValue();
		}
	}

	@Override
	public Double generateValue(String range) {
		return ThreadLocalRandom.current().nextDouble(getLower(range),getUpper(range));
	}
}
