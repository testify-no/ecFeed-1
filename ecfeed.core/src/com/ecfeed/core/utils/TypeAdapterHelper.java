package com.ecfeed.core.utils;

public class TypeAdapterHelper {
	
	public static final String USER_TYPE = "USER_TYPE";
	
	public static final String[] TYPES_CONVERTABLE_TO_NUMBERS = new String[]{
			JavaTypeHelper.TYPE_NAME_INT, 
			JavaTypeHelper.TYPE_NAME_FLOAT, 
			JavaTypeHelper.TYPE_NAME_DOUBLE, 
			JavaTypeHelper.TYPE_NAME_LONG, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_BYTE, 
			JavaTypeHelper.TYPE_NAME_CHAR
	};
	
	public static void reportRuntimeExceptionCannotConvert(String value, String typeName) {
		
		final String CANNOT_CONVERT_CHAR = "Cannot convert value [" + value + "] to " + typeName + ".";
		ExceptionHelper.reportRuntimeException(CANNOT_CONVERT_CHAR);
	}

}
