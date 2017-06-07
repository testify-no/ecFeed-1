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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ecfeed.core.net.HttpCommunicator;
import com.ecfeed.core.net.HttpProperty;
import com.ecfeed.core.net.IHttpComunicator;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.utils.EclipseHelper;

public class HttpCommunicatorWithProgress implements IHttpComunicator {

	public String sendGetRequest(String url, List<HttpProperty> properties, int timeoutInSeconds) throws RuntimeException {

		CommunicatorRunnable communicatorRunnable = new CommunicatorRunnable(url, properties, timeoutInSeconds);

		ProgressMonitorDialog progressMonitorDialog = 
				new ProgressMonitorDialog(EclipseHelper.getActiveShell());

		progressMonitorDialog.setCancelable(false);

		try {
			progressMonitorDialog.run(true, true, communicatorRunnable);
		} catch (InvocationTargetException | InterruptedException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());;
			return null;
		}

		String errorMessage = communicatorRunnable.getErrorMessage(); 
		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
			return null;			
		}

		return communicatorRunnable.getResponse();
	}

	private static class CommunicatorRunnable implements IRunnableWithProgress {

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
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

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
