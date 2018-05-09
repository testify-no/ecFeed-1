package com.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.IModelUpdateListener;

public abstract class AModelUpdateContext implements IModelUpdateContext {

	protected abstract List<IModelUpdateListener> createUpdateListeners(List<AbstractNode> nodesToSelectAfterTheOperation);
	protected abstract AbstractFormPart getAbstractFormPart();

	@Override
	public final void notifyUpdateListeners(List<AbstractNode> nodesToSelectAfterTheOperation) {

		List<IModelUpdateListener> updateListeners = createUpdateListeners(nodesToSelectAfterTheOperation);

		if (updateListeners == null) {
			return;
		}

		AbstractFormPart abstractFormPart = getAbstractFormPart(); 

		for (IModelUpdateListener listener : updateListeners) {
			listener.notifyModelUpdated(abstractFormPart);
		}
	}

}
