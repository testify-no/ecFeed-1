package com.ecfeed.core.adapter.operations;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.utils.TypeAdapterProvider;

public class TypeAdapterProviderTest {

	@Test
	public void generateStringValueTest() {
		
		@SuppressWarnings("unchecked")
		ITypeAdapter<String> adapter = (ITypeAdapter<String>) new TypeAdapterProvider().getAdapter("String");
		
		String regex = "[ab]{4,6}c";
		String value = adapter.generateValue(regex);
		assertTrue(value.matches(regex));
	}

}
