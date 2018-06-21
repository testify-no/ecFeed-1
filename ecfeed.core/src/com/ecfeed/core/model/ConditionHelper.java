package com.ecfeed.core.model;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.MessageStack;

public class ConditionHelper {

	public static String getSubstituteType(RelationStatement parentRelationStatement) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(
						parentRelationStatement.getLeftParameter().getType(), 
						JavaTypeHelper.getStringTypeName());

		if (substituteType == null) {
			final String MESSAGE = "Substitute type must not be null.";
			ExceptionHelper.reportRuntimeException(MESSAGE);
		}

		return substituteType;
	}

	public static void addValuesMessageToStack(
			String left, EStatementRelation relation, String right, MessageStack messageStack) {

		messageStack.addMessage(createMessage("Values", left + relation.toString() + right));
	}

	public static void addRelStatementToMesageStack(
			RelationStatement relationStatement, MessageStack messageStack) {

		messageStack.addMessage(createMessage("Statement", relationStatement.toString()));
	}

	public static void addConstraintNameToMesageStack(
			String constraintName, MessageStack messageStack) {

		messageStack.addMessage(createMessage("Constraint", constraintName.toString()));
	}

	public static String createMessage(String name, String value) {
		return name + " [" + value + "].";
	}

	public static String createMessage(String name, String value, String additionalMessage) {
		return name + " [" + value + "] " + additionalMessage + ".";
	}	
}
