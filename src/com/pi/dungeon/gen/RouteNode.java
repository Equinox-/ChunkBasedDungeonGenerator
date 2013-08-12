package com.pi.dungeon.gen;

import java.awt.Point;

public class RouteNode extends Point {
    private static final long serialVersionUID = 1L;
    private final RouteNode parent;

    public RouteNode(final RouteNode parent, int x, int y) {
	super(x, y);
	this.parent = parent;
    }

    public RouteNode getParent() {
	return parent;
    }
}
