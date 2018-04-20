package com.ecfeed.adapter.operations;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.junit.CollectiveOnlineRunner;
import com.ecfeed.junit.annotations.Constraints;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.Generator;

@RunWith(CollectiveOnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com/ecfeed/adapter/operations/Inntekstkomponenten.ect")
@Constraints(Constraints.ALL)
public class MethodParameterOperationSetTypeTest {
	
	@Test
	public void parseStringToLongTest(){
		
	}
	
	@Test
	public void parseAnotherStringArgumentToLongTest() {
		
	}
	
}
