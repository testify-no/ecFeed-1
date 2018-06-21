package com.ecfeed.core.utils;

public class RandomizedRangeHelper {

	public static final String DELIMITER = ":";

	public static boolean isRange(String value) {

		String[] values = value.split(DELIMITER);

		if (values.length != 2) {
			return false;
		}

		return true;
	}

	public static String[] splitToRange(String value) {

		String[] range = value.split(DELIMITER);

		if (range.length != 2) {
			ExceptionHelper.reportRuntimeException("Invalid format of adapted value.");
		}

		return range;
	}

	public static String createRange(String value) {
		return createRange(value, value);
	}

	public static String createRange(String firstValue, String secondValue) {
		return firstValue + DELIMITER + secondValue;
	}	
}
