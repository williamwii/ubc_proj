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
 * returns the pack of all the Beasts with grooviness below 0. The most ungroovy Beast in the pack is the leader. 
 * 
 * @author William
 *
 */
public class GlumPackFinder implements IPackFinder{
	/**
	 * The provider that defines the "world" of beasts.
	 */
	private IBeastProvider beastProvider;
	
	/**
	 * Construct a new GlumPackFinder.
	 * 
	 * @param provider a non-null provider defining the world of beasts
	 *        available
	 */
	public GlumPackFinder(IBeastProvider provider) {
		this.beastProvider = provider;
	}
	/**
	 * find all the beast with grooviness under 0
	 * 
	 * @param beast provide for the pack
	 * @return return an array with all the beast with grooviness under 0
	 */
	public BeastInfo[] getPack( BeastInfo beast){
		BeastInfo[] glumBeastInfo = new BeastInfo[beastProvider.getBeasts().length];
		int beastCounter = 0;
		for (int i=0; i<beastProvider.getBeasts().length; i++){
			if ( beastProvider.getBeasts()[i].getMood().getGrooviness() < 0){
				glumBeastInfo[beastCounter]= beastProvider.getBeasts()[i].getInfo();
				beastCounter++;
			}
		}
		
		//copy array into array of right size
		if ( beastCounter < beastProvider.getBeasts().length ){
			BeastInfo[] tempPack = new BeastInfo[beastCounter];
			System.arraycopy(glumBeastInfo, 0, tempPack, 0, beastCounter);
			glumBeastInfo = tempPack;
		}
		return glumBeastInfo;
	}
	/**
	 * get the leader of the pack. It is the beast with the lowest grooviness
	 * 
	 * @param beast provide for the pack
	 * @return the most ungrooy beast in the pack
	 */
    public BeastInfo getLeader( BeastInfo beast ){
    	double minGrooviness = 101.0;
    	BeastInfo leaderBeast = null;
    	for (int i=0; i<getPack(beast).length; i++){
    		if ( getPack(beast)[i].getMood().getGrooviness()<minGrooviness ){
    			minGrooviness = getPack(beast)[i].getMood().getGrooviness();
    			leaderBeast = getPack(beast)[i];
    		}
    	}
    	return leaderBeast;
    }

}
