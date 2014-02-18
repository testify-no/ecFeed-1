package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class ViewerSection extends BasicSection {
	public static final int BUTTONS_ASIDE = 1;
	public static final int BUTTONS_BELOW = 2;
	
	private final int fButtonsPosition;
	private Object fSelectedElement;

	private Composite fButtonsComposite;
	private StructuredViewer fViewer;
	
	public ViewerSection(Composite parent, FormToolkit toolkit, int style, int buttonsPosition) {
		super(parent, toolkit, style);
		fButtonsPosition = buttonsPosition;
	}	
	
	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		int columns = 1;
		if(fButtonsPosition == BUTTONS_ASIDE){
			columns = 2;
		}
		else if(fButtonsPosition == BUTTONS_BELOW){
			columns = 1;
		}
		
		client.setLayout(new GridLayout(columns, false));
		client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createViewerComposite(client); 
		fButtonsComposite = createButtonsComposite(client); 
		return client;
	}

	protected Composite createViewerComposite(Composite parent) {
		Composite viewerComposite = getToolkit().createComposite(parent);
		viewerComposite.setLayout(new GridLayout(1, false));
		viewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createViewerLabel(viewerComposite);
		fViewer = createViewer(viewerComposite, SWT.BORDER);
		createViewerColumns();

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fSelectedElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
			}
		});

		return viewerComposite;
	}

	protected abstract void createViewerColumns();

	protected void createViewerLabel(Composite viewerComposite) {
	}

	protected Composite createButtonsComposite(Composite parent) {
		Composite buttonsComposite = getToolkit().createComposite(parent);
		RowLayout rl = new RowLayout();
		rl.pack = false;
		if(fButtonsPosition == BUTTONS_ASIDE){
			rl.type = SWT.VERTICAL;
		}
		buttonsComposite.setLayout(rl);
		return buttonsComposite;
	}
	
	protected Button addButton(String text, SelectionAdapter adapter){
		Button button = getToolkit().createButton(fButtonsComposite, text, SWT.None);
		button.addSelectionListener(adapter);
		return button;
	}
	
	protected StructuredViewer getViewer(){
		return fViewer;
	}

	public void refresh(){
		fViewer.refresh();
	}
	
	public Object getSelectedElement(){
		return fSelectedElement;
	}
	
	protected GridData viewerLayoutData(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		return gd;
	}
	
	public void selectElement(Object element){
		getViewer().setSelection(new StructuredSelection(element), true);
	}

	public void setInput(Object input){
		fViewer.setInput(input);
	}
	
	protected abstract StructuredViewer createViewer(Composite viewerComposite, int style);
}
