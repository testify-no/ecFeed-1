package com.ecfeed.ui.editor;

import java.util.List;

import com.ecfeed.core.model.AbstractNode;

public class MainTreeProviderHelper {

	public static void notifyModelUpdated(
			IMainTreeProvider mainTreeProvider, 
			List<AbstractNode> fNodesToSelect) {

		mainTreeProvider.markDirty();
		mainTreeProvider.refresh();

		MainTreeProviderHelper.selectAndExpandNodes(mainTreeProvider, fNodesToSelect);
	}

	private static void selectAndExpandNodes(
			IMainTreeProvider mainTreeProvider, 
			List<AbstractNode> fNodesToSelect) {

		if (fNodesToSelect.size() == 0) {
			return;
		}

		if (fNodesToSelect.size() == 1) {

			AbstractNode nodeToSelect = fNodesToSelect.get(0);

			mainTreeProvider.expandChildren(nodeToSelect);
			mainTreeProvider.setCurrentNode(nodeToSelect);

		} else {
			AbstractNode[] nodes = (AbstractNode[]) fNodesToSelect.toArray();
			mainTreeProvider.setSelection(nodes);
		}

	}
}
