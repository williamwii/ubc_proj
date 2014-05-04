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
 * returns the pack of all Beasts within 200 pixels. 
 * The leader is the most intense Beast in the pack. 
 * 
 * @author William
 *
 */
public class NearbyIntensePackFinder implements IPackFinder{
	/**
	 * The provider that defines the "world" of beasts.
	 */
	private IBeastProvider beastProvider;
	
	/**
	 * Construct a new NearbyIntensePackFinder.
	 * 
	 * @param provider a non-null provider defining the world of beasts
	 *        available
	 */
	public NearbyIntensePackFinder(IBeastProvider provider) {
		this.beastProvider = provider;
	}
	/**
	 * Get a pack of beast within 200 pixels from the provided beast
	 * 
	 * @param beast beast provided as the central of pack
	 * @return an array of beast within 200 pixels from the provided beast
	 */
	public BeastInfo[] getPack( BeastInfo beast ){
		BeastInfo[] nearbyBeastInfo = new BeastInfo[beastProvider.getBeasts().length];
		int beastCounter = 0;
		for (int i=0; i<beastProvider.getBeasts().length; i++){
			double x1 = beastProvider.getBeasts()[i].getX();
			double y1 = beastProvider.getBeasts()[i].getY();
			if ( Direction.distance(x1, y1, beast.getX(), beast.getY()) <=200 ){
				nearbyBeastInfo[beastCounter] = beastProvider.getBeasts()[i].getInfo();
				beastCounter++;
			}
		}
		
		//copy array into array of right size
		if ( beastCounter < beastProvider.getBeasts().length ){
			BeastInfo[] tempPack = new BeastInfo[beastCounter];
			System.arraycopy(nearbyBeastInfo, 0, tempPack, 0, beastCounter);
			nearbyBeastInfo = tempPack;
		}
		return nearbyBeastInfo;
	}
	/**
	 * Choose the most intense beast in the pack to be the leader
	 * 
	 * @param beast beast provided
	 * @return the leader of the pack
	 */
	public BeastInfo getLeader( BeastInfo beast ){
		double maxIntensity = -1.0;
		BeastInfo leaderBeast = null;
		for (int i=0; i<this.getPack(beast).length; i++){
			if ( this.getPack(beast)[i].getMood().getIntensity()>maxIntensity ){
				maxIntensity = this.getPack(beast)[i].getMood().getIntensity();
				leaderBeast = this.getPack(beast)[i];
			}
		}
		return leaderBeast;
	}
}
