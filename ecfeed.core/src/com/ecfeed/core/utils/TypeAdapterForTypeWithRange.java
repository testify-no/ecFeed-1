/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.model.RangeHelper;

public abstract class TypeAdapterForTypeWithRange<T> implements ITypeAdapter<T> {

	protected abstract String convertSingleValue(String value, EConversionMode conversionMode);

	protected abstract String[] getSpecialValues();

	@Override
	public boolean isRandomizable() {
		return true;
	}

	@Override
	public String convert(String value, boolean isRandomized, EConversionMode conversionMode) {

		if (!RangeHelper.isRange(value)) {
			return convertNotRange(value, isRandomized, conversionMode);
		}

		return convertRange(value, isRandomized, conversionMode);
	}

	private String convertNotRange(String value, boolean isRandomized, EConversionMode conversionMode) {

		String result = convertSingleValue(value, conversionMode);

		if (!isRandomized) {
			return result;
		}

		if (conversionMode == EConversionMode.QUIET) {
			return RangeHelper.createRange(result);
		}

		final String VALUE_IS_INVALID = "Value [" + value + "] is not allowed for randomized choice.";
		ExceptionHelper.reportRuntimeException(VALUE_IS_INVALID);
		return null;
	}

	private String convertRange(String value, boolean isRandomized, EConversionMode conversionMode) {

		String[] range = RangeHelper.splitToRange(value);

		if (isRandomized) {

			String firstValue = convertSingleValue(range[0], conversionMode);
			String secondValue = convertSingleValue(range[1], conversionMode);

			checkRange(range, conversionMode);		

			return RangeHelper.createRange(firstValue, secondValue);
		}

		if (conversionMode == EConversionMode.QUIET) {
			return range[0];
		}

		final String VALUE_IS_INVALID = "Value [" + value + "] is not allowed for not randomized choice.";
		ExceptionHelper.reportRuntimeException(VALUE_IS_INVALID);
		return null;
	}

	private void checkRange(String[] range, EConversionMode conversionMode) {

		if (conversionMode != EConversionMode.WITH_EXCEPTION) {
			return;
		}

		if (!RangeHelper.isRangeCorrect(range, getMyTypeName())) {
			final String RANGE_IS_INVALID = "Range [" + range[0] + ", " + range[1] + "] is invalid.";
			ExceptionHelper.reportRuntimeException(RANGE_IS_INVALID);
		}
	}

}
