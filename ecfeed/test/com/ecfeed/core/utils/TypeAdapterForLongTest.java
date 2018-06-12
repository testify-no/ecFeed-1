package com.ecfeed.core.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

public class TypeAdapterForLongTest {

	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		Long max = Long.MAX_VALUE;
		Long maxMinusOne = Long.MAX_VALUE - 1;

		String maxStr = max.toString();
		String maxMinusOneStr = maxMinusOne.toString();

		Long result = typeAdapterForLong.generateValue(maxMinusOneStr + ":" + maxStr);		

		checkRange(result, maxMinusOne, max);
	}

	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		Long result = typeAdapterForLong.generateValue("0:MAX_VALUE");		

		checkRange(result, 0L, Long.MAX_VALUE);
	}	

	private void checkRange(Long result, Long min, Long max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}

}
