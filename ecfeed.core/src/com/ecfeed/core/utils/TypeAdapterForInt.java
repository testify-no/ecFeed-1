package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.model.RangeHelper;

public class TypeAdapterForInt extends TypeAdapterForNumericType<Integer> {

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_INT;
	}

	@Override
	protected String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			Integer integer = StringHelper.convertToInteger(value);
			return String.valueOf(integer);
		} catch (NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_INT);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}
	
	@Override
	public Integer generateValue(String range) {
		
		String[] range2 = RangeHelper.splitToRange(range);
		
		return ThreadLocalRandom.current().nextInt(
				JavaTypeHelper.parseIntValue(range2[0], EConversionMode.QUIET),
				JavaTypeHelper.parseIntValue(range2[1], EConversionMode.QUIET));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_INTEGER;
	}

}
