package com.ecfeed.ui.dialogs.basic;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.ecfeed.core.utils.INotifier;
import com.ecfeed.utils.EclipseHelper;

public class TooltipController {

	String fTooltipTitle;
	String fTooltipMessage;
	Shell fShell;
	Timer fTimer;
	TooltipView fTooltipView;

	public TooltipController(Widget widget, String tooltipTitle, String tooltipMessage) {

		fTooltipTitle = tooltipTitle;
		fTooltipMessage = tooltipMessage;
		fShell = EclipseHelper.getActiveShell();
		fTimer = null;
		fTooltipView = null;

		addMouseListeners(widget);
	}

	private void addMouseListeners(Widget widget) {

		widget.addListener(SWT.MouseEnter, new WidgetAreaMouseEnterListener());
		widget.addListener(SWT.MouseExit, new WidgetAreaMouseExitListener());
		widget.addListener(SWT.MouseMove, new WidgetAreaMouseMoveListener());
	}

	private void createTimerWithOpenViewTask() {

		TimerTask openViewTask = new OpenViewTask();
		createTimer(openViewTask, 1);
	}

	private void createTimerWithCloseViewTask() {

		TimerTask closeViewTask = new CloseViewTask();
		createTimer(closeViewTask, 2);
	}

	private void createTimer(TimerTask timerTask, int seconds) {

		final int milliseconds = 1000; 

		fTimer = new Timer();
		fTimer.schedule(timerTask, seconds * milliseconds);
	}

	private void deleteTimer() {

		if (fTimer == null) {
			return;
		}

		fTimer.cancel();
		fTimer = null;
	}

	private class WidgetAreaMouseEnterListener implements Listener {

		@Override
		public void handleEvent(Event e) {

			if (fTooltipView != null) {
				deleteTimer();
				return;
			}

			createTimerWithOpenViewTask();
		}
	}

	private class WidgetAreaMouseExitListener implements Listener {

		@Override
		public void handleEvent(Event event) {

			if (fTooltipView != null) {
				createTimerWithCloseViewTask();
				return;
			}

			deleteTimer();
		}

	}

	private class WidgetAreaMouseMoveListener implements Listener {

		@Override
		public void handleEvent(Event event) {

			if (fTooltipView != null) {
				return;
			}

			deleteTimer();
			createTimerWithOpenViewTask();
		}

	}

	private class TooltipAreaMouseEnterNotifier implements INotifier {

		@Override
		public void doNotify() {

			deleteTimer();
		}

	}

	private class TooltipAreaMouseExitNotifier implements INotifier {

		@Override
		public void doNotify() {

			createTimerWithCloseViewTask();
		}

	}

	private class OpenViewTask extends TimerTask {

		@Override
		public void run() {

			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					fTooltipView = 
							new TooltipView(
									fShell,
									fTooltipTitle,
									fTooltipMessage, 
									new TooltipAreaMouseEnterNotifier(),
									new TooltipAreaMouseExitNotifier());

					fTooltipView.open();
				}
			};

			Display.getDefault().asyncExec(runnable);
		}

	}

	private class CloseViewTask extends TimerTask {

		@Override
		public void run() {

			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					if (fTooltipView == null) {
						return;
					}

					fTooltipView.close();
					fTooltipView = null;
				}
			};

			Display.getDefault().asyncExec(runnable);
		}

	}

}
