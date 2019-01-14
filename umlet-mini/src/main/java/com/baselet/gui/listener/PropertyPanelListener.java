package com.baselet.gui.listener;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PropertyPanelListener implements KeyListener, DocumentListener {

	public PropertyPanelListener() {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\u001b') { // ESC Key: Leaves the Property Panel
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateGridElement();
			}
		});
	}

	protected void updateGridElement() {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}
}
