package model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Wrapper class for multiple games within a single game state.
 */
public class Division implements Serializable{

	private static final long serialVersionUID = 1L;
	
	String name;
	
	LinkedList<Player> players;
	LinkedList<Buyer> buyers;
	LinkedList<Seller> sellers;
	
	int buyerID=0;	// uID of the next buyer spawned to this division (incremented by +1 for each constructor call)
	int sellerID=0;	// uID of the next seller spawned to this division (incremented by +1 for each constructor call)
	
	int turn; // Turn number of this with 0 representing a freshly started game.
	
	/**
	 * Division can be used to simulate multiple games within a single game state. If only a
	 * single division is used, the name should be left blank "" to keep file names simple.
	 */
	public Division(String name) {
		players = new LinkedList<Player>();
		buyers = new LinkedList<Buyer>();
		sellers = new LinkedList<Seller>();
		
		this.name=name;
		turn=0;
	}
	
	/**
	 * Retrieves a reference to the player with the specified name if such a player exists
	 * in this division. Returns 0 otherwise.
	 */
	public Player getPlayer(String name){
		
		for(Player player: players) {
			if(name.equals(player.getName()))return player;
		}
		return null;
		
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	public LinkedList<Buyer> getBuyers() {
		return buyers;
	}

	public void setBuyers(LinkedList<Buyer> buyers) {
		this.buyers = buyers;
	}

	public LinkedList<Seller> getSellers() {
		return sellers;
	}

	public void setSellers(LinkedList<Seller> sellers) {
		this.sellers = sellers;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
