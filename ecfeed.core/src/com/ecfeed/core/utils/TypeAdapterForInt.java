package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForInt extends TypeAdapterForNumeric<Integer>{
	
	@Override
	public String convert(String value){
		String result = super.convert(value);
		if(result == null){
			try{
				result = String.valueOf(StringHelper.convertToInteger(value));
			}
			catch(NumberFormatException e){
				if(value.length() == 1){
					result = Integer.toString((int)value.charAt(0));
				} else {
					result = null;
				}
			}
		}
		return result;
	}

	@Override
	public Integer generateValue(String range) {
		return ThreadLocalRandom.current().nextInt(getLower(range),getUpper(range));
	}
}