package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForInt extends TypeAdapterForNumericType<Integer>{

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_INT;
	}

	@Override
	protected String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			Integer integer = StringHelper.convertToInteger(value);
			return String.valueOf(integer);
		} catch (NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_INT);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Integer generateValue(String range) {
		return ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_INTEGER;
	}

}
