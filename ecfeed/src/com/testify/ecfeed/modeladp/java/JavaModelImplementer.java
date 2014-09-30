package com.testify.ecfeed.modeladp.java;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.EImplementationStatus;
import com.testify.ecfeed.modeladp.IModelImplementer;

public abstract class JavaModelImplementer implements IModelImplementer{
	
	private JavaImplementationStatusResolver fImplementationStatusResolver;
	private class ImplementableVisitor implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(JavaUtils.isUserType(node.getType())){
				return true;
			}
			return false;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(node.getCategory() != null && JavaUtils.isUserType(node.getCategory().getType())){
				return true;
			}
			return false;
		}
	}

	public JavaModelImplementer(ILoaderProvider loaderProvider){
		fImplementationStatusResolver = new JavaImplementationStatusResolver(loaderProvider);
	}
	
	public boolean implementable(GenericNode node){
		try{
			return (boolean)node.accept(new ImplementableVisitor());
		}catch (Exception e){}
		return false;
	}
	
	public EImplementationStatus getImplementationStatus(GenericNode node){
		return fImplementationStatusResolver.getImplementationStatus(node);
	}
}