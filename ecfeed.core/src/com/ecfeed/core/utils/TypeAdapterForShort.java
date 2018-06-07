package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForShort extends TypeAdapterForNumericType<Short> {

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_SHORT;
	}

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(StringHelper.convertToShort(value));
		} catch (NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_SHORT);
				return null;
			} else {
				return getDefaultValue();
			}

		}
	}

	@Override
	public Short generateValue(String range) {
		return (short)ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SHORT_SPECIAL_VALUES;
	}

}
