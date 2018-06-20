package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

// ADR-REF - add tests for methods and other adapters 

public class TypeAdapterForIntTest {
	
	@Test
	public void shouldConvertNumericToRandomizedRange() {
		
		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();
		
		String result = typeAdapterForInt.convert("10", true);
		
		assertEquals("10:10", result);
	}
	
	@Test
	public void shouldConvertNumericValue() {
		
		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();
		
		String result = typeAdapterForInt.convert("10", false);
		
		assertEquals("10", result);
	}	
	
	@Test
	public void shouldConvertNonNumericValue() {
		
		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();
		
		String result = typeAdapterForInt.convert("ABC", false);
		
		assertNull(result);
	}	

}
