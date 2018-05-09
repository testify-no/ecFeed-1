package com.ecfeed.ui.common.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.EcException;

public class SourceCodeTextImplementerTest {

	@Test
	public void shouldThrowWhenEmptyContent() {

		try {
			SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					null, createChoiceNodeList());

		} catch (EcException e) {
			return;
		}

		fail();
	}

	@Test
	public void shouldIgnoreEmptyChoiceList() {

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					"?", new ArrayList<ChoiceNode>());
		} catch (EcException e) {
			fail();
		}

		assertEquals("?", newContent);
	}

	@Test
	public void shouldCorrectEnumItemV2() {

		String oldContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"), V2; Enum1(String value){} }";		

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					oldContent, createChoiceNodeList("2"));
		} catch (EcException e) {
			fail();
		}

		String expectedContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"), V2(\"V2\"); Enum1(String value){} }";		

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	@Test
	public void shouldAddEnumItemV2() {

		String oldContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"); Enum1(String value){} }";		

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					oldContent, createChoiceNodeList("2"));
		} catch (EcException e) {
			fail();
		}

		String expectedContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"), V2(\"V2\"); Enum1(String value){} }";		

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}	

	@Test
	public void shouldCorrectEnumItemV2WithMultilineSrc() {

		String oldContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2;\nEnum1(String value){}}\n";		

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					oldContent, createChoiceNodeList("2"));
		} catch (EcException e) {
			fail();
		}

		String expectedContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\");\nEnum1(String value){}}\n";

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	@Test
	public void shouldCorrectMultipleEnumItems() {

		String oldContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2, V3;\nEnum1(String value){}}\n";		

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					oldContent, createChoiceNodeList("3", "2"));
		} catch (EcException e) {
			fail();
		}

		String expectedContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\"), V3(\"V3\");\nEnum1(String value){}}\n";

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	@Test
	public void shouldAddMultipleEnumItems() {

		String oldContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\");\nEnum1(String value){}}\n";		

		String newContent = null;
		try {
			newContent = SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
					oldContent, createChoiceNodeList("2", "3"));
		} catch (EcException e) {
			fail();
		}

		String expectedContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\"), V3(\"V3\");\nEnum1(String value){}}\n";

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	private static List<ChoiceNode> createChoiceNodeList(String... choicesNames) {

		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();

		for (String choiceName : choicesNames) {
			ChoiceNode choiceNode = new ChoiceNode("N" + choiceName, "V" + choiceName);
			choiceNodes.add(choiceNode);
		}

		return choiceNodes;
	}
}
