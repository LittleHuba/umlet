package com.baselet.gui.menu;

import com.baselet.control.constants.SystemInfo;
import com.baselet.control.enums.Os;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.gui.helper.PlainColorIcon;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static com.baselet.control.constants.MenuConstants.ALIGN;
import static com.baselet.control.constants.MenuConstants.COPY;
import static com.baselet.control.constants.MenuConstants.CUT;
import static com.baselet.control.constants.MenuConstants.DELETE;
import static com.baselet.control.constants.MenuConstants.EDIT_SELECTED;
import static com.baselet.control.constants.MenuConstants.GROUP;
import static com.baselet.control.constants.MenuConstants.LAYER;
import static com.baselet.control.constants.MenuConstants.LAYER_DOWN;
import static com.baselet.control.constants.MenuConstants.LAYER_UP;
import static com.baselet.control.constants.MenuConstants.SET_BACKGROUND_COLOR;
import static com.baselet.control.constants.MenuConstants.SET_FOREGROUND_COLOR;
import static com.baselet.control.constants.MenuConstants.UNGROUP;

public class MenuFactorySwing extends MenuFactory {

	private static MenuFactorySwing instance = null;

	public static MenuFactorySwing getInstance() {
		if (instance == null) {
			instance = new MenuFactorySwing();
		}
		return instance;
	}

	public JMenuItem createDelete() {
		int[] keys = new int[]{KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE}; // backspace AND delete both work for deleting elements
		if (SystemInfo.OS == Os.MAC) { // MacOS shows the backspace key mapping because it's the only one working - see http://stackoverflow.com/questions/4881262/java-keystroke-for-delete/4881606#4881606
			return createJMenuItem(false, DELETE, keys, KeyEvent.VK_D, KeyEvent.VK_BACK_SPACE);
		} else {
			return createJMenuItem(false, DELETE, keys, KeyEvent.VK_D, KeyEvent.VK_DELETE);
		}
	}

	public JMenuItem createGroup() {
		return createJMenuItem(false, GROUP, KeyEvent.VK_G, true, null);
	}

	public JMenuItem createUngroup() {
		return createJMenuItem(false, UNGROUP, UNGROUP, KeyEvent.VK_N, KeyEvent.VK_U, true, null);
	}

	public JMenuItem createCut() {
		return createJMenuItem(false, CUT, CUT, KeyEvent.VK_T, KeyEvent.VK_X, true, null);
	}

	public JMenuItem createCopy() {
		return createJMenuItem(false, COPY, KeyEvent.VK_C, true, null);
	}

	public JMenuItem createEditSelected() {
		return createJMenuItemNoShortcut(false, EDIT_SELECTED, KeyEvent.VK_D);
	}


	public JMenu createSetColor(boolean fg) {
		String name = fg ? SET_FOREGROUND_COLOR : SET_BACKGROUND_COLOR;
		JMenu menu = new JMenu(name);
		menu.add(createJMenuItem(false, "default", name, null));
		for (String color : ColorOwn.COLOR_MAP.keySet()) {
			JMenuItem item = createJMenuItem(false, color, name, color);
			menu.add(item);
			item.setIcon(new PlainColorIcon(color));
		}
		return menu;
	}


	public JMenu createAlign() {
		JMenu alignMenu = new JMenu(ALIGN);
		for (String direction : new String[]{"Left", "Right", "Top", "Bottom"}) {
			alignMenu.add(createJMenuItem(false, direction, ALIGN, direction));
		}
		return alignMenu;
	}

	public JMenu createLayerUp() {
		JMenu alignMenu = new JMenu(LAYER);
		for (String direction : new String[]{LAYER_DOWN, LAYER_UP}) {
			alignMenu.add(createJMenuItem(false, direction, LAYER, direction));
		}
		return alignMenu;
	}

	private JMenuItem createJMenuItemNoShortcut(boolean grayWithoutDiagram, final String name, Integer mnemonic) {
		return createJMenuItem(grayWithoutDiagram, name, mnemonic, null, null); // because meta is null, no shortcut is created (only the mnemonic)
	}

	private JMenuItem createJMenuItem(boolean grayWithoutDiagram, final String name, Integer mnemonic, Boolean meta, Object param) {
		return createJMenuItem(grayWithoutDiagram, name, name, mnemonic, meta, param);
	}

	private JMenuItem createJMenuItem(boolean grayWithoutDiagram, final String menuName, final String actionName, final Object param) {
		return createJMenuItem(grayWithoutDiagram, menuName, actionName, null, null, param);
	}

	private JMenuItem createJMenuItem(boolean grayWithoutDiagram, final String menuName, final String actionName, Integer mnemonic, Boolean meta, final Object param) {
		return createJMenuItem(grayWithoutDiagram, menuName, actionName, mnemonic, mnemonic, meta, param);
	}

	private JMenuItem createJMenuItem(boolean grayWithoutDiagram, final String menuName, final String actionName, Integer mnemonic, Integer shortcut, Boolean meta, final Object param) {
		JMenuItem menuItem = new JMenuItem(menuName);
		if (mnemonic != null) {
			menuItem.setMnemonic(mnemonic);
			if (meta != null && shortcut != null) {
				menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut, !meta ? 0 : SystemInfo.META_KEY.getMask()));
			}
		}
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAction(actionName, param);
			}
		});
		if (grayWithoutDiagram) {
			diagramDependendComponents.add(menuItem);
		}
		return menuItem;
	}

	/**
	 * Create a JMenuItem with multiple key bindings (only one mnemonic can be set at any time).
	 *
	 * @see "http://docs.oracle.com/javase/tutorial/uiswing/misc/action.html"
	 */
	private JMenuItem createJMenuItem(boolean grayWithoutDiagram, final String name, int[] keyEvents, int mnemonic, int shortcut) {
		JMenuItem menuItem = new JMenuItem(name);
		MultipleKeyBindingsAction action = new MultipleKeyBindingsAction(name, shortcut);
		for (int keyEvent : keyEvents) {
			addKeyBinding(menuItem, keyEvent, name);
		}
		menuItem.getActionMap().put(name, action);
		menuItem.setAction(action);
		menuItem.setMnemonic(mnemonic);

		if (grayWithoutDiagram) {
			diagramDependendComponents.add(menuItem);
		}
		return menuItem;
	}

	private void addKeyBinding(JMenuItem menuItem, int keyEvent, String actionName) {
		menuItem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyEvent, 0), actionName);
	}

	@SuppressWarnings("serial")
	private class MultipleKeyBindingsAction extends AbstractAction {

		public MultipleKeyBindingsAction(String menuName, int shortcut) {
			super(menuName);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(shortcut, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doAction(getValue(NAME).toString(), null);
		}
	}
}
