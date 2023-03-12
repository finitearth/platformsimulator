package model;

/**
 * Contains all non platform or buyer/seller specific constants
 */
public class Constants {

	public static double sellerPassiveDissatisfaction = 0.06;	// higher factors lead to faster passive decay of seller satisfaction
	public static double buyerPassiveDissatisfaction = 0.04;	// higher factors lead to faster passive decay of buyer satisfaction
	
	// reputation effect of purchases for buyer by star rating (does not require any research to be applied)
	public static double buyer1star = -0.05;	
	public static double buyer2star = -0.01;
	public static double buyer3star = 0.03;
	public static double buyer4star = 0.05;
	public static double buyer5star = 0.10;
	
	public static double sellerSuccess = 0.03;		// reputation effect of purchases for seller
	public static double sellerFail = -0.10;

	public static double baseCPM = 5;				// number of impressions per user and turn at 100% ad activity
	public static double transactionCPM = 3;		// additional impressions during a transaction at 100% ad activity
	public static double priceCPM = 0.10;			// price per impression
	
	public static double basePPC = 5;				// number of impressions per user and turn at 100% ad activity
	public static double transactionPPC = 3;		// additional impressions during a transaction at 100% ad activity
	public static double pricePPC = 1.00;			// price per click
	public static double modifierCTR = 0.08;		// clicks per impression
	
	public static double dataOnTransaction = 0.15;	// data generated on each transaction
	public static double dataOnUser = 0.05;			// data generated per user per turn
	
	public static double salary=25;					// salary of employees (applies to all trades)
	public static double marketerEfficacy=5.00;		// insight generated per marketer per turn
	
	public static double distanceCost=1;			// overall penalty factor for misalignment in price and product type
	
	public static double lockedInBonus=50;			// bonus utility for current platform
	public static double lockedInFactor=0.5;		// utility factor for current platform
	
	public static double buyerTrollRate=0.10;		// amount of trolls on buyer side
	public static double sellerTrollRate=0.10;		// amount of trolls on seller side
	
	public static double outsideOption=3;			// utility when staying offline
}
