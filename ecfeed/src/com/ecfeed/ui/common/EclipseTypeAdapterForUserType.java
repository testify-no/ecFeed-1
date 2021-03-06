package com.ecfeed.ui.common;

import java.util.Arrays;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.JavaTypeHelper;

public class EclipseTypeAdapterForUserType<T extends Enum<T>> implements ITypeAdapter<T> {

	private String fType;

	private final String[] TYPES_CONVERTABLE_TO_USER_TYPE = new String[]{
			JavaTypeHelper.TYPE_NAME_STRING 
	};

	public EclipseTypeAdapterForUserType(String type){
		fType = type;
	}

	@Override
	public String getMyTypeName() {
		return "USER-TYPE";
	}
	
	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_USER_TYPE).contains(type);
	}

	public String convert(String value, boolean isRandomized, EConversionMode conversionMode) {
		return JavaLanguageHelper.isValidJavaIdentifier(value) ? value : getDefaultValue();
	}

	@Override
	public String getDefaultValue() {
		
		if (ApplicationContext.isApplicationTypeLocalStandalone()
				|| ApplicationContext.isApplicationTypeRemoteRap()) {
						return JavaTypeHelper.DEFAULT_EXPECTED_ENUM_VALUE;
		}
			
		return EclipseTypeHelper.getDefaultExpectedValue(fType);
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

}
