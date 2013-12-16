package util.leveleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import roguelike.ui.graphics.Graphic;
import roguelike.ui.graphics.Graphic.GraphicFile;

/**
 * Extension of JPanel where the user can place and edit tiles for the current
 * level.
 * 
 * @author Dan
 * 
 */
public class EditorPanel extends JPanel {

	private static final long serialVersionUID = -5584676219132420183L;

	EditorWindow parent;
	int width = 0, height = 0;
	JLabel[][] tiles;
	String[][] tileInfo;
	String[][] objectInfo;

	/**
	 * Creates a new instance of EditorPanel with the default settings.
	 */
	public EditorPanel() {
		initialize();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.BLACK);
		setFocusable(true);
	}

	/**
	 * Sets level dimensions to the specified dimensions.
	 * 
	 * @param width
	 *            Width of level.
	 * @param height
	 *            Height of level.
	 */
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets tileset displayed to the specified value.
	 * 
	 * @param name
	 *            Name of GraphicFile.
	 */
	public void setParent(EditorWindow parent) {
		this.parent = parent;
	}

	/**
	 * Method that sets dimensions and places blank icons in every tile. Called
	 * upon instantiation and when modifying the dimensions of a map.
	 */
	public void initialize() {
		removeAll(); // In case there is data in the editor.

		StringBuilder wString = new StringBuilder("[32]");
		StringBuilder hString = new StringBuilder("[32]");

		// If width/height haven't been touched, set to defaults.
		if (width == 0)
			width = 40;
		if (height == 0)
			height = 25;

		/*
		 * For MigLayout - we want each cell to be 32x32 with a space of 1
		 * between them, to act as a grid.
		 */
		for (int i = 1; i < width; i++) {
			wString.append("1[32]");
		}

		for (int i = 1; i < height; i++) {
			hString.append("1[32]");
		}

		setLayout(new MigLayout("", wString.toString(), hString.toString()));

		/*
		 * Clear any data by initializing the arrays.
		 */
		tiles = new JLabel[width][height];
		tileInfo = new String[width][height];
		objectInfo = new String[width][height];

		// Create a new blank white 32x32 image.
		BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				img.setRGB(i, j, Color.WHITE.getRGB());
			}
		}

		// Set all cells to the previous white square.
		ImageIcon ic = new ImageIcon(img);
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = new JLabel();
				tiles[i][j].setIcon(ic);
				add(tiles[i][j], "cell " + i + " " + j);
			}

		}

		revalidate();
	}

	/**
	 * Loads a map from a pre-existing map file into the editor.
	 * 
	 * @param map
	 *            ArrayList of Strings containing the data.
	 */
	public void loadMapFromText(ArrayList<String> map) {
		initialize();

		int i = map.indexOf("#START TILE KEY#");
		i++;
		
//		JOptionPane.showMessageDialog(this, "Loading...");
//
//		new Thread() {
//			@Override
//		    public void run() {
//
//		       SwingUtilities.invokeLater(new Runnable() {
//		           public void run() {
//		              loadingDialog.setVisible(true);
//		           }
//		       });
//
//		    };
//		};
		// Place tile key into a hashmap.
		HashMap<Character, String> hmTiles = new HashMap<Character, String>();
		while (!map.get(i).equals("#END TILE KEY#")) {
			hmTiles.put(map.get(i).charAt(0), map.get(i).substring(3, map.get(i).length()));
			i++;
		}

		i = map.indexOf("#START TILES#");
		for (int y = 0; y < (map.indexOf("#END TILES#") - i - 1); y++) {
			for (int x = 0; x < map.get(i + 1).length(); x++) {
				String params = hmTiles.get(map.get(y + i + 1).charAt(x));
				String[] parts = params.split(",");
				String tileset = parts[0];
				int row = Integer.valueOf(parts[1].replaceAll("[ ]", ""));
				int col = Integer.valueOf(parts[2].replaceAll("[ ]", ""));

				tiles[x][y].setIcon(new ImageIcon(Graphic.getImage(GraphicFile.valueOf(tileset), row, col)));
			}
		}

	}

	/**
	 * Returns the image at the given coordinates.
	 * 
	 * @param x
	 *            x-coordinate.
	 * @param y
	 *            y-coordinate.
	 * @return Image at the given coordinates.
	 */
	public BufferedImage getImageAt(int x, int y) {
		ImageIcon icon = (ImageIcon) tiles[x][y].getIcon();
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0); // paint the Icon to the BufferedImage.
		g.dispose();

		return bi;
	}

}


