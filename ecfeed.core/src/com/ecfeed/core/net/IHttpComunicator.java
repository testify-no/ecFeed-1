/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.net;

import java.io.IOException;
import java.util.List;


public interface IHttpComunicator {

	public String sendGetRequest(String url, List<HttpProperty> properties) throws IOException;

}
