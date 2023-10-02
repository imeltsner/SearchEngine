package edu.usfca.cs272;

public class SearchResult implements Comparable<SearchResult> {
    /** The words in a search query stored in a single string */
    private final String query;

    /** The location containing a query word */
    private final String location;

    /** The total query words found at the location */
    private int count;

    /** The total words at a location */
    private int totalWords;

    /** The score defined by count / total words */
    private double score;

    /**
     * Class constructor
     * @param query the search query
     */
    public SearchResult(String query, String location) {
        this.query = query;
        this.location = location;
        this.count = 0;
        this.totalWords = 0;
        this.score = 0;
    }

    /**
     * Adds number of words matched to the total query words matched
     * @param numWords the number of words matching a query word
     */
    public void addWordsFound(int numWords) {
        count += numWords;
    }

    /**
     * Adds the total words in a location
     * @param numWords the number of words in a location
     */
    public void addTotalWords(int numWords) {
        totalWords += numWords;
    }

    /**
     * Calculates the score defined by count / total words
     */
    public void calculateScore() {
        score = (double) count / (double) totalWords;
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
     * Returns the query line as a single string
     * @return the query line
     */
    public String getQuery() {
        return query;
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
        if (this.score != other.score) {
            return Double.compare(other.score, this.score);
        }
        else if (this.count != other.count) {
            return Integer.compare(other.count, this.count);
        }
        else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.location, other.getLocation());
        }
    }

    @Override
    public String toString() {
        String q = this.query.toUpperCase() + "\n";
        String w = "where: " + this.location + "\n";
        String c = "count: " + count + "\n";
        String s = "score: " + String.format("%.8f", score) + "\n";
        return q + c + s + w;
    }
}
