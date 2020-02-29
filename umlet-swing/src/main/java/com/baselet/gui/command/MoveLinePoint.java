package com.baselet.gui.command;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.relation.Relation;

/**
 * only for old deprecated relation; new relation handles linepoint movement in the class-code
 */
public class MoveLinePoint extends Command {
	private final Relation _relation;
	private final int _linePointId, _diffx, _diffy;

	public int getLinePointId() {
		return _linePointId;
	}

	public Relation getRelation() {
		return _relation;
	}

	public int getDiffX() {
		return _diffx;
	}

	public int getDiffY() {
		return _diffy;
	}

	public MoveLinePoint(Relation rel, int i, int diffx, int diffy) {
		_relation = rel;
		_linePointId = i;
		_diffx = diffx;
		_diffy = diffy;
	}

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		_relation.moveLinePoint(_linePointId, getDiffX(), getDiffY());
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);
		_relation.moveLinePoint(_linePointId, -getDiffX(), -getDiffY());
	}

	@Override
	public boolean isMergeableTo(Command c) {
		if (!(c instanceof MoveLinePoint)) {
			return false;
		}
		MoveLinePoint mlp = (MoveLinePoint) c;
		if (getRelation() != mlp.getRelation()) {
			return false;
		}
		if (getLinePointId() != mlp.getLinePointId()) {
			return false;
		}
		return true;
	}

	@Override
	public Command mergeTo(Command c) {
		MoveLinePoint tmp = (MoveLinePoint) c;
		MoveLinePoint ret = new MoveLinePoint(getRelation(), getLinePointId(), getDiffX() + tmp.getDiffX(), getDiffY() + tmp.getDiffY());
		return ret;
	}
}
