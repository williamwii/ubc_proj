/*
  Authors: Wei You
  UNIX login IDs: r9e7
  Student numbers: 77610095
  Date:   April 16, 2010
  
  By submitting this file, we acknowledge that the persons whose names
  appear above are the only authors of this code except as acknowledged in
  the code below.
*/

/**
 * return a pack with beasts have difference in grooviness within 20.
 * The one with the higher grooviness is the leader.
 * 
 * @author William
 *
 */
public class BuddyPackFinder implements IPackFinder{
	/**
	 * The provider that defines the "world" of beasts.
	 */
	private IBeastProvider beastProvider;
	
	/**
	 * maximum difference of grooviness between members in the pack
	 */
	private final double MAX_GROOVY_DIFF = 20.0;
	
	/**
	 * Construct a new BuddyPackFinder.
	 * 
	 * @param provider a non-null provider defining the world of beasts
	 *        available
	 */
	public BuddyPackFinder(IBeastProvider provider) {
		this.beastProvider = provider;
	}
	/**
	 * return a pack with beasts have difference in grooviness within 20.
	 * 
	 * @param beast beast provided
	 * @return return an array with beasts have difference in grooviness within 20.
	 */
	public BeastInfo[] getPack( BeastInfo beast ){
		BeastInfo[] buddyBeastInfo = new BeastInfo[beastProvider.getBeasts().length];
		int beastCounter = 0;
		for ( int i=0; i< beastProvider.getBeasts().length; i++){
			double groovinessDiff = Math.abs( beastProvider.getBeasts()[i].getMood().getGrooviness() - beast.getMood().getGrooviness() );
			if ( groovinessDiff<=MAX_GROOVY_DIFF ){
				buddyBeastInfo[beastCounter] = beastProvider.getBeasts()[i].getInfo();
				beastCounter++;
			}
		}
		
		//copy array into array of right size
		if ( beastCounter < beastProvider.getBeasts().length ){
			BeastInfo[] tempPack = new BeastInfo[beastCounter];
			System.arraycopy(buddyBeastInfo, 0, tempPack, 0, beastCounter);
			buddyBeastInfo = tempPack;
		}
		return buddyBeastInfo;
	}
	/**
	 * @param beast provided
	 * @return The leader with the higher grooviness in the pack
	 */
	public BeastInfo getLeader( BeastInfo beast ){
		double maxGrooviness = -101.0;
		BeastInfo leaderBeast = null;
		for (int i=0; i<getPack(beast).length; i++){
			if ( getPack(beast)[i].getMood().getGrooviness()>maxGrooviness ){
				maxGrooviness = getPack(beast)[i].getMood().getGrooviness();
				leaderBeast = getPack(beast)[i];
			}
		}
		return leaderBeast;
	}
}