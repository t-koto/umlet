package com.baselet.element;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.util.BitSet;

import com.baselet.control.HandlerElementMap;
import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.SelectorOld;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.swing.DrawHandlerSwing;
import com.baselet.element.interfaces.Component;
import com.baselet.element.relation.Relation;

public class ComponentSwing extends JComponent implements Component {
	private static final long serialVersionUID = 1L;

	private final DrawHandlerSwing drawer;
	private final DrawHandlerSwing metaDrawer;
	private final NewGridElement gridElement;

	public ComponentSwing(NewGridElement gridElement) {
		this.gridElement = gridElement;
		drawer = new DrawHandlerSwing(gridElement);
		metaDrawer = new DrawHandlerSwing(gridElement);
	}

	@Override
	public void paint(Graphics g) {
		drawer.setGraphics(g);
		metaDrawer.setGraphics(g);
		SelectorOld selector = HandlerElementMap.getHandlerForElement(gridElement).getDrawPanel().getSelector();
		boolean selected = selector.isSelected(gridElement);
		drawer.drawAll(selected);
		if (selected) {
			metaDrawer.drawAll();
		} else if (gridElement instanceof Relation) {
			Relation relation = (Relation) gridElement;
			if (selector.isRelationJointSelected(relation)) {
				BitSet selectedJoints = selector.getSelectedRelationJoints(relation);
				metaDrawer.drawSelectedJoint(relation, selectedJoints);
			}
		}
	}

	@Override
	public void translateForExport() { // translation breaks export of some elements, therefore its disabled - see issue 353
		// drawer.setTranslate(true);
		// metaDrawer.setTranslate(true);
	}

	@Override
	public DrawHandler getDrawHandler() {
		return drawer;
	}

	@Override
	public DrawHandler getMetaDrawHandler() {
		return metaDrawer;
	}

	/**
	 * Must be overwritten because Swing uses this method to tell if 2 elements are overlapping
	 * It's also used to determine which element gets selected if there are overlapping elements (the smallest one)
	 * IMPORTANT: on overlapping elements, contains is called for all elements until the first one returns true, then the others contain methods are not called
	 */
	@Override
	public boolean contains(java.awt.Point p) {
		return this.contains(p.x, p.y);
	}

	/**
	 * Must be overwritten because Swing sometimes uses this method instead of contains(Point)
	 */
	@Override
	public boolean contains(int x, int y) {
		Rectangle r = gridElement.getRectangle();
		// only check if element selectable on the position, because some elements are not everywhere selectable (eg: Relation)
		if (gridElement.isSelectableOn(new Point(r.getX() + x, r.getY() + y))) {
			return ElementUtils.checkForOverlap(gridElement, new Point(x, y));
		}
		else {
			return false;
		}
	}

	@Override
	public Rectangle getBoundsRect() {
		return Converter.convert(getBounds());
	}

	@Override
	public void repaintComponent() {
		this.repaint();
	}

	@Override
	public void setBoundsRect(Rectangle rect) {
		this.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public void afterModelUpdate() {
		repaint(); // necessary e.g. for NewGridElement Relation to make sure it gets redrawn correctly when a sticking element is moved around
	}

	public void setHandler(DiagramHandler diagramHandler) {
		drawer.setHandler(diagramHandler);
		metaDrawer.setHandler(diagramHandler);
	}

}
