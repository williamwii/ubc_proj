/*
 * Author: Tamara Munzner
 * Ugrad lab login ID: cs111
 * Date: Mon Mar  1 00:37:53 2010
 * 
 * By submitting this file, I acknowledge that the person whose name appears
 * above is the sole author of this code except as acknowledged in the code below.
 *
 * ASCIIArtiste class
 * Assignment 2 Part 1 stub. Many methods need to be written or
 * finished, and you may need to add more methods or fields. 
 * 
 */

import java.util.Scanner;

public class ASCIIArtiste
{

    /**
     * Proportion of box to upside down heart when drawing spade. It's fine, but
     * not required, to experiment by changing this default value.
     */
    private final double SPADE_BOX = .25;

    /**
     * Proportion of peak to triangle when drawing heart. It's fine, but not
     * required, to experiment by changing this default value.
     */
    private final double HEART_PEAK = .25;

    /**
     * Compute the width of a triangle given its height
     * 
     * @param triangleHeight number of rows in triangle
     * @return width of triangle at widest point
     */
    private int getTriangleWidth( int triangleHeight )
    {
        return 2 * triangleHeight - 1;
    }

    /**
     * Compute height of peak for heart shape, given total heart height
     * 
     * @param heartHeight number of rows to use for entire heart
     * @return number of rows to use for upper peak section
     */
    private int getPeakHeightForHeart( int heartHeight )
    {
        return (int) Math.round( heartHeight * HEART_PEAK );
    }

    /**
     * Compute height of box for spade shape, given total spade height
     * 
     * @param spadeHeight number of rows to use for entire spade
     * @return number of rows to use for lower box section
     */
    private int getBoxHeightForSpade( int spadeHeight )
    {
        return (int) ( spadeHeight * SPADE_BOX );
    }
    
    //added
    /**
     * Compute width of box for spade shape, given total spade height
     * 
     * @param spadeHeight number of rows to use for entire spade
     * @return number of columns to use for lower box section
     */
    private int getBoxWidthForSpade(int spadeHeight)
    {
    	if(getBoxHeightForSpade(spadeHeight)>1){
    		return getBoxHeightForSpade(spadeHeight) +1;
    	}
    	
    	return getBoxHeightForSpade(spadeHeight);
    }

    /**
     * Compute height of triangle for heart shape, given total heart height
     * 
     * @param heartHeight number of rows to use for entire heart
     * @return number of rows to use for triangle section
     */
    private int getTriangleHeightForHeart( int heartHeight )
    {
        // TODO: fill in
    	return (heartHeight - getPeakHeightForHeart(heartHeight));
        // HINT: this computation is easy if you use the
        // getPeakHeightForHeart method as a helper!
    }

    /**
     * Compute height of heart for spade shape, given total spade height
     * 
     * @param spadeHeight number of rows to use for entire spade
     * @return number of rows to use for heart section
     */
    private int getHeartHeightForSpade( int spadeHeight )
    {
        // TODO: fill in
    	return (spadeHeight - getBoxHeightForSpade(spadeHeight));
        // HINT: this computation is easy if you use the
        // getBoxHeightForSpade method as a helper!
    }

    /**
     * Compute width of heart for spade shape, given total spade height
     * 
     * @param spadeHeight number of rows to use for entire spade
     * @return number of rows to use for heart section
     */   
    //added
    private int getHeartWidthForSpade( int spadeHeight){
    	return (getTriangleWidth(getTriangleHeightForHeart(getHeartHeightForSpade(spadeHeight))));
    }
    
    
    /**
     * Helper function for printing the peaks of a heart shape, one line at a
     * time; only partially implemented.
     * 
     * @param i loop counter for main loop in calling method
     * @param height number of rows to print out
     * @param width width of heart at widest point
     * @param margin number of white space rows to insert on the left
     */
    private void printHeartPeaksBody( int i, int height, int width, int margin )
    {
        int sideGap = i - 1;
        int peakGap = i;
        int peakWidth = width - sideGap - sideGap - peakGap;
        int eachPeakWidth = (int) ( peakWidth / 2.0 );
        if( ( sideGap + eachPeakWidth + peakGap + eachPeakWidth + sideGap ) < width )
        {
            peakGap++;
        }
        // TODO: fill in the rest of this function

        for (int a=0; a<(margin+sideGap); a++){
       		System.out.print(" ");
        }
   		for (int b=0; b<eachPeakWidth; b++){
   			System.out.print("*");
   		}
       	for (int c=0; c<peakGap; c++){
       		System.out.print(" ");
       	}
       	for (int d=0; d<eachPeakWidth; d++){
       		System.out.print("*");
      	}
       	System.out.println();
    }

    /**
     * Print out top part of heart with twin peaks, using stars and whitespace.
     * 
     * @param height number of rows to use
     * @param width number of columns to use at widest point
     * @param pointUp if true, heart printout has point facing upwards and peaks
     *        facing downwards (upside down heart); if false, heart printout has
     *        point facing downwards and peaks facing upwards (standard heart
     *        configuration).
     */
    private void printHeartPeaks( int height, int width, boolean pointUp )
    {
        int i;
         for( i = 1; i <= height; i++ )
         {
             int pointedIndex = i;

             // Upside-down shapes have their indexes flipped.
             if( pointUp )
                 pointedIndex = height - (pointedIndex - 1);

             printHeartPeaksBody( pointedIndex, height, width, 0 );
         }
    }

    /**
     * Print out rectangular box at the given horizontal offset, using stars and
     * whitespace.
     * 
     * @param height number of rows to use
     * @param width number of columns to use
     * @param margin number of white space rows to insert on the left
     */
    private void printBox( int height, int width, int margin )
    {
        // TODO: fill in
    	int i;
    	for (i=0; i<height;i++){
    		for (int l=0; l<margin; l++){
    			System.out.print(" ");
    		}
    		for (int j=0; j<width; j++){
    			System.out.print("*");
    		}
    		System.out.println();
    	}
    }

    /**
     * Print out triangle that is symmetric around vertical axis, using stars
     * and whitespace.
     * 
     * @param height number of rows to use
     * @param margin number of white space rows to insert on the left
     * @param pointUp if true, triangle point is on top and flat part is on
     *        bottom; if false, triangle point is facing down and flat part is
     *        on top.
     */
    private void printTriangle( int height, int margin, boolean pointUp )
    {
        // TODO: fill in
    	int i;
    	int stars;
    	int spaces;
       	
    	for (i=0; i<height; i++){
    		if ( pointUp ){
    			stars = 2*i +1;
    			spaces = (getTriangleWidth(height) - stars)/2;
    		}
    		else{
    			spaces = i;
    			stars = getTriangleWidth(height) - 2*(spaces);
    		}
        	for (int a=0; a<(margin+spaces); a++){
       			System.out.print(" ");
       		}
       		for (int b=0; b<stars; b++){
       			System.out.print("*");
       		}
       		System.out.println();
       	}
        
    }
        

    /**
     * Print out a heart, using stars and whitespace, either right side up or
     * upside down.
     * 
     * @param height number of rows to use
     * @param pointUp if true, heart printout has point facing upwards and peaks
     *        facing downwards (upside down heart); if false, heart printout has
     *        point facing downwards and peaks facing upwards (standard heart
     *        configuration).
     * @return width of heart
     */
    private int printHeartShape( int height, boolean pointUp )
    {
        // TODO: fill in
    	
    	if ( pointUp ){
    		printTriangle(getTriangleHeightForHeart(height), 0, true);
    		printHeartPeaks(getPeakHeightForHeart(height), getTriangleWidth(getTriangleHeightForHeart(height)), false);
    	}
    	else {
    		printHeartPeaks(getPeakHeightForHeart(height), getTriangleWidth(getTriangleHeightForHeart(height)), true);
    		printTriangle(getTriangleHeightForHeart(height), 0, false);

    	}
    	
    	
    	
        // HINT: use printHeartPeaks for the top of the heart. use
        // printTriangle for the bottom of the heart, a triangle that
        // points down.

        return 0; // incorrect placeholder, just here so code compiles
    }

    /**
     * Create a new ASCIIArtiste object.
     */
    public ASCIIArtiste()
    {
    }

    /**
     * Print out a diamond using rows of stars and whitespace.
     * 
     * @param height number of rows to use
     */
    public void printDiamond( int height )
    {
        // TODO: fill in
    	
    	printTriangle((int)Math.ceil(height/2.0), 0, true);
    	printTriangle((int)Math.floor(height/2.0), 1, false);
    	
        // HINT: use printTriangle twice, top triangle should point up
        // and bottom triangle should point down

    }

    /**
     * Print out a heart using rows of stars and whitespace.
     * 
     * @param height number of rows to use
     */
    public void printHeart( int height )
    {
        printHeartShape( height, false );
    }

    /**
     * Print out a spade using rows of stars and whitespace.
     * 
     * @param height number of rows to use
     */
    public void printSpade( int height )
    {
        // TODO: fill in
    	
    	printHeartShape(getHeartHeightForSpade(height), true);
    	printBox(getBoxHeightForSpade(height), getBoxWidthForSpade(height), (getHeartWidthForSpade(height)-getBoxWidthForSpade(height))/2);
    	
        // HINT: use printHeartShape with the pointUp argument set to
        // true for the top part of the spade. use printBox for the
        // bottom part.

    }

    /**
     * Theoretically, print out a club using rows of stars and whitespace; right
     * now just prints out a simple box.
     * 
     * @param height number of rows to use
     */
    public void printClub( int height )
    {

        // OPTIONAL: improve this to print out something that's a
        // closer approximation to a club shape, for a small amount of
        // extra credit. Note that you do not need to do this to get
        // full credit on the assignment!

        System.out
                .println( "Um... I don't know how to draw clubs, so I'll fake it!" );
        printBox( height, height, 1 );
    }

    /**
     * Driver for ASCIIArtiste class.
     * 
     * Start by just using this as a test driver, ensuring that each
     * simple private piece of your program works correctly. 
     * Eventually however, you'll want to replace the entire body of
     * the main method with code to implement the Artiste Interface in
     * the specification. When you do change this to be the code for
     * the Artiste Interface, ensure that you call only public methods
     * of the ASCIIArtiste class. You can make that easier to do, if
     * you prefer, by writing the main method in another file named,
     * for example, CardMaker.java. If you use that solution, be sure
     * to submit the second file both physically and electronically
     * and state clearly in your README.txt file what you have done.
     *
     */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		ASCIIArtiste artiste = new ASCIIArtiste();

		System.out.println("Welcome to ASCIIArtiste. Hope you have fun!");
		System.out
				.println("__________________________________________________________________");
		System.out.println();

		String quit = "";
		while (!quit.equalsIgnoreCase("no")) {
			System.out.println("What size of card do you want? ");
			int size = scan.nextInt();

			if (size < 0) {
				System.out.println("Size was too small (" + size + ").");
				size = 5;
				System.out.println("I'll use " + size + " instead.");
			}

			System.out
					.print("What card suit do you want?(diamond, spade, club or heart)");
			String suit = scan.next();

			if (suit.equalsIgnoreCase("diamond")) {
				artiste.printDiamond(size);
			} else if (suit.equalsIgnoreCase("spade")) {
				artiste.printSpade(size);
			} else if (suit.equalsIgnoreCase("heart")) {
				artiste.printHeart(size);
			} else if (suit.equalsIgnoreCase("club")) {
				artiste.printClub(size);
			} else {
				System.out
						.println("Does have this card, like it or not, i will use heart");
				artiste.printHeart(size);
			}

			System.out.println("Play again? (enter \"no\" to quit)");
			quit = scan.next();
		}
	}
}