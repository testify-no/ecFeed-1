package com.ecfeed.core.model;

import java.util.List;

class AmbigousUtils {
	
	boolean isAmbigous(IStatementCondition leftCondition, EStatementRelation relation, IStatementCondition rightCondition) {
		return false;
	}
	
	boolean isAmbigous(IStatementCondition leftCondition, EStatementRelation relation, List<IStatementCondition> rightConditions) {
		return false;
	}
	
	boolean isAmbigous(List<IStatementCondition> leftConditions, EStatementRelation relation, List<IStatementCondition> rightConditions) {
		return false;
	}
}
