package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForFloat extends TypeAdapterFloatingPoint<Float>{

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(Float.parseFloat(value));
		} catch(NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, "Float");
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Float generateValue(String range) {
		int min = getLower(range);
		int max = getUpper(range);
		return ThreadLocalRandom.current().nextFloat() * (max - min) + min;
	}

}
