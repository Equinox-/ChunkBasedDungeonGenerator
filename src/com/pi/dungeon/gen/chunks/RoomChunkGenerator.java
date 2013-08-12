package com.pi.dungeon.gen.chunks;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.pi.dungeon.Direction;
import com.pi.dungeon.Tile;
import com.pi.dungeon.gen.DungeonGenerator;
import com.pi.dungeon.gen.RouteNode;

public class RoomChunkGenerator extends ChunkGenerator {
    // config
    int roomCenterVariance = 5;
    int minVerticalRoomSize = 4;
    int maxVerticalRoomSize = 15;
    int minHorizontalRoomSize = 4;
    int maxHorizontalRoomSize = 15;
    int pathRandomness = 5;
    boolean deadEnds = false; // Can result in unaccessible rooms

    List<Point> nodes = new ArrayList<Point>();
    ChunkRouteFinding pathing;

    private int minSpaceX = 0;
    private int minSpaceY = 0;
    private int maxSpaceX;
    private int maxSpaceY;

    public RoomChunkGenerator(DungeonGenerator dGen, int x, int y, int chunkSize) {
	super(dGen, x, y, chunkSize);
	pathing = new ChunkRouteFinding(this, 1, 1, chunkSize - 2,
		chunkSize - 2);
    }

    @Override
    public void generate() {
	minSpaceX = 0;
	minSpaceY = 0;
	maxSpaceX = chunkSize - 1;
	maxSpaceY = chunkSize - 1;

	// Copy in above and left edges
	if (dGen.getChunkGenerator(x - 1, y) != null) {
	    edges[Direction.LEFT] = dGen.getChunkGenerator(x - 1, y).getEdge(
		    Direction.RIGHT);
	} else if (deadEnds) {
	    genRandomEdge(Direction.LEFT, rand.nextInt(1) + 1);
	}
	if (dGen.getChunkGenerator(x, y - 1) != null) {
	    edges[Direction.UP] = dGen.getChunkGenerator(x, y - 1).getEdge(
		    Direction.DOWN);
	} else if (deadEnds) {
	    genRandomEdge(Direction.UP, rand.nextInt(1) + 1);
	}

	genRoom();

	if (deadEnds || dGen.getChunkGenerator(x, y + 1) != null)
	    genRandomEdge(Direction.DOWN, rand.nextInt(1) + 1);
	if (deadEnds || dGen.getChunkGenerator(x + 1, y) != null)
	    genRandomEdge(Direction.RIGHT, rand.nextInt(1) + 1);

	buildNodeList();
	connectNodes();

	copyInEdge(Direction.DOWN, Tile.ROOM, Tile.DOOR);
	copyInEdge(Direction.RIGHT, Tile.ROOM, Tile.DOOR);

	for (Point p : nodes)
	    if (getMap(p.x, p.y) != Tile.DOOR)
		setMap(p.x, p.y, Tile.CHUNK_DOOR);
    }

    public void genRoom() {

	int hSize2 = (rand.nextInt(maxHorizontalRoomSize
		- minHorizontalRoomSize) + minHorizontalRoomSize) / 2;
	int vSize2 = (rand.nextInt(maxVerticalRoomSize - minVerticalRoomSize) + minVerticalRoomSize) / 2;
	int x1 = (chunkSize / 2) - roomCenterVariance
		+ rand.nextInt(roomCenterVariance * 2) - hSize2;
	int y1 = (chunkSize / 2) - roomCenterVariance
		+ rand.nextInt(roomCenterVariance * 2) - vSize2;
	int x2 = Math.min(x1 + hSize2 + hSize2, maxSpaceX);
	int y2 = Math.min(y1 + vSize2 + vSize2, maxSpaceY);
	x1 = Math.max(x1, minSpaceX);
	y1 = Math.max(y1, minSpaceY);

	List<Integer> pDirs = new ArrayList<Integer>(4);
	if (x1 > 0)
	    pDirs.add(Direction.LEFT);
	if (y1 > 0)
	    pDirs.add(Direction.UP);
	if (x2 < chunkSize - 1)
	    pDirs.add(Direction.RIGHT);
	if (y2 < chunkSize - 1)
	    pDirs.add(Direction.DOWN);
	int dir = pDirs.get(rand.nextInt(pDirs.size()));

	if (x2 - x1 > 2 && y2 - y1 > 2) {
	    int rX = x1 + rand.nextInt(x2 - x1 - 2) + 1;
	    int rY = y1 + rand.nextInt(y2 - y1 - 2) + 1;
	    for (int x = x1; x <= x2; x++) {
		for (int y = y1; y <= y2; y++) {
		    if ((y == y1 && dir == Direction.UP && x == rX)
			    || (y == y2 && dir == Direction.DOWN && x == rX)
			    || (x == x1 && dir == Direction.LEFT && y == rY)
			    || (x == x2 && dir == Direction.RIGHT && y == rY)) {
			setMap(x, y, Tile.DOOR);
		    } else if (y == y1 || y == y2 || x == x1 || x == x2) {
			setMap(x, y, Tile.ROOM_WALL);
		    } else
			setMap(x, y, Tile.ROOM);
		}
	    }
	}
    }

    public void buildNodeList() {
	nodes.clear();
	for (int edge = 0; edge < edges.length; edge++) {
	    for (int i = 0; i < chunkSize; i++) {
		if (edges[edge][i]) {
		    nodes.add(new Point(getXFromEdge(edge, i, 0), getYFromEdge(
			    edge, i, 0)));
		}
	    }
	}

	for (int x = 0; x < chunkSize; x++) {
	    for (int y = 0; y < chunkSize; y++) {
		if (getMap(x, y) == Tile.DOOR) {
		    nodes.add(new Point(x, y));
		}
	    }
	}
    }

    public void connectNodes() {
	int[] closed = new int[nodes.size()];
	for (int i = 0; i < nodes.size(); i++) {
	    RouteNode[] bestPath = null;
	    int bestPathNode = -1;
	    int bestPathDist = Integer.MAX_VALUE;
	    Point me = nodes.get(i);
	    for (int i2 = 0; i2 < nodes.size(); i2++) {
		if (i2 != i) {
		    if (pathing.findRoute(me, nodes.get(i2), pathRandomness,
			    Tile.EMPTY, Tile.PATH)) {
			int len = pathing.getPathLength() * closed[i2];
			if (len < bestPathDist
				|| (len == bestPathDist && rand.nextBoolean())) {
			    bestPathNode = i2;
			    bestPath = pathing.getPathArray(
				    rand.nextInt(pathing.getPathCount()))
				    .clone();
			    bestPathDist = len;
			}
		    }
		}
	    }

	    if (bestPath != null) {
		closed[i]++;
		closed[bestPathNode]++;
		for (int q = 0; q < bestPath.length; q++) {
		    if ((bestPath[q].x != me.x || bestPath[q].y != me.y)
			    && (bestPath[q].x != nodes.get(bestPathNode).x || bestPath[q].y != nodes
				    .get(bestPathNode).y))
			setMap(bestPath[q].x, bestPath[q].y, Tile.PATH);
		}
	    }
	}
    }

    public Point[] genRandomEdge(int edge, int count) {
	Point[] pts = new Point[count];
	while (count > 0) {
	    int idx = rand.nextInt(chunkSize);
	    if (!edges[edge][idx]) {
		count--;
		pts[count] = new Point(getXFromEdge(edge, idx, 0),
			getYFromEdge(edge, idx, 0));
		edges[edge][idx] = true;
	    }
	}
	return pts;
    }

    public void copyInEdge(int edge, int... valid) {
	for (int i = 0; i < chunkSize; i++) {
	    int tile = getMap(getXFromEdge(edge, i, 0),
		    getYFromEdge(edge, i, 0));
	    for (int q : valid) {
		edges[edge][i] = edges[edge][i] || q == tile;
	    }
	}
    }
}
