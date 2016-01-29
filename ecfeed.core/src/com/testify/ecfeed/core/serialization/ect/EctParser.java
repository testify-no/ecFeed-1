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

package com.testify.ecfeed.core.serialization.ect;

import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import com.testify.ecfeed.core.model.AbstractStatement;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ChoicesParentStatement;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.ExpectedValueStatement;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.ModelVersionDistributor;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.StatementArray;
import com.testify.ecfeed.core.model.StaticStatement;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.serialization.IModelParser;
import com.testify.ecfeed.core.serialization.ParserException;
import com.testify.ecfeed.core.utils.ExceptionHelper;

public class EctParser implements IModelParser {

	private final static String UNKNOWN_VERSION = "Ect file has unknown version: ";
	
	Builder fBuilder = new Builder();
	XomAnalyser fXomAnalyser = null;

	@Override
	public RootNode parseModel(InputStream istream) throws ParserException {

		try {
			Document document = fBuilder.build(istream);
			Element element = document.getRootElement();
			int version = XomModelVersionDetector.getVersion(element);

			if (version > ModelVersionDistributor.getCurrentVersion()) {
				ExceptionHelper.reportRuntimeException(UNKNOWN_VERSION + version);
			}

			createXomAnalyser(version);
			return getXomAnalyser().parseRoot(element);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	private void createXomAnalyser(int version) throws ParserException {
		if (fXomAnalyser == null) {
			fXomAnalyser = XomAnalyserFactory.createXomAnalyser(version);
		}			
	}

	private XomAnalyser getXomAnalyser() throws ParserException {
		if (fXomAnalyser == null) {
			ParserException.report("XomAnalyzer must not be null.");
		}
		return fXomAnalyser;
	}

	@Override
	public ClassNode parseClass(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseClass(document.getRootElement(), null);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public MethodNode parseMethod(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseMethod(document.getRootElement(), null);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public GlobalParameterNode parseGlobalParameter(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseGlobalParameter(document.getRootElement());
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public MethodParameterNode parseMethodParameter(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseMethodParameter(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public ChoiceNode parseChoice(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoice(document.getRootElement());
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public TestCaseNode parseTestCase(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseTestCase(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public ConstraintNode parseConstraint(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseConstraint(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public AbstractStatement parseStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public StaticStatement parseStaticStatement(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStaticStatement(document.getRootElement());
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public ChoicesParentStatement parseChoicesParentStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoiceStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	@Override
	public ExpectedValueStatement parseExpectedValueStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseExpectedValueStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		}
	}

	@Override
	public StatementArray parseStatementArray(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatementArray(document.getRootElement(), method);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new StatementArray(null);
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new StatementArray(null);
		}
	}
}