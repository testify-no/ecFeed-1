package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;

public class GenericOperationAddPartition extends BulkOperation {

	private class AddPartitionOperation extends AbstractModelOperation{
		private PartitionedNode fTarget;
		private PartitionNode fPartition;
		private int fIndex;

		public AddPartitionOperation(PartitionedNode target, PartitionNode partition, int index) {
			super(OperationNames.ADD_PARTITION);
			fTarget = target;
			fPartition = partition;
			fIndex = index;
		}

		@Override
		public void execute() throws ModelIfException {
			if(fIndex == -1){
				fIndex = fTarget.getPartitions().size();
			}
			if(fTarget.getPartitionNames().contains(fPartition.getName())){
				throw new ModelIfException(Messages.PARTITION_NAME_DUPLICATE_PROBLEM);
			}
			if(fIndex < 0){
				throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fTarget.getPartitions().size()){
				throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
			}
			fTarget.addPartition(fPartition, fIndex);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationRemovePartition(fTarget, fPartition, false);
		}
	}

	public GenericOperationAddPartition(PartitionedNode target, PartitionNode partition, int index, boolean validate) {
		super(OperationNames.ADD_PARTITION, true);
		addOperation(new AddPartitionOperation(target, partition, index));
		if((target.getCategory().getMethod() != null) && validate){
			addOperation(new MethodOperationMakeConsistent(target.getCategory().getMethod()));
		}
	}

	public GenericOperationAddPartition(PartitionedNode target, PartitionNode partition, boolean validate) {
		this(target, partition, -1, validate);
	}
}