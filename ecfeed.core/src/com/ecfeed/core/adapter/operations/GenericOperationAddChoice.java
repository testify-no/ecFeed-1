/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.StringHelper;

public class GenericOperationAddChoice extends BulkOperation {

	public GenericOperationAddChoice(
			ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, int index, boolean validate) {

		super(OperationNames.ADD_PARTITION, true);
		addOperation(new AddChoiceOperation(target, choice, adapterProvider, index));

		for (MethodNode method : target.getParameter().getMethods()) {
			if((method != null) && validate){
				addOperation(new MethodOperationMakeConsistent(method));
			}
		}
	}

	public GenericOperationAddChoice(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, boolean validate) {

		this(target, choice, adapterProvider, -1, validate);
	}

	private class AddChoiceOperation extends AbstractModelOperation {
		private ChoicesParentNode fChoicesParentNode;
		private ChoiceNode fChoice;
		private int fIndex;
		private ITypeAdapterProvider fAdapterProvider;

		public AddChoiceOperation(ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, int index) {

			super(OperationNames.ADD_PARTITION);
			fChoicesParentNode = target;
			fChoice = choice;
			fIndex = index;
			fAdapterProvider = adapterProvider;
		}

		@Override
		public void execute() throws ModelOperationException {

			generateUniqueChoiceName(fChoice);

			if(fIndex == -1) {
				fIndex = fChoicesParentNode.getChoices().size();
			}
			if(fChoicesParentNode.getChoiceNames().contains(fChoice.getName())){
				ModelOperationException.report(Messages.CHOICE_NAME_DUPLICATE_PROBLEM(fChoicesParentNode.getName(), fChoice.getName()));
			}
			if(fIndex < 0){
				ModelOperationException.report(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fChoicesParentNode.getChoices().size()){
				ModelOperationException.report(Messages.TOO_HIGH_INDEX_PROBLEM);
			}

			validateChoiceValue(fChoice);
			fChoicesParentNode.addChoice(fChoice, fIndex);

			markModelUpdated();
		}

		private void generateUniqueChoiceName(ChoiceNode choiceNode) {

			String oldName = choiceNode.getName();
			String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);
			String newName = ChoicesParentNode.generateNewChoiceName(fChoicesParentNode, oldNameCore);

			choiceNode.setName(newName);
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new GenericOperationRemoveChoice(fChoicesParentNode, fChoice, fAdapterProvider, false);
		}

		//TODO 13
		private void validateChoiceValue(ChoiceNode choice) throws ModelOperationException {

			if (choice.isAbstract() == false) {

				String type = fChoicesParentNode.getParameter().getType();
				ITypeAdapter<?> adapter = fAdapterProvider.getAdapter(type);
				String newValue = 
						adapter.convert(
								choice.getValueString(), choice.isRandomizedValue(), EConversionMode.QUIET);

				if(newValue == null){
					ModelOperationException.report(Messages.PARTITION_VALUE_PROBLEM(choice.getValueString()));
				}
			}
			else {
				for(ChoiceNode child : choice.getChoices()) {
					validateChoiceValue(child);
				}
			}
		}
	}

}
