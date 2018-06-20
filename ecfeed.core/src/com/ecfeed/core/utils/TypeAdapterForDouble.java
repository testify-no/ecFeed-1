package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForDouble extends TypeAdapterFloatingPoint<Double>{
	@Override
	public String convert(String value){
		String result = super.convert(value);
		if(result == null){
			try{
				result = String.valueOf(Double.parseDouble(value));
			}
			catch(NumberFormatException e){
				result = null;
			}
		}
		return result;
	}

	@Override
	public Double generateValue(String range) {
		return ThreadLocalRandom.current().nextDouble(getLower(range),getUpper(range));
	}
}
