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
 * Tester of GlumPackFinder
 * 
 * 
 * A setup routine that creates a spinny beast as leader of the pack
 * and some groupie beasts
 * <p>
 * 
 * @author William
 */
public class GlumSetup implements IBeastProvider {
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
	public GlumSetup(int width, int height) {
		beasties = new Beast[4];
		beasties[0] = new Beast(new GroupieBrain(), new GlumPackFinder(this), 0,0, new BeastMood( 10, 0 ), Beast.DEFAULT_SPEED);
		beasties[1] = new Beast(new SpinnyBrain(), new GlumPackFinder(this), 0,0, new BeastMood( -10, 0 ), Beast.DEFAULT_SPEED);
		beasties[2] = new Beast(new GroupieBrain(), new GlumPackFinder(this), 0,0, new BeastMood( -1, 0 ), Beast.DEFAULT_SPEED);
		beasties[3] = new Beast(new GroupieBrain(), new GlumPackFinder(this), 0,0, new BeastMood( -2, 0 ), Beast.DEFAULT_SPEED);
		
		
		for (int i=0; i <beasties.length;i++){
			placeBeastRandomly(beasties[i],width,height);
			
		}

	}
	/**
	 * Set a beast's mood to random grooviness and maximum intensity.
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
