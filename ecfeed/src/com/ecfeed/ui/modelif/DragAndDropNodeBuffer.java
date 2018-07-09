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
import java.util.Iterator;
import java.util.List;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.utils.SessionAttributes;

public class DragAndDropNodeBuffer {

	public static void setDraggedNodes(List<AbstractNode> nodes) {
		removeDuplicatedChildren(nodes);
		SessionDataStore.set(SessionAttributes.SA_DRAG_AND_DROP_NODE_BUFFER, nodes);
	}

	public static List<AbstractNode> getDraggedNodes() {

		@SuppressWarnings("unchecked")
		List<AbstractNode> nodes 
		= (List<AbstractNode>) SessionDataStore.get(SessionAttributes.SA_DRAG_AND_DROP_NODE_BUFFER);

		if (nodes == null) {
			return new ArrayList<AbstractNode>();
		}
		return nodes;
	}

	public static List<AbstractNode> getDraggedNodesCopy() {

		List<AbstractNode> result = new ArrayList<>();
		List<AbstractNode> draggedNodes = getDraggedNodes();

		if (draggedNodes == null) {
			return result;
		}

		for (AbstractNode node : draggedNodes) {
			result.add(node.makeClone());
		}

		return result;
	}

	public static void clear() {

		List<AbstractNode> nodes = new ArrayList<AbstractNode>();
		setDraggedNodes(nodes);
	}

	private static void removeDuplicatedChildren(List<AbstractNode> nodes) {

		Iterator<AbstractNode> it = nodes.iterator();

		while (it.hasNext()) {
			AbstractNode node = it.next();
			AbstractNode parent = node.getParent();

			while (parent != null) {
				if (nodes.contains(parent)) {
					it.remove();
					break;
				}

				parent = parent.getParent();
			}
		}
	}
}
