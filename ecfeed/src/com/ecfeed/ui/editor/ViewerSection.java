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
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

/**
 * Section with a main StructuredViewer composite and buttons below or aside
 */
public abstract class ViewerSection extends ButtonsCompositeSection implements ISelectionProvider{

	private final int VIEWER_STYLE = SWT.BORDER | SWT.MULTI;

	private List<Object> fSelectedElements;
	private StructuredViewer fViewer;
	private Composite fViewerComposite;
	private Menu fMenu;
	private KeyRegistrator fKeyRegistrator = null;

	protected abstract void createViewerColumns();
	protected abstract StructuredViewer createViewer(Composite viewerComposite, int style);
	protected abstract IContentProvider createViewerContentProvider();
	protected abstract IBaseLabelProvider createViewerLabelProvider();


	public ViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider, 
			int style) {
		super(sectionContext, updateContext, javaProjectProvider, style);
		fSelectedElements = new ArrayList<>();
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		createViewer();

		return client;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(fViewer != null && fViewer.getControl().isDisposed() == false){
			fViewer.refresh();
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener){
		fViewer.addSelectionChangedListener(listener);
	}

	@Override
	public IStructuredSelection getSelection(){
		return (IStructuredSelection)fViewer.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener){
		fViewer.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection){
		fViewer.setSelection(selection);
	}

	@Override
	protected void setActionProvider(IActionProvider actionProvider) {

		super.setActionProvider(actionProvider);

		Control viewerControl = fViewer.getControl();

		configureContextMenu(viewerControl);
		registerKeyShortcuts(viewerControl, actionProvider);
	}


	public Object getSelectedElement() {

		if (fSelectedElements.size() > 0) {
			return fSelectedElements.get(0);
		}

		return null;
	}

	public void selectElement(Object element){
		getViewer().setSelection(new StructuredSelection(element), true);
	}

	public void setInput(Object input){
		fViewer.setInput(input);
		refresh();
	}

	public Object getInput(){
		return fViewer.getInput();
	}

	public StructuredViewer getViewer(){
		return fViewer;
	}

	protected void createViewer() {

		fViewer = createViewer(getMainControlComposite(), getViewerStyle());
		fViewer.setContentProvider(createViewerContentProvider());
		fViewer.setLabelProvider(createViewerLabelProvider());

		createViewerColumns();

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fSelectedElements = ((IStructuredSelection)event.getSelection()).toList();
			}
		});
	}

	protected int getViewerStyle(){
		return VIEWER_STYLE;
	}

	protected void addDoubleClickListener(IDoubleClickListener listener){
		getViewer().addDoubleClickListener(listener);
	}

	protected GridData viewerLayoutData(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		return gd;
	}

	protected Composite getViewerComposite(){
		return fViewerComposite;
	}

	protected KeyListener createKeyListener(int keyCode, int modifier, Action action){
		ActionKeyListener adapter = new ActionKeyListener(keyCode, modifier, action);
		fViewer.getControl().addKeyListener(adapter);
		return adapter;
	}

	protected MenuListener getMenuListener() {

		return new ViewerMenuListener(fMenu, getActionProvider(), fViewer);
	}

	protected Menu getMenu(){
		return fMenu;
	}


	protected class ActionSelectionAdapter extends SelectionAdapter{
		private Action fAction;
		private String fDescriptionWhenError;

		public ActionSelectionAdapter(Action action, String descriptionWhenError ){
			fAction = action;
			fDescriptionWhenError = descriptionWhenError;
		}

		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fAction.run();
			} catch (Exception e) {
				ExceptionCatchDialog.open(fDescriptionWhenError, e.getMessage());
			}
		}
	}

	private void configureContextMenu(Control viewerControl) {

		fMenu = new Menu(viewerControl);
		viewerControl.setMenu(fMenu);
		fMenu.addMenuListener(getMenuListener());
	}

	private void registerKeyShortcuts(
			Control viewerControl, IActionProvider actionProvider) {

		fKeyRegistrator = new KeyRegistrator(viewerControl, actionProvider);

		if (actionProvider != null) {
			fKeyRegistrator.registerKeyListeners();
		} else {
			fKeyRegistrator.unregisterKeyListeners();
		}
	}

}
