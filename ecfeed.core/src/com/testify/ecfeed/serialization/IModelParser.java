/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization;

import java.io.InputStream;

import com.testify.ecfeed.core.model.AbstractStatement;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ChoicesParentStatement;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.ExpectedValueStatement;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.StatementArray;
import com.testify.ecfeed.core.model.StaticStatement;
import com.testify.ecfeed.core.model.TestCaseNode;

public interface IModelParser {
	public RootNode parseModel(InputStream istream) throws ParserException;
	public ClassNode parseClass(InputStream istream) throws ParserException;
	public MethodNode parseMethod(InputStream istream) throws ParserException;
	public MethodParameterNode parseMethodParameter(InputStream istream, MethodNode method) throws ParserException;
	public GlobalParameterNode parseGlobalParameter(InputStream istream) throws ParserException;
	public ChoiceNode parseChoice(InputStream istream) throws ParserException;
	public TestCaseNode parseTestCase(InputStream istream, MethodNode method) throws ParserException;
	public ConstraintNode parseConstraint(InputStream istream, MethodNode method) throws ParserException;
	public AbstractStatement parseStatement(InputStream istream, MethodNode method) throws ParserException;
	public StaticStatement parseStaticStatement(InputStream istream) throws ParserException;
	public ChoicesParentStatement parseChoicesParentStatement(InputStream istream, MethodNode method) throws ParserException;
	public ExpectedValueStatement parseExpectedValueStatement(InputStream istream, MethodNode method) throws ParserException;
	public StatementArray parseStatementArray(InputStream istream, MethodNode method) throws ParserException;
}
