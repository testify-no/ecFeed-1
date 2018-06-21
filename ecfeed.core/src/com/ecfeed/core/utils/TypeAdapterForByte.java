package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForByte extends TypeAdapterForNumericType<Byte>{

	@Override
	protected String convertSingleValue(String value){

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try{
			return String.valueOf(StringHelper.convertToByte(value));
		}
		catch (NumberFormatException e) {
			return getDefaultValue();
		}
	}
	
	@Override
	protected String[] getSpecialValues() {
		return NUMERIC_SPECIAL_VALUES;
	}

	@Override
	public Byte generateValue(String range) {
		byte[] bytes = new byte[1];
		ThreadLocalRandom.current().nextBytes(bytes);
		return bytes[0];
	}
}
