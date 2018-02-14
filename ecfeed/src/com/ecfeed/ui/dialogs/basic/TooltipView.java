package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.utils.INotifier;

public class TooltipView {

	private Shell fShell;

	public TooltipView(
			Shell parentShell,
			String tooltipTitle,
			String tooltipText, 
			INotifier mouseEnterNotifier,
			INotifier mouseExitNotifier) {

		fShell = createShell(parentShell, tooltipTitle);
		createTextWidget(fShell, tooltipText, mouseEnterNotifier, mouseExitNotifier);
	}

	public void open() {
		fShell.open();
	}

	public void close() {
		fShell.close();
	}

	private static Shell createShell(
			Shell parentShell, 
			String tooltipTitle) {

		Shell shell = new Shell(parentShell, SWT.RESIZE);

		shell.setText(tooltipTitle);
		shell.setSize(450, 300);
		shell.setLayout(new GridLayout(1, true));

		return shell;
	}

	private static void createTextWidget(
			Shell shell, 
			String tooltipText,
			final INotifier mouseEnterNotifier,
			final INotifier mouseExitNotifier) {

		Text textWidget = DialogObjectToolkit.createTooltipText(shell, tooltipText);

		textWidget.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				mouseEnterNotifier.doNotify();
			}
		});

		textWidget.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				mouseExitNotifier.doNotify();
			}
		});
	}

}
