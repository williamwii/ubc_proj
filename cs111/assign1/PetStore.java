
import java.util.Scanner;
import java.text.DecimalFormat;

/* PetStore.java
 * @author Tamara Munzner
 *
 * Calculate the food requirements for a week at a pet store.
 *
 * Specification:
 *
 * Cats eat .33 cups of cat chow each day.
 * Dogs eat .75 cups of dog chow each day.
 * Snakes eat 1 mouse each week. 
 * The owner must pay 5% GST for every purchase. 
 * There is 7% PST charged on cat food and dog food, but the mice to feed the snakes are not taxed by the province.
 * Dog and cat chow is $40 per bag, and there are 32 cups in a bag.
 * Mice cost 75 cents each.
 *
 * Note: the code below contains both syntax and logic errors.
 */

public class PetStore
{
     public static void main( String[] args )
     {

       // Some constant values that never change from run to run:

         final double PST = 0.07; // The PST tax rate.
         final double GST = 0.05; // The GST tax rate.

         final int DAYS_PER_WEEK = 7; //  A week has 7 days.

         final double CAT_PORTION_SIZE = .33; 
         //Cats get 1/3 cup    of food each day.
         
         final double DOG_PORTION_SIZE = .75; 
         // Dogs get 3/4 cup of food each day.

         final double SNAKE_PORTION_SIZE = 1; 
         // Snakes get 1 mouse each week.

         final double CHOW_PRICE = 40.00; 
         // Cat and dog chow is $40 per bag.
         
         final int CUPS_PER_BAG = 32; 
         // A chow bag contains 32 cups of food.
         
         final double MOUSE_PRICE = .75; 
         // Mice cost 75 cents each.

         // A formatter to help with output.
         DecimalFormat currencyFormatter = new DecimalFormat( "$0.00" );

         // Construct an object to read input.
         Scanner scan = new Scanner( System.in );

         // Get counts of animals in the store now from the user.
         System.out.print( "How many cats do you have? " );
         int cats = scan.nextInt();

         System.out.print( "How many dogs do you have? " );
         int dogs = scan.nextInt();

         System.out.print( "How many snakes do you have? " );
         int snakes = scan.nextInt();

         System.out.println();

         // Now, do calculations. 

         // How many cups of cat chow do we need?
         double catChow = cats * CAT_PORTION_SIZE;

         // How many cups of dog chow do we need?
         double dogChow = dogs * DOG_PORTION_SIZE;

         // How many mice do we need?
         int mice = (int) (snakes * SNAKE_PORTION_SIZE);

        // Calculate number of bags of catchow needed for 1 week. 
	 double catChowBags = Math.ceil (catChow * DAYS_PER_WEEK / CUPS_PER_BAG);

        // Calculate number of bags of dogchow needed for 1 week. 
	 double dogChowBags = Math.ceil (dogChow * DAYS_PER_WEEK / CUPS_PER_BAG);

	 // Calculate cost of cat and dog chow.
         double mammalChowCost = (catChowBags + dogChowBags) * CHOW_PRICE * (1.0 + GST + PST);

	 // Calculate cost of snake food.
	 double snakeFoodCost = mice * MOUSE_PRICE * (1.0 + GST);

	 // Calculate total food cost.
	 double totalCost = mammalChowCost + snakeFoodCost;

    // Inform the user of how much to order and what it will cost
         System.out.println("Order " + catChowBags + " bags of cat chow and " + dogChowBags + " bags of dog chow and " + mice + " mice");

	 System.out.println("It will cost: "  + currencyFormatter.format(totalCost));
     }
}
