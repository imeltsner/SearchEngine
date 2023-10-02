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

    /**
     * Class constructor
     * @param query the search query
     */
    public SearchResult(String query, String location) {
        this.query = query;
        this.location = location;
        this.count = 0;
        this.totalWords = 0;
    }

    /**
     * Adds value to the total query words matched
     * @param numWords the number of words found matching a query word
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
     * Returns the score defined by words found divided by total words
     * @return the score to return
     */
    public double getScore() {
        return count / totalWords;
    }

    public int getCount() {
        return count;
    }

    public String getQuery() {
        return query;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int compareTo(SearchResult other) {
        if (this.getScore() != other.getScore()) {
            return Double.compare(this.getScore(), other.getScore());
        }
        else if (this.count != other.count) {
            return Integer.compare(this.count, other.count);
        }
        else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.location, other.getLocation());
        }
    }

    @Override
    public String toString() {
        String q = "Search query: " + this.query + "\n";
        String l = "Location: " + this.location + "\n";
        String c = "Count: " + count + "\n";
        String t = "Total words: " + totalWords + "\n";
        String s = "Score: " + getScore() + "\n";
        return q + l + c + t + s;
    }
}
