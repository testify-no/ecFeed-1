package com.ecfeed.ui.editor;

import com.ecfeed.core.model.AbstractNode;

public interface IMainTreeProvider {

	AbstractNode getCurrentNode();
	void markDirty();
	void refresh();

}
