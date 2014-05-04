import java.util.Scanner;
/**
 * The professional magician reads the "aether" to answer the user's questions.
 * <p>
 * In fact, the program uses some tricky Google searches to try to determine an
 * anwser to the user's question without ever actually understanding the
 * question.
 * <p>
 * First, the magician determines the type of question the user wants to ask:
 * "what is", "who is", "what was", or "who was". The important part of the
 * question is its key word: "is" for "what is" and "who is" and "was" for "what
 * was" and "who was".
 * <p>
 * Next, the magician obtains the remainder of the user's question. For example,
 * for "What is the tallest mountain in the world?", the magician works with the
 * phrase "the tallest mountain in the world".
 * <p>
 * Third, the magician searches on Google for the phrase with the key word
 * tacked onto the end. The magician uses phrase search so that the search will
 * find those exact words. For our example, the search is for "the tallest
 * mountain in the world is". Note that if you search on Google for "the tallest
 * mountain in the world is" WITH the quotes, it's a phrase search, and Google
 * will only return pages that include all of those words in the specified order
 * together. If you search for the same phrase WITHOUT the quotes, it's a word
 * search, and Google will return any page that contains all the words,
 * regardless of their order. (In fact, without the quotes, Google will ignore
 * common words that it calls "stop" words such as "is" and "the".)
 * <p>
 * Spend a moment right now figuring out how to create a string in Java that
 * looks like "the tallest mountain in the world is" WITH the quotes. Hint: just
 * "the tallest mountain in the world is" creates the string WITHOUT quotes. How
 * do you put quotation marks into a string?
 * <p>
 * Fourth, the magician sifts through the results that come back from Google,
 * investigating "candidate" responses. If our example brought back the results
 * "the tallest mountain in the world is actually mauna kea in hawaii..." and
 * "...question anyway the tallest mountain in the world is mount everest in the
 * himalayas...", the candidates would be groups of words following the phrase
 * sought: "the tallest mountain in the world". In this case: "actually",
 * "actually mauna", "actually mauna kea", "mount", "mount everest", "mount
 * everest in", and so forth. In other words, find the phrase sought and then
 * pull out longer and longer word sequences from the string immediately AFTER
 * the phrase. (Note: in your implementation, this step will likely overlap step
 * five and six below.)
 * <p>
 * Fifth, the magician tries to "verify" each candidate by searching for it
 * together with the key word and the original phrase, again as a "phrase
 * search" so that all words in exactly that order together are found. For our
 * example, the searches would be: "actually is the tallest mountain in the
 * world", "actually mauna is the tallest mountain in the world", "actually
 * mauna kea is the tallest mountain in the world", "mount is the tallest
 * mountain in the world", "mount everest is the tallest mountain in the world",
 * "mount everest in is the tallest mountain in the world", and so forth, all
 * WITH the quotes.
 * <p>
 * Sixth, the magician picks the candidate that had the largest number of hits
 * in its verification search and reports it as the answer. In our example, that
 * would be "mount everest" with more than a hundred hits (although "actually
 * mauna kea" will probably receive a small number of hits as well).
 */
public class ProfessionalMagician
{
    /**
     * Prints the opening banner.
     */
    private static void printBanner()
    {
        System.out
                .println( "Welcome to the Professional Magician's Crystal Ball!" );
        System.out
                .println( "----------------------------------------------------" );
        System.out.println();
    }

    /**
     * Explains to the user how to select a question type.
     */
    private static void printInstructions()
    {
        System.out
                .println( "I am ready to read the aether in my crystal ball and answer your question." );
        System.out.println( "I can answer questions of the following types:" );
        System.out.println();
        System.out.println( "\t1) What is..." );
        System.out.println( "\t2) Who is..." );
        System.out.println( "\t3) What was..." );
        System.out.println( "\t4) Who was..." );
        System.out.println();
        System.out
                .print( "Enter the number corresponding to your question or 0 to quit: " );
    }

    /**
     * Reads a question type from the user.
     * <p>
     * Keeps prompting the user for a response until the user supplies a valid
     * response.
     * <p>
     * There should be no input the user can provide that will cause an error in
     * this method, even if the user provides letters rather than numbers.
     * Instead, the method should explain any problems with the user's input and
     * re-prompt for proper input until it gets an appropriate number.
     * <p>
     * Note: the question types are hard-coded. 0 = quit. 1 = What is. 2 = Who
     * is. 3 = What was. 4 = Who was. If we had arrays, we could make a more
     * flexible solution.
     * <p>
     * Hint: can you somehow use the Scanner methods hasNextInt, nextInt, and
     * next to ensure that you get valid input?
     * 
     * @return a number corresponding to the question type to use, or 0 if the
     *         user wants to quit.
     */
    private static int getQuestionType()
    {
        // TODO: complete this method!
        // Currently, this method is a "stub" that just returns the code for a
        // "What is" style question.
    	int questionType = -1;
    	
    	while ( (questionType>4)||(questionType<0) ){
            Scanner scan = new Scanner(System.in);
    		if ( scan.hasNextInt() ){
        		questionType = scan.nextInt();
        		if ( (questionType>4)||(questionType<0) )
        			System.out.println("Error with question type, please enter again.");
        		
        	}
        	else
        		System.out.println("Error with question type, please enter again.");
        }
        return questionType;
    }

    /**
     * Repeatedly ask the user for a question to answer and answer the question.
     */
	public static void main(String[] args) {
		// TODO: complete this method!
		// This is currently a stub that just "tries out" each of the methods
		// above.
		Scanner scan = new Scanner(System.in);
		printBanner();

		int questionType = -1;
		String keyWord = "";

		while (questionType != 0) {
			printInstructions();
			questionType = getQuestionType();

			if (questionType != 0) {
				System.out.println();
				System.out.println("Please complete the question: ");

				if (questionType == 1) {
					keyWord = "is";
					System.out.println("What is ");
				} else if (questionType == 2) {
					keyWord = "is";
					System.out.println("Who is ");
				} else if (questionType == 3) {
					keyWord = "was";
					System.out.println("What was ");
				} else if (questionType == 4) {
					keyWord = "was";
					System.out.println("Who was ");
				}
				QuestionAnswerer myAnswerer = new QuestionAnswerer(keyWord);
				String query = scan.nextLine();
				myAnswerer.answer(query);

				//if the query is "what was the bloodiest battle in the civil war" there will be no good result in Google
				//but "what was the bloodiest battle of the civil war" will work
				
				if (myAnswerer.answer(query).contains(
						"I cannot scry the answer to your query about ")) {
					System.out.println(myAnswerer.answer(query));
					System.out.println();
				} else {
					System.out.println("I have scried your answer: " + query
							+ " " + myAnswerer.getKeyWord() + " "
							+ myAnswerer.answer(query));
					System.out.println();
				}
			} else {
				System.out.println();
				System.out.println("Goodbye!");
			}
		}
	}
}
