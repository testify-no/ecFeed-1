package com.ecfeed.core.utils;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;

public class TypeAdapterProvider implements ITypeAdapterProvider{

	protected ITypeAdapter getTypeAdapterBaseForUserType(String type) {
		return new TypeAdapterBaseForUserType(type);
	}
	
	public ITypeAdapter getAdapter(String type){
		if(JavaTypeHelper.isJavaType(type) == false){
			type = TypeAdapterHelper.USER_TYPE;
		}
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return new TypeAdapterForBoolean();
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return new TypeAdapterForByte();
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return new TypeAdapterForChar();
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return new TypeAdapterForDouble();
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return new TypeAdapterForFloat();
		case JavaTypeHelper.TYPE_NAME_INT:
			return new TypeAdapterForInt();
		case JavaTypeHelper.TYPE_NAME_LONG:
			return new TypeAdapterForLong();
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return new TypeAdapterForShort();
		case JavaTypeHelper.TYPE_NAME_STRING:
			return new TypeAdapterForString();
		default:
			return getTypeAdapterBaseForUserType(type);
		}
	}
}
