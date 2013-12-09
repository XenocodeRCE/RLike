package roguelike.main;

import roguelike.actors.Player;
import roguelike.etc.Session;
import roguelike.ui.Window;
import roguelike.ui.graphics.Graphic.GraphicFile;

public class LoadCave {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Window w = new Window();

		Session.player = new Player(0,0);
		Session.player.setImage(GraphicFile.CLASSM, 2, 5);
		
		w.notifyCharacterSelected();

	}

}
