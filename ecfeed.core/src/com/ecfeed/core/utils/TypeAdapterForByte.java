package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForByte extends TypeAdapterForNumericType<Byte>{

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_BYTE;
	}

	@Override
	protected String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(StringHelper.convertToByte(value));
		} catch (NumberFormatException e) {

			if (conversionMode == EConversionMode.QUIET) {
				return getDefaultValue();
			} else {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_BYTE);
				return null;
			}
		}
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_BYTE;
	}

	@Override
	public Byte generateValue(String range) {
		byte[] bytes = new byte[1];
		ThreadLocalRandom.current().nextBytes(bytes);
		return bytes[0];
	}

}
