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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.core.adapter.java.JavaPrimitiveTypePredicate;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RelationStatement.ChoiceCondition;
import com.ecfeed.core.model.RelationStatement.ICondition;
import com.ecfeed.core.model.RelationStatement.LabelCondition;
import com.ecfeed.core.model.RelationStatement.ParameterCondition;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.IRelationalStatement;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.AbstractStatementInterface;
import com.ecfeed.ui.modelif.ConstraintInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.StatementInterfaceFactory;

public class StatementEditor extends Composite {

	private final String STATEMENT_FALSE = new StaticStatement(false).getLeftOperandName();
	private final String STATEMENT_TRUE = new StaticStatement(true).getLeftOperandName();
	private final String STATEMENT_AND = new StatementArray(EStatementOperator.AND).getLeftOperandName();
	private final String STATEMENT_OR = new StatementArray(EStatementOperator.OR).getLeftOperandName();
	private final String[] FIXED_STATEMENTS = {STATEMENT_FALSE, STATEMENT_TRUE, STATEMENT_OR, STATEMENT_AND};

	private final int STATEMENT_COMBO_WIDTH = 3;
	private final int RELATION_COMBO_WIDTH = 1;
	private final int CONDITION_COMBO_WIDTH = 4;
	private final int TOTAL_EDITOR_WIDTH = STATEMENT_COMBO_WIDTH + RELATION_COMBO_WIDTH + CONDITION_COMBO_WIDTH;

	private AbstractStatementInterface fStatementIf;
	private AbstractStatement fSelectedStatement;
	private StructuredViewer fStructuredViewer;
	private IModelUpdateContext fModelUpdateContext;

	private Combo fStatementCombo;
	private Combo fRelationCombo;
	private Combo fConditionCombo;
	private Composite fRightOperandComposite;

	private ConstraintNode fConstraint;
	private ConstraintInterface fConstraintIf;
	private IFileInfoProvider fFileInfoProvider;

	public StatementEditor(
			Composite parent, IFileInfoProvider fileInfoProvider, 
			StructuredViewer structuredViewer, IModelUpdateContext updateContext) {

		super(parent, SWT.NONE);
		fFileInfoProvider = fileInfoProvider;
		fStructuredViewer = structuredViewer;
		fModelUpdateContext = updateContext;

		setLayout(new GridLayout(TOTAL_EDITOR_WIDTH, true));
		setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fConstraintIf = new ConstraintInterface(updateContext, fFileInfoProvider);

		createStatementCombo(this);
		createRelationCombo(this);
	}

	public void setInput(AbstractStatement statement) {

		if (statement == null) {
			return;
		}

		fSelectedStatement = statement;
		fStatementIf = StatementInterfaceFactory.getInterface(statement, fModelUpdateContext);
		fStatementCombo.setItems(getStatementComboItems(statement));
		fStatementCombo.setText(statement.getLeftOperandName());

		buildEditor(statement);
	}
	
	private void buildEditor(AbstractStatement statement) {
		try {
			statement.accept(new EditorBuilder(fFileInfoProvider));
		}catch(Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	public void setConstraintNode(ConstraintNode constraintNode) {

		fConstraint = constraintNode;
		fConstraintIf.setTarget(constraintNode);
	}

	public void refreshConditionCombo() {

		try {
			if (fConditionCombo != null) {
				String[] items = getAvailableConditions(fSelectedStatement);
				fConditionCombo.setItems(items);

				String currentConditionText = (String)fSelectedStatement.accept(new CurrentConditionProvider());
				fConditionCombo.setText(currentConditionText);
			}
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}

	public AbstractStatementInterface getStatementIf() {
		return fStatementIf;
	}

	private void createStatementCombo(Composite parent) {

		fStatementCombo = new ComboViewer(parent).getCombo();
		fStatementCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, STATEMENT_COMBO_WIDTH, 1));
		fStatementCombo.addSelectionListener(new StatementComboListener());
	}

	private void createRelationCombo(Composite parent) {

		fRelationCombo = new ComboViewer(parent).getCombo();
		fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, RELATION_COMBO_WIDTH, 1));
		fRelationCombo.addSelectionListener(new RelationComboListener());
	}

	private String[] getAvailableConditions(AbstractStatement statement) {

		try {
			return (String[]) statement.accept(new AvailableConditionsProvider());
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
		return new String[]{};
	}

	private String[] getStatementComboItems(AbstractStatement statement) {

		List<String> items = new ArrayList<String>();
		items.addAll(Arrays.asList(FIXED_STATEMENTS));
		boolean consequence = fConstraint.getConstraint().getConsequence() == statement;

		for (MethodParameterNode c : fConstraint.getMethod().getMethodParameters()) {
			if (c.isExpected()) {
				if (consequence) {
					items.add(c.getName());
				}
			}
			else {
				if (c.getChoices().size() > 0) {
					items.add(c.getName());
				}
			}
		}
		return items.toArray(new String[]{});
	}

	private class AvailableConditionsProvider implements IStatementVisitor {

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new String[]{};
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {
			return new String[]{};
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {

			MethodParameterNode parameter  = statement.getParameter();
			List<String> values = AbstractParameterInterface.getSpecialValues(parameter.getType());

			if (values.isEmpty()) {
				for (ChoiceNode p : parameter.getLeafChoices()) {
					values.add(p.getValueString());
				}
			}

			return values.toArray(new String[]{});
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {

			List<String> conditions = new ArrayList<String>();
			MethodParameterNode methodParameterNode = statement.getParameter();

			addChoices(methodParameterNode, conditions);
			addLabels(methodParameterNode, conditions);
			addParameters(methodParameterNode, conditions);

			return conditions.toArray(new String[]{});
		}
		
		private void addChoices(MethodParameterNode methodParameterNode, List<String> inOutConditions) {
			
			Set<ChoiceNode> allChoices = methodParameterNode.getAllChoices();
			
			for (ChoiceNode choice : allChoices) {
				
				ICondition condition = 
						new RelationStatement(
								methodParameterNode, EStatementRelation.EQUAL, choice).getCondition();

				inOutConditions.add(condition.toString());
			}
		}
		
		private void addLabels(MethodParameterNode methodParameterNode, List<String> inOutConditions) {
			
			Set<String> allLabels = methodParameterNode.getLeafLabels();
			
			for (String label : allLabels) {
				
				ICondition condition = 
						new RelationStatement(
								methodParameterNode, EStatementRelation.EQUAL, label).getCondition();
				
				inOutConditions.add(condition.toString());
			}
		}
		
		private void addParameters(MethodParameterNode methodParameterNode, List<String> inOutConditions) {
			
			List<String> parameterNames = methodParameterNode.getMethod().getParametersNames();
			parameterNames.remove(methodParameterNode.getName());
			
			for (String parameterName : parameterNames) {
				
				inOutConditions.add(parameterName + "[parameter]");
			}
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new String[]{};
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			return new String[]{};
		}

		@Override
		public Object visit(ParameterCondition condition) throws Exception {
			return new String[]{};
		}
	}

	private class CurrentConditionProvider implements IStatementVisitor {

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return "";
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {
			return "";
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			return statement.getCondition().getValueString();
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {
			return statement.getCondition().toString();
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return "";
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			return "";
		}

		@Override
		public Object visit(ParameterCondition condition) throws Exception {
			return "";
		}

	}

	private class ConditionComboListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			applyNewValue();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			applyNewValue();
		}

		protected void applyNewValue() {

			fStatementIf.setConditionValue(fConditionCombo.getText());
			String newText = fStatementIf.getConditionValue();
			fConditionCombo.setText(newText);
		}

	}

	private class ConditionComboFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {

			fStatementIf.setConditionValue(fConditionCombo.getText());
			String newText = fStatementIf.getConditionValue();
			fConditionCombo.setText(newText);
		}

	}

	private class StatementComboListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {

			EStatementOperator operator = EStatementOperator.getOperator(fStatementCombo.getText());

			if (fStatementIf.getOperator() != null && operator != null) {
				if (operator != fStatementIf.getOperator()) {
					fStatementIf.setOperator(operator);
				}
			}
			else {
				AbstractStatement statement = buildStatement();
				if (statement != null) {
					AbstractStatementInterface parentIf = fStatementIf.getParentInterface();
					boolean result = false;
					if (parentIf != null) {
						result = parentIf.replaceChild(fSelectedStatement, statement);
					}
					else {
						result = fConstraintIf.replaceStatement(fSelectedStatement, statement);
					}
					if (result) {
						fStructuredViewer.setSelection(new StructuredSelection(statement));

					}
				}
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		private AbstractStatement buildStatement() {

			String statementText = fStatementCombo.getText();

			if (statementText.equals(STATEMENT_TRUE) || statementText.equals(STATEMENT_FALSE)) {
				return new StaticStatement(Boolean.parseBoolean(statementText));
			}

			if (statementText.equals(STATEMENT_AND) || statementText.equals(STATEMENT_OR)) {
				return new StatementArray(EStatementOperator.getOperator(statementText));
			}

			MethodParameterNode parameter = fConstraint.getMethod().getMethodParameter(statementText);
			EStatementRelation relation = EStatementRelation.EQUAL;

			if (parameter != null && parameter.isExpected()) {

				ChoiceNode condition = new ChoiceNode("expected", parameter.getDefaultValue());
				condition.setParent(parameter);

				return new ExpectedValueStatement(parameter, condition, new JavaPrimitiveTypePredicate());
			}
			else if (parameter != null && parameter.getChoices().size() > 0) {

				ChoiceNode condition = parameter.getChoices().get(0);
				return new RelationStatement(parameter, relation, condition);
			}

			return null;
		}
	}

	private class RelationComboListener implements SelectionListener{

		@Override
		public void widgetSelected(SelectionEvent e) {

			EStatementRelation relation = EStatementRelation.getRelation(fRelationCombo.getText());
			fStatementIf.setRelation(relation);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private class EditorBuilder implements IStatementVisitor {

		public EditorBuilder(IFileInfoProvider fileInfoProvider) {
			fFileInfoProvider = fileInfoProvider;
		}

		@Override
		public Object visit(StaticStatement statement) throws Exception {

			fRelationCombo.setVisible(false);
			if (fRightOperandComposite != null) {
				fRightOperandComposite.setVisible(false);
			}

			StatementEditor.this.redraw();
			return null;
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {

			fRelationCombo.setVisible(false);

			if (fRightOperandComposite != null) {
				fRightOperandComposite.setVisible(false);
			}

			StatementEditor.this.redraw();
			return null;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {

			disposeRightOperandComposite();

			if (AbstractParameterInterface.hasLimitedValuesSet(statement.getParameter())) {
				fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this).getCombo();
			} else {
				fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this, SWT.BORDER).getCombo();
			}

			prepareRelationalStatementEditor(
					statement, getAvailableConditions(statement), statement.getCondition().getValueString());

			StatementEditor.this.layout();

			return null;
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {

			disposeRightOperandComposite();
			fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this).getCombo();
			prepareRelationalStatementEditor(
					statement, getAvailableConditions(statement), statement.getCondition().toString());

			StatementEditor.this.layout();
			return null;
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			return null;
		}
		
		@Override
		public Object visit(ParameterCondition condition) throws Exception {
			return null;
		}		

		private void disposeRightOperandComposite() {

			if (fRightOperandComposite != null && fRightOperandComposite.isDisposed() == false) {
				fRightOperandComposite.dispose();
			}
		}

		private void prepareRelationalStatementEditor(IRelationalStatement statement, String[] items, String item) {

			fRelationCombo.setVisible(true);
			fRelationCombo.setItems(getAvailableRelations(statement));
			fRelationCombo.setText(statement.getRelation().toString());

			fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, CONDITION_COMBO_WIDTH, 1));
			fConditionCombo.setItems(items);
			fConditionCombo.setText(item);
			fConditionCombo.addSelectionListener(new ConditionComboListener());
			fConditionCombo.addFocusListener(new ConditionComboFocusLostListener());

			StatementEditor.this.layout();
		}

		private String[] getAvailableRelations(IRelationalStatement statement) {

			List<String> relations = new ArrayList<String>();

			for (EStatementRelation r : statement.getAvailableRelations()) {
				relations.add(r.toString());
			}

			return relations.toArray(new String[]{});
		}

	}

}
