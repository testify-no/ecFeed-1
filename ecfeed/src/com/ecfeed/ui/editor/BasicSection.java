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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.application.SectionDecorationsHolder;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.ImageManager;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.editor.actions.IActionGrouppingProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class BasicSection extends SectionPart {
	private Composite fClientComposite;
	private Control fTextClient;
	private IActionGrouppingProvider fActionGroupingProvider;
	private IModelUpdateContext fUpdateContext;
	private IJavaProjectProvider fJavaProjectProvider;
	private ISectionContext fSectionContext;
	private ToolBarManager fToolBarManager;

	public BasicSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider,
			int style){
		super(sectionContext.getSectionComposite(), sectionContext.getEcFormToolkit().getEclipseToolkit(), style);
		fSectionContext = sectionContext;
		fUpdateContext = updateContext;
		fJavaProjectProvider = javaProjectProvider;

		saveSectionDecorationsInApplicationContext();
		createContent();
	}

	private void saveSectionDecorationsInApplicationContext() {

		Section section = getSection();

		SectionDecorationsHolder sessionDecorationsHolder = ApplicationContext.getSessionDecorationsHolder();

		sessionDecorationsHolder.setDecorations(
				section.getBackground(),
				section.getForeground(),
				section.getTitleBarBackground(),
				section.getTitleBarBorderColor(),
				section.getTitleBarForeground(),
				section.getFont());
	}

	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}

	protected EcFormToolkit getEcFormToolkit() {
		return fSectionContext.getEcFormToolkit();
	}

	public void setText(String title){
		getSection().setText(title);
	}

	protected Composite getClientComposite(){
		return fClientComposite;
	}

	protected void createContent(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		getSection().setLayoutData(gd);
		fTextClient = createTextClient();
		fClientComposite = createClientComposite();
	}

	protected Composite createClientComposite() {
		Composite client = getEcFormToolkit().createComposite(getSection());
		client.setLayout(clientLayout());
		if(clientLayoutData() != null){
			client.setLayoutData(clientLayoutData());
		}
		getSection().setClient(client);
		getEcFormToolkit().adapt(client);
		getEcFormToolkit().paintBordersFor(client);
		return client;
	}

	protected Control createTextClient() {
		Composite textClient = getEcFormToolkit().createComposite(getSection());
		textClient.setLayout(new FillLayout());
		getSection().setTextClient(fTextClient);
		return textClient;
	}

	protected ToolBar createToolBar(Composite parent) {
		ToolBar toolbar = getToolBarManager().createControl(parent);
		toolbar.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
		fToolBarManager.update(true);
		addToolbarActions();
		return toolbar;
	}

	protected void addToolbarActions(){
		for(Action action : toolBarActions()){
			getToolBarManager().add(action);
		}
	}

	protected List<Action> toolBarActions(){
		return new ArrayList<Action>();
	}

	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(1, false);
		return layout;
	}

	protected Object clientLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	protected void updateTextClient() {
	}

	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}

	protected ToolBarManager getToolBarManager(){
		if(fToolBarManager == null){
			fToolBarManager = new ToolBarManager(SWT.RIGHT);
		}
		return fToolBarManager;
	}

	public Action getAction(String actionId) {
		if(fActionGroupingProvider != null){
			return fActionGroupingProvider.getAction(actionId);
		}
		return null;
	}

	protected void setActionGrouppingProvider(IActionGrouppingProvider provider){
		fActionGroupingProvider = provider;
	}

	protected IActionGrouppingProvider getActionGroupingProvider(){
		return fActionGroupingProvider;
	}

	protected ImageDescriptor getIconDescription(String fileName) {
		return ImageManager.getInstance().getImageDescriptor(fileName);
	}

	public void setVisible(boolean visible) {
		GridData gd = (GridData)getSection().getLayoutData();
		gd.exclude = !visible;
		getSection().setLayoutData(gd);
		getSection().setVisible(visible);
	}

	public void setEnabled(boolean enabled) {
		getSection().setEnabled(enabled);
	}	

	public IModelUpdateContext getModelUpdateContext() {
		return fUpdateContext;
	}

	public AbstractFormPart getSourceForm(){
		return this;
	}

	protected IJavaProjectProvider getJavaProjectProvider() {
		return fJavaProjectProvider;
	}

	protected class SelectNodeDoubleClickListener implements IDoubleClickListener {

		private IMainTreeProvider fMainTreeProvider;

		public SelectNodeDoubleClickListener(IMainTreeProvider mainTreeProvider) {
			fMainTreeProvider = mainTreeProvider;
		}

		@Override
		public void doubleClick(DoubleClickEvent event) {

			if (!(event.getSelection() instanceof IStructuredSelection)) {
				return;
			}


			IStructuredSelection selection = (IStructuredSelection)event.getSelection();

			Object firstObject = selection.getFirstElement();
			if (!(firstObject instanceof AbstractNode)) {
				return;
			}

			fMainTreeProvider.setCurrentNode((AbstractNode)firstObject);
		}
	}

}
