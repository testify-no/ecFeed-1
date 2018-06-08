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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.SystemLogger;

public class OperationExecuter {

	private IModelUpdateContext fModelUpdateContext;
	private IOperationHistory fOperationHistory;

	public OperationExecuter(IModelUpdateContext updateContext) {

		fOperationHistory = OperationHistoryFactory.getOperationHistory();

		if (updateContext != null) {
			fModelUpdateContext = updateContext;
		}
	}

	public boolean execute(IModelOperation operation, String errorMessageTitle) {

		try {
			UndoableOperation action = 
					new UndoableOperation(
							operation, getUpdateContext().getUndoContext(), errorMessageTitle);

			fOperationHistory.execute(action, null, null);
			return true;
		} catch (ExecutionException e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return false;
	}

	protected IModelUpdateContext getUpdateContext() {
		return fModelUpdateContext;
	}

	private class UndoableOperation extends AbstractOperation {

		private IModelOperation fOperation;
		private String fErrorMessageTitle;


		public UndoableOperation(IModelOperation operation, IUndoContext context, String errorMessageTitle) {
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

		private IStatus executeOperation(IModelOperation operation, IProgressMonitor monitor, IAdaptable info) {

			try {
				operation.execute();

				CachedImplementationStatusResolver.clearCache();
				fModelUpdateContext.notifyUpdateListeners(operation.getNodesToSelect());

				return Status.OK_STATUS;

			} catch (ModelOperationException e) {

				fModelUpdateContext.notifyUpdateListeners(null);
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						fErrorMessageTitle,
						e.getMessage());

				return operation.modelUpdated()?Status.OK_STATUS:Status.CANCEL_STATUS;
			}
		}

	}

}

