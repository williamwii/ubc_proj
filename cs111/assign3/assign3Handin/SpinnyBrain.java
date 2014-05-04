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
 * A spinny brain that goes in circle
 * 
 * @author William
 *
 */
public class SpinnyBrain implements IBeastBrain {
	
	/**
	 * mood staying neutral
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public BeastMood chooseMood (BeastInfo info, IPackFinder packFinder){
		return info.getMood();
	}
	/**
	 * goes in circle
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public Direction chooseDirection (BeastInfo info, IPackFinder packFinder){
		return new Direction (info.getAge()*5.0);
	}
	
}
