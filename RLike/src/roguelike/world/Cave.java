package roguelike.world;

import java.awt.Point;
import java.util.ArrayList;

import roguelike.actors.Actor;
import roguelike.actors.Feature.FeatureType;
import roguelike.actors.Tile;
import roguelike.ui.graphics.Graphic.GraphicFile;

/**
 * Cave-based floor.
 * 
 * @author Dan
 * 
 */
public class Cave extends Floor {

	double tolerance;

	/**
	 * Creates a cave with default enclosed area.
	 */
	public Cave() {
		actors = new ArrayList<Actor>();
		tolerance = 0.51;
	}

	/**
	 * * Creates a cave with the specified tolerance for determining which areas
	 * are wall and which are open. For best results, use a tolerance of around
	 * 0.50.
	 * 
	 * @param tolerance
	 *            Double between 0.0 - 1.0
	 * @throws IllegalArgumentException
	 *             If tolerance is outside the expected range.
	 */
	public Cave(int tolerance) throws IllegalArgumentException {
		if (tolerance < 0.0 || tolerance > 1.0)
			throw new IllegalArgumentException();
		actors = new ArrayList<Actor>();
		this.tolerance = tolerance;
	}

	@Override
	public void generateFloor() {
		super.generateFloor();

		for (int x = 0; x < XMAX; x++) {
			for (int y = 0; y < YMAX; y++) {
				if (Math.random() > tolerance) {
					Tile t = new Tile(x, y, false, GraphicFile.FEATURES, 3);
					actors.add(t);
				}
			}
		}

		/*
		 * Utilize celluar automata rule B678/S345678 to generate a natural cave
		 * looking layout.
		 */
		int count = 1;
		while (count != 0) {
			count = 0;

			for (int x = 0; x < XMAX; x++) {
				for (int y = 0; y < YMAX; y++) {
					int n = numberOfNeighbors(x, y);
					Actor a = getActorAt(x, y);

					if (n >= 6 && a == null) { // If birth conditions are met
						Tile t = new Tile(x, y, false, GraphicFile.FEATURES, 3);
						actors.add(t);
						count++;
					} else if (n < 3 && a != null) { // If survival not met
						actors.remove(a);
						count++;
					}
				}
			}
		}

		encloseLevel(GraphicFile.FEATURES, 2);
		fillLevelWithTiles(GraphicFile.GROUNDS, 3, true);

		Point p = getRandomOpenTile();
		createStairs(p.x, p.y, FeatureType.DOWNSTAIRS, GraphicFile.DUNGEON, 42);
		
		if (depth > 0) {
			p = getRandomAccessibleTile(downstairs.getCoords());
			createStairs(p.x, p.y, FeatureType.UPSTAIRS, GraphicFile.DUNGEON, 41);
		}
		
		populateWithCreatures();
		generateItems();
	}

	/**
	 * Get number of adjacent cells containing a tile.
	 * 
	 * @param x
	 *            x-coordinate.
	 * @param y
	 *            y-coordinate.
	 * @return Number of adjacent cells containing a tile.
	 */
	private int numberOfNeighbors(int x, int y) {

		final int[] XARR = { x - 1, x, x + 1, x + 1, x + 1, x, x - 1, x - 1 };
		final int[] YARR = { y - 1, y - 1, y - 1, y, y + 1, y + 1, y + 1, y };

		int count = 0;
		for (int i = 0; i < XARR.length; i++) {
			if (XARR[i] >= 0 && XARR[i] <= XMAX && YARR[i] >= 0 && YARR[i] <= YMAX) {
				if (getActorAt(XARR[i], YARR[i]) != null)
					count++;
			}
		}

		return count;
	}

	@Override
	public Tile getFloorTile(int x, int y) {
		return new Tile(x, y, true, GraphicFile.GROUNDS, 3);
	}
}
