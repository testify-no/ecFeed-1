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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.utils.SessionAttributes;

public class NodeClipboard {

	@SuppressWarnings("unchecked")
	public static List<AbstractNode> getContent() {

		List<AbstractNode> content = 
				(List<AbstractNode>)SessionDataStore.get(SessionAttributes.SA_CLIPBOARD_NODES);

		if (content != null) {
			return content;
		}

		return new ArrayList<AbstractNode>();
	}

	public static List<AbstractNode> getContentCopy() {

		List<AbstractNode> clipboardNodes = getContent();
		List<AbstractNode> copy = new ArrayList<AbstractNode>();

		for (AbstractNode node : clipboardNodes) {
			copy.add(node.makeClone());
		}

		return copy;
	}

	public static void setContent(AbstractNode node){

		List<AbstractNode> nodes = new ArrayList<AbstractNode>();
		nodes.add(node.makeClone());
		setContent(nodes);
	}

	public static void setContent(List<AbstractNode> oldNodes){

		List<AbstractNode> newNodes = new ArrayList<AbstractNode>();

		for (AbstractNode node : oldNodes) {
			if (isPredecessorInCollection(node, oldNodes) == false) {
				newNodes.add(node.makeClone());
			}
		}

		SessionDataStore.set(SessionAttributes.SA_CLIPBOARD_NODES, newNodes);
	}

	private static boolean isPredecessorInCollection(AbstractNode node, Collection<AbstractNode> nodes) {

		AbstractNode predecessor = node.getParent();

		while (predecessor != null) {
			if (nodes.contains(predecessor)) {
				return true;
			}

			predecessor = predecessor.getParent();
		}

		return false;
	}
}
