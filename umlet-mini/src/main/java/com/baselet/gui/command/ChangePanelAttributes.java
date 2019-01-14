package com.baselet.gui.command;

import com.baselet.control.Main;
import com.baselet.diagram.DiagramHandler;
import com.baselet.element.interfaces.GridElement;

public class ChangePanelAttributes extends Command {
	private GridElement _entity;

	public GridElement getEntity() {
		return _entity;
	}

	private String _newState;
	private String _oldState;
	private int _oldCaret;
	private int _newCaret;

	public String getNewState() {
		return _newState;
	}

	public String getOldState() {
		return _oldState;
	}

	public int getOldCaret() {
		return _oldCaret;
	}

	public int getNewCaret() {
		return _newCaret;
	}

	public ChangePanelAttributes(GridElement e, String oldState, String newState, int oldCaret, int newCaret) {
		_entity = e;
		_newState = newState;
		_oldState = oldState;
		_newCaret = newCaret;
		_oldCaret = oldCaret;
	}

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		_entity.setPanelAttributes(_newState);
		_entity.repaint();

		GridElement gridElement = Main.getInstance().getEditedGridElement();
	}

	@Override
	public void undo(DiagramHandler handler) {
		// AB: Do not call super.undo() which would deselect the entity
		// super.undo(handler);
		_entity.setPanelAttributes(_oldState);
		_entity.repaint();

		GridElement gridElement = Main.getInstance().getEditedGridElement();
		if (gridElement != null && gridElement.equals(_entity)) {
		}
	}

	@Override
	public boolean isMergeableTo(Command c) {
		// method is not mergeable (to allow undo of property changes)
		return false;
	}

	@Override
	public Command mergeTo(Command c) {
		ChangePanelAttributes tmp = (ChangePanelAttributes) c;
		ChangePanelAttributes ret = new ChangePanelAttributes(getEntity(), tmp.getOldState(), getNewState(), tmp.getOldCaret(), getNewCaret());
		return ret;
	}

	@Override
	public String toString() {
		return "Changestate from " + getOldState() + " to " + getNewState();
	}
}
