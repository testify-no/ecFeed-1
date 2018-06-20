package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForShort extends TypeAdapterForNumeric<Short>{
	@Override
	public String convert(String value, boolean isRandomized){
		String result = super.convert(value, isRandomized);
		if(result == null){
			try{
				result = String.valueOf(StringHelper.convertToShort(value));
			}
			catch(NumberFormatException e){
				if(value.length() == 1){
					int charValue = (int)value.charAt(0);
					if((charValue > Short.MAX_VALUE) == false){
						result = Integer.toString(charValue);
					}
				} else {
					result = null;
				}
			}
		}

		// ADR-REF similar code in adapters
		if (isRandomized) {
			result = generateRange(result);
		}

		return result;
	}

	@Override
	public Short generateValue(String range) {
		return (short)ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}
}
