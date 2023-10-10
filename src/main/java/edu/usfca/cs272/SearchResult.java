package edu.usfca.cs272;

/**
 * A class to store search result metadata
 * @author Isaac Meltsner
 */
public class SearchResult implements Comparable<SearchResult> { // TODO Make this a inner class inside of inverted index 
    /** The location containing a query word */
    private final String location;
    
    /** The total words at a location */
    private final int totalWords;

    /** The total query words found at the location */
    private int count;

    /** The score defined by count / total words */
    private double score;

    /**
     * Class constructor
     * @param location the location of the search
     * @param totalWords the total words found at the location
     */
    public SearchResult(String location, int totalWords) {
        this.location = location;
        this.totalWords = totalWords;
        this.count = 0;
        this.score = 0;
    }

    /**
     * Increments match count and calculates score
     * @param matches number of matches found
     */
    public void calculateScore(int matches) {
        this.count += matches;
        this.score = (double) this.count / (double) this.totalWords;
    }

    /**
     * Returns number of query words matched
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the score defined by count / total words
     * @return the score 
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the location associated with the search result
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    @Override
    public int compareTo(SearchResult other) {
        int scoreComapre = Double.compare(other.score, this.score);
        int countCompare = Integer.compare(other.count, this.count);
        int stringCompare = String.CASE_INSENSITIVE_ORDER.compare(this.location, other.location);

        return scoreComapre != 0 ? scoreComapre : countCompare != 0 ? countCompare : stringCompare;
    }

    @Override
    public String toString() {
        String w = "where: " + this.location + "\n";
        String c = "count: " + count + "\n";
        String s = "score: " + String.format("%.8f", score) + "\n";
        return c + s + w;
    }
}
