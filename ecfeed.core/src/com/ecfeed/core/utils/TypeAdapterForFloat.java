package com.ecfeed.core.utils;

import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.model.RangeHelper;

public class TypeAdapterForFloat extends TypeAdapterFloatingPoint<Float>{

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_FLOAT;
	}

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(Float.parseFloat(value));
		} catch(NumberFormatException e) {

			if (conversionMode == EConversionMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_FLOAT);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Float generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		return (float) ThreadLocalRandom.current().nextDouble(
				JavaTypeHelper.parseFloatValue(range[0], EConversionMode.QUIET),
				JavaTypeHelper.parseFloatValue(range[1], EConversionMode.QUIET));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_FLOAT;
	}	

}
