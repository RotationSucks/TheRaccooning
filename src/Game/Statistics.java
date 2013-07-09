package Game;

public class Statistics {
	
	private static final Statistics instance = new Statistics();

	// initialise in resetStats() to make sure we reset every counter we need
	private int							laserActivationCounter;
	private int							laserEnergyCounter;
	private int							wholeSpentBolts;
	private int							wholeCollectedBolts;
	private int							tailwhipCounter;
	private int							groundpoundCounter;
	private int							generatorsRepaired;
	private int							generatorsUsed;
	private int							killedPigsCounter;
	private int							tailwhipKills;
	private int							groundPoundKills;
	private int							laserKills;
	private int							ambushKills;
	private int							originalTailwhipKill;
	private int							bounceofTailwhipKill;
	private int 						timeCounter;
	

	public static Statistics getInstance() {
		return instance;
	}
	
	public void resetStats() {
		timeCounter 				= 0;
		generatorsRepaired 			= 0;
		generatorsUsed 				= 0;
		groundpoundCounter 			= 0;
		laserActivationCounter 		= 0;
		laserEnergyCounter 			= 0;
		tailwhipCounter 			= 0;
		wholeCollectedBolts 		= 0;
		wholeSpentBolts 			= 0;
		laserKills					= 0;
		ambushKills					= 0;
		groundPoundKills			= 0;
		tailwhipKills				= 0;
		killedPigsCounter 			= 0;
		originalTailwhipKill		= 0;
		bounceofTailwhipKill		= 0;
	}

	public void printStats(){

		float time = ((int) (timeCounter/60f*100))/100f;
		
		System.out.println("You survived "				+ time			 										+ " Seconds.");
		System.out.println("You killed " 				+ Statistics.getInstance().getKilledPigsCounter() 		+ " Pigs.");
		System.out.println("You activated your Laser "	+ Statistics.getInstance().getLaserActivationCounter() 	+ " times.");
		System.out.println("You repaired " 				+ Statistics.getInstance().getGeneratorsRepaired() 		+ " broken generators.");
		System.out.println("You used generators" 		+ Statistics.getInstance().getGeneratorsUsed() 			+ " times.");
		System.out.println("You attacked " 				+ Statistics.getInstance().getGroundpoundCounter() 		+ " times with Groundpound.");
		System.out.println("You used " 					+ Statistics.getInstance().getLaserEnergyCounter() 		+ " laserenergy.");
		System.out.println("You attacked " 				+ Statistics.getInstance().getTailwhipCounter() 		+ " times with tailwhip.");
		System.out.println("You collected " 			+ Statistics.getInstance().getWholeCollectedBolts() 	+ " Bolts/Nuts.");
		System.out.println("You used " 					+ Statistics.getInstance().getWholeSpentBolts() 		+ " Bolts/Nuts.");
		System.out.println("tailwhip kill " 			+ Statistics.getInstance().getTailwhipKills() 			+ " .");
		System.out.println("groundpound kill " 			+ Statistics.getInstance().getGroundPoundKills() 		+ " .");
		System.out.println("laser kill " 				+ Statistics.getInstance().getLaserKills()		 		+ " .");
		System.out.println("ambush kill " 				+ Statistics.getInstance().getAmbushKills()		 		+ " .");
		System.out.println("original tailwhip kill" 	+ Statistics.getInstance().getOriginalTailwhipKill() 	+ " .");
		System.out.println("bounc of tailwhip kill "	+ Statistics.getInstance().getBounceofTailwhipKill()	+ " .");
	}
	
	public int getLaserActivationCounter() {
		return laserActivationCounter;
	}

	public int getLaserEnergyCounter() {
		return laserEnergyCounter;
	}

	public int getWholeSpentBolts() {
		return wholeSpentBolts;
	}

	public int getWholeCollectedBolts() {
		return wholeCollectedBolts;
	}

	public int getTailwhipCounter() {
		return tailwhipCounter;
	}

	public int getGroundpoundCounter() {
		return groundpoundCounter;
	}

	public int getGeneratorsRepaired() {
		return generatorsRepaired;
	}

	public int getGeneratorsUsed() {
		return generatorsUsed;
	}

	public void incLaserActivationCounter() {
		++laserActivationCounter;
	}

	public void incLaserEnergyCounter() {
		++laserEnergyCounter;
	}

	public void incWholeSpentBolts() {
		++wholeSpentBolts;
	}

	public void incWholeCollectedBolts() {
		++wholeCollectedBolts;
	}

	public void incTailwhipCounter() {
		++tailwhipCounter;
	}

	public void incGroundpoundCounter() {
		++groundpoundCounter;
	}

	public void incGeneratorsRepaired() {
		++generatorsRepaired;
	}

	public void incGeneratorsUsed() {
		++generatorsUsed;
	}

	public int getKilledPigsCounter() {
		return killedPigsCounter;
	}
	public void incKilledPigsCounter() {
		++killedPigsCounter;
	}
	
	public int getTailwhipKills() {
		return tailwhipKills;
	}

	public int getGroundPoundKills() {
		return groundPoundKills;
	}

	public int getLaserKills() {
		return laserKills;
	}

	public void incLaserKills() {
		++laserKills;
	}
	public void incTailwhipKills() {
		++tailwhipKills;
	}

	public void incGroundPoundKills() {
		++groundPoundKills;
	}

	public int getAmbushKills() {
		return ambushKills;
	}

	public void incAmbushKills() {
		++ambushKills;
	}

	public int getOriginalTailwhipKill() {
		return originalTailwhipKill;
	}

	public void incOriginalTailwhipKill() {
		++originalTailwhipKill;
	}

	public int getBounceofTailwhipKill() {
		return bounceofTailwhipKill;
	}

	public void incBounceofTailwhipKill() {
		++bounceofTailwhipKill;
	}
	
	
	
}
