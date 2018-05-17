package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForLong extends TypeAdapterForNumeric<Long>{

	@Override
	public String convert(String value, boolean isRandomized){
		String result = super.convert(value, isRandomized);
		if(result == null){
			try{
				result = String.valueOf(StringHelper.convertToLong(value));
			}
			catch(NumberFormatException e){
				result = null;
			}
		}

		if (isRandomized) {
			result = generateRange(result);
		}

		return result;
	}

	@Override
	public Long generateValue(String range) {
		return ThreadLocalRandom.current().nextLong(getLower(range),getUpper(range));
	}
}
