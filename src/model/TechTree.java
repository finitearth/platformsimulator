package model;

import main.Logger;


/**
 * This class represents the tech tree of the game and includes both the costs and effects of the technologies.
 */
public class TechTree {

	public static TechTree instance;
	
	public static TechTree getInstance() {
		
		if(instance==null) {
			instance=new TechTree();
		}
		return instance;
		
	}
	
	/**
	 * Updates the variables of the given player to its researched technologies. The technologies in this method are
	 * not sorted by their number but by the primary variable they influence.
	 */
	public void updatePlayer(Player player) {
		
		// Pop Cap
		
		player.buyerPopCap = 100;
		player.sellerPopCap = 40;
		
		if(player.techs.get(14).active) { // Cummulative: 100/100
			player.sellerPopCap = player.sellerPopCap*2.5;  
		}
		
		if(player.techs.get(16).active) { // Cummulative: 200/200
			player.sellerPopCap = player.sellerPopCap*2;
			player.buyerPopCap = player.buyerPopCap*2;
		}
		
		if(player.techs.get(21).active) { // Cummulative: 600/600
			player.sellerPopCap = player.sellerPopCap*3;
			player.buyerPopCap = player.buyerPopCap*3;
		}
		
		if(player.techs.get(11).active && player.techs.get(14).active) { // Cummulative: 1200/1200
			player.sellerPopCap = player.sellerPopCap*2;
			player.buyerPopCap = player.buyerPopCap*2;
		}
		
		if(player.techs.get(11).active && player.techs.get(16).active) { // Cummulative: 2400/2400
			player.sellerPopCap = player.sellerPopCap*2;
			player.buyerPopCap = player.buyerPopCap*2;
		}
		
		if(player.techs.get(11).active && player.techs.get(21).active) { // Cummulative: 4800/4800
			player.sellerPopCap = player.sellerPopCap*2;
			player.buyerPopCap = player.buyerPopCap*2;
		}
		
		
		// Code generation
		
		player.factorCodeGeneration=50.0;
		
		if(player.techs.get(6).active) {
			player.factorCodeGeneration=player.factorCodeGeneration*1.20;
		}
		
		if(player.techs.get(7).active) {
			player.factorCodeGeneration=player.factorCodeGeneration*1.30;
			player.code = player.code + 25;
		}
		
		if(player.eventDecisions[2]==3)player.factorCodeGeneration=player.factorCodeGeneration*1.10;
		
		// Data generation
		
		player.factorDataGeneration=1.00;
		
		if(player.techs.get(9).active) {
			player.factorDataGeneration=player.factorDataGeneration*1.20;
		}
		if(player.techs.get(10).active) {
			player.factorDataGeneration=player.factorDataGeneration*1.30;
		}
		
		if(player.eventDecisions[4]==1)player.factorDataGeneration=player.factorDataGeneration*0.85;
		if(player.eventDecisions[4]==3)player.factorDataGeneration=player.factorDataGeneration*1.15;
		
		// Data conversion
		
		player.factorDataConversion=15.00;
		
		if(player.techs.get(11).active) {
			player.factorDataConversion=player.factorDataConversion*1.30;
		}
		if(player.techs.get(12).active) {
			player.factorDataConversion=player.factorDataConversion*1.50;
		}
		
		if(player.eventDecisions[2]==2)player.factorDataConversion=player.factorDataConversion*1.10;
		if(player.eventDecisions[4]==1)player.factorDataConversion=player.factorDataConversion*0.85;
		if(player.eventDecisions[4]==3)player.factorDataConversion=player.factorDataConversion*1.15;
		
		// Cooldowns
		
		player.factorCooldownBuyers=1.00;
		player.factorCooldownSellers=1.00;
		
		if(player.techs.get(15).active) {
			player.factorCooldownBuyers=player.factorCooldownBuyers*0.80;
		}
		
		if(player.techs.get(22).active) {
			player.factorCooldownSellers=player.factorCooldownSellers*0.80;
		}
		
		if(player.techs.get(0).active) {
			player.factorCooldownBuyers=player.factorCooldownBuyers*0.95;
			player.factorCooldownSellers=player.factorCooldownSellers*0.95;
		}
		
		if(player.techs.get(4).active) {
			player.factorCooldownBuyers=player.factorCooldownBuyers*0.90;
			player.factorCooldownSellers=player.factorCooldownSellers*0.90;
		}
		
		if(player.techs.get(5).active) {
			player.factorCooldownBuyers=player.factorCooldownBuyers*0.85;
			player.factorCooldownSellers=player.factorCooldownSellers*0.85;
		}
		
		// Matching Distance - Base Value
		
		player.factorMatchingDistance=0.20;
		
		if(player.techs.get(1).active) {
			player.factorMatchingDistance=player.factorMatchingDistance*1.10;
		}
		
		if(player.techs.get(1).active && player.techs.get(11).active) {
			player.factorMatchingDistance=player.factorMatchingDistance*1.10;
		}
		
		if(player.techs.get(14).active) {
			player.factorMatchingDistance=player.factorMatchingDistance*1.15;
		}
		
		if(player.techs.get(16).active) {
			player.factorMatchingDistance=player.factorMatchingDistance*1.10;
		}
		
		// Matching Distance - Scaling with quality
		
		player.factorMatchQualityScaling=0.00;
		
		if(player.techs.get(18).active) {
			player.factorMatchQualityScaling=0.08;
		}
		
		if(player.techs.get(18).active && player.techs.get(17).active) {
			player.factorMatchQualityScaling=0.12;
		}
		
		// Ad Disutility
		
		player.factorAdDisutility=1.00;
		
		if(player.techs.get(3).active) {
			player.factorAdDisutility=player.factorAdDisutility*0.75;
		}
		
		if(player.eventDecisions[5]==2)player.factorAdDisutility=player.factorAdDisutility*1.25;
		
		// Ad Accuracy
		
		player.factorAdAccuracy=1.00; // scales PPC ad revenue
		
		if(player.techs.get(2).active) {
			player.factorAdAccuracy=player.factorAdAccuracy*1.25;
		}
		
		if(player.techs.get(2).active && player.techs.get(11).active) {
			player.factorAdAccuracy=player.factorAdAccuracy*1.25;
		}
		
		if(player.eventDecisions[5]==2)player.factorAdAccuracy=player.factorAdAccuracy*1.25;
		
		// Cost Disutility
		
		player.factorCostDisutility=1.00;
		
		if(player.techs.get(21).active) {
			player.factorCostDisutility=player.factorCostDisutility*0.90;
		}
		
		if(player.techs.get(23).active) {
			player.factorCostDisutility=player.factorCostDisutility*0.90;
		}
		
		if(player.techs.get(24).active) {
			player.factorCostDisutility=player.factorCostDisutility*0.90;
		}
		
		if(player.eventDecisions[5]==1)player.factorCostDisutility=player.factorCostDisutility*0.67;
		
		// Moderation - Cheats
		
		player.factorCheatSupression=0.00;

		if(player.techs.get(19).active) {
			player.factorCheatSupression=player.factorCheatSupression+0.50;
		}
		
		// Moderation - FakeReviews
		
		player.factorFakeReviewSupression=0.00;	
		
		if(player.techs.get(19).active) {
			player.factorFakeReviewSupression=player.factorFakeReviewSupression+0.50;
		}
		
		// Signaling
		
		player.factorSignalGeneration=0.50;
		
		if(player.techs.get(20).active) {
			player.factorSignalGeneration=1;
		}
		
		// Betas
		
		player.factorBuyerBeta=0.05;
		player.factorSellerBeta=0.15;
		
		if(player.techs.get(28).active) {
			player.factorBuyerBeta=player.factorBuyerBeta*1.15;
			player.factorSellerBeta=player.factorSellerBeta*1.15;
		}
		
		if(player.techs.get(28).active) {
			
			double scaling = 1.00 + Math.min(0.30, player.insight/2000);
			
			
			player.factorBuyerBeta=player.factorBuyerBeta*scaling;
			player.factorSellerBeta=player.factorSellerBeta*scaling;
		}
		
		// Intrinsic Value
		
		player.intrinsicBuyerValue=2;
		player.intrinsicSellerValue=2;	
		
		if(player.techs.get(25).active) {
			player.intrinsicBuyerValue=player.intrinsicBuyerValue+2;
			player.intrinsicSellerValue=player.intrinsicSellerValue+2;
		}
		
		if(player.techs.get(26).active) {
			player.intrinsicBuyerValue=player.intrinsicBuyerValue+3;
			player.intrinsicSellerValue=player.intrinsicSellerValue+3;
		}
		
		if(player.techs.get(0).active) {
			player.intrinsicBuyerValue=player.intrinsicBuyerValue*1.25;
			player.intrinsicSellerValue=player.intrinsicSellerValue*1.25;
		}
		
		if(player.techs.get(4).active) {
			player.intrinsicBuyerValue=player.intrinsicBuyerValue*1.35;
			player.intrinsicSellerValue=player.intrinsicSellerValue*1.35;
		}
		
		if(player.techs.get(5).active) {
			player.intrinsicBuyerValue=player.intrinsicBuyerValue*1.50;
			player.intrinsicSellerValue=player.intrinsicSellerValue*1.50;
		}
		
		// SkillBonus
		
		player.factorSkillBonus = 1.00;
				
		if(player.techs.get(25).active) {
			player.factorSkillBonus=player.factorSkillBonus*1.10;
		}
		if(player.techs.get(26).active) {
			player.factorSkillBonus=player.factorSkillBonus*1.15;
		}
		if(player.techs.get(1).active) {
			player.factorSkillBonus=player.factorSkillBonus*1.10;
		}
		if(player.techs.get(1).active && player.techs.get(11).active) {
			player.factorSkillBonus=player.factorSkillBonus*1.10;
		}
		
	    Logger.instance.log(player.getName() + " factor set "+
	    		player.factorCodeGeneration + "," +
	    		player.factorCodeGeneration + ","+	    		
				player.factorCodeGeneration + ","+	
				player.factorDataGeneration + ","+	
				player.factorDataConversion + ","	+
				player.factorCooldownBuyers + ","+	
				player.factorCooldownSellers + ","+	
				player.factorMatchingDistance + ","	+
				player.factorMatchQualityScaling + ","	+
				player.factorAdDisutility + ","	+
				player.factorAdAccuracy + ","+	
				player.factorCostDisutility + ","	+
				player.factorFakeReviewSupression + ","	+
				player.factorCheatSupression + ","+	
				player.factorSignalGeneration + ","	+
				player.factorBuyerBeta + ","+	
				player.factorSellerBeta	 + ","	+
				player.factorSkillBonus + ","+	
				player.intrinsicBuyerValue + ","	+
				player.intrinsicSellerValue	 + ","		    		
	    		,2);
		

	}
	
	/**
	 * Creates the tech tree, defining names and resource costs. The order matters because the constructor adds
	 * the newly created technology to a linked list stored in the given player instance.
	 */
	public void createTechTree(Player player) {
		
		new Tech(player,0, "UI/UX Optimierung",50,100,0,0);		// 0
		new Tech(player,1,"Seller Insights",0,100,0,50);	
		new Tech(player,2,"Advertiser Insights",0,100,0,50);	
		new Tech(player,3,"Nudges",35,0,0,15);	
		new Tech(player,4,"Massen AB-Test",75,150,0,0);
		new Tech(player,5,"Personalisierete UI",250,0,0,50);
		
		new Tech(player,6,"Backend Optimierung",25,75,0,0); 		// 6
		new Tech(player,7,"APIs",0,100,0,0);
		new Tech(player,8,"Datenbankoptimierung",35,80,0,0);
		new Tech(player,9,"Tracking",0,100,0,0);
		new Tech(player,10,"Off-Site Tracking",0,150,0,0);
		new Tech(player,11,"Machine Learning",100,100,100,0);
		new Tech(player,12,"Content-Based Algorithmen",0,300,0,150);
		new Tech(player,13,"Collaborative Filter Algorithmen",0,300,0,150);
		
		new Tech(player,14,"Optimierte Suchfunktion",0,35,0,0); 	// 14
		new Tech(player,15,"Personalisierte Suche",0,0,0,100);

		new Tech(player,16,"Recommender",0,50,0,50);				// 16
		new Tech(player,17,"Vergleichsansichten",0,150,0,0);		
		
		new Tech(player,18,"Bewertungssystem",0,100,0,0);			// 18
		new Tech(player,19,"NLP Technologie",0,150,0,0);
		new Tech(player,20,"Reminder",50,50,0,0);
		
		new Tech(player,21,"Moderierte Transaktionen",0,50,0,0);	// 21
		new Tech(player,22,"Logistikunterstüztung",25,75,0,0);
		new Tech(player,23,"SEPA Mandate",75,25,0,0);
		new Tech(player,24,"Online Payment",100,200,0,0);
		
		new Tech(player,25,"FAQ & Guidelines",50,0,0,0); 			// 25
		new Tech(player,26,"Tutorials",75,25,0,0);
		new Tech(player,27,"Zertifizierung",150,50,0,0);
		new Tech(player,28,"Community Mitarbeiter",200,0,0,0);
		
	}
	
	public boolean ratingExists(Player player) {
		return player.techs.get(18).active;
	}
	
}
