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
 * a follower brain that follow the leader of the pack, have the same grooviness and half intensity of the leader
 * 
 * @author William
 *
 */
public class FollowerBrain implements IBeastBrain{
	/**
	 * have half the intensity of leader and same grooviness
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public BeastMood chooseMood (BeastInfo info, IPackFinder packFinder){
		BeastMood leaderMood = packFinder.getLeader(info).getMood();
		BeastMood followerMood = new BeastMood(leaderMood.getGrooviness(),(leaderMood.getIntensity()/2.0));
		return followerMood;
	}
	/**
	 * lead toward the leader of the pack
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public Direction chooseDirection (BeastInfo info, IPackFinder packFinder){
		double leaderX = packFinder.getLeader(info).getX();
		double leaderY = packFinder.getLeader(info).getY();
		double diffX = leaderX - info.getX();
		double diffY = leaderY - info.getY();
		return new Direction(diffX,diffY);
	}
}
