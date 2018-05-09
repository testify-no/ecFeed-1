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
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ecfeed.core.model.AbstractNode;

public abstract class NodeSelectionHelper {

	public static List<AbstractNode> getSelectedNodes(ISelection selection) {

		return getSelectedNodes(getStructuredSelection(selection));
	}

	private static List<AbstractNode> getSelectedNodes(IStructuredSelection structuredSelection) {

		if (structuredSelection == null) {
			return null;
		}

		List<AbstractNode> result = new ArrayList<>();

		for (Object obj : structuredSelection.toList()) {

			if (obj instanceof AbstractNode) {
				result.add((AbstractNode)obj);
			}
		}

		return result;
	}

	public static AbstractNode getFirstSelectedNode(ISelection selection) {

		return getFirstSelectedNode(getStructuredSelection(selection));
	}

	private static AbstractNode getFirstSelectedNode(IStructuredSelection structuredSelection) {

		if (structuredSelection == null) {
			return null;
		}

		List<AbstractNode> selectedNodes = getSelectedNodes(structuredSelection);

		if(selectedNodes.size() == 0) {
			return null;
		}

		return selectedNodes.get(0);
	}

	private static IStructuredSelection getStructuredSelection(ISelection selection) {

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		return (IStructuredSelection)selection;
	}
}
