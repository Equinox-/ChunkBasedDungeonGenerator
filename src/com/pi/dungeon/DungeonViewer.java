package com.pi.dungeon;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import com.pi.dungeon.gen.DungeonGenerator;

public class DungeonViewer extends JFrame {
    private static final long serialVersionUID = 1L;
    private DungeonGenerator gen;

    public DungeonViewer() {
	super("ChunkGenerator");
	gen = new DungeonGenerator(20, 10, 10,7124L);
	gen.generate();
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setSize(1000, 1000);
	setLocation(0, 0);
	setVisible(true);
    }

    int scale = 4;

    @Override
    public void paint(Graphics g) {
	super.paint(g);
	int[][] map = gen.getMap();
	for (int x = 0; x < map.length; x++) {
	    for (int y = 0; y < map[0].length; y++) {
		switch (map[x][y]) {
		case Tile.EMPTY:
		    g.setColor(Color.BLACK);
		    break;
		case Tile.ROOM:
		    g.setColor(Color.WHITE);
		    break;
		case Tile.ROOM_WALL:
		    g.setColor(Color.RED);
		    break;
		case Tile.PATH:
		    g.setColor(Color.BLUE);
		    break;
		case Tile.DOOR:
		    g.setColor(Color.CYAN);
		    break;
		case Tile.CHUNK_DOOR:
		    g.setColor(Color.MAGENTA);
		    break;
		}
		g.fillRect(x * scale + 20, y * scale + 30, scale, scale);
		if (y % 20 == 0) {
		    g.setColor(Color.CYAN);
		    g.drawLine(0, y * scale + 30, getWidth(), y * scale + 30);
		}
	    }
	    if (x % 20 == 0) {
		g.setColor(Color.CYAN);
		g.drawLine(x * scale + 20, 0, x * scale + 20, getHeight());
	    }
	}
    }

    public static void main(String[] args) {
	new DungeonViewer();
    }
}
