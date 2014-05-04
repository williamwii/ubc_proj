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
 * a brain that takes the opposite direction of the leader of the pack
 * Its grooviness increase when it is farer away from leader. When it is 200 pixels away, its grooviness reaches the max
 * Its intensity decreases with the number of other Beasts in its pack, 
 * reaching 100 when there are 5 or more others. 
 * @author William
 *
 */
public class LonelyBrain implements IBeastBrain{
	/**
	 * grooviness increase as distance from leader increases, its intensity decreases with the number of other Beasts in its pack, 
	 * reaching 100 when there are 5 or more others. 
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
		double grooviness = distanceFromLeader-100;
		
		double intensity = BeastMood.MIN_INTENSITY+(packFinder.getPack(info).length-1)*BeastMood.MAX_INTENSITY/5.0;
		
		return new BeastMood(grooviness, intensity);
	}
	/**
	 * return direction that is in opposite way with the leader
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public Direction chooseDirection (BeastInfo info, IPackFinder packFinder){
		double oppDirection;
		if ( packFinder.getLeader(info) == null )
			oppDirection = 180;
		else
			oppDirection = packFinder.getLeader(info).getDirection().getDirection() + 180;
		return new Direction (oppDirection);
	}
}
