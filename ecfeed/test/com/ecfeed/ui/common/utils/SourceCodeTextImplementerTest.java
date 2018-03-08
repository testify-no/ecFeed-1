package com.ecfeed.ui.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;

public class SourceCodeTextImplementerTest {

	@Test
	public void shouldIgnoreEmptyContent() {

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						null, createChoiceNodeList());

		assertEquals(null, newContent);
	}

	@Test
	public void shouldIgnoreEmptyChoiceList() {

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						"?", new ArrayList<ChoiceNode>());

		assertEquals("?", newContent);
	}

	@Test
	public void shouldIgnoreInvalidChoices() {

		String oldContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"); Enum1(String value){} }";		

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						oldContent, createChoiceNodeList("C1"));

		assertEquals(oldContent, newContent);
	}

	@Test
	public void shouldCorrectEnumItemV2() {

		String oldContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"), V2; Enum1(String value){} }";		

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						oldContent, createChoiceNodeList("V2"));

		String expectedContent = 
				"package com.example.test; public enum Enum1 { V1(\"V1\"), V2(\"V2\"); Enum1(String value){} }";		

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	@Test
	public void shouldCorrectEnumItemV2WithMultilineSrc() {

		String oldContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2;\nEnum1(String value){}}\n";		

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						oldContent, createChoiceNodeList("V2"));

		String expectedContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\");\nEnum1(String value){}}\n";

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}

	@Test
	public void shouldCorrectMultipleEnumItems() {

		String oldContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2, V3;\nEnum1(String value){}}\n";		

		String newContent = 
				SourceCodeTextImplementer.correctItemsForEnumWithStringConstructor(
						oldContent, createChoiceNodeList("V3", "V2"));

		String expectedContent = 
				"package com.example.test;\npublic enum Enum1 {V1(\"V1\"), V2(\"V2\"), V3(\"V3\");\nEnum1(String value){}}\n";

		boolean isMatch = expectedContent.equals(newContent);

		assertTrue(isMatch);
	}


	private static List<ChoiceNode> createChoiceNodeList(String... choicesNames) {

		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();

		for (String choiceName : choicesNames) {
			ChoiceNode choiceNode = new ChoiceNode(choiceName, choiceName);
			choiceNodes.add(choiceNode);
		}

		return choiceNodes;
	}
}
