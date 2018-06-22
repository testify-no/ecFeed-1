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

public class TypeAdapterForDouble extends TypeAdapterFloatingPoint<Double>{

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_DOUBLE;
	}

	@Override
	public String convertSingleValue(String value, EConversionMode conversionMode) {

		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(JavaTypeHelper.parseDoubleValue(value, EConversionMode.WITH_EXCEPTION));

		} catch(NumberFormatException e) {

			return TypeAdapterHelper.handleConversionError(value, getDefaultValue(), conversionMode);
		}
	}

	@Override
	public Double generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		return ThreadLocalRandom.current().nextDouble(
				JavaTypeHelper.parseDoubleValue(range[0], EConversionMode.QUIET),
				JavaTypeHelper.parseDoubleValue(range[1], EConversionMode.QUIET));

	}

	protected final Double getLowerDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[0]);
	}

	protected final Double getUpperDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[1]);
	}	

	@Override
	protected String[] getSpecialValues() {
		return JavaTypeHelper.SPECIAL_VALUES_FOR_DOUBLE;
	}	

}
