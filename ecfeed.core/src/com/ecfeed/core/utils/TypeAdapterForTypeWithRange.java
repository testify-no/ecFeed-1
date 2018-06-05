package com.ecfeed.core.utils;

import com.ecfeed.core.adapter.ITypeAdapter;

public abstract class TypeAdapterForTypeWithRange<T> implements ITypeAdapter<T> {

	protected abstract String convertSingleValue(String value, EConversionMode conversionMode);

	protected abstract String[] getSpecialValues();

	@Override
	public boolean isRandomizable() {
		return true;
	}

	@Override
	public String convert(String value, boolean isRandomized, EConversionMode conversionMode) {

		if (!RandomizedRangeHelper.isRange(value)) {

			String result = convertSingleValue(value, conversionMode);

			if (isRandomized) {
				result = RandomizedRangeHelper.createRange(result);
			}

			return result;
		}

		String[] range = RandomizedRangeHelper.splitToRange(value);

		if (!isRandomized) {
			return range[0];
		}

		String firstValue = convertSingleValue(range[0], conversionMode);
		String secondValue = convertSingleValue(range[1], conversionMode);

		return RandomizedRangeHelper.createRange(firstValue, secondValue);
	}

}
