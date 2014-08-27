package com.testify.ecfeed.modelif.java.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class BulkOperation implements IModelOperation{

	List<IModelOperation> fOperations;
	List<IModelOperation> fExecutedOperations;
	// either all operation or none. if false, all operations are executed
	// otherwise after first error the reverse operation is called
	private boolean fAtomic;
	private boolean fModelUpdated; 
	
	public BulkOperation(boolean atomic) {
		fModelUpdated = false;
		fOperations = new ArrayList<IModelOperation>();
		fExecutedOperations = new ArrayList<IModelOperation>();
		fAtomic = atomic;
	}
	
	public BulkOperation(List<IModelOperation> operations) {
		fOperations = operations;
	}
	
	protected void addOperation(IModelOperation operation) {
		fOperations.add(operation);
	}
	
	@Override
	public void execute() throws ModelIfException {
		Set<String> errors = new HashSet<String>();
		for(IModelOperation operation : fOperations){
			try{
				operation.execute();
				fModelUpdated = true;
				fExecutedOperations.add(operation);
			}catch(ModelIfException e){
				errors.add(e.getMessage());
				if(fAtomic){
					reverseOperation().execute();
					break;
				}
			}
		}
		if(errors.size() > 0){
			String message = Messages.PROBLEM_WITH_BULK_OPERATION;
			for(String error : errors){
				message += "\n" + error;
			}
			throw new ModelIfException(message);
		}
	}

	@Override
	public IModelOperation reverseOperation(){
		return new BulkOperation(reverseOperations());
	}
	
	
	protected List<IModelOperation> operations(){
		return fOperations;
	}

	protected List<IModelOperation> executedOperations(){
		return fExecutedOperations;
	}

	protected List<IModelOperation> reverseOperations(){
		List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();
		for(IModelOperation operation : executedOperations()){
			reverseOperations.add(0, operation);
		}
		return reverseOperations;
	}

	public boolean modelUpdated() {
		return fModelUpdated;
	}
}