import java.applet.Applet;
import java.awt.*;
public class PetLandFlag extends Applet
{
    /** 
     * method that is called automatically when the applet is to be painted
     * (i.e. when it is first displayed and when the window is resized)
     */
    public void paint( Graphics page )
    {
        super.paint(page);
    
        Graphics2D g2 = (Graphics2D) page;
        
        // get current width of the applet window
        int width = getWidth();
        int height = getHeight();


       Rectangle topRectangle;
       Rectangle bottomRectangle;
       Rectangle middleRectangle;
       Rectangle petRectangle;

       // now draw the PetLand flag
       topRectangle 
          = new Rectangle (0, 0, width, 25 * height / 100);
       bottomRectangle 
          = new Rectangle (0, 75 * height / 100, width, 25 * height / 100);
       middleRectangle 
          = new Rectangle (0, 25 * height / 100, width, 5 * height / 10);

       Polygon Pet = new Polygon();
       Pet.addPoint(width / 2 - 80, 45 * height / 100);
       Pet.addPoint(width / 2 - 80, 35 * height / 100);
       Pet.addPoint(width / 2 - 100, 35 * height / 100);
       Pet.addPoint(width / 2 - 100, 45 * height / 100);
       Pet.addPoint(width / 2 - 100, 60 * height / 100);
       Pet.addPoint(width / 2, 65 * height / 100);
       Pet.addPoint(width / 2 + 100, 60 * height / 100);
       Pet.addPoint(width / 2 + 100, 45 * height / 100);
       Pet.addPoint(width / 2 + 100, 35 * height / 100);
       Pet.addPoint(width / 2 + 80, 35 * height / 100);
       Pet.addPoint(width / 2 + 80, 45 * height / 100);

       Polygon PetEye1 = new Polygon();
       PetEye1.addPoint(width / 2 - 50, 50 * height / 100);
       PetEye1.addPoint(width / 2 - 50, 53 * height / 100);
       PetEye1.addPoint(width / 2 - 35, 53 * height / 100);
       PetEye1.addPoint(width / 2 - 35, 50 * height / 100);

       Polygon PetEye2 = new Polygon();
       PetEye2.addPoint(width / 2 + 50, 50 * height / 100);
       PetEye2.addPoint(width / 2 + 50, 53 * height / 100);
       PetEye2.addPoint(width / 2 + 35, 53 * height / 100);
       PetEye2.addPoint(width / 2 + 35, 50 * height / 100);

       g2.setColor(Color.BLUE);
       g2.fill(topRectangle);
       g2.fill(bottomRectangle);
       g2.setColor(Color.YELLOW);
       g2.fill(middleRectangle);
       g2.setColor(Color.BLACK);
       g2.fill(Pet);
       g2.setColor(Color.RED);
       g2.fill(PetEye1);
       g2.fill(PetEye2);
    }
}