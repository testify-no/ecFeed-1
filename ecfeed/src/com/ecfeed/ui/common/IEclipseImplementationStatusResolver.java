package com.ecfeed.ui.common;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.EcException;

public interface IEclipseImplementationStatusResolver {

	boolean androidCodeImplemented(ClassNode classNode) throws EcException;
	boolean classDefinitionImplemented(String qualifiedName);
	boolean methodDefinitionImplemented(MethodNode method);
	boolean enumDefinitionImplemented(String qualifiedName);
	boolean enumValueImplemented(String qualifiedName, String value);

}
