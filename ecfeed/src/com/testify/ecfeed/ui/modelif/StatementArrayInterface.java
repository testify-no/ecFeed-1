package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.operations.StatementOperationAddStatement;
import com.testify.ecfeed.modelif.operations.StatementOperationChangeOperator;
import com.testify.ecfeed.modelif.operations.StatementOperationRemoveStatement;
import com.testify.ecfeed.modelif.operations.StatementOperationReplaceChild;

public class StatementArrayInterface extends BasicStatementInterface{

	private StatementArray fTarget;
	
	public StatementArrayInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(StatementArray target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean addStatement(BasicStatement statement, AbstractFormPart source, IModelUpdateListener updateListener){
		IModelOperation operation = new StatementOperationAddStatement(fTarget, statement, fTarget.getChildren().size()); 
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
	}
	
	public boolean removeChild(BasicStatement child, AbstractFormPart source, IModelUpdateListener updateListener){
		IModelOperation operation = new StatementOperationRemoveStatement(fTarget, child); 
		return execute(operation, source, updateListener, Messages.DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean setOperator(Operator operator, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(operator != fTarget.getOperator()){
			IModelOperation operation = new StatementOperationChangeOperator(fTarget, operator); 
			return execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public Operator getOperator() {
		return fTarget.getOperator();
	}

	@Override
	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(child != newStatement){
			return execute(new StatementOperationReplaceChild(fTarget, child, newStatement), source, updateListener, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

}
