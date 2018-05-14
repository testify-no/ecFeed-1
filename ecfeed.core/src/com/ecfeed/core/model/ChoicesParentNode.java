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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ChoicesParentNode extends AbstractNode{

	protected List<ChoiceNode> fChoices;

	public ChoicesParentNode(String name) {
		super(name);
		fChoices = new ArrayList<ChoiceNode>();
	}

	public abstract Object accept(IChoicesParentVisitor visitor) throws Exception;

	@Override
	public List<? extends AbstractNode> getChildren(){
		return fChoices;
	}

	@Override
	public boolean isMatch(AbstractNode node){
		if(node instanceof ChoicesParentNode == false){
			return false;
		}
		ChoicesParentNode comparedChoiceParent = (ChoicesParentNode)node;
		if(getChoices().size() != comparedChoiceParent.getChoices().size()){
			return false;
		}

		for(int i = 0; i < getChoices().size(); i++){
			if(getChoices().get(i).isMatch(comparedChoiceParent.getChoices().get(i)) == false){
				return false;
			}
		}
		return super.isMatch(node);
	}

	public abstract AbstractParameterNode getParameter();

	protected void correctNodeValuesAfterChoicesAdd() {
	}

	public void addChoice(ChoiceNode choice) {
		addChoice(choice, fChoices.size());
	}

	public void addChoice(ChoiceNode choice, int index) {
		fChoices.add(index, choice);
		choice.setParent(this);
		correctNodeValuesAfterChoicesAdd();
	}

	public void addChoices(List<ChoiceNode> choices) {
		for(ChoiceNode p : choices){
			addChoice(p);
		}
	}

	public List<ChoiceNode> getChoices() {
		return fChoices;
	}

	public List<ChoiceNode> getChoicesWithCopies() {
		return fChoices;
	}

	public ChoiceNode getChoice(String qualifiedName) {
		return (ChoiceNode)getChild(qualifiedName);
	}

	public boolean choiceExistsAsDirectChild(String choiceNameToFind) {

		for (ChoiceNode choiceNode : fChoices) {
			if (choiceNode.getName().equals(choiceNameToFind)) {
				return true;
			}
		}

		return false;
	}

	public List<ChoiceNode> getLeafChoices() {
		return getLeafChoices(getChoices());
	}	

	public List<ChoiceNode> getLeafChoicesWithCopies() {
		return getLeafChoices(getChoicesWithCopies());
	}	

	public Set<ChoiceNode> getAllChoices() {
		return getAllChoices(getChoices());
	}

	public Set<String> getChoiceNames() {
		return getChoiceNames(getChoices());
	}

	public Set<String> getAllChoiceNames() {
		return getChoiceNames(getAllChoices());
	}

	public Set<String> getLeafChoiceNames(){
		return getChoiceNames(getLeafChoices());
	}

	public Set<ChoiceNode> getLabeledChoices(String label) {
		return getLabeledChoices(label, getChoices());
	}

	public Set<String> getLeafLabels() {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : getLeafChoices()){
			result.addAll(p.getAllLabels());
		}
		return result;
	}

	public Set<String> getLeafChoiceValues(){
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : getLeafChoices()){
			result.add(p.getValueString());
		}
		return result;
	}

	public boolean removeChoice(ChoiceNode choice) {
		if(fChoices.contains(choice) && fChoices.remove(choice)){
			choice.setParent(null);
			return true;
		}
		return false;
	}

	public void replaceChoices(List<ChoiceNode> newChoices) {
		fChoices.clear();
		fChoices.addAll(newChoices);
		for(ChoiceNode p : newChoices){
			p.setParent(this);
		}
		correctNodeValuesAfterChoicesAdd();
	}

	protected List<ChoiceNode> getLeafChoices(Collection<ChoiceNode> choices) {
		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		for(ChoiceNode p : choices){
			if(p.isAbstract() == false){
				result.add(p);
			}
			result.addAll(p.getLeafChoices());
		}
		return result;
	}

	protected Set<ChoiceNode> getAllChoices(Collection<ChoiceNode> choices) {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : choices){
			result.add(p);
			result.addAll(p.getAllChoices());
		}
		return result;
	}

	protected Set<String> getChoiceNames(Collection<ChoiceNode> choices) {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : choices){
			result.add(p.getQualifiedName());
		}
		return result;
	}

	protected Set<ChoiceNode> getLabeledChoices(String label, List<ChoiceNode> choices) {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : choices){
			if(p.getLabels().contains(label)){
				result.add(p);
			}
			result.addAll(p.getLabeledChoices(label));
		}
		return result;
	}

	public static String generateNewChoiceName(ChoicesParentNode fChoicesParentNode, String startChoiceName) {

		if (!fChoicesParentNode.choiceExistsAsDirectChild(startChoiceName)) {
			return startChoiceName;
		}

		for (int i = 0;   ; i++) {

			String newParameterName = startChoiceName + String.valueOf(i);

			if (!fChoicesParentNode.choiceExistsAsDirectChild(newParameterName)) {
				return newParameterName;
			}
		}
	}	
}
