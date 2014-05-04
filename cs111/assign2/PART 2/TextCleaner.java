/**
 * Cleans text into a "canonical" form, a simple form in which it's easy to make
 * comparisons among very differently formatted text. The key modifications are
 * to keep only certain "allowed" characters, change to lower case, remove HTML
 * tags, and change successive whitespace into a single space.
 * <p>
 * THIS VERSION IS JUST A STUB (for use in testing StringTiler.java and
 * QuestionAnswerer.java or as a starting point for writing TextCleaner.java.)
 * Note that this does actually include a correct implementation of the method
 * eliminateHTMLTags. You can use that as a starting point to understand how to
 * write collapseWhitespace and eliminateDisallowedCharacters. (Note that the
 * other methods are unlikely to work exactly the same way, however!)
 */
public class TextCleaner
{
    /**
     * The default list of letter, number, and whitespace characters to allow.
     * Used to initialize the cleaner in the default constructor.
     */
    public static final String DEFAULT_RETAIN_LIST = "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + " \t\n";
    
    /**
     * retainList create by the user
     */
    public String retainList;
    
    
    /**
     * Create the default text cleaner, which retains letter, number, and
     * whitespace characters.
     */
    public TextCleaner()
    {
        this( DEFAULT_RETAIN_LIST );
    }

    /**
     * Create a text cleaner with the given retention list (of characters to
     * retain on calls to eliminateDisallowedCharacters).
     * 
     * @param retainList a list of characters to retain
     */
    public TextCleaner( String retainList )
    {
        // TODO
    	this.retainList = retainList;
    }

    /**
     * Convert the given text to lower case.
     * 
     * @param text the text to convert (non-null)
     * @return the text, in lower-case
     */
    public static String convertToLowercase( String text )
    {
        // TODO
    	text = text.toLowerCase();
        return text;
    }

    /**
     * Collapse consecutive whitespace characters in the given text to a single
     * space. Also removes "dangling" whitespace on either end of the text. (See
     * the String method named trim.)
     * <p>
     * Hint: build a new string up character-by-character from the old text.
     * Just add each non-whitespace character of text (see the isWhitespace
     * method of Character). Also, keep track of whether the last character was
     * whitespace or not. Skip all the whitespace, BUT when you see a
     * non-whitespace character immediately after a whitespace character, add in
     * an extra blank as the new word separator.
     * 
     * @param text the text to convert (non-null)
     * @return editedText, with whitespace collapsed
     */
    public static String collapseWhitespace( String text )
    {
        // TODO
    	String editedText = "";
    	for (int i=0; i<text.length(); i++){
    		if(i==0){
    			if(!Character.isWhitespace(text.charAt(i))){
    				editedText += text.charAt(i);
    			}
    		}
    		else{
    			if ( !Character.isWhitespace(text.charAt(i)) ){
    				if ( Character.isWhitespace(text.charAt(i-1)) ){
    					editedText += " " + text.charAt(i);
    				}
    				else{
    					editedText += text.charAt(i);
    				}
    			}
    		}
    	}
    	editedText = editedText.trim();
        return editedText;
    }

    /**
     * Eliminate HTML tags from the given text. That is, remove everything
     * between each pair of left and right angle brackets, including the
     * brackets.
     * <p>
     * Hint: build up a new string character-by-character from the old text.
     * Keep track of whether you're currently between a left and right angle
     * bracket. If you are, just don't add any characters at all.
     * 
     * @param text the text to convert (non-null)
     * @return the text, without HTML tags
     */
    public static String eliminateHTMLTags( String text )
    {
        String newText = "";
        boolean inHTMLTag = false;
        for( int i = 0; i < text.length(); i++ )
        {
            if( text.charAt( i ) == '<' )
            {
                inHTMLTag = true;
            }
            else if( text.charAt( i ) == '>' )
            {
                inHTMLTag = false;
            }
            else if( !inHTMLTag )
            {
                newText += text.charAt( i );
            }
        }
        return newText;
    }

    /**
     * Eliminate all the characters except those in the supplied retention list
     * from the string.
     * <p>
     * Hint: try building up a new string starting with the empty string, adding
     * to it each character of text that is in the retain list (and NOT adding
     * the characters outside the retain list).
     * 
     * @param text the text to convert (non-null)
     * @param retainList the list of characters to retain (non-null)
     * @return the text, with disallowed characters eliminated
     */
    public static String eliminateDisallowedCharacters( String text,
            String retainList )
    {
        // TODO
    	String newText ="";
    	for (int i=0; i<text.length(); i++){
    		for (int j=0; j<retainList.length(); j++){
    			if (text.charAt(i) == retainList.charAt(j))
    				newText += text.charAt(i);
    		}
    	}
        return newText;
    }

    /**
     * Eliminate all the characters except those in the list getRetainList from
     * the string.
     * 
     * @param text the text to convert (non-null)
     * @return the text, with disallowed characters eliminated
     */
    public String eliminateDisallowedCharacters( String text )
    {
        // TODO
        // Hint: can you implement this method in terms of one of the other
        // methods?
    	text = eliminateDisallowedCharacters(text, getRetainList());
        return text;
    }

    /**
     * Clean the given text by performing all TextCleaner transformations on it
     * (with whitespace collapse happening last and character elimination
     * happening second-to-last).
     * 
     * @param text the text to clean (non-null)
     * @return the text, cleaned
     */
    public String clean( String text )
    {
        // TODO
        // Hint: this method should be VERY easy to write (in terms of other
        // methods).
    	text = eliminateHTMLTags(text);
    	text = convertToLowercase(text);
    	text = eliminateDisallowedCharacters(text);
    	text = collapseWhitespace(text);
        return text;
    }

    /**
     * Get the list of characters to retain on calls to
     * eliminateDisallowedCharacters
     * 
     * @return the list of characters to retain
     */
    public String getRetainList()
    {
        // TODO
        return retainList;
    }

    /**
     * Set the list of characters to retain on calls to
     * eliminateDisallowedCharacters
     * 
     * @param list the list of characters to retain (non-null)
     */
    public void setRetainList( String list )
    {
        // TODO
    	retainList = list;
    }
}