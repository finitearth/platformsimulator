package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.Buyer;
import model.Division;
import model.GameState;
import model.Player;
import model.Seller;
import model.TechTree;

/**
 * This class houses the main method and manages the high-level operation of the game (create/save/load).
 * Additionally, it stores the decisions made by the students.
 */
public class GameController {
	
	static GameController instance;
	
	boolean newGame;
	
	String path;
	GameState state;

	/**
	 * If newGame is true, a new game is initiated
	 * If newGame is false, each execution of the main function advances the game by one turn.
	 */
	public static void main(String[] args) {
		
		
		// Detect path the project in launched from (project folder for launch within eclipse and
		// path of the jar file for launch outside of the IDE.
		
		instance = new GameController();
		instance.path = System.getProperty("user.dir")+"/saves/";
		
		// Toggles between new game and resume game
		instance.newGame = false;
		
		// Starts a new game with configuration as specified in newGame method		
		if(instance.newGame) {
			instance.newGame();		
			instance.saveGame();
			ExportHTML.getInstance().exportGameState();
		}

		// Resumes a running game		
		if(! instance.newGame) {
			instance.loadGame();
			instance.applyDecisions();
			instance.executeSimulation();
			instance.saveGame();
			ExportHTML.getInstance().exportGameState();	
		}
		
	}

	/**
	 * Creates a new game with the divisions and players specified within the function.
	 * Currently holds decisions of WMK-MM-20A
	 */
	public void newGame() {		
		
		// This instance of the game employs a single division with 6 student groups
		state = GameState.getInstance();
		Division d = new Division("");
		
		// All six players are added to the same division.
		Player p= new Player("Einfach.Lina",d);
		d.getPlayers().add(p);
		
		p= new Player("Selfmade Meetingpoint",d);
		d.getPlayers().add(p);
		
		p= new Player("Eleven Friends",d);
		d.getPlayers().add(p);
		
		p= new Player("Se Plättform",d);
		d.getPlayers().add(p);
		
		p= new Player("Probst Industries",d);
		d.getPlayers().add(p);
		
		p= new Player("Unique Art",d);
		d.getPlayers().add(p);

		// In the end, the division with six players is added to the game state
		state.getDivision().add(d);
		
	}
	
	/**
	 * Loads a previously saved game state. The file path supplied in the first line of the try block
	 * determines if the most recent save or a save from a specific turn is loaded.
	 * 
	 * Use (path+"\\save.txt") to load the most recent save
	 * Use (path+"\\save_3.txt") to load a save from a specific turn (turn 3 in this example)
	 */
	public void loadGame() {
		
		try {
		    FileInputStream fileInputStream = new FileInputStream(path+"\\save.txt");	// load most recent save or specific turn
		    
		    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		    state = (GameState) objectInputStream.readObject();
		    GameState.setInstance(state);
		    objectInputStream.close();     
		    Logger.getInstane().log("Game state loaded. Turn: "+state.getDivision().getFirst().getTurn(), 1);
		    
		    
		}catch(Exception e) {
			System.out.println("Unable to load game state!");
			e.printStackTrace();			
		}
		
	}
	
	
	/**
	 * Saves the game both as the new most recent save file (save.txt) and as a turn specific game state. 
	 * See loadGame() on how to recover a specific save file instead of the most recent one.
	 */
	public void saveGame() {
		
		try {
		    FileOutputStream fileOutputStream = new FileOutputStream(path+"\\save.txt");
		    
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(state);
		    objectOutputStream.flush();
		    objectOutputStream.close();
		    
		    fileOutputStream = new FileOutputStream(path+"\\save_" + state.getDivision().getFirst().getTurn() + ".txt");
		    objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(state);
		    objectOutputStream.flush();
		    objectOutputStream.close();
		    
		    Logger.getInstane().log("Game state saved. Turn: "+state.getDivision().getFirst().getTurn(), 1);
		}
		catch(Exception e) {
			System.out.println("Unable to save game state!");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Applies the decisions of the players to the game's variables. For each player and each turn
	 * there is a block of code to be executed.
	 */
	public void applyDecisions() {
		
		// Reset income/cost counters from the previous round
		
		for(Player player:state.getDivision().get(0).getPlayers()) {
			player.clearRoundData();
		}
		
		int turn = state.getDivision().get(0).getTurn();	// Get current turn
		
		// Decisions of player: Einfach.Lina
		
		Player target=state.getDivision().get(0).getPlayer("Einfach.Lina");
		if(turn==0) {
			
			target.alignmentPrice = 0.5;
			target.alignmentProduct = 0.65;
			
			target.sellerPriceSign = 5;
			target.sellerPriceSub = 10;
			
			target.sellerPriceComission =0.02;
			target.buyerPriceComission = 0.02;
			
			target.buyerAdActivityCPM = 0.03;
			target.buyerAdActivityPPC = 0.07;
			
			target.sellerAdActivityCPM = 0.03;
			target.sellerAdActivityPPC = 0.07;
			
			target.coders = 3;
			target.marketers = 2;
			target.dataScientists = 1;
			
			target.getTechs().get(0).research();
			target.eventDecisions[turn]=1;
			
		}else if(turn==2) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.5;
			target.alignmentProduct = 0.6;
			
			target.getTechs().get(21).research();
			target.getTechs().get(18).research();
			target.getTechs().get(14).research();
			
			target.buyerPriceSign = 3.99;
			target.buyerPriceSub = 0;
			target.buyerPriceComission=0.01;
			target.buyerAdActivityPPC = 0.05;
			
			target.sellerPriceSign = 0;
			target.sellerPriceSub = 4;
			target.sellerPriceComission =0.04;
			target.sellerAdActivityPPC = 0.05;
			
		}else if(turn==3) {
			
			target.eventDecisions[turn]=1;
			
			target.coders = 1;
			target.marketers = 2;
			target.dataScientists = 1;
			target.mods = 1;
			
			target.getTechs().get(6).research();
			
			target.buyerAdActivityCPM = 0.01;
			target.buyerAdActivityPPC = 0.10;
			
			target.sellerPriceSub = 3;
			target.sellerPriceComission =0.08;
			
			target.sellerAdActivityPPC = 0.10;
			target.sellerAdActivityCPM = 0.01;
			
		}else if(turn==4) {
			
			target.eventDecisions[turn]=1;
			
			target.coders = 1;
			target.marketers = 0;
			target.dataScientists = 2;
			target.mods = 0;
			
			target.alignmentPrice = 0.6;
			target.alignmentProduct = 0.6;
			
			target.getTechs().get(3).research();
			target.getTechs().get(11).research();
			target.getTechs().get(25).research();
			
			target.buyerPriceComission=0.012;
			target.buyerAdActivityCPM = 0.015;
			target.buyerAdActivityPPC = 0.085;
			
			target.sellerPriceComission =0.055;
			target.sellerAdActivityCPM = 0.015;
			target.sellerAdActivityPPC = 0.085;
			
		}else if(turn==5) {
			
			target.coders = 5;
			target.marketers = 1;
			target.dataScientists = 5;
			target.mods = 1;
			
			target.eventDecisions[turn]=2;
			
			target.getTechs().get(23).research();
			target.getTechs().get(26).research();
			
			target.buyerPriceSign = 3.99;
			target.buyerPriceSub =0;
			target.buyerPriceComission = 0.012;
			
			target.sellerPriceSign = 0.99;
			target.sellerPriceSub =3.00;
			target.sellerPriceComission = 0.055;
			
		}else if(turn==6) {
			
			target.coders = 8;
			target.marketers = 6;
			target.dataScientists = 8;
			target.mods = 2;
			
			target.getTechs().get(8).research();
			target.getTechs().get(16).research();
			target.getTechs().get(20).research();
			target.getTechs().get(22).research();
			target.getTechs().get(27).research();
			
			target.sellerPriceSign = 0.00;

		}else if(turn==7) {
			
			target.coders = 10;
			target.marketers = 10;
			target.dataScientists = 10;
			target.mods = 5;
			
			target.alignmentPrice = 0.6;
			target.alignmentProduct = 0.6;
			
			target.getTechs().get(1).research();
			target.getTechs().get(2).research();
			target.getTechs().get(4).research();
			target.getTechs().get(7).research();
			target.getTechs().get(15).research();
			target.getTechs().get(28).research();
			
			target.buyerPriceSign = 3.99;
			target.buyerPriceSub = 0.00;
			target.buyerPriceComission = 0.012;
			target.buyerAdActivityCPM = 0.015;
			target.buyerAdActivityPPC = 0.085;
			
			target.sellerPriceSign = 0.00;
			target.sellerPriceSub = 3.00;
			target.sellerPriceComission = 0.055;
			target.sellerAdActivityCPM = 0.015;
			target.sellerAdActivityPPC = 0.085;
			
			
			
		}
		
		// Decisions of player: Selfmade Meetingpoint
		
		target=state.getDivision().get(0).getPlayer("Selfmade Meetingpoint");
		if(turn==0) {
			
			target.eventDecisions[turn]=3;
			target.getTechs().get(0).research();
			
			target.alignmentPrice = 0.75;
			target.alignmentProduct = 0.75;
			
			target.sellerPriceComission = 0.20;
			
			target.buyerAdActivityCPM = 0.05;
			target.buyerAdActivityPPC = 0.01;
			
			target.sellerAdActivityCPM = 0.05;
			target.sellerAdActivityPPC = 0.01;
			
			target.coders = 2;
			target.marketers = 2;
			target.dataScientists = 1;
			target.mods = 2;
			
		}else if(turn==1) {
			
			target.eventDecisions[turn]=1;
			
			target.buyerPriceSub = 2.50;
			target.buyerFreemium = true;
			
			target.sellerPriceComission = 0.12;
			
			target.buyerAdActivityCPM = 0.05;
			target.buyerAdActivityPPC = 0.065;
			
			target.sellerAdActivityCPM = 0.05;
			target.sellerAdActivityPPC = 0.065;
			target.mods = 1;
			
			target.getTechs().get(14).research();
			target.getTechs().get(18).research();
			target.getTechs().get(21).research();
			
		}else if(turn==2) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.69;
			target.alignmentProduct = 0.75;
			
			target.buyerPriceSign = 0;
			target.buyerPriceSub = 3.90;
			target.buyerPriceComission = 0.00;
			target.buyerFreemium = true;
			
			target.sellerPriceSign = 1.20;
			target.sellerPriceSub = 0;
			target.sellerPriceComission = 0.11;
			
			target.buyerAdActivityCPM = 0.035;
			target.buyerAdActivityPPC = 0.072;
			
			target.sellerAdActivityCPM = 0.035;
			target.sellerAdActivityPPC = 0.072;
			
			target.coders = 1;
			target.marketers = 1;
			target.dataScientists = 0;
			target.mods = 0;
			
			target.sellData(0, 30);
			
			
		}else if(turn==3) {
			
			target.buyerPriceComission = 0.0125;
			target.sellerPriceComission = 0.12;
			
			target.buyerAdActivityCPM = 0.035;
			target.buyerAdActivityPPC = 0.079;
			
			target.sellerAdActivityCPM = 0.035;
			target.sellerAdActivityPPC = 0.079;
			
			target.dataScientists = 1;
			
			target.eventDecisions[turn]=1;
			target.getTechs().get(19).research();
			
		}else if(turn==4) {
			
			target.buyerPriceSub = 3.99;
			target.buyerPriceComission = 0.0125;
			target.buyerFreemium = true;
			
			target.sellerPriceSign = 2.90;
			target.sellerPriceComission = 0.12;
			
			target.buyerAdActivityCPM = 0.035;
			target.buyerAdActivityPPC = 0.084;
			
			target.sellerAdActivityCPM = 0.035;
			target.sellerAdActivityPPC = 0.084;
			
			target.coders = 2;
			target.marketers = 1;
			target.dataScientists = 2;
			target.mods = 1;
			
			target.eventDecisions[turn]=2;
			target.getTechs().get(16).research();
			target.getTechs().get(25).research();
			
		}else if(turn==5) {
			
			target.alignmentPrice = 0.63;
			target.alignmentProduct = 0.71;
			
			target.buyerPriceSign = 0;
			target.buyerPriceSub = 3.99;
			target.buyerPriceComission = 0.0125;
			target.buyerFreemium = true;
			
			target.sellerPriceSign = 2.90;
			target.sellerPriceSub = 1.99;
			target.sellerPriceComission = 0.078;
			target.sellerFreemium = false;
			
			target.buyerAdActivityCPM = 0.038;
			target.buyerAdActivityPPC = 0.095;
			
			target.sellerAdActivityCPM = 0.038;
			target.sellerAdActivityPPC = 0.095;
			
			target.coders = 2;
			target.marketers = 2;
			target.dataScientists = 2;
			target.mods = 1;
			
			target.eventDecisions[turn]=2;
			
			target.getTechs().get(11).research();
			target.getTechs().get(26).research();
			
			target.sellData(100, 8);
			
		}else if(turn==6) {
			
			target.buyerPriceComission = 0.013;
			
			target.buyerAdActivityCPM = 0.029;
			target.buyerAdActivityPPC = 0.105;
			
			target.sellerAdActivityCPM = 0.029;
			target.sellerAdActivityPPC = 0.105;
			
			target.coders = 4;
			target.marketers = 3;
			target.dataScientists = 5;
			target.mods = 2;
			
			target.getTechs().get(3).research();
			target.getTechs().get(6).research();
			
		}else if(turn==7) {
			
			target.alignmentPrice = 0.6;
			target.alignmentProduct = 0.69;
			
			target.buyerPriceSign = 0.00;
			target.buyerPriceSub = 3.60;
			target.buyerPriceComission = 0.013;
			target.buyerFreemium = true;
			
			target.sellerPriceSign = 2.90;
			target.sellerPriceSub = 1.99;
			target.sellerPriceComission = 0.078;
			target.sellerFreemium = false;
			
			target.buyerAdActivityCPM = 0.012;
			target.buyerAdActivityPPC = 0.105;
			
			target.sellerAdActivityCPM = 0.012;
			target.sellerAdActivityPPC = 0.105;
			
			target.coders = 5;
			target.marketers = 5;
			target.dataScientists = 9;
			target.mods = 2;
			
			target.getTechs().get(4).research();
			target.getTechs().get(7).research();
			target.getTechs().get(15).research();
			target.getTechs().get(23).research();
			
		}
		
		// Decisions of player: Eleven Friends
		
		target=state.getDivision().get(0).getPlayer("Eleven Friends");
		if(turn==0) {
		
			target.eventDecisions[turn]=3;
			
			target.alignmentPrice = 0.40;
			target.alignmentProduct = 0.60;
			
			target.coders = 1;
			target.marketers = 2;
			target.dataScientists = 2;
			target.mods = 1;
			
			target.buyerAdActivityCPM = 0.06;
			target.buyerAdActivityPPC = 0.06;
			
			target.sellerPriceSign = 1;
			target.sellerPriceComission = 0.05;
			
			target.sellerAdActivityCPM = 0.06;
			target.sellerAdActivityPPC = 0.06;
			
			target.getTechs().get(0).research();
			target.getTechs().get(3).research();
			
			
		}else if(turn==1) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.30;
			target.alignmentProduct = 0.60;
			
			target.buyerAdActivityCPM = 0.70;
			target.buyerAdActivityPPC = 0.70;
			
			target.sellerPriceSign = 10;
			target.sellerPriceComission = 0.05;
			target.sellerAdActivityCPM = 0.25;
			target.sellerAdActivityPPC = 0.25;
			
			target.getTechs().get(6).research();
			target.getTechs().get(14).research();
			
		}
		
		// Decisions of player: Se Plättform
		
		target=state.getDivision().get(0).getPlayer("Se Plättform");
		if(turn==0) {
			
			target.alignmentPrice = 0.75;
			target.alignmentProduct = 0.75;
			
			target.sellerPriceSub = 10;
			target.sellerPriceComission = 0.03;
			target.buyerPriceComission = 0.03;
			
			target.buyerAdActivityCPM = 0.035;
			target.buyerAdActivityPPC = 0.065;
			
			target.sellerAdActivityCPM = 0.035;
			target.sellerAdActivityPPC = 0.065;
			
			target.coders = 2;
			target.marketers = 1;
			target.dataScientists = 1;
			
			target.getTechs().get(0).research();
			target.eventDecisions[turn]=1;
			
			
		}else if(turn==1) {
			
			target.alignmentPrice = 0.63;
			target.alignmentProduct = 0.75;
			
			target.sellerPriceSub = 0;
			target.sellerPriceComission = 0.06;
			target.buyerPriceComission = 0.06;
			
			target.buyerAdActivityCPM = 0.035;
			target.buyerAdActivityPPC = 0.050;
			
			target.sellerAdActivityCPM = 0.035;
			target.sellerAdActivityPPC = 0.050;
			
			target.getTechs().get(6).research();
			target.getTechs().get(14).research();
			
		}else if(turn==4) {
			
			target.buyerPriceSign = 0;
			target.sellerPriceSign = 0;
			
			target.buyerPriceSub = 0;
			target.sellerPriceSub = 0;
			
			target.sellerPriceComission = 0.05;
			target.buyerPriceComission = 0.05;
			
			target.buyerAdActivityCPM = 0.030;
			target.buyerAdActivityPPC = 0.040;
			
			target.sellerAdActivityCPM = 0.030;
			target.sellerAdActivityPPC = 0.040;
			
			target.getTechs().get(18).research();
			target.getTechs().get(21).research();
			
		}else if(turn==5) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.60;
			target.alignmentProduct = 0.75;
			
			target.buyerPriceSign = 0;
			target.sellerPriceSign = 0;
			
			target.buyerPriceSub = 0;
			target.sellerPriceSub = 0;
			
			target.sellerPriceComission = 0.03;
			target.buyerPriceComission = 0.03;
			
			target.buyerAdActivityCPM = 0.020;
			target.buyerAdActivityPPC = 0.060;
			
			target.sellerAdActivityCPM = 0.020;
			target.sellerAdActivityPPC = 0.060;
			
			target.coders = 1;
			target.marketers = 1;
			target.dataScientists = 2;
			target.mods = 0;
			
			target.getTechs().get(11).research();
			target.getTechs().get(9).research();
			
		}else if(turn==6) {
			
			target.sellerPriceSub = 2.50;
			target.sellerPriceComission = 0.04;
			target.buyerPriceComission = 0.00;
			
			target.buyerAdActivityCPM = 0.020;
			target.buyerAdActivityPPC = 0.070;
			
			target.sellerAdActivityCPM = 0.020;
			target.sellerAdActivityPPC = 0.070;
			
			target.coders = 2;
			target.marketers = 2;
			target.dataScientists = 3;
			target.mods = 1;
			
			target.getTechs().get(4).research();
			target.getTechs().get(5).research();
			target.getTechs().get(25).research();
			
		}else if(turn==7) {
			
			target.sellerPriceSign = 1.00;
			target.sellerPriceSub = 2.50;
			target.sellerPriceComission = 0.04;
			
			target.buyerPriceSign = 0.00;
			target.buyerPriceSub = 0.00;
			target.buyerPriceComission = 0.00;
			
			target.buyerAdActivityCPM = 0.015;
			target.buyerAdActivityPPC = 0.075;
			
			target.sellerAdActivityCPM = 0.015;
			target.sellerAdActivityPPC = 0.075;
			
			target.coders = 2;
			target.marketers = 3;
			target.dataScientists = 3;
			target.mods = 2;
			
			target.getTechs().get(20).research();
			target.getTechs().get(22).research();
			target.getTechs().get(23).research();
			target.getTechs().get(26).research();
		}
		
		// Decisions of player: Probst Industries
		
		target=state.getDivision().get(0).getPlayer("Probst Industries");
		if(turn==0) {
			
			target.eventDecisions[turn]=3;
			
			target.alignmentPrice = 0.68;
			target.alignmentProduct = 0.60;
			
			target.coders = 2;
			target.mods = 1;
			target.dataScientists = 1;
			
			target.getTechs().get(14).research();
			target.getTechs().get(18).research();
			
		}else if(turn==1) {
			
			target.eventDecisions[turn]=1;
			
			target.buyerPriceSign = 4.99;
			target.sellerPriceSign = 4.99;
			target.sellerPriceComission = 0.03;
			target.marketers = 1;
			
			target.getTechs().get(0).research();
			target.getTechs().get(16).research();
			target.getTechs().get(25).research();
			
		}else if(turn==2) {
			
			target.eventDecisions[turn]=1;
			target.alignmentPrice = 0.65;
			
			target.buyerPriceSign = 0.00;
			target.sellerPriceSign = 4.99;
			
			target.buyerPriceComission = 0.015;
			target.sellerPriceComission = 0.045;
			
			target.buyerAdActivityCPM = 0.025;
			target.sellerAdActivityCPM = 0.025;
			
			target.buyerAdActivityPPC = 0.05;
			target.sellerAdActivityPPC = 0.05;
			
			target.getTechs().get(6).research();
			target.getTechs().get(21).research();
			
			
		}else if(turn==3) {
			
			target.alignmentProduct = 0.69;
			target.dataScientists = 1;
			target.eventDecisions[turn]=1;
			target.getTechs().get(22).research();
			
		}else if(turn==4) {
			
			target.eventDecisions[turn]=2;
			target.buyerPriceComission = 0.02;
			target.sellerPriceComission = 0.05;
			
			target.buyerAdActivityCPM = 0.05;
			target.sellerAdActivityCPM = 0.05;
			
			target.buyerAdActivityPPC = 0.1;
			target.sellerAdActivityPPC = 0.1;
			
			target.mods = target.mods +1;
			target.marketers = target.marketers +1;
			target.coders = target.coders+1;
			target.dataScientists = target.dataScientists +1;
			
			target.getTechs().get(3).research();
			target.getTechs().get(26).research();
			
		}else if(turn==5) {
			
			target.eventDecisions[turn]=3;
			
			target.buyerAdActivityPPC = 0.08;
			target.sellerAdActivityPPC = 0.08;
			
			target.getTechs().get(7).research();
			target.getTechs().get(27).research();
			
			target.sellData(200, 0);
			
		}else if(turn==6) {
			
			target.alignmentPrice = 0.55;
			target.alignmentProduct = 0.70;
			
			target.buyerPriceSign = 1.99;
			target.sellerPriceSign = 3.00;
			
			target.coders = target.coders +3;
			target.dataScientists = target.dataScientists +3;
			target.marketers = target.marketers +1;
			
			target.buyerAdActivityCPM = 0.04;
			target.sellerAdActivityCPM = 0.025;
			
			target.buyerAdActivityPPC = 0.1;
			target.sellerAdActivityPPC = 0.1;
			
			target.getTechs().get(1).research();
			target.getTechs().get(11).research();
			target.getTechs().get(28).research();
			
		}else if(turn==7) {
			
			target.buyerPriceSign = 0;
			target.sellerPriceSign = 0;
			
			target.dataScientists =target.dataScientists -3;
			target.mods = 0;
			
			target.sellData(116, 0);
			
			target.getTechs().get(2).research();
			target.getTechs().get(4).research();
			target.getTechs().get(5).research();
			target.getTechs().get(19).research();
			
		}

		target=state.getDivision().get(0).getPlayer("Unique Art");
		if(turn==0) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.6;
			target.alignmentProduct = 0.5;
			
			target.sellerPriceSub = 25;
			target.sellerPriceComission = 0.10;
			target.sellerPriceAction = 0.30;
			
			target.sellerAdActivityCPM = 0.03;
			target.sellerAdActivityPPC = 0.04;
			
			target.buyerAdActivityCPM = 0.04;
			target.buyerAdActivityPPC = 0.04;
			
			target.coders = 2;
			target.mods = 1;
			target.dataScientists = 2;
			target.marketers = 2;
			
			target.getTechs().get(0).research();
			
		}else if(turn==1) {
			
			target.eventDecisions[turn]=1;
			
			target.alignmentPrice = 0.5;
			target.alignmentProduct = 0.5;
			
			target.sellerPriceSub = 15;
			target.sellerPriceComission = 0.03;
			target.sellerPriceAction = 0.30;
			target.sellerAdActivityCPM = 0.05;
			target.sellerAdActivityPPC = 0.05;
			target.sellerFreemium = true;
			
			target.getTechs().get(6).research();
			
		}else if(turn==2) {
			
			target.eventDecisions[turn]=2;
			
			target.sellerPriceSub = 5;
			target.sellerPriceComission = 0.03;
			target.sellerPriceAction = 0.30;
			target.sellerAdActivityCPM = 0.02;
			target.sellerAdActivityPPC = 0.02;
			target.sellerFreemium = true;
			
			target.buyerAdActivityCPM = 0.02;
			target.buyerAdActivityPPC = 0.02;
			
			target.coders = 0;
			target.mods = 0;
			target.dataScientists = 1;
			target.marketers = 0;
			
			target.getTechs().get(14).research();
			target.getTechs().get(16).research();
			target.getTechs().get(18).research();
		
		}else if(turn==3) {
			
			target.eventDecisions[turn]=1;
			
			target.sellerAdActivityPPC = 0.05;
			
			target.buyerPriceSign = 2.95;
			target.buyerPriceSub =0;
			target.buyerAdActivityCPM = 0.02;
			target.buyerAdActivityPPC = 0.05;
			
			target.dataScientists = 0;
			
			target.getTechs().get(21).research();
			

			
			
		}

	}
	
	
	/**
	 * High level structure of the simulation with a fixed series of things to do for each division: Spawn new buyers/sellers,
	 * then update player variables, then simulate buyer/seller behavior, then update player score and finally increment turn
	 * counter by 1.
	 */
	public void executeSimulation() {
		

		for(Division division: state.getDivision()) {
			
		    Logger.instance.log("Division " + division.toString() + "Pre-Spawn Buyers: "+division.getBuyers().size(), 1);
		    Logger.instance.log("Division " + division.toString() + "Pre-Spawn Sellers: "+division.getSellers().size(), 1);
		    
			// Set spawn rates depending on turn number
			int spawnRate = 0;		
			if(division.getTurn()==0)spawnRate=300;
			if(division.getTurn()==1)spawnRate=500;
			if(division.getTurn()==2)spawnRate=1000;
			if(division.getTurn()==3)spawnRate=1500;
			if(division.getTurn()==4)spawnRate=2000;
			if(division.getTurn()==5)spawnRate=1500;
			if(division.getTurn()==6)spawnRate=1000;
			if(division.getTurn()==7)spawnRate=500;
			if(division.getTurn()==8)spawnRate=300;
			
			// Spawn new buyers and sellers
			for(int i=0;i<spawnRate;i++) {
				division.getBuyers().add(new Buyer(division));				
				division.getSellers().add(new Seller(division));	
			}
			
		    Logger.instance.log("Division " + division.toString() + "Post-Spawn Buyers: "+division.getBuyers().size(), 1);
		    Logger.instance.log("Division " + division.toString() + "Post-Spawn Sellers: "+division.getSellers().size(), 1);
			
			// Update Player variables depending on event choices and tech tree research
			for(Player player: division.getPlayers()) {
				
				player.events(); // Effects of event choices
				TechTree.getInstance().updatePlayer(player); // Effects of tech tree research 
				player.employeeAction(); // Effects of employees
				
			}
			
			// Utility Maximization and Platform Membership Decision		
			for(int i=0;i<division.getBuyers().size();i++) {
				division.getBuyers().get(i).utilityMaximization();
				division.getSellers().get(i).utilityMaximization();
			}
						
			// Buyers go shopping
			
			for(Seller seller: division.getSellers()) {
				seller.surf();
			}
			
			for(Buyer buyer: division.getBuyers()) {
				buyer.surf();
				buyer.shop();
			}
			
			// Calculate Score
			
			for(Player player:division.getPlayers()) {
				player.ratePlayer();
			}
			
			// Advance Round Counter
			
			division.setTurn(division.getTurn()+1);
			
			
			
		}	
	}	
	
	/**
	 * Sets up a new game with stylized built orders that represent various concepts of platform economics
	 */
	public void newGame_test() {
		
		state = GameState.getInstance();
		
		Division d = new Division("");
		Player p= new Player("Ad Focussed",d);
		p.alignmentPrice=0.33;
		p.alignmentProduct=0.67;
		d.getPlayers().add(p);
		
		p= new Player("Fast Growth",d);
		p.alignmentPrice=0.67;
		p.alignmentProduct=0.67;
		d.getPlayers().add(p);
		
		p= new Player("Tech Heavy",d);
		p.alignmentPrice=0.67;
		p.alignmentProduct=0.33;
		d.getPlayers().add(p);
		
		p= new Player("Freemium",d);
		p.alignmentPrice=0.33;
		p.alignmentProduct=0.33;
		d.getPlayers().add(p);
		
		p= new Player("Subscription",d);
		p.alignmentPrice=0.55;
		p.alignmentProduct=0.45;
		d.getPlayers().add(p);
		
		p= new Player("Comission",d);
		p.alignmentPrice=0.55;
		p.alignmentProduct=0.45;
		d.getPlayers().add(p);
		
		state.getDivision().add(d);
		
	}
	
	/**
	 * Simulates game with stylized built orders that represent various concepts of platform economics
	 */
	public void applyDecisions_test() {
		
		int turn = state.getDivision().get(0).getTurn();
		
		Player target=state.getDivision().get(0).getPlayer("Tech Heavy");
		if(turn==0) {
			target.eventDecisions[0]=3;
			target.getTechs().get(6).research();
			target.getTechs().get(25).research();
			target.coders=3;
		}if(turn==1) {
			target.eventDecisions[1]=2;	
			target.getTechs().get(7).research();
			target.getTechs().get(9).research();
		}if(turn==2) {
			target.eventDecisions[2]=2;	
			target.getTechs().get(8).research();
			target.getTechs().get(14).research();
			target.getTechs().get(18).research();	
			target.coders=1;
			target.dataScientists=1;
			target.buyerAdActivityCPM=0.05;
			target.sellerAdActivityCPM=0.05;
		}if(turn==3) {
			target.coders=0;
			target.dataScientists=0;
			target.getTechs().get(21).research();	
			target.sellerPriceComission=0.05;
		}if(turn==4) {
			target.eventDecisions[4]=2;	
			target.buyerAdActivityCPM=0.15;
			target.sellerAdActivityCPM=0.15;
			target.coders=1;
		}if(turn==5) {
			target.getTechs().get(11).research();
		}
		
		target=state.getDivision().get(0).getPlayer("Comission");
		if(turn==0) {
			target.eventDecisions[0]=2;
			target.sellerPriceAction=0.25;
			target.getTechs().get(21).research();
			target.coders=4;
		}if(turn==1) {
			target.eventDecisions[1]=3;	
			target.getTechs().get(22).research();
		}if(turn==2) {
			target.eventDecisions[2]=2;	
			target.getTechs().get(14).research();
			target.coders=0;
			target.sellerPriceAction=1.00;
			target.sellerPriceComission=0.01;
		}if(turn==3) {
			target.getTechs().get(18).research();
			target.sellerPriceAction=1.50;
			target.sellerPriceComission=0.02;
		}if(turn==4) {
			target.eventDecisions[4]=1;	
			target.sellerPriceAction=1.50;
			target.sellerPriceComission=0.02;
		}
		
		target=state.getDivision().get(0).getPlayer("Subscription");
		if(turn==0) {
			target.eventDecisions[0]=2;
			target.coders=2;
		}if(turn==1) {
			target.getTechs().get(14).research();
			target.getTechs().get(21).research();
			target.buyerPriceSub=0;
			target.sellerPriceSub=1;
			target.eventDecisions[1]=1;	
		}if(turn==2) {
			target.eventDecisions[2]=2;	
			target.getTechs().get(0).research();
			target.getTechs().get(22).research();
			target.buyerPriceSub=0;
			target.sellerPriceSub=2;
		}if(turn==3) {
			target.buyerPriceSub=0;
			target.sellerPriceSub=3;
		}if(turn==4) {
			target.eventDecisions[4]=1;	
			target.buyerPriceSub=0;
			target.sellerPriceSub=5;
		}
		
		target=state.getDivision().get(0).getPlayer("Fast Growth");
		if(turn==0) {
			target.eventDecisions[0]=1;
			target.getTechs().get(25).research();
			target.getTechs().get(26).research();
			target.marketers=1;
		}if(turn==1) {
			target.getTechs().get(14).research();
			target.eventDecisions[1]=1;	
		}if(turn==2) {
			target.eventDecisions[2]=1;	
			target.getTechs().get(21).research();
			target.marketers=2;
		}if(turn==3) {
			target.buyerAdActivityCPM=0.25;
			target.sellerAdActivityCPM=0.25;
		}if(turn==4) {
			target.eventDecisions[4]=3;	
			target.coders=4;
			target.buyerAdActivityCPM=0.25;
			target.sellerAdActivityCPM=0.25;
		}if(turn==5) {
			target.dataScientists=3;
		}
		
		target=state.getDivision().get(0).getPlayer("Freemium");
		if(turn==0) {
			target.eventDecisions[0]=2;
			target.buyerPriceSub=2;
			target.sellerPriceSub=2;
			
			target.buyerAdActivityCPM=0.10;
			target.sellerAdActivityCPM=0.10;
			
			target.buyerFreemium=true;
			target.sellerFreemium=true;
			
			target.getTechs().get(25).research();
			target.getTechs().get(0).research();
			target.coders=2;
		}	
		if(turn==1) {
			target.getTechs().get(14).research();
			target.getTechs().get(21).research();			
			target.eventDecisions[1]=1;			
		}if(turn==2) {
			target.buyerAdActivityCPM=0.20;
			target.sellerAdActivityCPM=0.20;
		}if(turn==3) {
			target.getTechs().get(18).research();		
			target.buyerAdActivityCPM=0.30;
			target.sellerAdActivityCPM=0.30;
		}if(turn==4) {
			target.eventDecisions[4]=1;	
			target.coders=3;
			target.dataScientists=3;
			target.getTechs().get(9).research();	
			target.buyerAdActivityCPM=0.40;
			target.sellerAdActivityCPM=0.40;
		}if(turn==5) {
			target.getTechs().get(26).research();	
			target.getTechs().get(27).research();	
			target.getTechs().get(28).research();	
		}
		
		target=state.getDivision().get(0).getPlayer("Ad Focussed");
		if(turn==0) {
			target.eventDecisions[0]=3;

			target.buyerAdActivityCPM=0.10;
			target.sellerAdActivityCPM=0.10;

			target.getTechs().get(0).research();
			target.getTechs().get(25).research();			
			target.coders=1;
			target.marketers=3;
		}	
		if(turn==1) {
			target.getTechs().get(21).research();	
			target.getTechs().get(14).research();
			target.eventDecisions[1]=1;	
			target.coders=0;
			target.marketers=0;
		}if(turn==2) {
			target.getTechs().get(3).research();
			target.buyerAdActivityCPM=0.00;
			target.sellerAdActivityCPM=0.00;
			target.buyerAdActivityPPC=0.25;
			target.sellerAdActivityPPC=0.25;
		}if(turn==3) {
			target.buyerAdActivityPPC=0.40;
			target.sellerAdActivityPPC=0.40;
		}if(turn==4) {
			target.eventDecisions[4]=3;	
			target.coders=2;
		}if(turn==5) {
			target.getTechs().get(2).research();
		}
		
	}
	
	
}
