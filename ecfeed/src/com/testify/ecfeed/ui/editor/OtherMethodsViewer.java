package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ModelUtils;

public class OtherMethodsViewer extends CheckboxTableViewerSection {
	
	public final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private ClassNode fSelectedClass;

	private class AddSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			for(Object object : getCheckboxViewer().getCheckedElements()){
				if(object instanceof MethodNode){
					fSelectedClass.addMethod((MethodNode)object);
				}
			}
			modelUpdated();
		}
	}
	
	public OtherMethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		addButton("Add selected", new AddSelectedAdapter());
	}
	
	@Override
	protected void createTableColumns() {
	}
	
	public void setInput(ClassNode classNode){
		fSelectedClass = classNode;
		List<MethodNode> notContainedMethods = ModelUtils.getNotContainedMethods(fSelectedClass, fSelectedClass.getQualifiedName());
		setText("Other methods in " + fSelectedClass.getLocalName());
		setVisible(notContainedMethods.size() > 0);
		super.setInput(notContainedMethods);
	}
	
	private void setVisible(boolean visible) {
		GridData gd = (GridData)getSection().getLayoutData();
		gd.exclude = !visible;
		getSection().setLayoutData(gd);
		getSection().setVisible(visible);
	}
}
