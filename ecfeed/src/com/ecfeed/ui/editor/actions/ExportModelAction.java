/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.utils.EclipseHelper;
import com.testify.ecfeed.rap.application.DownloadManager;
import com.testify.ecfeed.rap.views.NavigationViewPart;

public class ExportModelAction extends DescribedAction {

	public ExportModelAction() {
		super(ActionId.EXPORT_MODEL);
	}

	@Override
	public void run() {

		NavigationViewPart navigationViewPart = (NavigationViewPart)EclipseHelper.getViewPartById(NavigationViewPart.ID);
		RootNode rootNode = navigationViewPart.getModelRootNode();

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());
		try {
			serializer.serialize(rootNode);
		} catch (Exception e) {
			ErrorDialog.open("Can not export model.");
			return;
		}

		DownloadManager.downloadFile(rootNode.getName() + ".ect", ostream.toString());
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
