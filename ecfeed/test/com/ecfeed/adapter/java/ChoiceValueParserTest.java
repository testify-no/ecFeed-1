/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.adapter.java.ChoiceValueParser;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.junit.CollectiveOnlineRunner;
import com.ecfeed.junit.annotations.Constraints;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.Generator;

@RunWith(CollectiveOnlineRunner.class)
@EcModel("test/com.ecfeed.adapter.java.ect")
@Generator(CartesianProductGenerator.class)
@Constraints(Constraints.ALL)
public class ChoiceValueParserTest{

	private ChoiceValueParser fParser;

	public enum ImplementedType{
		IMPLEMENTED
	}

	public ChoiceValueParserTest() {
		ClassLoader parentLoader = this.getClass().getClassLoader();
		ModelClassLoader loader = new ModelClassLoader(new URL[]{}, parentLoader);
		fParser =  new ChoiceValueParser(loader, false);
	}

	@Test
	public void parseBooleanTest(String valueString, boolean value, boolean nullExpected){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_BOOLEAN);

		if(nullExpected){
			assertTrue(parsed == null);
		}
		else{
			assertEquals(value, fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_BOOLEAN));
		}
	}

	@Test
	public void parseByteTest(String valueString, byte parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_BYTE);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseShortTest(String valueString, short parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_SHORT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseIntegerTest(String valueString, int parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_INT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseLongTest(String valueString, long parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_LONG);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseCharTest(String valueString, char parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_CHAR);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseFloatTest(String valueString, float parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_FLOAT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseDoubleTest(String valueString, double parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_DOUBLE);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseStringTest(String valueString, String parsedValue){

		Object parsed = fParser.parseValue(valueString, false, JavaTypeHelper.TYPE_NAME_STRING);
		assertEquals(parsedValue, parsed);
	}

	@Test
	public void parseUserTypeTest(String valueString, String type, boolean parsedCorrectly){

		Object parsed = fParser.parseValue(valueString, false, type);

		if(parsedCorrectly){
			assertEquals(ImplementedType.IMPLEMENTED, parsed);
		}
		else{
			assertEquals(null, parsed);
		}
	}

	@Test
	public void parseTestForRandomizedChoice() {

		Object parsed = fParser.parseValue("0:9", true, JavaTypeHelper.TYPE_NAME_INT);

		assertNotNull(parsed);

		int result = (int)parsed;

		assertTrue(result >= 0);
		assertTrue(result <= 9);
	}	

}

