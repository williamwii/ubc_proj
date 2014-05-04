/**
 * Helper class for QuestionAnswerer.
 * <p>
 * A StringTiler "tiles" a string: decomposing it into longer and longer
 * sequences of words. For example, the tiles in the string "actually mauna kea
 * in hawaii" are "actually", "actually mauna", "actually mauna kea", "actually
 * mauna kea in", and "actually mauna kea in hawaii".
 * <p>
 * Note that the use of a TextCleaner makes the StringTiler MUCH simpler to
 * implement. Once the input text is cleaned, each word is separated from the
 * next by exactly one space character ' '.
 * <p>
 * THIS VERSION IS JUST A STUB (for use in testing QuestionAnswerer.java or as a
 * starting point for writing StringTiler.java.)
 */
public class StringTiler
{
    /**
     * A TextCleaner to use for putting input text into a simple, canonical
     * form.
     */
    private static final TextCleaner ourCleaner = new TextCleaner();

    /**
     * The text to operate on.
     */
    private String myText;

    /**
     * Create a StringTiler that uses as its text everything AFTER the given
     * targetText within the given originalText.
     * <p>
     * If the target text is not found, the StringTiler uses the empty string as
     * its text (and therefore returns no tiles). If the target text IS found,
     * the StringTiler uses everything in the original text after the target
     * text as its text.
     * <p>
     * For example, if the text is "question anyway the tallest mountain in the
     * world is mount everest in the himalayas" and the targetText is "the
     * tallest mountain in the world is", the StringTiler would take as its text
     * "mount everest in the himalayas". (Note that any space after the target
     * text should be dropped; recleaning with the text cleaner will accomplish
     * this.)
     * <p>
     * (Note that both text parameters are run through a TextCleaner before
     * use.)
     * 
     * @param originalText the entire text to look through (non-null)
     * @param targetText the targetText after which to start building tiles
     *        (non-null)
     */
    public StringTiler( String originalText, String targetText )
    {
        targetText = ourCleaner.clean( targetText );
        originalText = ourCleaner.clean( originalText );

        // TODO: complete this method
        // This currently just uses the empty string as the tiler's text. You'll
        // need to find the appropriate text inside originalText to use. Be sure
        // to handle the case where targetText does not appear inside
        // originalText!
        this.myText = "";
        
        if ( originalText.contains(targetText) ){
        	// index = index of the last index of target text appears in the original text
        	int index = originalText.lastIndexOf(targetText);
        	while ( (index)<originalText.length() ){
        		this.myText += originalText.charAt(index);
        		index++;
        	}
        }

        //clean up the new text
        myText = ourCleaner.clean( myText );
    }

    /**
     * Create a StringTiler that uses as its text the given text. (Note that the
     * fullText parameter is cleaned before use.)
     * 
     * @param fullText the text to tile
     */
    public StringTiler( String fullText )
    {
        this.myText = ourCleaner.clean( fullText );
    }

    /**
     * Get the number of tiles (equal to the number of words) in this tiler's
     * text.
     * <p>
     * Hints: First, handle the empty string as a separate case. It has zero
     * tiles in it. Second, try calculating by hand the difference between the
     * lengths of "actually mauna kea" and "actually mauna kea" with all of its
     * spaces removed. How does that value relate to the number of words in the
     * string? Does your formula work for text with just one word? With five
     * words? With any number of words?
     * 
     * @return the number of tiles in the text
     */
    public int getNumTiles()
    {
        // If there's no text, there are no tiles.
        // Return 0 immediately.
        if( this.myText == null || this.myText.equals( "" ) )
            return 0;
		
        //case where text is found
        //number of words equals to number of space plus one
        int spaceCount = 0;
		for (int i=0; i<myText.length(); i++){
			if ( Character.isWhitespace(myText.charAt(i)) )
				spaceCount++;
		}
		return (spaceCount+1);

    }

    /**
     * Construct a tile with the given number of words in it. For example, if
     * the text of this tiler is "actually mauna kea in hawaii", the tile of
     * length 3 is "actually mauna kea".
     * <p>
     * Hint: First, handle a request for a tile whose length is the same as
     * getNumTiles specially (it's the whole text!). Second, if the tile has 2
     * words, it must stretch from the start of the string past the first space
     * character (which separates the first two words) and up to the second
     * space character (which separates the second and third words). Can you use
     * a loop to find the right space at which to cut off the tile?
     * 
     * @param wordLength the length (in words) of the tile to construct (must be
     *        between 1 and getNumTiles).
     * @return a tile of the given length
     */
    public String getTileOfWordLength( int wordLength )
    {
        // TODO: complete this method.
        // This currently just returns the entire text. You'll need to make it
        // return the appropriate tile.
        //
        // Actually, if you're testing, a good first step is to return just the
        // first tile (that is, the first word in the text).
    	
    	if ( wordLength==getNumTiles() )
    		return this.myText;
    	
    	//if wordLength is not equal to the total number of word
    	int i=0;
    	int wordCount = 0;
    	while ( (wordCount<wordLength)&&(i<myText.length()) ){
    		if ( Character.isWhitespace(myText.charAt(i)) ){
    			wordCount++;
    			i++;
    		}
    		else
    			i++;
    	}
    	int indexLastSpace = i-1;
    	String tileOfWordLength = myText.substring(0, indexLastSpace);
    	return tileOfWordLength;
    }
}