/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.forms.AbstractFormPart;

import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;

public class OperationExecuter {

	private IModelUpdateContext fUpdateContext;
	private List<IModelUpdateListener> fUpdateListeners;
	private IOperationHistory fOperationHistory;

	private class UndoableOperation extends AbstractOperation{

		private IModelOperation fOperation;
		private String fErrorMessageTitle;


		public UndoableOperation(IModelOperation operation, IUndoContext context, String errorMessageTitle){
			super(operation.getName());
			fOperation = operation;
			fErrorMessageTitle = errorMessageTitle;
			addContext(context);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation, monitor, info);
		}


		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation, monitor, info);
		}


		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation.getReverseOperation(), monitor, info);
		}

		private IStatus executeOperation(IModelOperation operation, IProgressMonitor monitor, IAdaptable info){
			try {
				executeWithoutExceptionLogging(operation);

				CachedImplementationStatusResolver.clearCache();
				updateListeners();
				return Status.OK_STATUS;
			} catch (ModelOperationException e) {
				updateListeners();
				ErrorDialog.open(fErrorMessageTitle, e.getMessage());

				return operation.modelUpdated()?Status.OK_STATUS:Status.CANCEL_STATUS;
			}
		}

		private void executeWithoutExceptionLogging(IModelOperation operation) throws ModelOperationException {
			try {
				ModelOperationException.pushLoggingState(false);
				operation.execute();
			} finally {
				ModelOperationException.popLoggingState();
			}
		}

		private void updateListeners() {
			if(fUpdateListeners != null){
				for(IModelUpdateListener listener : fUpdateListeners){
					listener.modelUpdated(getSourceForm());
				}
			}
		}
	}

	public OperationExecuter(IModelUpdateContext updateContext){
		fOperationHistory = OperationHistoryFactory.getOperationHistory();
		if(updateContext != null){
			fUpdateContext = updateContext;
			fUpdateListeners = updateContext.getUpdateListeners();
		}
	}

	protected boolean execute(IModelOperation operation, String errorMessageTitle){
		try{
			UndoableOperation action = new UndoableOperation(operation, getUpdateContext().getUndoContext(), errorMessageTitle);
			fOperationHistory.execute(action, null, null);
			return true;
		} catch (ExecutionException e) {SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	protected IModelUpdateContext getUpdateContext(){
		return fUpdateContext;
	}

	private AbstractFormPart getSourceForm(){
		return getUpdateContext().getSourceForm();
	}
}

