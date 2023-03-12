package model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Root node of the game state used for saving/loading
 */
public class GameState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	static GameState instance;
	
	LinkedList<Division> division;
	
	public static GameState getInstance() {
		if(instance==null) {
			instance=new GameState();
			instance.division = new LinkedList<Division>();
		}
		return instance;
	}

	public static void setInstance(GameState load) {
		instance = load;
	}

	public LinkedList<Division> getDivision() {
		return division;
	}

	public void setDivision(LinkedList<Division> division) {
		this.division = division;
	}


	
	
	
}
