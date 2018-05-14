package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForLong extends TypeAdapterForNumeric<Long>{
	
	@Override
	public String convert(String value){
		String result = super.convert(value);
		if(result == null){
			try{
				result = String.valueOf(StringHelper.convertToLong(value));
			}
			catch(NumberFormatException e){
				result = null;
			}
		}
		return result;
	}

	@Override
	public Long generateValue(String range) {
		return ThreadLocalRandom.current().nextLong(getLower(range),getUpper(range));
	}
}
