package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TypeAdapterForByte extends TypeAdapterForNumeric<Byte>{

	// ADR-REF
	@Override
	public String convert(String value, boolean isRandomized){
		
		String result = super.convert(value, isRandomized);
		
		if (result == null) {
			try{
				result = String.valueOf(StringHelper.convertToByte(value));
			}
			catch (NumberFormatException e) {
				if (value.length() == 1) {
					int charValue = (int)value.charAt(0);
					if((charValue > Byte.MAX_VALUE) == false){
						result = Integer.toString(charValue);
					}
				} else {
					result = null;
				}
			}
		}
		
		if (isRandomized) {
			result = generateRange(result);
		}
		
		return result;
	}
	
	@Override
	public Byte generateValue(String range) {
		byte[] bytes = new byte[1];
		ThreadLocalRandom.current().nextBytes(bytes);
		return bytes[0];
		//In Java 8: SecureRandom.getInstanceStrong().nextBytes(bytes);
	}
}
