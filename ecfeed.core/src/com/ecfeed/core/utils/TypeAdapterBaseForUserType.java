package com.ecfeed.core.utils;

import java.util.Arrays;

import com.ecfeed.core.adapter.ITypeAdapter;

public class TypeAdapterBaseForUserType<T extends Enum<T>> implements ITypeAdapter<T> {

	private final String[] TYPES_CONVERTABLE_TO_USER_TYPE = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING 
	};

	@SuppressWarnings("unused")
	private String fType;

	public TypeAdapterBaseForUserType(String type){
		fType = type;
	}

	@Override
	public boolean isCompatible(String type){

		return Arrays.asList(TYPES_CONVERTABLE_TO_USER_TYPE).contains(type);
	}

	public String convert(String value, boolean isRandomized){

		if (isRandomized) {
			final String CAN_NOT_CONVERT = "Cannot convert to randomized user type.";
			ExceptionHelper.reportRuntimeException(CAN_NOT_CONVERT);
		}		

		return JavaLanguageHelper.isValidJavaIdentifier(value) ? value : null;
	}

	@Override
	public String getDefaultValue() {
		return null;
	}

	@Override
	public boolean isNullAllowed() {
		return true;
	}

	@Override
	public T generateValue(String range) {
		return null;
	}

	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}

}
