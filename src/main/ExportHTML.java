package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;

import model.Division;
import model.GameState;
import model.Player;

/**
 * The ExportHTML class uses the templates in "/htmls/template/" to represent the current
 * game state as HTML web pages
 */
public class ExportHTML {

	public static ExportHTML instance;
	String path;
	
	/**
	 * Creates or retrieves the single instance of this class. The variable path is set to the
	 * directory the project in launched from (project folder for launch within eclipse and
	 * path of the jar file for launch outside of the IDE.
	 */
	public static ExportHTML getInstance() {
		
		if(instance==null) {
			instance = new ExportHTML();
			instance.path = System.getProperty("user.dir");
			
		}
		return instance;
		
	}
	
	/**
	 * Creates a ranking for each division in the game. This method is called from within the
	 * exportGameState() method.
	 * @param div The division of players the ranking is created for
	 * @throws Exception
	 */
	public void createRanking(Division div) throws Exception {
		
		String rankPath = path+"/htmls/export/ranking_"+div.getName()+".html";
		if(div.getName().equals("")) {
			rankPath = path+"/htmls/export/ranking.html";	// Keep names simple if a single unnamed division is used.
		}				
		
		// Totally inefficient sorting algorithm. But it should do the job.
		
		LinkedList<Player> ranked = new LinkedList<Player>();
		while(ranked.size() < div.getPlayers().size()) {
			
			Player target = null;
			for(Player player:div.getPlayers()) {
				
				if(ranked.contains(player))continue;
				if(higher(target,player)) {
					target=player;
				}
					
			}
			
			ranked.add(target);	
		}
		
		// Go through all lines of the template and replace the variable tokens %varXX with the values for the current player
		
		PrintStream wif = new PrintStream(new File(rankPath));
		BufferedReader rff = new BufferedReader(new InputStreamReader(new FileInputStream(path+"/htmls/template/ranking.html"),"UTF-8"));
		String line=rff.readLine();
		
		while(line!=null) {
			
			int rank=1;
			for(Player player:ranked) {			
				line=line.replace("%var01-"+String.valueOf(rank), player.getName());
				line=line.replace("%var02-"+String.valueOf(rank), String.valueOf(player.popBuyers));
				line=line.replace("%var03-"+String.valueOf(rank), String.valueOf(player.popSellers));
				line=line.replace("%var04-"+String.valueOf(rank), String.valueOf(player.transactions));
				line=line.replace("%var05-"+String.valueOf(rank), round(100*player.reputation,1)+"%");		
				line=line.replace("%var06-"+String.valueOf(rank), String.valueOf(player.score));
				rank++;
			}
			
			wif.println(line);
			line=rff.readLine();
			
		}
		
		rff.close();
		wif.close();
		
	}
	
	/**
	 * Decides which of the given player ranks higher. Primary criteria is the score of the players.
	 * Secondary criteria is the stock of money held by the players. If both criteria result in a
	 * tie, the first player is placed higher. 
	 */
	public boolean higher(Player current,Player player) {
		
		if(current==null)return true;
		
		if(player.score < current.score)return false;
		if(player.score > current.score)return true;
		
		if(player.gold < current.gold)return false;
		return true;
		
	}
	
	
	/**
	 * Creates game state HTMLs for each player. For large numbers of players, these can be divided
	 * into multiple divisions that are simulated and scored separately.
	 */
	public void exportGameState() {

		try {
			
			// Iteration over divisions 
			for(Division division: GameState.getInstance().getDivision()) {
				
				createRanking(division); // Create the overall ranking page for the current division
				
				// Iteration over players in the current division
				for(Player player: division.getPlayers()) {

					PrintStream wif = new PrintStream(new File(path+"/htmls/export/state_"+player.getName()+".html"));
					BufferedReader rff = new BufferedReader(new InputStreamReader(new FileInputStream(path+"/htmls/template/template.html"),"UTF-8"));
					String line=rff.readLine();
					
					// Go through all lines of the template and replace the variable tokens %varXX with the values for the current player
					while(line!=null) {
						
						line=line.replace("Spielername", player.getName());
					
						line=line.replace("%var01", round(player.alignmentPrice, 2));
						line=line.replace("%var02", round(player.alignmentProduct, 2));
						line=line.replace("%var03", truncate(player.coders));
						line=line.replace("%var04", truncate(player.dataScientists));
						line=line.replace("%var05", truncate(player.marketers));
						line=line.replace("%var06", truncate(player.mods));
						
						line=line.replace("%var07", monetize(player.buyerPriceSign));
						line=line.replace("%var08", monetize(player.buyerPriceSub));
						line=line.replace("%var09", monetize(player.buyerPriceAction)+" & "+round(100*player.buyerPriceComission,1)+"%");
						line=line.replace("%var10", round(100*player.buyerAdActivityCPM,1)+"%");
						line=line.replace("%var11", round(100*player.buyerAdActivityPPC,1)+"%");
						
						if(player.buyerFreemium) {
							line=line.replace("%var12", "JA");
						}else {
							line=line.replace("%var12", "NEIN");
						}

						line=line.replace("%var13", monetize(player.sellerPriceSign));
						line=line.replace("%var14", monetize(player.sellerPriceSub));
						line=line.replace("%var15", monetize(player.sellerPriceAction)+" & "+round(100*player.sellerPriceComission,1)+"%");
						line=line.replace("%var16", round(100*player.sellerAdActivityCPM,1)+"%");
						line=line.replace("%var17", round(100*player.sellerAdActivityPPC,1)+"%");
						
						if(player.sellerFreemium) {
							line=line.replace("%var18", "JA");
						}else {
							line=line.replace("%var18", "NEIN");
						}
						
						line=line.replace("%var19", truncate(player.scoreGold));
						line=line.replace("%var20", truncate(player.scoreInsight));
						line=line.replace("%var21", truncate(player.scoreTech));
						line=line.replace("%var22", truncate(player.scorePopulation));
						line=line.replace("%var23", truncate(player.scoreActivity));
						line=line.replace("%var24", truncate(player.scoreReputation));
						line=line.replace("%var25", truncate(player.score));
						
						line=line.replace("%var26", round(player.incomeSignUp,1));
						line=line.replace("%var27", round(player.incomeSub,1));
						line=line.replace("%var28", round(player.incomeAction,1));
						line=line.replace("%var29", round(player.incomeAds,1));
						line=line.replace("%var30", round(player.incomeSelling,1));
						line=line.replace("%var31", round(player.income,1));
						
						line=line.replace("%var32", round(player.costsOperation,1));
						line=line.replace("%var33", round(player.costsInvest,1));
						line=line.replace("%var34", round(player.costSignUp,1));
						line=line.replace("%var35", round(player.costs,1));
						
						line=line.replace("%var36", truncate(player.gold));
						line=line.replace("%var37", truncate(player.code));
						line=line.replace("%var38", truncate(player.data));
						line=line.replace("%var39", truncate(player.insight));
						
						line=line.replace("%var40", player.popBuyers+"/"+truncate(player.buyerPopCap));
						line=line.replace("%var41", player.popSellers+"/"+truncate(player.sellerPopCap));
						line=line.replace("%var42", round(100*player.reputation,1)+"%");						
						line=line.replace("%var43", String.valueOf(player.transactions));		
						line=line.replace("%var44", String.valueOf(division.getTurn())+"/8");	
						
						wif.println(line);
						line=rff.readLine();
					}
					
					rff.close();
					wif.close();
					
				}
			}
			
		}catch(Exception e) {
			System.out.println("Error! Export of game state failed");
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Helper functions that rounds the given double to a specific amount of decimals and converts the
	 * notation to DIN standard with the "," character as comma.
	 */
    public String round(double val,int digits){
    	return(String.valueOf(Math.floor(val*Math.pow(10, digits))/Math.pow(10, digits)).replace(".", ","));
    }

	/**
	 * Helper functions that drops all decimals of the given double and converts the notation to DIN 
	 * standard with the "," character as comma.
	 */
    public String truncate(double val){

    	try{
        	double dbl = Math.floor(val);
        	return String.valueOf(dbl).split("\\.")[0];
    	}catch(Exception e){
    		return("-");
    	}

    }
    
	/**
	 * Helper functions that rounds the given double to two decimals and converts the notation to
	 * the accounting style with the currency sign after the number.
	 */
    public String monetize(double val){

    	try{
        	double dbl = Math.floor(val*Math.pow(10, 2))/Math.pow(10, 2);
        	if(String.valueOf(dbl).split("\\.")[1].length()==1){
        		return String.valueOf(dbl)+"0 €";
        	}else{
        		return String.valueOf(dbl)+" €";
        	}
    	}catch(Exception e){
    		return("- €");
    	}

    }
	
}
