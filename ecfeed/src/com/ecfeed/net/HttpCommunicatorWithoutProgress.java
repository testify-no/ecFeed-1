/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.net;

import java.util.List;

import com.ecfeed.core.net.HttpCommunicator;
import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.net.IHttpCommunicator;
import com.ecfeed.core.utils.ExceptionHelper;

public class HttpCommunicatorWithoutProgress implements IHttpCommunicator {

	public String sendGetRequest(String url, List<HttpProperty> properties, int timeoutInSeconds) throws RuntimeException {

		CommunicatorRunnable communicatorRunnable = new CommunicatorRunnable(url, properties, timeoutInSeconds);

		Thread thread = new Thread(communicatorRunnable);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
			return null;
		}		

		String errorMessage = communicatorRunnable.getErrorMessage(); 
		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
			return null;			
		}

		return communicatorRunnable.getResponse();
	}

	private static class CommunicatorRunnable implements Runnable {

		private String fUrl;
		private List<HttpProperty> fProperties;
		private int fTimeoutInSeconds;
		String fResponse = null;
		String fErrorMessage = null;


		public CommunicatorRunnable(String url, List<HttpProperty> properties, int timeoutInSeconds) {
			fUrl = url;
			fProperties = properties;
			fTimeoutInSeconds = timeoutInSeconds;
		}

		@Override
		public void run() {

			fResponse = null;
			fErrorMessage = null;
			HttpCommunicator httpComunicator = new HttpCommunicator();

			try {
				fResponse = httpComunicator.sendGetRequest(fUrl, fProperties, fTimeoutInSeconds);
			} catch (Exception e) {
				fErrorMessage = e.getMessage();
			}
		}

		public String getErrorMessage() {
			return fErrorMessage;
		}

		public String getResponse() {
			return fResponse;
		}		
	}	
}
