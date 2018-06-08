package com.ecfeed.core.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ModelTestHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.JavaTypeHelper;

public class MethodOperationAddParameterTest {

	@Test
	public void test1() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<Model name='AddParameter' version='2'>\n");
		sb.append("    <Class name='com.example.test.TestClass'>\n");
		sb.append("        <Method name='testMethod'>\n");
		sb.append("        </Method>\n");
		sb.append("    </Class>\n");
		sb.append("</Model>\n");		
		String xml = sb.toString();
		xml = xml.replace("'", "\"");

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		MethodParameterNode methodParameterNode = 
				new MethodParameterNode(
						"paramX", JavaTypeHelper.TYPE_NAME_INT, "0", false);

		MethodOperationAddParameter methodOperationAddParameter = 
				new MethodOperationAddParameter(
						methodNode, methodParameterNode, 0);

		try {
			methodOperationAddParameter.execute();
		} catch (ModelOperationException e) {
			fail();
		}

		MethodParameterNode addedNode = methodNode.getMethodParameter(0);

		assertEquals(addedNode, methodParameterNode);

		try {
			methodOperationAddParameter.reverseOperation().execute();
		} catch (ModelOperationException e) {
			fail();
		}

		assertEquals(0, methodNode.getParametersCount());
	}


}
