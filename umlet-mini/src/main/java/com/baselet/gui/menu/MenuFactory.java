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

	public void updateDiagramDependendComponents() {
		DrawPanel currentDiagram = CurrentGui.getInstance().getGui().getCurrentDiagram();
		if (currentDiagram == null) {
			return; // Possible if method is called at loading a palette
		}
		DiagramHandler handler = currentDiagram.getHandler();
		boolean enable = !(handler == null || handler.getDrawPanel().getGridElements().isEmpty());
		for (JComponent component : diagramDependendComponents) {
			component.setEnabled(enable);
		}

	}

}
