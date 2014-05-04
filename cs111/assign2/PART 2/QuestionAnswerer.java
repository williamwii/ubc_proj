import java.util.Scanner;
import edu.ubc.cpsc.googlex.SimpleGoogleSearch;
/**
 * A question answerer is capable of answering queries (using the strategy
 * outlined in the ProfessionalMagician class's notes) of a specified type, such
 * as queries using the key word "is" ("who is" or "what is" queries).
 * <p>
 * THIS VERSION IS JUST A STUB (for use in testing ProfessionalMagician.java or
 * as a starting point for writing QuestionAnswerer.java.)
 */
public class QuestionAnswerer
{
    /**
     * The Google search key to use when accessing Google.
     * <p>
     * You should initialize this constant with the Google search key provided
     * to you!
     */
    private static final String KEY = "b2CVx0ZQFHJlbvQmy/qspJHAene+nCqu";

    /**
     * The maximum number of snippets from the list of Google search results to
     * consider in answering a query.
     * <p>
     * Setting this to a small number can reduce the time your program takes,
     * keep you under the search limit on your search key, and ease debugging.
     */
    public static final int MAX_SNIPPETS_TO_SEARCH = 4;

    /**
     * The maximum number of tiles to form from a given search result. For
     * example, if the query is "the tallest mountain in the world" and the
     * search result is "question anyway the tallest mountain in the world is
     * mount everest in the himalayas", the candidate tiles are "mount", "mount
     * everest", "mount everest in", "mount everest in the", and "mount everest
     * in the himalayas". If the maximum number of tiles to form is 3, the
     * QuestionAnswerer would stop considering tiles after "mount everest in".
     * <p>
     * Setting this to a small number can reduce the time your program takes,
     * keep you under the search limit on your search key, and ease debugging.
     */
    public static final int MAX_TILES_TO_FORM = 3;

    /**
     * the key word of searching pass in by user
     */
    private String keyWord;
    
    /**
     * A TextCleaner to use for putting input text into a simple, canonical
     * form.
     */
    private static final TextCleaner ourCleaner = new TextCleaner();
    
    /**
     * Construct a QuestionAnswerer that focuses on the given key word.
     * 
     * @param keyWord the word to use after search phrases initially and before
     *        them in verification (non-null)
     */
    
    
    public QuestionAnswerer( String keyWord )
    {
        // TODO
    	this.keyWord = keyWord;
    }

    /**
     * Get the key word associated with this QuestionAnswerer.
     * 
     * @return the key word
     */
    public String getKeyWord()
    {
        // TODO
        return keyWord;
    }
    
    /**
     * Build the verification query that tests whether the given answer goes well with the given query. 
     * If the answer were "actually mauna kea", the query were "the tallest mountain in the world", 
     * and this object's key word were "is", the query would be a phrase search for "actually 
     * mauna kea is the tallest mountain in the world". 
     * 
     * @param answer candidate answer of query
     * @param query query from the user
     * @return     the full phrase search (including the surrounding quotation marks)
     */
    private String buildVerificationQuery ( String answer, String query ){
    	
    	String fullPhrase = "\"" + answer + " " + keyWord +" " + query + "\"";
    	return fullPhrase;
    	
    }

    /**
     * Answer the given query from the user. If no answer can be found, this
     * method returns null to signal the failure.
     * 
     * @param queryWithKeyWord the user's query (non-null)
     * @return the best answer to the user's query OR null if no answer can be
     *         found
     */
	public String answer(String query) {
		// TODO
		// This method is just a stub that returns the query given.
		// Notice that you can test a COMPLETE version of
		// ProfessionalMagician.java using this stub of QuestionAnswerer.java,
		// without using either of the other two classes you have to write for
		// this assignment and without using SimpleGoogleSearch.
		//
		// To really thoroughly test ProfessionalMagician.java, try changing
		// this stub so that it can return null. Here's some example code that
		// lets you do that flexibly:

		query = ourCleaner.clean(query);
		SimpleGoogleSearch search = new SimpleGoogleSearch();
		String stringMostHitCount = "";
		int mostHitCount = -1;
		String queryWithKeyWord = query + " " + keyWord;

		// searching without key word
		if (search.doSearch("\"" + query + "\"")) {
			if (search.getNumResults() > MAX_SNIPPETS_TO_SEARCH) {
				for (int i = 0; i < search.getNumResults(); i++) {

					String cleanedSnippet = ourCleaner.clean(search
							.getSnippetAt(i));

					// tempNum: index of phrase after the snippet
					int tempNum = query.length()
							+ cleanedSnippet.indexOf(query);
					String tempString = cleanedSnippet.substring(tempNum);

					if (i < MAX_SNIPPETS_TO_SEARCH) {
						if (tempNum != cleanedSnippet.length()) {
							StringTiler searchResults = new StringTiler(
									cleanedSnippet, tempString);

							for (int wordLength = 1; wordLength <= MAX_TILES_TO_FORM; wordLength++) {
								SimpleGoogleSearch searchAgain = new SimpleGoogleSearch();
								String verificationQuery = buildVerificationQuery(
										searchResults
												.getTileOfWordLength(wordLength),
										query);

								if (searchAgain.doSearch(verificationQuery)) {

									if (searchAgain.getHitCount() > mostHitCount) {
										mostHitCount = searchAgain
												.getHitCount();
										stringMostHitCount = searchResults
												.getTileOfWordLength(wordLength);
									}
								}
							}
						} else
							i++;
					} else
						i = search.getNumResults();
				}

			} else
				stringMostHitCount = "I cannot scry the answer to your query about "
						+ query;
		}

		// searching with key word
		if (search.doSearch("\"" + queryWithKeyWord + "\"")) {
			if (search.getNumResults() > MAX_SNIPPETS_TO_SEARCH) {
				for (int i = 0; i < search.getNumResults(); i++) {

					String cleanedSnippet = ourCleaner.clean(search
							.getSnippetAt(i));

					// tempNum: index of phrase after the snippet
					int tempNum = queryWithKeyWord.length()
							+ cleanedSnippet.indexOf(query);
					String tempString = cleanedSnippet.substring(tempNum);

					if (i < MAX_SNIPPETS_TO_SEARCH) {
						if (tempNum != cleanedSnippet.length()) {
							StringTiler searchResults = new StringTiler(
									cleanedSnippet, tempString);

							for (int wordLength = 1; wordLength <= MAX_TILES_TO_FORM; wordLength++) {
								SimpleGoogleSearch searchAgain = new SimpleGoogleSearch();
								String verificationQuery = buildVerificationQuery(
										searchResults
												.getTileOfWordLength(wordLength),
										query);

								if (searchAgain.doSearch(verificationQuery)) {

									if (searchAgain.getHitCount() > mostHitCount) {
										mostHitCount = searchAgain
												.getHitCount();
										stringMostHitCount = searchResults
												.getTileOfWordLength(wordLength);
									}
								}
							}
						} else
							i++;
					} else
						i = search.getNumResults();
				}
			}
		}

		return stringMostHitCount;
	}
}