package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.model.RangeHelper;

public class TypeAdapterForShort extends TypeAdapterForNumericType<Short> {

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_SHORT;
	}

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(StringHelper.convertToShort(value));
		} catch (NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_SHORT);
				return null;
			} else {
				return getDefaultValue();
			}

		}
	}

	@Override
	public Short generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		return (short) ThreadLocalRandom.current().nextInt(
				JavaTypeHelper.parseShortValue(range[0], EConversionMode.QUIET), 
				JavaTypeHelper.parseShortValue(range[1], EConversionMode.QUIET));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_SHORT;
	}

}
