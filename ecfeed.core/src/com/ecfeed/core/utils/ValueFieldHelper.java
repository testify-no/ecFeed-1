package com.ecfeed.core.utils;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.ModelOperationException;

public class ValueFieldHelper {
	
	private ValueFieldHelper() {}
	
	public static final String DELIMITER = ":";
	
	//TODO
	@Deprecated
	public static void adapt(String oldValue, boolean isRandomized, String newType) {
		
	}

	//TODO and refactor next time
	public static String adapt(String type, String value, boolean randomizeValue, ITypeAdapterProvider iTypeAdapterProvider) throws ModelOperationException {
		
		ITypeAdapter<?> typeAdapter = iTypeAdapterProvider.getAdapter(type);
		
		if (randomizeValue && !type.equals(JavaTypeHelper.TYPE_NAME_STRING)) {
			String[] values = value.split(":");
			if (values.length != 2) {
				ValueFieldOperationException.report("todo");
			}

			String firstValue = values[0];
			String secondValue = values[1];

			firstValue = typeAdapter.convert(firstValue);
			secondValue = typeAdapter.convert(secondValue);

			if (firstValue == null || secondValue == null) {
				return null; // wrong value
			}

			if (isCorrect(type, value)) {
				// TODO, should match with regex, e.g.: int:int, /d:/d
				// instead of splitting possibly incorrect values
			}
			return firstValue + DELIMITER + secondValue;
		}
		return typeAdapter.convert(value);
	}
	
	private static boolean isCorrect(String type, String value) {
		//
		return true;
	}

	
}
