package com.pi.dungeon.gen.chunks;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pi.dungeon.gen.RouteNode;

public class ChunkRouteFinding {
    private final ChunkGenerator gen;
    private List<LinkedList<RouteNode>> route = new ArrayList<LinkedList<RouteNode>>();
    private int pathLength = -1;
    boolean cont = false;
    int minX, minY, maxX, maxY;

    public ChunkRouteFinding(ChunkGenerator gen, int minX, int minY, int maxX,
	    int maxY) {
	this.gen = gen;
	this.minX = minX;
	this.minY = minY;
	this.maxX = maxX;
	this.maxY = maxY;
    }

    public boolean findRoute(Point start, Point end, int lengthDelta,
	    int... okTiles) {
	route.clear();
	pathLength = -1;
	List<RouteNode> open = new ArrayList<RouteNode>();
	open.add(new RouteNode(null, start.x, start.y));
	final boolean[][] closed = new boolean[gen.getChunkSize()][gen
		.getChunkSize()];

	while (open.size() > 0) {
	    List<RouteNode> nOpen = new ArrayList<RouteNode>();
	    for (RouteNode node : open) {
		int x = node.x, y = node.y;
		if (!closed[x][y]) {
		    if (Math.abs(x - end.x) + Math.abs(y - end.y) <= 1) {
			buildPath(new RouteNode(node, end.x, end.y));
		    } else {
			closed[x][y] = true;
		    }
		    if (x > minX && !closed[x - 1][y]
			    && isOpen(x - 1, y, okTiles)) {
			nOpen.add(new RouteNode(node, x - 1, y));
		    }
		    if (y > minY && !closed[x][y - 1]
			    && isOpen(x, y - 1, okTiles)) {
			nOpen.add(new RouteNode(node, x, y - 1));
		    }
		    if (x < maxX && !closed[x + 1][y]
			    && isOpen(x + 1, y, okTiles)) {
			nOpen.add(new RouteNode(node, x + 1, y));
		    }
		    if (y < maxY && !closed[x][y + 1]
			    && isOpen(x, y + 1, okTiles)) {
			nOpen.add(new RouteNode(node, x, y + 1));
		    }
		}
	    }
	    if (route.size() > 0) {
		lengthDelta--;
		if (lengthDelta <= 0) {
		    return true;
		}
	    }
	    open.clear();
	    open.addAll(nOpen);
	}
	return false;
    }

    private void buildPath(RouteNode node) {
	LinkedList<RouteNode> ls = new LinkedList<RouteNode>();
	pathLength = 0;
	RouteNode curr = node;
	while (curr != null) {
	    ls.addLast(curr);
	    pathLength++;
	    curr = curr.getParent();
	}
	route.add(ls);
    }

    public int getPathLength() {
	return pathLength;
    }

    public Iterator<RouteNode> getPath(int i) {
	return route.get(i).iterator();
    }

    public RouteNode[] getPathArray(int i) {
	return route.get(i).toArray(new RouteNode[route.get(i).size()]);
    }

    public int getPathCount() {
	return route.size();
    }

    private boolean isOpen(int x, int y, int... okTiles) {
	for (int i : okTiles) {
	    if (gen.getMap(x, y) == i)
		return true;
	}
	return false;
    }
}
