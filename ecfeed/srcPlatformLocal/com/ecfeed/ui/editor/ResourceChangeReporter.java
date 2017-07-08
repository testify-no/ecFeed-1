/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.utils.SystemLogger;

public class ResourceChangeReporter {

	public static void registerResourceChangeListener(IModelPageProvider modelPageProvider) {
		ResourceChangeListener listener = new ResourceChangeListener(modelPageProvider);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

	private static class ResourceChangeListener implements IResourceChangeListener {

		IModelPageProvider fModelPageProvider;

		ResourceChangeListener(IModelPageProvider modelPageProvider) {
			fModelPageProvider = modelPageProvider;
		}

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
			case IResourceChangeEvent.POST_BUILD:
				try {
					ModelPage modelPage = fModelPageProvider.getModelPage();
					event.getDelta().accept(new ResourceDeltaVisitor(modelPage));
				} catch (CoreException e) {
					SystemLogger.logCatch(e.getMessage());
				}
				break;
			default:
				break;
			}
		}
	}

	private static class ResourceDeltaVisitor implements IResourceDeltaVisitor {

		ModelPage fModelPage;

		public ResourceDeltaVisitor(ModelPage modelPage) {
			fModelPage = modelPage;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.REMOVED:
			case IResourceDelta.CHANGED:

				if (!Display.getDefault().isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							CachedImplementationStatusResolver.clearCache();

							if(fModelPage.getMasterBlock().getMasterSection() != null) {
								fModelPage.getMasterBlock().getMasterSection().refresh();
							}

							if(fModelPage.getMasterBlock().getCurrentPage() != null) {
								fModelPage.getMasterBlock().getCurrentPage().refresh();
							}
						}
					});
				}
				break;
			default:
				break;
			}
			return false;
		}
	}


}
