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
 * Tester for NearbyIntensePackFinder
 * 
 * 
 * 
 * @author William
 *
 */
public class NearbyIntenseSetup implements IBeastProvider {
	/**
	 * My sweet, beasts.
	 */
	private Beast[] beasties;

	/**
	 * Create a beast provider to set up a simulation.
	 * 
	 * @param width
	 *            the width of the simulation window
	 * @param height
	 *            the height of the simulation window
	 */
	public NearbyIntenseSetup(int width, int height) {
		beasties = new Beast[5];
		NearbyIntensePackFinder packFinder = new NearbyIntensePackFinder(this);
		beasties[0] = new Beast(new SpinnyBrain(), packFinder,0,0,new BeastMood(0,100),5);
		beasties[1] = new Beast(new LonelyBrain(), packFinder,0,0,new BeastMood(0,60),5);
		beasties[2] = new Beast(new GroupieBrain(), packFinder,0,0,new BeastMood(0,40),5);
		beasties[3] = new Beast(new RandomWalkingBrain(), packFinder,0,0,new BeastMood(0,20),5);
		beasties[4] = new Beast(new FollowerBrain(), packFinder,0,0, new BeastMood(0,0),5);
		for (int i=0; i <beasties.length;i++){
			placeBeastRandomly(beasties[i],width,height);
		}
	}
	
	/**
	 * Set a beast's mood to random grooviness and max intensity.
	 * 
	 * @param beast The beast to emote
	 */
	private void grooveBeastRandomly(Beast beast) {
		double groove = Math.random()
				* (BeastMood.MAX_GROOVINESS - BeastMood.MIN_GROOVINESS)
				+ BeastMood.MIN_GROOVINESS;
		beast.setMood(new BeastMood(groove, BeastMood.MAX_INTENSITY));
	}

	/**
	 * Place a beast at a random location going a random direction
	 * 
	 * @param beast
	 *            The beast to place
	 * @param maxX
	 *            width range for location
	 * @param maxY
	 *            height range for location
	 */
	private void placeBeastRandomly(Beast beast, double maxX, double maxY) {
		double x, y, direction;

		x = Math.random() * maxX;
		y = Math.random() * maxY;
		direction = Math.random() * Direction.FULL_CIRCLE;

		beast.setLocation(x, y);
		beast.setDirection(direction);
	}

	/**
	 * Get the setup list of beasts.
	 */
	public Beast[] getBeasts() {
		return beasties;
	}
}
