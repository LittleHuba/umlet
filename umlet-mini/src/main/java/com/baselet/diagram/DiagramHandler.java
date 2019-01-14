package com.baselet.diagram;

import com.baselet.control.HandlerElementMap;
import com.baselet.control.SharedUtils;
import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.constants.Constants;
import com.baselet.diagram.io.DiagramFileHandler;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.old.custom.CustomElement;
import com.baselet.element.old.element.Relation;
import com.baselet.gui.command.Controller;
import com.baselet.gui.listener.DiagramListener;
import com.baselet.gui.listener.GridElementListener;
import com.baselet.gui.listener.OldRelationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Component;
import java.awt.MouseInfo;
import java.io.File;
import java.util.List;
import java.util.Vector;

public class DiagramHandler {

	private static final Logger log = LoggerFactory.getLogger(DiagramHandler.class);

	private boolean isChanged;
	private final DiagramFileHandler fileHandler;
	private FontHandler fontHandler;

	protected DrawPanel drawpanel;
	private Controller controller;
	protected DiagramListener listener;
	private String helptext;
	private boolean enabled;
	private int gridSize;

	private OldRelationListener relationListener;
	private GridElementListener gridElementListener;

	public static DiagramHandler forExport(FontHandler fontHandler) {
		DiagramHandler returnHandler = new DiagramHandler(null, false);
		if (fontHandler != null) {
			returnHandler.fontHandler = fontHandler;
		}
		return returnHandler;
	}

	public static DiagramHandler forExport(String diagram) {
		return new DiagramHandler(diagram);
	}

	private DiagramHandler(String diagram) {
		gridSize = Constants.DEFAULTGRIDSIZE;
		isChanged = false;
		enabled = true;
		drawpanel = createDrawPanel();
		fontHandler = new FontHandler(this);
		fileHandler = DiagramFileHandler.createInstance(this, diagram);
		fileHandler.doOpen();
	}

	public DiagramHandler(File diagram) {
		this(diagram, false);
	}

	protected DiagramHandler(File diagram, boolean nolistener) {
		gridSize = Constants.DEFAULTGRIDSIZE;
		isChanged = false;
		enabled = true;
		drawpanel = createDrawPanel();
		controller = new Controller(this);
		fontHandler = new FontHandler(this);
		fileHandler = DiagramFileHandler.createInstance(this, diagram);
		if (!nolistener) {
			setListener(new DiagramListener(this));
		}
		if (diagram != null) {
			fileHandler.doOpen();
		}
	}

	protected DrawPanel createDrawPanel() {
		return new DrawPanel(this, true);
	}

	public void setEnabled(boolean en) {
		if (!en && enabled) {
			drawpanel.removeMouseListener(listener);
			drawpanel.removeMouseMotionListener(listener);
			enabled = false;
		} else if (en && !enabled) {
			drawpanel.addMouseListener(listener);
			drawpanel.addMouseMotionListener(listener);
			enabled = true;
		}
	}

	protected void setListener(DiagramListener listener) {
		this.listener = listener;
		drawpanel.addMouseListener(this.listener);
		drawpanel.addMouseMotionListener(this.listener);
		drawpanel.addMouseWheelListener(this.listener);
	}

	public DiagramListener getListener() {
		return listener;
	}

	public void setChanged(boolean changed) {
		if (isChanged != changed) {
			isChanged = changed;
		}
	}

	public DrawPanel getDrawPanel() {
		return drawpanel;
	}

	public DiagramFileHandler getFileHandler() {
		return fileHandler;
	}

	public FontHandler getFontHandler() {
		return fontHandler;
	}

	public Controller getController() {
		return controller;
	}

	// reloads the diagram from file + updates gui
	public void reload() {
		drawpanel.removeAll();
		fileHandler.doOpen();
		drawpanel.updatePanelAndScrollbars();
	}

	public String getName() {
		String name = fileHandler.getFileName();
		if (name.contains(".")) {
			name = name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}

	public GridElementListener getEntityListener(GridElement e) {
		if (e instanceof Relation) {
			if (relationListener == null) {
				relationListener = new OldRelationListener(this);
			}
			return relationListener;
		} else {
			if (gridElementListener == null) {
				gridElementListener = new GridElementListener(this);
			}
			return gridElementListener;
		}
	}

	public void setHelpText(String helptext) {
		this.helptext = helptext;
	}

	public String getHelpText() {
		if (helptext == null) {
			return Constants.getDefaultHelptext();
		} else {
			return helptext;
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public int getGridSize() {
		return gridSize;
	}

	public float getZoomFactor() {
		return (float) getGridSize() / (float) Constants.DEFAULTGRIDSIZE;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int realignToGrid(double val) {
		return realignToGrid(true, val, false);
	}

	public int realignToGrid(boolean logRealign, double val) {
		return realignToGrid(logRealign, val, false);
	}

	public int realignToGrid(boolean logRealign, double val, boolean roundUp) {
		return SharedUtils.realignTo(logRealign, val, roundUp, gridSize);
	}

	public static int realignTo(int val, int toVal) {
		return SharedUtils.realignTo(false, val, false, toVal);
	}

	public static void zoomEntity(int fromFactor, int toFactor, GridElement e) {
		Vector<GridElement> vec = new Vector<GridElement>();
		vec.add(e);
		zoomEntities(fromFactor, toFactor, vec);
	}

	public static void zoomEntities(int fromFactor, int toFactor, List<GridElement> selectedEntities) {

		/**
		 * The entities must be resized to the new factor
		 */

		for (GridElement entity : selectedEntities) {
			int newX = entity.getRectangle().x * toFactor / fromFactor;
			int newY = entity.getRectangle().y * toFactor / fromFactor;
			int newW = entity.getRectangle().width * toFactor / fromFactor;
			int newH = entity.getRectangle().height * toFactor / fromFactor;
			entity.setLocation(realignTo(newX, toFactor), realignTo(newY, toFactor));
			// Normally there should be no realign here but relations and custom elements sometimes must be realigned therefore we don't log it as an error
			if (entity instanceof CustomElement) {
				entity.setSize(newW, newH); // #478: do not realign width and height for custom elements, because this would mess up the CustomElement.changeSizeIfNoBugfix() call
			} else {
				entity.setSize(realignTo(newW, toFactor), realignTo(newH, toFactor));
			}

			// Resize the coordinates of the points of the relations
			if (entity instanceof Relation) {
				for (Point point : ((Relation) entity).getLinePoints()) {
					newX = point.getX() * toFactor / fromFactor;
					newY = point.getY() * toFactor / fromFactor;
					point.setX(realignTo(newX, toFactor));
					point.setY(realignTo(newY, toFactor));
				}
			}
		}
	}

	public void setGridAndZoom(int factor) {
		setGridAndZoom(factor, true);
	}

	public void setGridAndZoom(int factor, boolean manualZoom) {

		/**
		 * Store the old gridsize and the new one. Furthermore check if the zoom process must be made
		 */

		int oldGridSize = getGridSize();

		if (factor < 1 || factor > 20) {
			return; // Only zoom between 10% and 200% is allowed
		}
		if (factor == oldGridSize) {
			return; // Only zoom if gridsize has changed
		}

		setGridSize(factor);

		/**
		 * Zoom entities to the new gridsize
		 */

		zoomEntities(oldGridSize, gridSize, getDrawPanel().getGridElements());

		// AB: Zoom origin
		getDrawPanel().zoomOrigin(oldGridSize, gridSize);

		/**
		 * The zoomed diagram will shrink to the upper left corner and grow to the lower right
		 * corner but we want to have the zoom center in the middle of the actual visible drawpanel
		 * so we have to change the coordinates of the entities again
		 */

		if (manualZoom) {
			// calculate mouse position relative to UMLet scrollpane
			Point mouseLocation = Converter.convert(MouseInfo.getPointerInfo().getLocation());
			Point viewportLocation = Converter.convert(getDrawPanel().getScrollPane().getViewport().getLocationOnScreen());
			float x = mouseLocation.x - viewportLocation.x;
			float y = mouseLocation.y - viewportLocation.y;

			// And add any space on the upper left corner which is not visible but reachable by scrollbar
			x += getDrawPanel().getScrollPane().getViewport().getViewPosition().getX();
			y += getDrawPanel().getScrollPane().getViewport().getViewPosition().getY();

			// The result is the point where we want to center the zoom of the diagram
			float diffx, diffy;
			diffx = x - x * gridSize / oldGridSize;
			diffy = y - y * gridSize / oldGridSize;

			// AB: Move origin in opposite direction
			log.debug("diffX/diffY: " + diffx + "/" + diffy);
			log.debug("Manual Zoom Delta: " + realignToGrid(false, diffx) + "/" + realignToGrid(false, diffy));
			getDrawPanel().moveOrigin(realignToGrid(false, -diffx), realignToGrid(false, -diffy));

			for (GridElement e : getDrawPanel().getGridElements()) {
				e.setLocationDifference(realignToGrid(false, diffx), realignToGrid(false, diffy));
			}

			/**
			 * Now we have to do some additional "clean up" stuff which is related to the zoom
			 */

			getDrawPanel().updatePanelAndScrollbars();

			// Set changed only if diagram is not empty (otherwise no element has been changed)
			if (!drawpanel.getGridElements().isEmpty()) {
				setChanged(true);
			}

			float zoomFactor = CurrentDiagram.getInstance().getDiagramHandler().getZoomFactor() * 100;
			String zoomtext;
			if (CurrentDiagram.getInstance().getDiagramHandler() instanceof PaletteHandler) {
				zoomtext = "Palette zoomed to " + Integer.toString((int) zoomFactor) + "%";
			} else {
				zoomtext = "Diagram zoomed to " + Integer.toString((int) zoomFactor) + "%";
			}
			Notifier.getInstance().showInfo(zoomtext);
		}
	}

	public void setHandlerAndInitListeners(GridElement element) {
		if (HandlerElementMap.getHandlerForElement(element) != null) {
			((Component) element.getComponent()).removeMouseListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
			((Component) element.getComponent()).removeMouseMotionListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		}
		HandlerElementMap.setHandlerForElement(element, this);
		((Component) element.getComponent()).addMouseListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		((Component) element.getComponent()).addMouseMotionListener(HandlerElementMap.getHandlerForElement(element).getEntityListener(element));
		if (element instanceof NewGridElement) {
			((ComponentSwing) element.getComponent()).setHandler(this);
		}
		element.updateModelFromText(); // must be updated here because the new handler could have a different zoom level
	}
}
