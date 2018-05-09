package com.ecfeed.core.adapter.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.JavaTypeHelper;

public class EclipseTypeHelper {


	public static List<String> getSpecialValues(String typeName) {

		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(CommonConstants.BOOLEAN_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(CommonConstants.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaTypeHelper.TYPE_NAME_BYTE:
		case JavaTypeHelper.TYPE_NAME_INT:
		case JavaTypeHelper.TYPE_NAME_LONG:
		case JavaTypeHelper.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(CommonConstants.INTEGER_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(CommonConstants.FLOAT_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
			break;
		default:
			return null;
		}
		return result;
	}
	
	public static String getDefaultExpectedValue(String type) {
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return CommonConstants.DEFAULT_EXPECTED_BYTE_VALUE;
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return CommonConstants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return CommonConstants.DEFAULT_EXPECTED_CHAR_VALUE;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return CommonConstants.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return CommonConstants.DEFAULT_EXPECTED_FLOAT_VALUE;
		case JavaTypeHelper.TYPE_NAME_INT:
			return CommonConstants.DEFAULT_EXPECTED_INT_VALUE;
		case JavaTypeHelper.TYPE_NAME_LONG:
			return CommonConstants.DEFAULT_EXPECTED_LONG_VALUE;
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return CommonConstants.DEFAULT_EXPECTED_SHORT_VALUE;
		case JavaTypeHelper.TYPE_NAME_STRING:
			return CommonConstants.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return null;
		}
	}
}
