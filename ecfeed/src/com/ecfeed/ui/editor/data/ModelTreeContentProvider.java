package com.ecfeed.ui.editor.data;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.CommonConstants;
import com.ecfeed.ui.modelif.AbstractParameterInterface;

public class ModelTreeContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {

	public final Object[] EMPTY_ARRAY = {};

	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof ModelWrapper) {
			RootNode root = ((ModelWrapper)inputElement).getModel();
			return new Object[]{root};
		}

		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		//Because of potentially large amount of children, MethodNode is special case
		//We filter out test suites with too many test cases
		if (parentElement instanceof MethodNode) {

			MethodNode method = (MethodNode)parentElement;
			ArrayList<Object> children = new ArrayList<Object>();

			children.addAll(method.getParameters());
			children.addAll(method.getConstraintNodes());

			for (String testSuite : method.getTestSuites()) {
				Collection<TestCaseNode> testCases = method.getTestCases(testSuite);

				if(testCases.size() <= CommonConstants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
					children.addAll(testCases);
				}
			}

			return children.toArray();
		}

		if (parentElement instanceof MethodParameterNode) {

			MethodParameterNode parameter = (MethodParameterNode)parentElement;
			if (parameter.isExpected() && AbstractParameterInterface.isPrimitive(parameter.getType())) {
				return EMPTY_ARRAY;
			}

			if (parameter.isLinked()) {
				return EMPTY_ARRAY;
			}
		}

		if (parentElement instanceof AbstractNode) {
			AbstractNode node = (AbstractNode)parentElement;

			if (node.getChildren().size() < CommonConstants.MAX_DISPLAYED_CHILDREN_PER_NODE) {
				return node.getChildren().toArray();
			}
		}

		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {

		if (element instanceof AbstractNode) {
			return ((AbstractNode)element).getParent();
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
