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
 * returns the pack of all beasts provided by the IBeastProvider. 
 * However, it returns a different pack leader for each beast: it returns the next beast in the array 
 * (wrapping around to the 0th beast for the leader of the last beast). 
 * If the beast passed to getLeader is not in the array, getLeader should return null. 
 * 
 * @author William
 */
public class CongaPackFinder implements IPackFinder{
	/**
	 * The provider that defines the "world" of beasts.
	 */
	private IBeastProvider beastProvider;
	
	/**
	 * Construct a new CongaPackFinder.
	 * 
	 * @param provider a non-null provider defining the world of beasts
	 *        available
	 */
	public CongaPackFinder(IBeastProvider provider) {
		this.beastProvider = provider;
	}
	
	/**
	 * return an array with all the beast in the world.
	 * 
	 * @return a list of all the info for all the beasts in the world
	 */
	public BeastInfo[] getPack( BeastInfo beast ){
		BeastInfo[] allBeastInfo = new BeastInfo[beastProvider.getBeasts().length];
		int beastCounter = 0;
		for (int i=0; i<beastProvider.getBeasts().length; i++){
			allBeastInfo[beastCounter] = beastProvider.getBeasts()[i].getInfo();
			beastCounter++;
		}
		
		//copy array into array of right size
		if ( beastCounter < beastProvider.getBeasts().length ){
			BeastInfo[] tempPack = new BeastInfo[beastCounter];
			System.arraycopy(allBeastInfo, 0, tempPack, 0, beastCounter);
			allBeastInfo = tempPack;
		}
		return allBeastInfo;
	}
	
	/**
	 * return the leader of the pack
	 * 
	 * @param beast provided
	 * @return return the next beast in array as leader( if reaches the last beast in array, then use the first beast in array)
	 */
	public BeastInfo getLeader( BeastInfo beast ){
		BeastInfo leaderBeast = null;
		for ( int i=0; i<beastProvider.getBeasts().length; i++){
			if ( beast.getId()==i ){
				if ( i==(beastProvider.getBeasts().length-1) ){
					leaderBeast = beastProvider.getBeasts()[0].getInfo();
				}
				else{
					leaderBeast = beastProvider.getBeasts()[i+1].getInfo();
				}
			}
		}
		return leaderBeast;
	}
}
