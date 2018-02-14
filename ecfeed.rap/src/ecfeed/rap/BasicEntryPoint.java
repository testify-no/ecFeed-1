package ecfeed.rap;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

//import com.ecfeed.core.utils.StringHelper;


public class BasicEntryPoint extends AbstractEntryPoint {

	private static final long serialVersionUID = -3245693274920341976L;

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText("Hello");
        Button button = new Button(parent, SWT.PUSH);
        
        String buttonText = "World";
        if (StringHelper.isNullOrEmpty(buttonText)) {
        	buttonText = buttonText + " EMPTY";
        } else {
        	buttonText = buttonText + " NOT EMPTY";
        }
        button.setText(buttonText);
    }

	public static class StringHelper {
		public static final boolean isNullOrEmpty(String buttonText) {
			return buttonText==null || buttonText==""; 
		}
	}

}
