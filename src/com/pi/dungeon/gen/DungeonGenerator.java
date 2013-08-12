package com.pi.dungeon.gen;

import java.util.Random;

import com.pi.dungeon.gen.chunks.ChunkGenerator;
import com.pi.dungeon.gen.chunks.RoomChunkGenerator;

public class DungeonGenerator {
    private final int[][] map;
    private final ChunkGenerator[][] chunkGen;
    private final int width, height, chunkSize;
    private final Random rand;

    public DungeonGenerator(int chunkSize, int width, int height, long seed) {
	map = new int[width * chunkSize][height * chunkSize];
	chunkGen = new ChunkGenerator[width][height];
	this.width = width;
	this.height = height;
	this.chunkSize = chunkSize;
	this.rand = new Random(seed);
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		chunkGen[x][y] = new RoomChunkGenerator(this, x, y, chunkSize);
	    }
	}
    }

    public void setMap(int x, int y, int val) {
	map[x][y] = val;
    }

    public int getMap(int x, int y) {
	return map[x][y];
    }

    public void generate() {
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		chunkGen[x][y].generate();
	    }
	}
    }

    public int[][] getMap() {
	return map;
    }

    public void copyChunkIntoMap(int x, int y, int[][] chunkData) {
	int xOff = x * chunkSize;
	int yOff = y * chunkSize;
	for (int row = 0; row < chunkSize; row++) {
	    System.arraycopy(chunkData[row], 0, map[row + yOff], xOff,
		    chunkSize);
	}
    }

    public ChunkGenerator getChunkGenerator(int x, int y) {
	if (x >= 0 && y >= 0 && x < chunkGen.length && y < chunkGen[x].length)
	    return chunkGen[x][y];
	else
	    return null;
    }

    public Random getRandom() {
	return rand;
    }
}
