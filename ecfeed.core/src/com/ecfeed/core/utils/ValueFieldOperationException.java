package com.ecfeed.core.utils;

import com.ecfeed.core.model.ModelOperationException;

public final class ValueFieldOperationException extends ModelOperationException {

	private ValueFieldOperationException(String message) {
		super(message);
	}
	
	public static void report(String message) throws ModelOperationException {
		throw new ValueFieldOperationException(message);
	}

	private static final long serialVersionUID = 1L;

}
