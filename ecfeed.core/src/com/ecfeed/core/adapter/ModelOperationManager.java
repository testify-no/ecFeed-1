/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ModelOperationException;

public class ModelOperationManager {
	private List<IModelOperation> fHistory;
	private int fHistoryIndex = 0;

	public ModelOperationManager(){
		fHistory = new ArrayList<IModelOperation>();
	}

	protected void updateHistory(IModelOperation operation){
		if(fHistory.size() > fHistoryIndex){
			fHistory = fHistory.subList(0, fHistoryIndex);
		}
		else if(fHistory.size() < fHistoryIndex){
			//shouldn't happen, but let's protect against it.
			fHistoryIndex = fHistory.size();
		}

		fHistory.add(operation);
		++fHistoryIndex;
	}

	public boolean undoEnabled(){
		return fHistoryIndex > 0;
	}

	public boolean redoEnabled(){
		return fHistoryIndex < fHistory.size();
	}

	public void undo() throws ModelOperationException{
		if(fHistoryIndex > 0){
			IModelOperation operation = fHistory.get(fHistoryIndex - 1).reverseOperation();
			operation.execute();
			--fHistoryIndex;
		}
	}

	public void redo() throws ModelOperationException{
		if(fHistoryIndex < fHistory.size()){
			IModelOperation operation = fHistory.get(fHistoryIndex);
			operation.execute();
			++fHistoryIndex;
		}
	}

	public void execute(IModelOperation operation) throws ModelOperationException{
		operation.execute();
		updateHistory(operation);
	}

}
