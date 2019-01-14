package com.baselet.control;

import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.control.util.CanOpenDiagram;
import com.baselet.control.util.Path;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.PaletteHandler;
import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.pane.OwnSyntaxPane;

import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class Main implements CanCloseProgram, CanOpenDiagram {

	private static Main main = new Main();

	private GridElement editedGridElement;
	private TreeMap<String, PaletteHandler> palettes;
	private final ArrayList<DiagramHandler> diagrams = new ArrayList<DiagramHandler>();

	public static Main getInstance() {
		return main;
	}

	public void setPropertyPanelToGridElement(final GridElement e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setPropertyPanelToGridElementHelper(e);
			}
		});
	}

	private void setPropertyPanelToGridElementHelper(GridElement e) {
		editedGridElement = e;
		OwnSyntaxPane propertyPane = CurrentGui.getInstance().getGui().getPropertyPane();
		if (e != null) {
			propertyPane.switchToElement(e);
		} else {
			DiagramHandler handler = CurrentDiagram.getInstance().getDiagramHandler();
			if (handler == null) {
				propertyPane.switchToNonElement("");
			} else {
				propertyPane.switchToNonElement(handler.getHelpText());
			}
		}
	}

	public void doNew() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				doNewHelper();
			}
		});
	}

	private void doNewHelper() {
		if (lastTabIsEmpty()) {
			return; // If the last tab is empty do nothing (it's already new)
		}
		DiagramHandler diagram = new DiagramHandler(null);
		diagrams.add(diagram);
		CurrentGui.getInstance().getGui().open(diagram);
		if (diagrams.size() == 1) {
			setPropertyPanelToGridElement(null);
		}
	}

	@Override
	public void doOpen(final String filename) {
	}

	/**
	 * If the last diagram tab and it's undo history (=controller) is empty return true, else return false
	 */
	private boolean lastTabIsEmpty() {
		if (!diagrams.isEmpty()) {
			DiagramHandler lastDiagram = diagrams.get(diagrams.size() - 1);
			if (lastDiagram.getController().isEmpty() && lastDiagram.getDrawPanel().getGridElements().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * called by UI when main is closed
	 */
	@Override
	public void closeProgram() {
		ConfigHandler.saveConfig(CurrentGui.getInstance().getGui());
	}

	public TreeMap<String, PaletteHandler> getPalettes() {
		if (palettes == null) {
			palettes = new TreeMap<String, PaletteHandler>(Constants.DEFAULT_FIRST_COMPARATOR);
			// scan palettes
			List<File> palettes = scanForPalettes();
			for (File palette : palettes) {
				this.palettes.put(getFilenameWithoutExtension(palette), new PaletteHandler(palette));
			}
		}
		return palettes;
	}

	private String getFilenameWithoutExtension(File file) {
		return file.getName().substring(0, file.getName().indexOf("."));
	}

	private List<File> scanForPalettes() {
		// scan palettes directory...
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File[] paletteFiles = fileSystemView.getFiles(new File(Path.homeProgram() + "palettes/"), false);
		List<File> palettes = new ArrayList<File>();
		for (File palette : paletteFiles) {
			if (palette.getName().endsWith("." + Program.getInstance().getExtension())) {
				palettes.add(palette);
			}
		}
		return palettes;
	}

	public List<String> getTemplateNames() {
		ArrayList<String> templates = new ArrayList<String>();
		// scan palettes directory...
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File[] templateFiles = fileSystemView.getFiles(new File(Path.customElements()), false);
		for (File template : templateFiles) {
			if (template.getName().endsWith(".java")) {
				templates.add(template.getName().substring(0, template.getName().length() - 5));
			}
		}
		Collections.sort(templates, Constants.DEFAULT_FIRST_COMPARATOR);
		return templates;
	}

	public List<DiagramHandler> getDiagrams() {
		return diagrams;
	}

	public Collection<DiagramHandler> getDiagramsAndPalettes() {
		List<DiagramHandler> returnList = new ArrayList<DiagramHandler>(getDiagrams());
		returnList.addAll(getPalettes().values());
		return returnList;
	}

	public GridElement getEditedGridElement() {
		return editedGridElement;
	}

	public PaletteHandler getPalette() {
		String name = CurrentGui.getInstance().getGui().getSelectedPalette();
		if (name != null) {
			return getPalettes().get(name);
		}
		return null;
	}

}
