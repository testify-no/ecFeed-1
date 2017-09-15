package com.ecfeed.ui.editor;

import com.ecfeed.core.model.AbstractNode;

public interface IMainTreeProvider {

	AbstractNode getCurrentNode();
	void setCurrentNode(AbstractNode abstractNode);
	void markDirty();
	void refresh();

}
