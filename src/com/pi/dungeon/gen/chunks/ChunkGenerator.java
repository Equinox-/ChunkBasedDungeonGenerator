package com.pi.dungeon.gen.chunks;

import java.util.Random;

import com.pi.dungeon.Direction;
import com.pi.dungeon.gen.DungeonGenerator;

public abstract class ChunkGenerator {
    protected final DungeonGenerator dGen;
    protected final int x, y, chunkSize;
    protected Random rand;
    protected boolean[][] edges;

    public ChunkGenerator(DungeonGenerator dGen, int x, int y, int chunkSize) {
	this.dGen = dGen;
	this.x = x;
	this.y = y;
	this.chunkSize = chunkSize;
	edges = new boolean[4][chunkSize];
	this.rand = dGen.getRandom();
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public boolean[] getEdge(int edge) {
	return edges[edge];
    }

    public int getXFromEdge(int edge, int edgeSpace, int out) {
	switch (edge) {
	case Direction.UP:
	case Direction.DOWN:
	    return edgeSpace;
	case Direction.LEFT:
	    return out;
	case Direction.RIGHT:
	    return chunkSize - 1 - out;
	default:
	    return -1;
	}
    }

    public int getYFromEdge(int edge, int edgeSpace, int out) {
	switch (edge) {
	case Direction.UP:
	    return out;
	case Direction.DOWN:
	    return chunkSize - 1 - out;
	case Direction.LEFT:
	case Direction.RIGHT:
	    return edgeSpace;
	default:
	    return -1;
	}
    }

    public void setMap(int x, int y, int val) {
	if (x < 0 || x >= chunkSize || y < 0 || y >= chunkSize)
	    throw new ArrayIndexOutOfBoundsException("Out of bounds!");
	dGen.setMap(x + (this.x * chunkSize), y + (this.y * chunkSize), val);
    }

    public int getMap(int x, int y) {
	if (x < 0 || x >= chunkSize || y < 0 || y >= chunkSize)
	    throw new ArrayIndexOutOfBoundsException("Out of bounds!");
	return dGen.getMap(x + (this.x * chunkSize), y + (this.y * chunkSize));
    }

    public abstract void generate();

    public int getChunkSize() {
	return chunkSize;
    }
}
