/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.runner.RunnerException;

public class CollectiveOnlineRunner extends AbstractOnlineRunner {

	public CollectiveOnlineRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected void addFrameworkMethods(
			FrameworkMethod frameworkMethod,
			IGenerator<ChoiceNode> initializedGenerator,
			List<FrameworkMethod> inOutFrameworkMethods) throws RunnerException {

		inOutFrameworkMethods.add(
				new CollectiveOnlineRunnerMethod(
						frameworkMethod.getMethod(), 
						initializedGenerator, 
						getLoader()));
	}

}
