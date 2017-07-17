package com.ecfeed.ui.editor.composites;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.ui.editor.EcFormToolkit;
import com.ecfeed.ui.editor.IValueApplier;
import com.ecfeed.ui.modelif.RootInterface;

public class ModelNameComposite {
	
	private Text fModelNameText;
	private RootInterface fRootIf;
	

	public ModelNameComposite(Composite parent, EcFormToolkit ecFormToolkit, RootInterface rootIf) {

		fRootIf = rootIf;
		
		Composite composite = ecFormToolkit.createGridComposite(parent, 2);		

		ecFormToolkit.createLabel(composite, "Model name");
		fModelNameText = ecFormToolkit.createGridText(composite, new ModelNameApplier());
		ecFormToolkit.paintBorders(composite);
	}
	
	public void refresh() {
		
		String name = fRootIf.getName();
		fModelNameText.setText(name);
	}
	
	private class ModelNameApplier implements IValueApplier{

		@Override
		public void applyValue() {
			
			fRootIf.setName(fModelNameText.getText());
			fModelNameText.setText(fRootIf.getName());
		}
	}	
	
}
