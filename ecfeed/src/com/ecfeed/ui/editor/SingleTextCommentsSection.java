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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class SingleTextCommentsSection extends AbstractCommentsSection {

	private final int TEXT_STYLE = SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY;

	private Text fCommentsText;

	public SingleTextCommentsSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider);
	}

	@Override
	public Composite createMainControlComposite(Composite parent){
		//overriding tab folder creation

		fCommentsText = getEcFormToolkit().getFormToolkitAdapter().createText(parent, "", TEXT_STYLE); // TODO TOOLKIT
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 150;
		fCommentsText.setLayoutData(gd);
		fCommentsText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

		return parent;
	}

	@Override
	public void refresh(){
		super.refresh();
		setCommentsText();
	}

	private void setCommentsText() {

		AbstractNodeInterface abstractNodeInterface = getTargetIf();

		if (abstractNodeInterface == null) {
			return;
		}

		fCommentsText.setText(abstractNodeInterface.getComments());
	}


}
