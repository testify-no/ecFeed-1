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

import java.util.concurrent.ThreadLocalRandom;

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
	public Integer generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);
		
		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaTypeHelper.parseIntValue(range[0], EConversionMode.QUIET);
		}

		return ThreadLocalRandom.current().nextInt(
				JavaTypeHelper.parseIntValue(range[0], EConversionMode.QUIET),
				JavaTypeHelper.parseIntValue(range[1], EConversionMode.QUIET));
	}
	
	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_INTEGER;
	}

}
