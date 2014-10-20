package com.testify.ecfeed.adapter;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class AbstractModelImplementer implements IModelImplementer {

	private ImplementableVisitor fImplementableVisitor;
	private NodeImplementer fNodeImplementerVisitor;
	private IImplementationStatusResolver fStatusResolver;
	
	private class ImplementableVisitor implements IModelVisitor{
		@Override
		public Object visit(RootNode node) throws Exception {
			return implementable(node); 
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementable(node); 
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementable(node); 
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return implementable(node); 
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementable(node); 
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return implementable(node); 
		}
	}

	private class NodeImplementer implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			for(ClassNode classNode : node.getClasses()){
				if(implementable(classNode) && getImplementationStatus(classNode) != EImplementationStatus.IMPLEMENTED){
					implement(classNode);
				}
			}
			return true;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(classDefinitionImplemented(node) == false){
				implementClassDefinition(node);
			}
			for(MethodNode method : node.getMethods()){
				if(implementable(method) && getImplementationStatus(method) != EImplementationStatus.IMPLEMENTED){
					implement(method);
				}
			}
			return true;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(methodDefinitionImplemented(node) == false){
				implementMethodDefinition(node);
			}
			for(CategoryNode parameter : node.getCategories()){
				if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
					implement(parameter);
				}
			}
			for(TestCaseNode testCase : node.getTestCases()){
				if(implementable(testCase) && getImplementationStatus(testCase) != EImplementationStatus.IMPLEMENTED){
					implement(testCase);
				}
			}
			return true;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(parameterDefinitionImplemented(node) == false){
				implementParameterDefinition(node);
			}
			for(PartitionNode partition : node.getPartitions()){
				if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
					implement(partition);
				}
			}
			return true;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			for(PartitionNode partition : node.getTestData()){
				if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
					implement(partition);
				}
			}
			return true;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(node.isAbstract()){
				for(PartitionNode partition : node.getPartitions()){
					if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
						implement(partition);
					}
				}
			}
			else{
				if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
					implementPartitionDefinition(node);
				}
			}
			return true;
		}
		
	}
	
	public AbstractModelImplementer(IImplementationStatusResolver statusResolver) {
		fImplementableVisitor = new ImplementableVisitor();
		fNodeImplementerVisitor = new NodeImplementer();
		fStatusResolver = statusResolver;
	}
	
	@Override
	public boolean implementable(Class<? extends GenericNode> type){
		if(type.equals(RootNode.class) ||
			(type.equals(ClassNode.class))||
			(type.equals(MethodNode.class))||
			(type.equals(CategoryNode.class))||
			(type.equals(TestCaseNode.class))||
			(type.equals(PartitionNode.class))
		){
			return true;
		}
		return false;
	}

	@Override
	public boolean implementable(GenericNode node) {
		try{
			return (boolean)node.accept(fImplementableVisitor);
		}catch(Exception e){}
		return false;
	}

	@Override
	public boolean implement(GenericNode node) {
		try{
			if(implementable(node)){
				return (boolean)node.accept(fNodeImplementerVisitor);
			}
		}catch(Exception e){}
		return false;
	}

	@Override
	public EImplementationStatus getImplementationStatus(GenericNode node) {
		return fStatusResolver.getImplementationStatus(node);
	}

	protected boolean implementable(RootNode node){
		return hasImplementableNode(node.getClasses());
	}
	
	protected boolean implementable(ClassNode node){
		return hasImplementableNode(node.getMethods());
	}
	
	protected boolean implementable(MethodNode node){
		return hasImplementableNode(node.getCategories()) || hasImplementableNode(node.getTestCases());
	}
	
	protected boolean implementable(CategoryNode node){
		return hasImplementableNode(node.getPartitions());
	}
	protected boolean implementable(PartitionNode node){
		return hasImplementableNode(node.getPartitions());
	}
	
	protected boolean implementable(TestCaseNode node){
		return hasImplementableNode(node.getTestData());
	}
	
	protected boolean hasImplementableNode(List<? extends GenericNode> nodes){
		for(GenericNode node : nodes){
			if(implementable(node)){
				return true;
			}
		}
		return false;
	}

	protected abstract boolean classDefinitionImplemented(ClassNode node);
	protected abstract boolean methodDefinitionImplemented(MethodNode node);
	protected abstract boolean parameterDefinitionImplemented(CategoryNode node);
	
	protected abstract void implementClassDefinition(ClassNode node) throws CoreException;
	protected abstract void implementMethodDefinition(MethodNode node) throws CoreException;
	protected abstract void implementParameterDefinition(CategoryNode node) throws CoreException;
	protected abstract void implementPartitionDefinition(PartitionNode node) throws CoreException;
}