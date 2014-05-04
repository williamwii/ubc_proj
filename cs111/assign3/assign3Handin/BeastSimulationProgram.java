import acm.program.*;

/**
 * Run a simulation of beast behaviour. Aren't they cute?
 * 
 * @author CPSC 111 instructors.. AND YOU!
 * 
 */
public class BeastSimulationProgram extends GraphicsProgram
{
	private static final long serialVersionUID = 1L;

	/**
     * Time to pause between animation cycles (in milliseconds).
     * <p>
     * TODO: you may want to change this to change the simulation speed!!
     */
    private static final int PAUSE_TIME = 100;

    /**
     * Width of the game display (all coordinates are in pixels)
     */
    private static final int WIDTH = 400;

    /**
     * Height of the game display
     */
    private static final int HEIGHT = 600;

    /**
     * The manager of this beast simulation.
     */
    private BeastManager beastManager = new BeastManager( this.getGCanvas() );

    /**
     * Runs the program as an application. This method differs from the simplest
     * possible boilerplate in that it passes parameters to specify the
     * dimensions of the simulation.
     */
    public static void main( String[] args )
    {
        String[] sizeArgs = { "width=" + WIDTH, "height=" + HEIGHT };
        new BeastSimulationProgram().start( sizeArgs );

    }

    /**
     * Returns a beast provider that gives the initial beasts to use for the
     * simulation.
     * 
     * @return a non-null beast provider
     */
    protected IBeastProvider setup()
    {
        // TODO: Write new classes that implement interface IBeastProvider that
        // create an array of new Beasts to test your various BeastBrains and
        // PackFinders. Change this line below to use them.

        return new GlumSetup( this.getWidth(), this.getHeight() );
    }

    /**
     * Runs the program.
     */
    public void run()
    {
        IBeastProvider setup = this.setup();

        // have the beasts move all at once
        getGCanvas().setAutoRepaintFlag( false );
        Beast[] newBeasts = setup.getBeasts();
        for( int i = 0; i < newBeasts.length; i++ )
        {
            this.beastManager.addBeast( newBeasts[ i ] );
        }
        repaint();

        while( true )
        {
            this.pause( PAUSE_TIME );
            this.beastManager.updateBeasts();
            repaint();
        }
    }
}
