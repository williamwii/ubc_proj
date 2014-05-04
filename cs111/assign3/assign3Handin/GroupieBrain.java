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
 * directs the beast toward the pack leader, and its mood becomes more groovy the closer it gets, 
 * reaching maximum grooviness when it is 100 pixels or closer to the leader. 
 * Its intensity decreases with the number of other Beasts in its pack, 
 * reaching 0 when there are 5 or more others. 
 * 
 * @author William
 *
 */
public class GroupieBrain implements IBeastBrain{
	/**
	 * grooveness increases when gets closer to leader, intensity depends on number of beasts in the pack
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public BeastMood chooseMood (BeastInfo info, IPackFinder packFinder){
		double leaderX = packFinder.getLeader(info).getX();
		double leaderY = packFinder.getLeader(info).getY();
		double distanceFromLeader = Direction.distance(leaderX, leaderY, info.getX(), info.getY());
		double grooviness = (BeastMood.MAX_GROOVINESS-BeastMood.MIN_GROOVINESS)-distanceFromLeader;
		
		//(packFinder.getPack(info).length-1) indicates other beasts in the pack(not including this groupie beast itself
		double intensity = BeastMood.MAX_INTENSITY - ((packFinder.getPack(info).length-1)*BeastMood.MAX_INTENSITY/5.0);
		
		return new BeastMood(grooviness, intensity);
			
	}
	/**
	 * lead toward the leader of the pack
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the direction of beast provided
	 */
	public Direction chooseDirection (BeastInfo info, IPackFinder packFinder){
		double leaderX = packFinder.getLeader(info).getX();
		double leaderY = packFinder.getLeader(info).getY();
		double diffX = leaderX - info.getX();
		double diffY = leaderY - info.getY();
		return new Direction(diffX,diffY);
	}
}
