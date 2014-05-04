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
 * directs the beast towards the center of its pack. 
 * Its mood becomes groovier when it is close to other pack members, 
 * with an intensity that depends on the number of Beasts in the pack. 
 * If there are 10 or more pack members, it reaches maximum intensity. 
 * If there are no other Beasts in its pack, it wanders aimlessly by picking a random direction 
 * and its mood is neutral (intensity and grooviness both set to the middle of the possible range). 
 * 
 * @author William
 *
 */
public class FlockBrain implements IBeastBrain{
	/**
	 * grooveness increase when gets closer to other pack members, intensity depends on number of beasts in the pack
	 * if no other beast 
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the mood of beast provided
	 */
	public BeastMood chooseMood (BeastInfo info, IPackFinder packFinder){
		
		BeastMood flockBeastMood;
		if ( packFinder.getPack(info).length<=1 )
			flockBeastMood = new BeastMood ((BeastMood.MAX_GROOVINESS-BeastMood.MIN_GROOVINESS)/2.0 , (BeastMood.MAX_INTENSITY-BeastMood.MIN_INTENSITY)/2.0);
		
		else{
			double minDistance=5000;
			for ( int i=0; i<packFinder.getPack(info).length; i++){
				double distanceFromOther = Direction.distance(packFinder.getPack(info)[i].getX(), packFinder.getPack(info)[i].getY(), info.getX(), info.getY());
			
				if (!info.equals(packFinder.getPack(info)[i])){
					if ( minDistance>distanceFromOther ){
						minDistance = distanceFromOther;
					}
				}
			}
			double grooviness = BeastMood.MAX_GROOVINESS - minDistance;
			double intensity = BeastMood.MIN_INTENSITY+(packFinder.getPack(info).length*10.0);
			flockBeastMood = new BeastMood (grooviness,intensity);
		}
		
		return flockBeastMood;
	}
	/**
	 * leads toward the center of the pack
	 * 
	 * @param info information of the beast
	 * @param packFinder the pack the beast is in
	 * 
	 * @return return the direction of beast provided
	 */
	public Direction chooseDirection (BeastInfo info, IPackFinder packFinder){
		Direction direction;
		if ( packFinder.getPack(info).length<=1 ){
			direction = new Direction( Math.random() * Direction.FULL_CIRCLE );
		}
		else{
			double totalX =0;
			double totalY =0;
			for ( int i=0; i<packFinder.getPack(info).length; i++){
					totalX += packFinder.getPack(info)[i].getX();
					totalY += packFinder.getPack(info)[i].getY();
			}
			double diffX = totalX/packFinder.getPack(info).length - info.getX();
			double diffY = totalY/packFinder.getPack(info).length - info.getY();

			direction = new Direction ( diffX,diffY );
		}
		//return a direction with the average X and average Y direction
		return direction;
	}

}
