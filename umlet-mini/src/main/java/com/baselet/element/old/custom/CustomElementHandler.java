package com.baselet.element.old.custom;

import com.baselet.control.constants.Constants;
import com.baselet.custom.CustomCodeSyntaxPane;
import com.baselet.custom.CustomElementPanel;
import com.baselet.diagram.CustomPreviewHandler;
import com.baselet.element.interfaces.GridElement;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class CustomElementHandler {

	private final Timer timer;
	private final CustomCodeSyntaxPane codepane;
	private final CustomPreviewHandler preview;
	private GridElement editedEntity;
	private TimerTask compiletask;
	private final ErrorHandler errorhandler;
	private boolean compilation_running;
	private final CustomElementPanel panel;
	boolean keypressed;
	private String old_text;

	public CustomElementHandler() {
		codepane = new CustomCodeSyntaxPane();
		errorhandler = new ErrorHandler(codepane);
		codepane.getTextComponent().addMouseMotionListener(errorhandler);
		preview = new CustomPreviewHandler();
		timer = new Timer("customElementTimer", true);
		compilation_running = false;
		old_text = null;
		panel = new CustomElementPanel(this);
	}

	public CustomElementPanel getPanel() {
		return panel;
	}

	public CustomPreviewHandler getPreviewHandler() {
		return preview;
	}

	public CustomCodeSyntaxPane getCodePane() {
		return codepane;
	}

	private void updatePreview(GridElement e) {
		if (e != null) {
			Iterator<GridElement> iter = preview.getDrawPanel().getGridElements().iterator();
			if (iter.hasNext()) {
				GridElement element = iter.next();
				e.setRectangle(element.getRectangle());
				e.setPanelAttributes(element.getPanelAttributes());
				preview.getDrawPanel().removeElement(element);
			}

			preview.setHandlerAndInitListeners(e);
			preview.getDrawPanel().addElement(e);
			e.repaint();
		}
	}

	// starts the task
	private void start() {
		compiletask = new CustomElementCompileTask(this);
		timer.schedule(compiletask, Constants.CUSTOM_ELEMENT_COMPILE_INTERVAL,
			Constants.CUSTOM_ELEMENT_COMPILE_INTERVAL);
	}

	// stops the task
	private void stop() {
		if (compiletask != null) {
			compiletask.cancel();
		}
	}

	// runs compilation every 1 seconds and updates gui/errors...
	protected void runCompilation() {
		if (!compilation_running && !keypressed) // prevent 2 compilations to run at the same time (if compilation takes more then 1sec)
		{
			compilation_running = true;
			String txt = codepane.getText();
			if (!txt.equals(old_text)) {
				errorhandler.clearErrors();
				old_text = txt;
				editedEntity = CustomElementCompiler.getInstance().genEntity(txt, errorhandler);
				panel.setCustomElementSaveable(true);
				updatePreview(editedEntity);
			}
			compilation_running = false;
		}
		keypressed = false;
	}
}
