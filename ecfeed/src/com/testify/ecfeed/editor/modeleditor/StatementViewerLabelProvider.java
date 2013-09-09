package com.testify.ecfeed.editor.modeleditor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StatementArray;


public class StatementViewerLabelProvider extends LabelProvider {
	
	private Constraint fConstraint;

	public StatementViewerLabelProvider(Constraint constraint){
		fConstraint = constraint;
	}
	
	public String getText(Object element){
		if(element instanceof StatementArray){
			return ((StatementArray)element).getOperator().toString();
		}
		else if(element instanceof BasicStatement){
			return ((BasicStatement)element).toString();
		}
		return null;
	}
	
	public Image getImage(Object element){
		if(element == fConstraint.getPremise()){
			return getImage("premise_statement.gif");
		}
		else if(element == fConstraint.getConsequence()){
			return getImage("consequence_statement.gif");
		}
		return null;
	}
	
	private static Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(ModelLabelProvider.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
	    return image.createImage();
	  }

}
