package com.baselet.gui.menu;

import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.gui.CurrentGui;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;

public class MenuFactory {

	public void doAction(final String menuItem, final Object param) {
	}

	// These components should only be enabled if the drawpanel is not empty
	protected List<JComponent> diagramDependendComponents = new ArrayList<JComponent>();


}
