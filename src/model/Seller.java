package model;

import java.io.Serializable;
import java.util.HashMap;

import main.Logger;

/**
 * This class represents a seller that can register, surf and sell on the players platforms.
 */
public class Seller implements Serializable {

	private static final long serialVersionUID = 1L;
	
	int actorID=0;
	
	Division division;  // The division this seller is spawned into
	Player player; // The platform this seller is registered to (null = none)
	
	public double adAversion=4;			// how much disutility the seller takes from ads
	public double feeAversion=1.00;		// how much disutility the seller takes form transaction fees
	
	int freemium;						// if the platform offers a freemium option, tracks if the seller chose premium or basic
										// a value of 1 means, the seller opted for basic (no fees)
										// a value of -1 means, the seller opted for premium (no ads)
										// a value of 0 means, the platform the seller is a member of has no freemium pricing model
	
	public double alignmentPrice;		// the price class of products this seller is offering (range 0 to 1)
	public double alignmentProduct;		// the type of products this seller is offering (range 0 to 1)
	
	public double skill;				// the skill of the seller is critical in determining the happiness of buyers with transactions
	public boolean isTroll;				// if a seller is a troll, it does stupid stuff like shipping empty boxes
	
	public int[] ratings;				// a histogram of ratings this seller got from buyers
	public double averageRating;		// the average rating of this seller
	
	public double satisfaction;			// how happy the seller is with his current platform membership (range 0 to 1)
	
	public double rnd;
	boolean cooldown = false;			// if true, the seller is out of stock and won't sell anything until the next turn
	
	public boolean banned=false;		// if true, the seller is kicked from the platform at the end of this turn
	
	/**
	 * Spawns a new seller in the given division. The preferred price point and type of goods is randomized.
	 * Some behavioral variables of the seller depend on these preferences.
	 */
	public Seller(Division division) {
		this.division = division;
		actorID=division.sellerID;
		division.sellerID=division.sellerID+1;
		
		player=null;
		ratings = new int[5];
		satisfaction = 0.75; // newly registered members start at 75% satisfaction
		
		alignmentPrice = Math.random();
		alignmentProduct = Math.random();
		skill = Math.random();
		
		// High end product sellers are very sensitive to ads
		adAversion = 0.04+0.06*alignmentPrice;
		
		// Low end product sellers are very sensitive to fees
		feeAversion=0.10;
		if(alignmentPrice < 0.33)feeAversion=0.15;
		if(alignmentPrice < 0.15)feeAversion=0.20;
		
		if(Math.random() < Constants.sellerTrollRate)this.isTroll = true;
		
	}
	
	/**
	 * This method registers/unregisters the seller from the given platforms.
	 * @param current The current platform the seller is on. Can be null if the seller is freshly spawned or went offline
	 * @param successor The next platform the seller moves to. Can be null if the seller is kicked or chooses to go offline
	 */
	public void changeOwner(Player current, Player successor) {
		
		String source ="null";
		String target ="null";
		if(current!=null)source=current.getName();
		if(successor!=null)target = successor.getName();
	    Logger.instance.log("Seller " + actorID + " changes from "+ source + " to " + target,3);
	    
		if(banned && successor!=null)return;
		
		if(current!=null) {
			current.popSellers=current.popSellers-1;
		}
		
		if(successor!=null) {
			successor.popSellers=successor.popSellers+1;
		}
		
		player=successor;
		if(player!=null)player.gain(0.001 * player.sellerPriceSign,0);
		ratings = new int[5];
		satisfaction = 0.75;
		cooldown=false;
	}
	
	/**
	 * In each turn, the seller considers all available platforms, as well as the option of staying offline.
	 * For each platform, a utility score is calculated. The choice to register/change platforms is stochastic
	 * with higher probabilities for higher scored platforms. 
	 */
	public double utilityMaximization() {
		
		HashMap<Player,Double> screening = new HashMap<Player,Double>();
		HashMap<Player,Integer> freemiumPreference = new HashMap<Player,Integer>();
		
		// Evaluation all plattforms within division
		
		for(Player player:division.getPlayers()) {
			
			// Intrinsic utility from the platform on its own
			
			double attraction=player.intrinsicSellerValue;
			if(player.techs.get(27).active && skill >= 0.8)attraction = attraction+3;
			Logger.getInstane().log("Seller "+actorID + " base utility " +attraction, 5);
				
			// Network utility depending on the sellers on the platfrom
			
			double nScore =0;
			
			for(Buyer buyer :division.getBuyers()) {
				
				if(buyer.player==null || (! buyer.player.equals(player)))continue;
				
				double distancePrice = Math.abs(alignmentPrice - buyer.alignmentPrice);
				double distanceProduct = Math.abs(alignmentProduct - buyer.alignmentProduct);			
				double distance = Math.pow(Math.pow(distancePrice, 2)+Math.pow(distanceProduct, 2),0.5);
				
				double score = Math.max(1-distance*Constants.distanceCost, 0);
				
				nScore=nScore + score ;				
			}
			
			attraction = attraction + nScore * player.factorSellerBeta;
			Logger.getInstane().log("Seller "+actorID + " network utility " +nScore * player.factorSellerBeta, 5);
			
			//  Marketing Activity Bonus

			attraction = attraction * player.factorMarketerBonus;
			
			// Misalignment penalty if the platform caters to a different product type or price range
			
			double distancePrice = Math.abs(alignmentPrice - player.alignmentPrice);
			double distanceProduct = Math.abs(alignmentProduct - player.alignmentProduct);			
			double distance = Math.pow(Math.pow(distancePrice, 2)+Math.pow(distanceProduct, 2),0.5);
			
			attraction = attraction * Math.max(0,(1- distance * Constants.distanceCost));
			Logger.getInstane().log("Seller "+actorID + " price alignment " + alignmentPrice + " vs. "+player.alignmentPrice,5);
			Logger.getInstane().log("Seller "+actorID + " product alignment " + alignmentProduct + " vs. "+player.alignmentProduct,5);
			Logger.getInstane().log("Seller "+actorID + " alignment factor " +Math.max(0,(1- distance * Constants.distanceCost)), 5);
			
			//  Reputation Penalty
			
			attraction = attraction * player.reputation;
			Logger.getInstane().log("Seller "+actorID + " reputation factor " +player.reputation, 5);
			
			//  Penalty for fees and ads
			
			double disutilityAd = Math.pow(player.sellerAdActivityCPM,2) * 100*adAversion * 1 + Math.pow(player.sellerAdActivityPPC,2) * 100*adAversion * player.factorAdDisutility;			
			double disutilitySub = (player.sellerPriceSign + 3*player.sellerPriceSub + 5* player.sellerPriceAction+ 100* player.sellerPriceComission*alignmentPrice);

			if(this.player!=null && this.player.equals(player)) {
				disutilitySub = disutilitySub - player.sellerPriceSign; // Sign up fee is not paid again when remaining
			}
			
			disutilitySub=disutilitySub*feeAversion;// Low Price Segment is more affected by fees
			
			Logger.getInstane().log("Seller "+actorID + " adPenalty " +disutilityAd, 5);
			Logger.getInstane().log("Seller "+actorID + " subPenalty " +disutilitySub, 5);

			
			if(player.sellerFreemium) {
				
				if(disutilityAd < disutilitySub) {
					freemiumPreference.put(player,1);
					attraction = attraction - (disutilityAd);
				}else {
					freemiumPreference.put(player,-1);
					attraction = attraction - disutilitySub;
				}
				
			}else {
				
				freemiumPreference.put(player,0);
				attraction = attraction - disutilityAd - disutilitySub;
				
			}
			
			Logger.getInstane().log(player.getName()+": Seller "+actorID + " freemium choice " + freemiumPreference.get(player), 5);
			
			// Locked-In Bonus that makes sellers stay at their current platform if alternatives are not clearly better
			// and if the seller is not too dissatisfied
			
			if(this.player == player){			
				attraction = attraction + Constants.lockedInBonus*satisfaction;
				attraction = attraction * (1+satisfaction*Constants.lockedInFactor)*(1+0.1*division.getTurn());	
				
				Logger.getInstane().log("Seller "+actorID + " locked in bonus " + Constants.lockedInBonus*satisfaction, 5);
				Logger.getInstane().log("Seller "+actorID + " locked in factor " + (1+satisfaction*Constants.lockedInFactor), 5);
			}
			
			// Overcrowding Penalty
			
			double ratio = Math.max(0,player.popSellers/player.sellerPopCap - 1);
			attraction = attraction * (1-ratio);
			
			Logger.getInstane().log("Seller "+actorID + " overcrowding factor " + (1-ratio), 5);
			
			// Effects from events
			
			if(player.eventDecisions[0]==1)attraction=attraction*1.33;
			if(player.eventDecisions[0]==3)attraction=attraction*0.50;	
			
			if(player.eventDecisions[3]==2)attraction=attraction+2;
			
			Logger.getInstane().log("Seller "+actorID+" incurs utility of "+attraction+" from player "+player.getName(), 4);
			
			if(attraction >0)screening.put(player,attraction);	// negative attraction values lose against outside option	 
			
		}
		
		if(screening.isEmpty()) {
			if(!(this.player == null)) {
				changeOwner(this.player,null);
			}
		}
		
		// Stochastic utility maximization
		
		double total = Constants.outsideOption;
		
		for(Double x:screening.values()) {
			total = total +x; 
		}
		
		double rnd = Math.random()*total;
		
		total=0;
		
		for(Player candidate: screening.keySet()) {
			
			double x = screening.get(candidate);
				
			if(rnd <= total+x) {
				
				freemium = freemiumPreference.get(candidate);
				
				if(this.player == candidate) {
					return x;
				}else {
					changeOwner(player,candidate);
				}
				return x;
			}
			total=total+x;			
			}
		
		return 0;
	}
	
	/**
	 * Returns the average rating of this seller
	 */
	public double calculateRating() {
		
		double ct;
		double pts;
		
		ct = ratings[0]+ratings[1]+ratings[2]+ratings[3]+ratings[4];
		pts = ratings[0]+2*ratings[1]+3*ratings[2]+4*ratings[3]+5*ratings[4];
		
		if(ct==0) {
			averageRating=3;
		}else {
			averageRating=pts/ct;
		}

		return averageRating;
		
	}
	
	/**
	 * The seller spends some time on the platform giving ad income. However, spending time and seeing ads
	 * reduces the satisfaction of the buyer. This loss of satisfaction must be compensated by good deals the
	 * seller makes on the platform.
	 */
	public void surf() {
		
		if(player==null)return;
		if(satisfaction > 1)satisfaction=1;
		
		// Passive dissatisfaction increases with the size of the platform as customers grow more and more
		// demanding towards their platform.
		
		double decay = 1.00;
		
		if(player.popBuyers + player.popSellers > 1000)decay=1.15;
		if(player.popBuyers + player.popSellers > 2500)decay=1.30;
		if(player.popBuyers + player.popSellers > 5000)decay=1.50;
		if(player.popBuyers + player.popSellers > 10000)decay=1.75;
		if(player.popBuyers + player.popSellers > 15000)decay=2.00;
		if(player.popBuyers + player.popSellers > 25000)decay=2.50;
		
		decay = decay * (1- player.mods * 0.025);

		satisfaction = satisfaction - Constants.sellerPassiveDissatisfaction * decay;
		
		// Passive dissatisfaction from costs
		
		if(freemium!=1) {
			satisfaction = satisfaction - 0.01*Math.pow(player.sellerPriceSub/5.0,2)*player.factorCostDisutility;

		}

		// Passive dissatisfaction from ads

		if(freemium!=-1) {
			satisfaction = satisfaction - adAversion * player.sellerAdActivityCPM - adAversion * player.sellerAdActivityPPC * player.factorAdDisutility;
		}
		
		// Ad income
		
		if(freemium!=-1) {
			player.gain(Constants.baseCPM * Constants.priceCPM * player.sellerAdActivityCPM,3);
			player.gain(Constants.basePPC * Constants.pricePPC * Constants.modifierCTR * player.sellerAdActivityPPC * player.factorAdAccuracy,3);
		}
		
		// Churn if completely dissatisfied
		
		if(satisfaction<=0) {
			changeOwner(player,null);
			return;
		}
		
		// Payments
		
		if(freemium < 1)player.gain(0.001*12* player.sellerPriceSub,1);
		
		player.data = player.data + Constants.dataOnUser*player.factorDataGeneration;
		if(player.techs.get(7).active) {
			player.code = player.code + 0.1*Constants.dataOnUser*player.factorDataGeneration;
		}
		
		// Roll random number for transaction simulation
		
		rnd = Math.random();
		cooldown = false;
		
	}
	
}
