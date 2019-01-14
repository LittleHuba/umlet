package com.baselet.diagram;

import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.listener.GridElementListener;
import com.baselet.gui.listener.PaletteEntityListener;

import java.io.File;

public class PaletteHandler extends DiagramHandler {

	public PaletteHandler(File palettefile) {
		super(palettefile);
	}

	@Override
	public GridElementListener getEntityListener(GridElement e) {
		return PaletteEntityListener.getInstance(this);
	}

	@Override
	protected DrawPanel createDrawPanel() {
		return new DrawPanel(this, false); /* no startup and filedrop */
	}
}
