/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.BooleanHelper;

public abstract class AbstractNode{
	private String fName;
	private AbstractNode fParent;
	private String fDescription;
	private NodeProperties fProperties = new NodeProperties();
	protected final List<AbstractNode> EMPTY_CHILDREN_ARRAY = new ArrayList<AbstractNode>();

	public AbstractNode(String name){
		this.fName = name;
	}

	public int getMyIndex(){
		if(getParent() == null){
			return -1;
		}
		return getParent().getChildren().indexOf(this);
	}

	public static List<String> getNames(Collection<AbstractNode> nodes){
		List<String> result = new ArrayList<String>();
		for(AbstractNode node : nodes){
			result.add(node.getName());
		}
		return result;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public void setParent(AbstractNode newParent) {
		fParent = newParent;
	}

	public void setDescription(String desc){
		fDescription = desc;
	}

	public String getDescription(){
		return fDescription;
	}

	public List<? extends AbstractNode> getChildren() {
		return EMPTY_CHILDREN_ARRAY;
	}

	public boolean hasChildren(){
		if(getChildren() != null){
			return (getChildren().size() > 0);
		}
		return false;
	}

	public List<AbstractNode> getAncestors(){
		List<AbstractNode> ancestors;
		AbstractNode parent = getParent();
		if(parent != null){
			ancestors = parent.getAncestors();
			ancestors.add(parent);
		}
		else{
			ancestors = new ArrayList<>();
		}
		return ancestors;
	}

	public AbstractNode getParent(){
		return fParent;
	}

	public AbstractNode getRoot(){
		if(getParent() == null){
			return this;
		}
		return getParent().getRoot();
	}

	public AbstractNode getChild(String qualifiedName) {
		String[] tokens = qualifiedName.split(":");
		if(tokens.length == 0){
			return null;
		}
		if(tokens.length == 1){
			for(AbstractNode child : getChildren()){
				if(child.getName().equals(tokens[0])){
					return child;
				}
			}
		}
		else{
			AbstractNode nextChild = getChild(tokens[0]);
			if(nextChild == null) return null;
			tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
			String newName = qualifiedName.substring(qualifiedName.indexOf(":") + 1);
			return nextChild.getChild(newName);
		}
		return null;
	}

	public AbstractNode getSibling(String name){
		if(getParent() == null) return null;
		for(AbstractNode sibling : getParent().getChildren()){
			if(sibling.getName().equals(name) && sibling != this){
				return sibling;
			}
		}
		return null;
	}

	public boolean hasSibling(String name){
		return getSibling(name) != null;
	}

	public int subtreeSize(){
		int size = 1;
		for(AbstractNode child : getChildren()){
			size += child.subtreeSize();
		}
		return size;
	}

	@Override
	public String toString(){
		return getName();
	}

	public boolean isMatch(AbstractNode node){
		if (!getName().equals(node.getName())) {
			return false;
		}
		if (!fProperties.isMatch(node.fProperties)) {
			return false;
		}
		return true;
	}

	public int getMaxIndex() {
		if(getParent() != null){
			return getParent().getChildren().size();
		}
		return -1;
	}

	public abstract AbstractNode makeClone();
	public abstract Object accept(IModelVisitor visitor) throws Exception;

	public void setProperties(NodeProperties nodeProperties) {
		fProperties = nodeProperties.getCopy(); 
	}

	public NodeProperties getProperties() {
		return fProperties; 
	}


	public int getMaxChildIndex(AbstractNode potentialChild) {
		return getChildren().size();
	}

	public void setPropertyValue(NodePropertyDefs.PropertyId propertyId, String value) {
		NodeProperty nodeProperty = new NodeProperty(NodePropertyDefs.getPropertyType(propertyId), value);
		fProperties.put(NodePropertyDefs.getPropertyName(propertyId), nodeProperty);
	}

	public void setPropertyDefaultValue(NodePropertyDefs.PropertyId propertyId) {

		NodeProperty nodeProperty = 
				new NodeProperty(
						NodePropertyDefs.getPropertyType(propertyId), 
						NodePropertyDefs.getPropertyDefaultValue(propertyId, null));

		fProperties.put(NodePropertyDefs.getPropertyName(propertyId), nodeProperty);
	}	

	public String getPropertyValue(NodePropertyDefs.PropertyId propertyId) {
		String propertyName = NodePropertyDefs.getPropertyName(propertyId);
		NodeProperty nodeProperty = fProperties.get(propertyName);
		if (nodeProperty == null) {
			return null;
		}
		return nodeProperty.getValue();
	}

	public boolean getPropertyValueBoolean(NodePropertyDefs.PropertyId propertyId) {
		String str = getPropertyValue(propertyId);
		return BooleanHelper.parseBoolean(str);
	}	

	public Set<String> getPropertyKeys() {
		return fProperties.getKeys();
	}

	public int getPropertyCount() {
		return fProperties.size();
	}

	public void removeProperty(NodePropertyDefs.PropertyId propertyId) {
		fProperties.remove(NodePropertyDefs.getPropertyName(propertyId));
	}
}
