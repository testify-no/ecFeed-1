package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForShort extends TypeAdapterForNumeric<Short>{
	@Override
	public String convert(String value){
		String result = super.convert(value);
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
		return result;
	}

	@Override
	public Short generateValue(String range) {
		return (short)ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}
}
