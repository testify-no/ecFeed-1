package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForDouble extends TypeAdapterFloatingPoint<Double>{

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_DOUBLE;
	}

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(JavaTypeHelper.parseDoubleValue(value, EConversionMode.WITH_EXCEPTION));

		} catch(NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_DOUBLE);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Double generateValue(String range) {
		return ThreadLocalRandom.current().nextDouble(getLowerDouble(range),getUpperDouble(range));
	}

	protected final Double getLowerDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[0]);
	}

	protected final Double getUpperDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[1]);
	}	

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_DOUBLE;
	}	

}
