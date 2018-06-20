package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForFloat extends TypeAdapterFloatingPoint<Float>{
	
	@Override
	public String convert(String value){
		String result = super.convert(value);
		if(result == null){
			try{
				result = String.valueOf(Float.parseFloat(value));
			}
			catch(NumberFormatException e){
				result = null;
			}
		}
		return result;
	}

	@Override
	public Float generateValue(String range) {
		int min = getLower(range);
		int max = getUpper(range);
		return ThreadLocalRandom.current().nextFloat() * (max - min) + min;
	}
}
