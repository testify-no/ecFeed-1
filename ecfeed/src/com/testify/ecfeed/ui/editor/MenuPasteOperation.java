/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/
package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import static com.testify.ecfeed.utils.ModelUtils.setUniqueNodeName;

public class MenuPasteOperation extends MenuOperation{
	protected IGenericNode fSource;
	protected IGenericNode fTarget;
	protected ModelMasterSection fModel;
	protected final String DIALOG_OPERATION_FAILED_TITLE = "Paste failed";
	protected final String DIALOG_OPERATION_FAILED_MESSAGE = "Clipboard content doesn't match here.";

	@Override
	public void execute(){
		if(!paste()){
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), DIALOG_OPERATION_FAILED_TITLE,
					DIALOG_OPERATION_FAILED_MESSAGE);
		} else{
			fModel.markDirty();
			fModel.refresh();
		}
	}

	@Override
	public boolean isEnabled(){
		if(fSource != null && fTarget != null){
			if(fTarget instanceof PartitionNode && fSource instanceof PartitionNode)
				return true;
			if(fTarget instanceof CategoryNode && fSource instanceof PartitionNode)
				return true;
			if(fTarget instanceof MethodNode && 
					(fSource instanceof CategoryNode || fSource instanceof TestCaseNode || fSource instanceof ConstraintNode))
				return true;
			if(fTarget instanceof ClassNode && fSource instanceof MethodNode)
				return true;
			if(fTarget instanceof RootNode && fSource instanceof ClassNode)
				return true;
		}
		return false;
	}

	public MenuPasteOperation(IGenericNode target, IGenericNode source, ModelMasterSection model){
		super("Paste");
		fSource = source;
		fTarget = target;
		fModel = model;
	}

	public boolean paste(){
		if(fSource != null && fTarget != null){
			if(fTarget instanceof PartitionNode){
				PartitionNode target = (PartitionNode)fTarget;
				if(fSource instanceof PartitionNode){
					PartitionNode source = (PartitionNode)fSource;
					if(target.getCategory().getType().equals(source.getCategory().getType())){
						setUniqueNodeName(source, target);
						target.addPartition(source);
						return true;
					}
				}
			} else if(fTarget instanceof CategoryNode){
				CategoryNode target = (CategoryNode)fTarget;
				if(!target.isExpected()){
					if(fSource instanceof PartitionNode){
						PartitionNode source = (PartitionNode)fSource;
						if(target.getType().equals(source.getCategory().getType())){
							setUniqueNodeName(source, target);
							target.addPartition(source);
							return true;
						}
					}
				}
			} else if(fTarget instanceof MethodNode){
				MethodNode target = (MethodNode)fTarget;
				if(fSource instanceof CategoryNode){
					CategoryNode source = (CategoryNode)fSource;
					setUniqueNodeName(source, target);
					target.addCategory(source);
					target.clearTestCases();
					return true;
				} else if(fSource instanceof ConstraintNode){
					ConstraintNode source = (ConstraintNode)fSource;
					if(source.updateReferences(target)){
						target.addConstraint(source);
						return true;
					}
				} else if(fSource instanceof TestCaseNode){
					TestCaseNode source = (TestCaseNode)fSource.getCopy();
					if(source.updateReferences(target)){
						target.addTestCase(source);
						return true;
					}
				}
			} else if(fTarget instanceof ClassNode){
				ClassNode target = (ClassNode)fTarget;
				if(fSource instanceof MethodNode){
					MethodNode source = (MethodNode)fSource;
					setUniqueNodeName(source, target);
					target.addMethod(source);
					return true;
				}
			} else if(fTarget instanceof RootNode){
				RootNode target = (RootNode)fTarget;
				if(fSource instanceof ClassNode){
					ClassNode source = (ClassNode)fSource;
					setUniqueNodeName(source, target);
					target.addClass(source);
					return true;
				}
			}
		}
		return false;
	}

}
