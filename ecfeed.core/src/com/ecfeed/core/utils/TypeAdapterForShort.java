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

			return TypeAdapterHelper.handleConversionError(value, getDefaultValue(), conversionMode);
		}
	}

	@Override
	public Short generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaTypeHelper.parseShortValue(range[0], EConversionMode.QUIET);
		}
		
		return (short) ThreadLocalRandom.current().nextInt(
				JavaTypeHelper.parseShortValue(range[0], EConversionMode.QUIET), 
				JavaTypeHelper.parseShortValue(range[1], EConversionMode.QUIET));
	}

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_SHORT;
	}

}
