/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;

public class GeneratorFactory<E> {

	public final static String GEN_TYPE_N_WISE = "N-wise generator";

	private Map<String, Class<? extends IGenerator<E>>> fAvailableGenerators;

	@SuppressWarnings("unchecked")
	public GeneratorFactory(){
		fAvailableGenerators = new LinkedHashMap<String, Class<? extends IGenerator<E>>>();
		registerGenerator(GEN_TYPE_N_WISE, (Class<? extends IGenerator<E>>) NWiseGenerator.class);
		registerGenerator("Cartesian Product generator", (Class<? extends IGenerator<E>>) CartesianProductGenerator.class);
		registerGenerator("Adaptive random generator", (Class<? extends IGenerator<E>>) AdaptiveRandomGenerator.class);
		registerGenerator("Random generator", (Class<? extends IGenerator<E>>) RandomGenerator.class);
	}

	public Set<String> availableGenerators(){
		return fAvailableGenerators.keySet();
	}

	public IGenerator<E> getGenerator(String name) throws GeneratorException{
		try {
			return fAvailableGenerators.get(name).newInstance();
		} catch (Exception e) {
			GeneratorException.report("Cannot instantiate " + name + ": " + e);
			return null;
		}
	}

	private void registerGenerator(String name, Class<? extends IGenerator<E>> generatorClass) {
		fAvailableGenerators.put(name, generatorClass);
	}
}
