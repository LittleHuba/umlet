package com.baselet.gui.listener;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.interfaces.GridElement;

import java.util.HashMap;

public class CustomPreviewEntityListener extends GridElementListener {

	private static HashMap<DiagramHandler, CustomPreviewEntityListener> entitylistener = new HashMap<DiagramHandler, CustomPreviewEntityListener>();

	public static CustomPreviewEntityListener getInstance(DiagramHandler handler) {
		if (!entitylistener.containsKey(handler)) {
			entitylistener.put(handler, new CustomPreviewEntityListener(handler));
		}
		return entitylistener.get(handler);
	}

	public CustomPreviewEntityListener(DiagramHandler handler) {
		super(handler);
	}

	@Override
	public void mouseDoubleClicked(GridElement me) {

	}
}
