package model;

import java.io.Serializable;

/**
 * This class represents a single technology within the tech tree. Note that this class only defines the
 * structure of a technology and does not contain the actual implementation. See TechTree class...
 */
public class Tech implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	Player player;
	String name;		// name of the technology
	boolean active;		// true if this technology is researched for the player given above
	
	double gold;		// gold cost of the technology
	double code;		// code cost of the technology
	double data;		// data cost of the technology
	double insight;		// insight cost of the technology
	
	public Tech(Player parent, String name, double gold, double code, double data, double insight) {
		this.player=parent;
		this.name=name;
		this.gold=gold;
		this.code=code;
		this.data=data;
		this.insight = insight;
		player.techs.add(this);
		active=false;
	}
	
	/**
	 * Call this method when a player intends to research this technology
	 */
	public void research() {
		
		// Return warning message if this technology was already researched in a previous turn
		
		if(active) {
			System.out.println("Warning - "+ name + " is already researched for player "+player.getName());
			return;
		}
		
		// Return a warning message if the player lacks the resources to do so. Can be bypassed to give
		// some leeway if the player is short by a minor amount that is covered by income during the turn
		
		if((gold > player.gold || code > player.code || data > player.data || insight > player.insight) && true) {			
			System.out.println("Warning - Unable to research "+name+" on player "+player.getName());		
		}else {
			player.gain(-gold,2);
			player.code = player.code - code;
			player.data = player.data - data;
			player.insight = player.insight - insight;
			
			player.costsInvest = player.costsInvest + gold;
			
			active = true;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public double getGold() {
		return gold;
	}

	public void setGold(double gold) {
		this.gold = gold;
	}

	public double getCode() {
		return code;
	}

	public void setCode(double code) {
		this.code = code;
	}

	public double getData() {
		return data;
	}

	public void setData(double data) {
		this.data = data;
	}

	public double getInsight() {
		return insight;
	}

	public void setInsight(double insight) {
		this.insight = insight;
	}
	

	
	
	

}
