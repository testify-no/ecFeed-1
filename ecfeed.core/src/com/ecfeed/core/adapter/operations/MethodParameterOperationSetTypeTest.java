package com.ecfeed.core.adapter.operations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelLogger;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.utils.TypeAdapterProvider;

public class MethodParameterOperationSetTypeTest {
	@Test
	public void adaptConstraintsAfterChangeFeilmeldingParameterTypeTest()
			throws ParserException, ModelOperationException, IOException {
		File file = new File(
				"src/com/ecfeed/core/adapter/operations/MethodParameterOperationSetTypeTest.ect");
		assertTrue(file.exists());
		InputStream istream = new FileInputStream(file);
		IModelParser parser = new EctParser();
		RootNode model = parser.parseModel(istream);

		ClassNode classNode = model.getClasses().get(0);
		MethodNode method = classNode.getMethod("testMethod", Arrays.asList(
				"String", "String", "String", "String", "String", "String",
				"String", "String"));
		assertNotNull(method);

		String[] constraintsNames = { "SecondConstraint", "AnyConstraint",
				"Mangler tilgang til BidragsforskuddA-Inntekt",
				"Sykepenger og Bidragsforskudd a-inntekt",
				"Mangler tilgang til Bidrag a-inntekt",
				"Sykepenger og Bidrag a-inntekt", "AnotherConstraint" };

		MethodParameterNode methodParameterNode = method
				.getMethodParameter("feilmelding");

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProvider();
		BulkOperation operation = new MethodParameterOperationSetType(
				methodParameterNode, "long", typeAdapterProvider);

		List<ConstraintNode> constraints = method.getConstraintNodes();
		assertTrue(Arrays.equals(
				constraintsNames,
				constraints.stream().map(ConstraintNode::getName)
						.toArray(String[]::new)));

		operation.execute();

		String[] constraintsAfterExecute = constraints.stream()
				.map(ConstraintNode::getName).toArray(String[]::new);
		
		assertTrue(Arrays
				.equals(new String[] { 
						"SecondConstraint", 
						"AnyConstraint",
						"Mangler tilgang til BidragsforskuddA-Inntekt",
						"Sykepenger og Bidragsforskudd a-inntekt",
						"Mangler tilgang til Bidrag a-inntekt",
						"Sykepenger og Bidrag a-inntekt",
						"AnotherConstraint" }, constraintsAfterExecute));
		istream.close();
	}

	@Test
	public void adaptConstraintsAfterChangeAnotherParameterParamterTypeTest()
			throws FileNotFoundException, ParserException,
			ModelOperationException {
		File file = new File(
				"src/com/ecfeed/core/adapter/operations/MethodParameterOperationSetTypeTest.ect");
		assertTrue(file.exists());
		InputStream istream = new FileInputStream(file);
		IModelParser parser = new EctParser();
		RootNode model = parser.parseModel(istream);

		ClassNode classNode = model.getClasses().get(0);
		MethodNode method = classNode.getMethod("testMethod", Arrays.asList(
				"String", "String", "String", "String", "String", "String",
				"String", "String"));
		assertNotNull(method);

		String[] constraintsNames = { "SecondConstraint", "AnyConstraint",
				"Mangler tilgang til BidragsforskuddA-Inntekt",
				"Sykepenger og Bidragsforskudd a-inntekt",
				"Mangler tilgang til Bidrag a-inntekt",
				"Sykepenger og Bidrag a-inntekt", "AnotherConstraint" };

		MethodParameterNode methodParameterNode = method
				.getMethodParameter("anotherParameter");

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProvider();
		BulkOperation operation = new MethodParameterOperationSetType(
				methodParameterNode, "long", typeAdapterProvider);

		List<ConstraintNode> constraints = method.getConstraintNodes();
		
		assertTrue(Arrays.equals(
				constraintsNames,
				constraints.stream().map(ConstraintNode::getName)
						.toArray(String[]::new)));

		ModelLogger.printModel("Przed zmiana", model);
		
		operation.execute();
		
		

		String[] constraintsAfterExecute = constraints.stream()
				.map(ConstraintNode::getName).toArray(String[]::new);
		
		ModelLogger.printModel("Po zmiane", model);		
		
		assertTrue(Arrays.equals(new String[] { 
				"SecondConstraint",
				"AnyConstraint",
				"Mangler tilgang til BidragsforskuddA-Inntekt",
				"Sykepenger og Bidragsforskudd a-inntekt",
				"Mangler tilgang til Bidrag a-inntekt",
				"Sykepenger og Bidrag a-inntekt",
				"AnotherConstraint"}, constraintsAfterExecute));
	}
}
