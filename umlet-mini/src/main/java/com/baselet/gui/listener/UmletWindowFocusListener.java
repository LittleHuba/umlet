package com.baselet.gui.listener;

import com.baselet.control.config.SharedConfig;

import java.awt.event.WindowEvent;

public class UmletWindowFocusListener implements java.awt.event.WindowFocusListener {

	@Override
	public void windowGainedFocus(WindowEvent e) {
		SharedConfig.getInstance().setStickingEnabled(true); // shift button may have stopped being pressed, therefore assume sticking is enabled again
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
	}

}
