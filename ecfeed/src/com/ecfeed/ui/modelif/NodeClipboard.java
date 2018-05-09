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

import com.ecfeed.core.model.AbstractNode;

public class NodeClipboard{

	private static List<AbstractNode> fClipboardNodes = new ArrayList<>();

	public static List<AbstractNode> getContent(){
		return fClipboardNodes;
	}

	public static List<AbstractNode> getContentCopy(){
		List<AbstractNode> copy = new ArrayList<AbstractNode>();
		for(AbstractNode node : fClipboardNodes){
			copy.add(node.makeClone());
		}
		return copy;
	}

	public static void setContent(AbstractNode node){
		fClipboardNodes.clear();
		fClipboardNodes.add(node.makeClone());
	}

	public static void setContent(List<AbstractNode> nodes){

		fClipboardNodes.clear();
		for(AbstractNode node : nodes){
			if(isPredecessorInCollection(node, nodes) == false){
				fClipboardNodes.add(node.makeClone());
			}
		}
	}

	private static boolean isPredecessorInCollection(AbstractNode node, Collection<AbstractNode> nodes) {
		AbstractNode predecessor = node.getParent();
		while(predecessor != null){
			if(nodes.contains(predecessor)){
				return true;
			}
			predecessor = predecessor.getParent();
		}
		return false;
	}
}
