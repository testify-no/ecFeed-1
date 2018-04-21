package com.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.adapter.operations.AbstractParameterOperationSetType;
import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.junit.CollectiveOnlineRunner;
import com.ecfeed.junit.annotations.Constraints;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.Generator;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;

@RunWith(CollectiveOnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com/ecfeed/adapter/operations/MethodParameterOperationSetTypeTest.ect")
@Constraints(Constraints.ALL)
public class MethodParameterOperationSetTypeTest {
/*	
	@Test
	public void adaptConstraintsStringFeilmeldingToLongTest(){
		
	}
	
	@Test
	public void adaptConstraintsStringAnotherArgumentToLongTest() {
		
	}*/
	
	private ModelOperationManager fOperationManager;
	private ITypeAdapterProvider fAdapterProvider = new EclipseTypeAdapterProvider();
	
	@Test
	public void testMethod(String ident, String id, String startMaaned,
			String slutMaaned, String filter, String formaal,
			String feilmelding, String anotherParameter) {
		
		String currentTypeName = feilmelding;
		String newTypeName = slutMaaned;
		boolean exceptionExpected = false;
	
//		AbstractParameterNode parameter = new AbstractParameterNodeImp("parameter", currentTypeName);
//		fOperationManager = new ModelOperationManager();
//		IModelOperation operation = new AbstractParameterOperationSetType(parameter, newTypeName, fAdapterProvider);
//		try{
//			fOperationManager.execute(operation);
//			if(exceptionExpected){
//				fail("Exception expected");
//			}
//			assertEquals(newTypeName, parameter.getType());
//
//			fOperationManager.undo();
//			assertEquals(currentTypeName, parameter.getType());
//			fOperationManager.redo();
//			assertEquals(newTypeName, parameter.getType());
//			fOperationManager.undo();
//			assertEquals(currentTypeName, parameter.getType());
//			fOperationManager.redo();
//			assertEquals(newTypeName, parameter.getType());
//		}catch(ModelOperationException e){
//			if(exceptionExpected == false){
//				fail("Unexception exception: " + e.getMessage());
//			}
//			assertEquals(currentTypeName, parameter.getType());
//		}
	}
	
	
	private class AbstractParameterNodeImp extends AbstractParameterNode{

		public AbstractParameterNodeImp(String name, String type) {
			super(name, type);
		}

		@Override
		public AbstractParameterNode getParameter() {
			return this;
		}

		@Override
		public AbstractNode makeClone() {
			return null;
		}

		@Override
		public Object accept(IModelVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public List<MethodNode> getMethods() {
			return null;
		}

		@Override
		public Object accept(IParameterVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public Object accept(IChoicesParentVisitor visitor) throws Exception {
			return null;
		}

		@Override
		public Set<ConstraintNode> mentioningConstraints() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<ConstraintNode> mentioningConstraints(String label) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
}
