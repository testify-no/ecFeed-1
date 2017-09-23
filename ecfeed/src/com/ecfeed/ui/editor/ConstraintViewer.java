/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.ui.common.ImageManager;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.ActionId;
import com.ecfeed.ui.editor.actions.ModelModifyingAction;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ConstraintViewer extends TreeViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private Button fAddStatementButton;
	private Button fRemoveStatementButton;

	private Constraint fCurrentConstraint;
	private AbstractStatement fSelectedStatement;

	private StatementEditor fStatementEditor;


	public ConstraintViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {

		super(sectionContext, updateContext, javaProjectProvider, STYLE);
		getSection().setText("Constraint editor");

		fAddStatementButton = addButton("Add statement", new AddStatementAdapter());

		fRemoveStatementButton = 
				addButton("Remove statement", 
						new ActionSelectionAdapter(
								new DeleteStatementAction(updateContext), 
								Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		getViewer().addSelectionChangedListener(new StatementSelectionListener());

		fStatementEditor = new StatementEditor(getClientComposite(), javaProjectProvider, getViewer(), updateContext);
		createKeyListener(SWT.DEL, SWT.NONE, new DeleteStatementAction(updateContext));
	}

	@Override
	protected IContentProvider createViewerContentProvider() {
		return new StatementViewerContentProvider();
	}

	@Override
	protected IBaseLabelProvider createViewerLabelProvider() {
		return new StatementViewerLabelProvider();
	}

	@Override
	protected int getButtonsPosition() {
		return BUTTONS_ASIDE;
	}

	@Override
	protected int getViewerStyle() {
		return VIEWER_STYLE;
	}

	public void setInput(ConstraintNode constraintNode) {

		//Update the statement provider before setting input to get the correct images
		fCurrentConstraint = constraintNode.getConstraint();
		super.setInput(constraintNode.getConstraint());

		fStatementEditor.refreshConditionCombo();
		fStatementEditor.setConstraintNode(constraintNode);
		fStatementEditor.setInput(fSelectedStatement);

		getTreeViewer().expandAll();
		if (getSelectedElement() == null) {
			getViewer().setSelection(new StructuredSelection(constraintNode.getConstraint().getPremise()));
		}
	}

	private class StatementViewerContentProvider extends TreeNodeContentProvider {

		public final Object[] EMPTY_ARRAY = {};

		@Override
		public Object[] getElements(Object inputElement) {

			if (inputElement instanceof Constraint) {
				Constraint constraint = (Constraint)inputElement;
				return new Object[]{constraint.getPremise(), constraint.getConsequence()};
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof AbstractStatement) {
				return ((AbstractStatement)parentElement).getChildren().toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {

			if (element instanceof AbstractStatement) {
				return ((AbstractStatement)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {

			if (element instanceof StatementArray) {
				StatementArray statementArray = (StatementArray)element;
				List<AbstractStatement> children = statementArray.getChildren();
				return (children.size() > 0);
			}
			return false;
		}
	}

	private class StatementViewerLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof StatementArray) {
				return ((StatementArray)element).getOperator().toString();
			}
			else if (element instanceof AbstractStatement) {
				return ((AbstractStatement)element).toString();
			}
			return null;
		}

		@Override
		public Image getImage(Object element) {

			if (fCurrentConstraint != null) {
				if (element == fCurrentConstraint.getPremise()) {
					return getImage("premise_statement.gif");
				}
				else if (element == fCurrentConstraint.getConsequence()) {
					return getImage("consequence_statement.gif");
				}
			}
			return null;
		}

		private Image getImage(String file) {
			return ImageManager.getInstance().getImage(file);
		}
	}

	private class AddStatementAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent ev) {

			try {
				AbstractStatement statement = fStatementEditor.getStatementIf().addNewStatement();
				if (statement != null) {
					//modelUpdated must be called before to refresh viewer before selecting the newly added statement
					getTreeViewer().expandToLevel(statement, 1);
					getTreeViewer().setSelection(new StructuredSelection(statement));
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add statement.", e.getMessage());
			}
		}
	}

	public class DeleteStatementAction extends ModelModifyingAction {

		public DeleteStatementAction(IModelUpdateContext updateContext) {
			super(ActionId.DELETE, getTreeViewer(), getModelUpdateContext());
		}

		@Override
		public boolean isEnabled() {
			return fSelectedStatement.getParent() != null;
		}

		@Override
		public void run() {

			AbstractStatement parent = fSelectedStatement.getParent();
			if (parent != null && fStatementEditor.getStatementIf().getParentInterface().removeChild(fSelectedStatement)) {
				getViewer().setSelection(new StructuredSelection(parent));
			}
		}
	}

	private class StatementSelectionListener implements ISelectionChangedListener{

		@Override
		public void selectionChanged(SelectionChangedEvent event) {

			AbstractStatement statement = (AbstractStatement)((StructuredSelection)event.getSelection()).getFirstElement();

			if (statement != null) {
				fSelectedStatement = statement;
				fStatementEditor.setInput(statement);
				updateSideButtons(statement);
			}
		}

		private void updateSideButtons(AbstractStatement selectedStatement) {

			boolean enableAddStatementButton = (selectedStatement instanceof StatementArray || selectedStatement.getParent() != null);
			boolean enableRemoveStatementButton = (selectedStatement.getParent() != null);

			fAddStatementButton.setEnabled(enableAddStatementButton);
			fRemoveStatementButton.setEnabled(enableRemoveStatementButton);
		}

	}

}
