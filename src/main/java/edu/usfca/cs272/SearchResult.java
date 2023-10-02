package edu.usfca.cs272;

public class SearchResult implements Comparable<SearchResult> {
    /** The words in a search query stored in a single string */
    private final String query;

    /** The location containing a query word */
    private final String location;

    /** The total query words found at the location */
    private int wordsFound;

    /** The total words at a location */
    private int totalWords;

    /**
     * Class constructor
     * @param query the search query
     */
    public SearchResult(String query, String location) {
        this.query = query;
        this.location = location;
        this.wordsFound = 0;
        this.totalWords = 0;
    }

    /**
     * Adds value to the total query words matched
     * @param numWords the number of words found matching a query word
     */
    public void addWordsFound(int numWords) {
        wordsFound += numWords;
    }

    /**
     * Adds the total words in a location
     * @param numWords the number of words in a location
     */
    public void addTotalWords(int numWords) {
        totalWords += numWords;
    } 

    @Override
    public int compareTo(SearchResult o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }
    
}
