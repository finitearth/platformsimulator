package model;

import java.io.Serializable;
import java.util.HashMap;

import main.Logger;

/**
 * This class represents a buyer that can register, surf and shop on the players platforms.
 */
public class Buyer implements Serializable {

	private static final long serialVersionUID = 1L;

	int actorID=0;
	
	Division division; // The division this buyer is spawned into
	Player player; // The platform this buyer is registered to (null = none)
	
	public double adAversion=4.00;		// how much disutility the buyer takes from ads
	public double failAversion=1.00;	// how much disutility the buyer takes from failed transactions
	public double feeAversion=1.00;		// how much disutility the buyer takes form transaction fees
	public double matchingBonus=0.25;	// expands the maximum distance in price and product type the buyer is willing to endure in general
	public double ratingBonus=0.25;		// expands the maximum distance in price and product type the buyer is willing to endure for high rated sellers
	
	int freemium;						// if the platform offers a freemium option, tracks if the buyer chose premium or basic
										// a value of 1 means, the buyer opted for basic (no fees)
										// a value of -1 means, the buyer opted for premium (no ads)
										// a value of 0 means, the platform the buyer is a member of has no freemium pricing model
	
	public double alignmentPrice;		// the price class of products this buyer is interested in (range 0 to 1)
	public double alignmentProduct;		// the type of products this buyer is interested in (range 0 to 1)
	
	public boolean isTroll;				// if true, the buyer does stupid stuff like fake reviews
	
	public double satisfaction;			// how happy the buyer is with his current platform membership (range 0 to 1)
	
	public double rnd;
	boolean cooldown = false;			// if true, the buyer is exhausted and won't buy anything until the next turn
	
	public boolean banned=false;		// if true, the buyer is kicked from the platform at the end of this turn
	
	/**
	 * Spawns a new buyer in the given division. The preferred price point and type of goods is randomized.
	 * Some behavioral variables of the buyer depend on these preferences.
	 */
	public Buyer(Division division) {
		this.division = division;
		actorID=division.buyerID;
		division.buyerID=division.buyerID+1;
		
		player=null;
		satisfaction = 0.75; // newly registered members start at 75% satisfaction
		
		alignmentPrice = Math.random();
		alignmentProduct = Math.random();
		
		// High end product buyers are very sensitive to ads
		adAversion = 0.04+0.06*alignmentPrice;
		
		// Low end product buyers are very sensitive to fees
		feeAversion=0.20;
		if(alignmentPrice < 0.33)feeAversion=0.30;
		if(alignmentPrice < 0.15)feeAversion=0.40;
		
		// High end product buyers are very sensitive to fraud & misbehavior of sellers
		if(alignmentPrice > 0.67)failAversion=1.5;
		if(alignmentPrice > 0.85)failAversion=2;
	
		// Buyers of art products are more specific in their tastes
		if(alignmentProduct > 0.67)matchingBonus=0.50;
		if(alignmentProduct > 0.85)matchingBonus=0.75;
		
		// Buyers of practical products are more relying on ratings
		if(alignmentProduct < 0.33)ratingBonus=0.5;
		if(alignmentProduct < 0.15)ratingBonus=1.0;
		
		if(Math.random() < Constants.buyerTrollRate)this.isTroll = true;
	}
	
	/**
	 * This method registers/unregisters the buyer from the given platforms.
	 * @param current The current platform the buyer is on. Can be null if the buyer is freshly spawned or went offline
	 * @param successor The next platform the buyer moves to. Can be null if the buyer is kicked or chooses to go offline
	 */
	public void changeOwner(Player current,Player successor) {
		
		String source ="null";
		String target ="null";
		if(current!=null)source=current.getName();
		if(successor!=null)target = successor.getName();
	    Logger.instance.log("Buyer " + actorID + " changes from "+ source + " to " + target,3);
		
		if(banned && successor!=null)return;
		
		if(current!=null) {
			current.popBuyers=current.popBuyers-1;
		}
		
		if(successor!=null) {
			successor.popBuyers=successor.popBuyers+1;
		}
	
		player=successor;
		if(player!=null)player.gain(0.001 * player.buyerPriceSign,0);
		satisfaction = 0.75;
		cooldown=false;
	}
	
	/**
	 * In each turn, the buyer considers all available platforms, as well as the option of staying offline.
	 * For each platform, a utility score is calculated. The choice to register/change platforms is stochastic
	 * with higher probabilities for higher scored platforms. 
	 */
	public double utilityMaximization() {
		
		HashMap<Player,Double> screening = new HashMap<Player,Double>();
		HashMap<Player,Integer> freemiumPreference = new HashMap<Player,Integer>();
		
		// Evaluation all plattforms within division
		
		for(Player player:division.getPlayers()) {
			
			// Intrinsic utility from the platform on its own
			
			double attraction=player.intrinsicBuyerValue;
			Logger.getInstane().log("Buyer "+actorID + " base utility " +attraction, 5);
			
			// Network utility depending on the sellers on the platfrom
			
			double nScore =0;
			
			for(Seller seller :division.getSellers()) {
				
				if(seller.player==null  || (! seller.player.equals(player)))continue;
				
				double distancePrice = Math.abs(alignmentPrice - seller.alignmentPrice);
				double distanceProduct = Math.abs(alignmentProduct - seller.alignmentProduct);			
				double distance = Math.pow(Math.pow(distancePrice, 2)+Math.pow(distanceProduct, 2),0.5);
				
				double score = Math.max(1-distance*Constants.distanceCost, 0);
				
				nScore=nScore + score;			
			}
			
			attraction = attraction + nScore * player.factorBuyerBeta;
			Logger.getInstane().log("Buyer "+actorID + " network utility " +nScore * player.factorBuyerBeta, 5);
			
			//  Marketing Activity Bonus

			attraction = attraction * player.factorMarketerBonus;
			
			// Misalignment penalty if the platform caters to a different product type or price range
			
			double distancePrice = Math.abs(alignmentPrice - player.alignmentPrice);
			double distanceProduct = Math.abs(alignmentProduct - player.alignmentProduct);			
			double distance = Math.pow(Math.pow(distancePrice, 2)+Math.pow(distanceProduct, 2),0.5);
			
			attraction = attraction * Math.max(0,(1- distance * Constants.distanceCost));
			Logger.getInstane().log("Buyer "+actorID + " price alignment " + alignmentPrice + " vs. "+player.alignmentPrice,5);
			Logger.getInstane().log("Buyer "+actorID + " product alignment " + alignmentProduct + " vs. "+player.alignmentProduct,5);
			Logger.getInstane().log("Buyer "+actorID + " alignment factor " +Math.max(0,(1- distance * Constants.distanceCost)), 5);
						
			//  Reputation penalty
			
			attraction = attraction * player.reputation;
			Logger.getInstane().log("Buyer "+actorID + " reputation factor " +player.reputation, 5);
			
			//  Penalty for fees and ads
			
			double disutilityAd = Math.pow(player.buyerAdActivityCPM,2) * 100*adAversion  + Math.pow(player.buyerAdActivityPPC,2) * 100*adAversion * player.factorAdDisutility;
			
			double disutilitySub = (player.buyerPriceSign + 3*player.buyerPriceSub + 5* player.buyerPriceAction + 100* player.buyerPriceComission*alignmentPrice);

			if(this.player!=null && this.player.equals(player)) {
				disutilitySub = disutilitySub - player.buyerPriceSign; // Sign up fee is not paid again when remaining
			}
			
			disutilitySub=disutilitySub*feeAversion; // Low Price Segment is more affected by fees	
			
			Logger.getInstane().log("Buyer "+actorID + " adPenalty " +disutilityAd, 5);
			Logger.getInstane().log("Buyer "+actorID + " subPenalty " +disutilitySub, 5);
			
			if(player.buyerFreemium) {
				
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
			
			Logger.getInstane().log("Buyer "+actorID + " freemium choice " + freemiumPreference.get(player), 5);
			
			// Locked-In Bonus that makes buyers stay at their current platform if alternatives are not clearly better
			// and if the buyer is not too dissatisfied
			
			if(this.player == player){			
				
				attraction = attraction + Constants.lockedInBonus*satisfaction;
				attraction = attraction * (1+satisfaction*Constants.lockedInFactor)*(1+0.1*division.getTurn());	
				
				Logger.getInstane().log("Buyer "+actorID + " locked in bonus " + Constants.lockedInBonus*satisfaction, 5);
				Logger.getInstane().log("Buyer "+actorID + " locked in factor " + (1+satisfaction*Constants.lockedInFactor), 5);
			}
			
			// Overcrowding penalty if the platform exceeds its pop-cap.
			
			double ratio = Math.max(0,player.popBuyers/player.buyerPopCap - 1);
			attraction = attraction * (1-ratio);
			
			Logger.getInstane().log("Buyer "+actorID + " overcrowding factor " + (1-ratio), 5);
			
			
			// Effects from events
			
			if(player.eventDecisions[0]==1)attraction=attraction*1.33;
			if(player.eventDecisions[0]==3)attraction=attraction*0.50;	
			
			if(player.eventDecisions[3]==1)attraction=attraction+2;
			
			Logger.getInstane().log("Buyer "+actorID+" incurs utility of "+attraction+" from player "+player.getName(), 4);
			
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
					changeOwner(this.player,candidate);
				}
				return x;
			}
			total=total+x;
				
			}
		
		return 0;
	}
	
	/**
	 * Makes this buyer go shopping
	 */
	public void shop(){
		
		if(player==null)return; // Buyer is not a member of any platform - no shopping
		
		// The buyer browses through the offers of all sellers on its platform
		for(Seller seller: division.getSellers()) {
			
			if(seller.player==null)continue; // Null pointer filter
			if(!(seller.player.equals(this.player)))continue; // Filter for sellers that are on the same platform as the buyer
			if(cooldown)break; // Buyer had enough shopping for this round
			
			// Distance between the sellers offer and the buyers buying intent (higher = worse)
			double rndDistance = Math.abs(rnd-seller.rnd); // On average 0.33 with skew
			double distancePrice = Math.abs(alignmentPrice - seller.alignmentPrice);
			double distanceProduct = Math.abs(alignmentProduct - seller.alignmentProduct);			
			double fitDistance = Math.pow(Math.pow(distancePrice, 2)+Math.pow(distanceProduct, 2),0.5); // Should be between 0 and 0.25
			
			// Threshold of distance the buyer is willing to take (higher = better)
			double threshold = player.factorMatchingDistance + (seller.calculateRating()-3)*player.factorMatchQualityScaling*(1+ratingBonus);
			threshold = threshold + (player.factorMatchingDistance-0.20)*matchingBonus;
			
			if(player.techs.get(27).active && seller.skill >= 0.8)threshold = threshold + 0.05;
			
			// Buy if distance is below threshold for purchase
			if(rndDistance+fitDistance < threshold) {
				Logger.getInstane().log(player.getName()+" : Buyer "+actorID+" intends to buy from seller "+ seller.actorID + " with "+rndDistance+" + " +fitDistance+" below threshold "+threshold, 4);
				transact(seller);
			}
		}
		
	}
	
	/**
	 * This method handes the transaction with the given seller
	 */
	public void transact(Seller seller) {
		
		// Randomize transaction value
		double pricePoint = Math.pow((10+10*Math.random()),1+2*(seller.alignmentPrice));
		
		// Fees the buyer and the seller would have to pay (may depend on the transaction value)
		double feeBuyer = player.buyerPriceAction + pricePoint*player.buyerPriceComission;
		double feeSeller = player.sellerPriceAction + pricePoint*player.sellerPriceComission;
		
		if(freemium==1 && player.buyerFreemium)feeBuyer=0;
		if(seller.freemium==1 && player.sellerFreemium)feeSeller=0;
		
		Logger.getInstane().log("Transaction pricepoint is "+pricePoint+" with "+feeBuyer+" / "+feeSeller+" transaction costs" , 4);
		
		// If fees are too high, the transaction is aborted. At 10% two-sided commission load, the probability is 36% 
		
		if(Math.random() < 2.5*((feeBuyer*player.factorCostDisutility)/pricePoint)) {
			Logger.getInstane().log("Buyer walks away from trade" , 4);
			satisfaction = satisfaction - 0.1;
			return;
		}
		
		if(Math.random() < 2.5*((feeSeller*player.factorCostDisutility)/pricePoint)) {
			Logger.getInstane().log("Seller walks away from trade" , 4);
			seller.satisfaction = seller.satisfaction - 0.1;
			return;
		}
		
		// If not, the purchase is made!
		
		// Calculate how happy the buyer is with his purchase
		double success = 0.7*Math.min(seller.skill*player.factorSkillBonus,1.0) + 0.3*Math.random();
		
		// If the seller is a troll and moderation / security measures do not stop it, the buyer is in for a bad surprise.
		// If the troll is stopped, the transaction fails, but no harm is done to the buyer and thus the platforms reputation.
		if(seller.isTroll) {
			
			if(Math.random() < player.factorCheatSupression) {
				seller.banned=true;
				seller.changeOwner(seller.player, null);
				return;
			}else {
				success=0;
			}

		}
		
		Logger.getInstane().log("Transaction success rating is "+success, 4);
		
		// The number of transactions on the platform is increased by 5. The factor 5 is chosen to make for somewhat 
		// realistic numbers.
		
		player.transactions=player.transactions+5;
		
		// If the platform uses ads, the buyer and seller will have seen quite a few by now. The ads pay out!
		
		if(freemium != -1) {
			player.gain(Constants.transactionCPM * Constants.priceCPM * (player.buyerAdActivityCPM+player.sellerAdActivityCPM),3);
			player.gain(Constants.transactionPPC * Constants.pricePPC *  (player.buyerAdActivityCPM+player.sellerAdActivityCPM) * Constants.modifierCTR * player.factorAdAccuracy,3);
		
		}
		
		// Satisfaction and rating if the rating system tech is unlocked.

		if(success < 0.2) {
			if(TechTree.getInstance().ratingExists(seller.player))rate(seller,1);
			satisfaction = satisfaction + Constants.buyer1star*failAversion;
		}else if(success < 0.4) {
			if(TechTree.getInstance().ratingExists(seller.player))rate(seller,2);
			satisfaction = satisfaction + Constants.buyer2star*failAversion;
		}else if(success < 0.6) {
			if(TechTree.getInstance().ratingExists(seller.player))rate(seller,3);
			satisfaction = satisfaction + Constants.buyer3star;
		}else if(success < 0.8) {
			if(TechTree.getInstance().ratingExists(seller.player))rate(seller,4);
			satisfaction = satisfaction + Constants.buyer4star;
		}else {
			if(TechTree.getInstance().ratingExists(seller.player))rate(seller,5);
			satisfaction = satisfaction + Constants.buyer5star;
		}
		
		if(isTroll && ! seller.isTroll) {
			seller.satisfaction = seller.satisfaction + Constants.sellerFail;
		}else {
			seller.satisfaction = seller.satisfaction + Constants.sellerSuccess;
		}
		
		if(player==null)return;
		
		if(player!=null && player.techs.get(22).active) {
			satisfaction = satisfaction + 0.01;
			seller.satisfaction = seller.satisfaction + 0.01;
		}
		
		if(Math.random() < player.factorCooldownBuyers) {
			cooldown=true;
		}
		
		if(Math.random() < player.factorCooldownSellers) {
			seller.cooldown=true;
		}
		
		player.gain(0.001*5*(feeBuyer+feeSeller),2); // Prices for buyers and sellers are quoted in €, while the resource money is given in 1k€
		
		player.data = player.data + Constants.dataOnTransaction*player.factorDataGeneration;
		if(player.techs.get(7).active) {
			player.code = player.code + 0.1*Constants.dataOnTransaction*player.factorDataGeneration;
		}
		
	}
	
	/*
	 * If the rating technology is unlocked, there is a chance that the buyer leaves a seller rating.
	 * If the buyer is a troll, it will give bad ratings to good sellers and good ratings for bad sellers.
	 */
	public void rate(Seller seller,int rating) {
		
		if(Math.random() > player.factorSignalGeneration && ! isTroll)return; // Some buyers don`t leave ratings
		
		if(isTroll) {
			if(Math.random() > player.factorFakeReviewSupression ) {
				rating = 6-rating; // Fake positive and negative reviews
			}else {
				banned=true;
				changeOwner(player,null);
				return;
			}
		}
		
		satisfaction = satisfaction + 0.05;
		
		seller.ratings[rating-1]=seller.ratings[rating-1]+1;	
		
		Logger.getInstane().log("Rating of seller "+ seller.actorID +"is now "+seller.calculateRating() + " after an additional rating of "+rating, 4);
		
	}
	
	/**
	 * The buyer spends some time on the platform giving further ad income. However, spending time and seeing ads
	 * reduces the satisfaction of the buyer. This loss of satisfaction must be compensated by good deals the buyer
	 * makes on the platform.
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
		
		satisfaction = satisfaction - Constants.buyerPassiveDissatisfaction * decay;

		// Passive dissatisfaction from costs

		if(freemium!=1) {
			satisfaction = satisfaction - 0.02*Math.pow(player.buyerPriceSub/5.0,2)*player.factorCostDisutility;
		}
		
		// Passive dissatisfaction from ads
		
		if(freemium!=-1) {
			satisfaction = satisfaction - adAversion * player.buyerAdActivityCPM - adAversion * player.buyerAdActivityPPC * player.factorAdDisutility;
		}
		
		// Ad income
		
		if(freemium!=-1) {
			player.gain(Constants.baseCPM * Constants.priceCPM * player.buyerAdActivityCPM,3);
			player.gain(Constants.basePPC * Constants.pricePPC * Constants.modifierCTR * player.buyerAdActivityPPC * player.factorAdAccuracy,3);
		}
		
		// Churn if completely dissatisfied
		
		if(satisfaction<=0) {
			changeOwner(player,null);
			return;
		}

		// Payments
		
		if(freemium < 1)player.gain(0.001*12* player.buyerPriceSub,1);
		
		player.data = player.data + Constants.dataOnUser*player.factorDataGeneration;
		if(player.techs.get(7).active) {
			player.code = player.code + 0.1*Constants.dataOnUser*player.factorDataGeneration;
		}
		
		// Roll random number for transaction simulation
		
		rnd = Math.random();
		cooldown = false;
		
	}
	
	
}
