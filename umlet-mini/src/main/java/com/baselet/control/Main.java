package com.baselet.control;

import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.control.util.CanOpenDiagram;
import com.baselet.control.util.Path;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.PaletteHandler;
import com.baselet.element.interfaces.GridElement;

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

	@Override
	public void doOpen(final String filename) {
	}

	/**
	 * called by UI when main is closed
	 */
	@Override
	public void closeProgram() {
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
		return null;
	}

}
