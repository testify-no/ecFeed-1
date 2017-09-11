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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.GlobalActions;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.editor.actions.NamedAction;
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
	private Set<KeyListener> fKeyListeners;

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
		fKeyListeners = new HashSet<KeyListener>();
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
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		createViewer();
		return client;
	}

	@Override
	protected void setActionProvider(IActionProvider provider){
		setActionProvider(provider, true);
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

	public List<AbstractNode> getSelectedNodes(){
		List<AbstractNode> result = new ArrayList<>();
		for(Object o : getSelection().toList()){
			if(o instanceof AbstractNode){
				result.add((AbstractNode)o);
			}
		}
		return result;
	}

	public AbstractNode getFirstSelectedNode() {
		List<AbstractNode> selectedNodes = getSelectedNodes();

		if(selectedNodes.size() == 0) {
			return null;
		}

		return selectedNodes.get(0);
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
		ViewerKeyAdapter adapter = new ViewerKeyAdapter(keyCode, modifier, action);
		fViewer.getControl().addKeyListener(adapter);
		return adapter;
	}

	protected void setActionProvider(IActionProvider provider, boolean addDeleteAction){
		super.setActionProvider(provider);
		fMenu = new Menu(fViewer.getControl());
		fViewer.getControl().setMenu(fMenu);
		fMenu.addMenuListener(getMenuListener());

		if(provider != null) {
			addKeyListenersForActions(provider, addDeleteAction);
		} else {
			removeKeyListeners();
		}
	}

	private void addKeyListenersForActions(IActionProvider provider, boolean addDeleteAction) {

		addKeyListener(GlobalActions.INSERT.getId(), SWT.INSERT, SWT.NONE, provider);
		addKeyListener(GlobalActions.DELETE.getId(), SWT.DEL, SWT.NONE, provider);

		addKeyListener(GlobalActions.MOVE_UP.getId(), SWT.ARROW_UP, SWT.ALT, provider);
		addKeyListener(GlobalActions.MOVE_DOWN.getId(), SWT.ARROW_DOWN, SWT.ALT, provider);

		if (!ApplicationContext.isProjectAvailable()) {
			addActionsForStandaloneApp(provider);
		}
	}

	private void addActionsForStandaloneApp(IActionProvider provider) {

		int ctrlModifier = getCtrlModifier();

		addKeyListener(GlobalActions.COPY.getId(), 'c', ctrlModifier, provider);
		addKeyListener(GlobalActions.CUT.getId(), 'x', ctrlModifier, provider);
		addKeyListener(GlobalActions.PASTE.getId(), 'v', ctrlModifier, provider);

		addKeyListener(GlobalActions.SAVE.getId(), 's', ctrlModifier, provider);
		addKeyListener(GlobalActions.UNDO.getId(), 'z', ctrlModifier, provider);
		addKeyListener(GlobalActions.REDO.getId(), 'z', ctrlModifier | SWT.SHIFT, provider);
	}

	private void addKeyListener(String actionId, int keyCode, int modifier, IActionProvider provider) {
		NamedAction action = provider.getAction(actionId);
		if (action == null) {
			return;
		}
		fKeyListeners.add(createKeyListener(keyCode, modifier, action));
	}


	int getCtrlModifier() {

		if (SystemHelper.isOperatingSystemMacOs()) {
			return SWT.COMMAND;
		} else {
			return SWT.CTRL;
		}
	}

	private void removeKeyListeners() {
		Iterator<KeyListener> it = fKeyListeners.iterator();
		while(it.hasNext()){
			fViewer.getControl().removeKeyListener(it.next());
			it.remove();
		}
	}

	protected MenuListener getMenuListener() {

		INodeSelectionProvider nodeSelectionProvider = new INodeSelectionProvider() {

			@Override
			public AbstractNode getFirstSelectedAbstractNode() {
				return getFirstSelectedNode();
			}
		};

		return new ViewerMenuListener(fMenu, getActionProvider(), nodeSelectionProvider);
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

	protected class ViewerKeyAdapter extends KeyAdapter {
		private int fKeyCode;
		private Action fAction;
		private int fModifier;

		public ViewerKeyAdapter(int keyCode, int modifier, Action action){
			fKeyCode = keyCode;
			fModifier = modifier;
			fAction = action;
		}

		@Override
		public void keyReleased(KeyEvent e) {

			if(e.keyCode != fKeyCode) {
				return;
			}
			if (e.stateMask != fModifier) {
				return;
			}
			fAction.run();
		}
	}

}
