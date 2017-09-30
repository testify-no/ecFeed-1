package com.ecfeed.ui.editor;

import com.ecfeed.core.model.AbstractNode;

public interface IMainTreeProvider {

	AbstractNode getCurrentNode();
	void setCurrentNode(AbstractNode abstractNode);
	void expandChildren(AbstractNode abstractNode);
	void setSelection(AbstractNode[] abstractNodes);
	void markDirty();
	void refresh();

}
