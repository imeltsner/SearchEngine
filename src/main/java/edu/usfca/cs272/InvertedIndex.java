package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** 
 * A class to store an inverted index and 
 * map of wordcounts
 * 
 * @author Isaac Meltsner
 */
public class InvertedIndex {
    /** Stores filenames and wordcounts */
    private final TreeMap<String, Integer> wordCounts;

    /** Stores words, locations, and word positions in locations */
    private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

    /** Class constructor to initialize map and inverted index */
    public InvertedIndex() {
        wordCounts = new TreeMap<>();
        invertedIndex = new TreeMap<>();
    }

    /**
     * Returns of view of the map containing file names and word counts
     * @return an unmodifiable map of the file names and word counts
     */
    public Map<String, Integer> viewCounts() {
        return Collections.unmodifiableMap(wordCounts);
    }
    
    /**
     * Returns a view of the words of the inverted index
     * @return an unmodifiable set of the words in the inverted index
     */
    public Set<String> viewWords() {
    	return Collections.unmodifiableSet(invertedIndex.keySet());
    }
    
    /**
     * Returns a view of the locations associated with a word in the inverted index
     * @param word the word in the inverted index
     * @return an unmodifiable set of the locations associated with a word or an empty set
     *          if the word is not found in the index
     * 
     * @see #hasWord(String)
     */
    public Set<String> viewLocations(String word) {
        return hasWord(word) ? Collections.unmodifiableSet(invertedIndex.get(word).keySet()) : Collections.emptySet();
    }

    /**
     * Returns a view of all positions where a word occured in a location
     * @param word the word in the location
     * @param location the location where the word is found
     * @return an unmodifiable set containing all the positions a word was found at a given location
     *          or an empty set if word is not in index or not found at location
     */
    public Set<Integer> viewPositions(String word, String location) {
        return hasLocation(word, location) ? Collections.unmodifiableSet(invertedIndex.get(word).get(location)) : Collections.emptySet();
    }

    /**
     * Gets a word count from the wordCounts map
     * @param location the file associated with the count
     * @return the number of words in a file or 0 if file not found
     */
    public int getCount(String location) {
        return wordCounts.getOrDefault(location, 0);
    }

    /**
     * Returns the number files in the map
     * @return the number of files in the map
     */
    public int numCounts() {
        return wordCounts.size();
    }

    /**
     * Returns the number of words in the inverted index
     * @return the number of words in the inverted index
     */
    public int numWords() {
        return invertedIndex.size();
    }

    /**
     * Returns the number of locations a word was found
     * @param word the word to check
     * @return the number of locations a word was found
     */
    public int numLocations(String word) {
        return viewLocations(word).size();
    }
    
    /**
     * Returns the number of occurences of a word at a location
     * @param word the word to check
     * @param location the location to check
     * @return the number of occurences of a word at a location
     */
    public int numPositions(String word, String location) {
        return viewPositions(word, location).size();
    }

    /**
     * Checks if wordCount map contains a given file name
     * @param location the file name to check for
     * @return true if file name is in map false otherwise
     */
    public boolean hasCount(String location) {
        return wordCounts.containsKey(location);
    }
    
    /**
     * Checks if a given word is stored in the inverted index
     * @param word the word to check
     * @return true if word is in inverted index false otherwise
     */
    public boolean hasWord(String word) {
        return viewWords().contains(word);
    }

    /**
     * Checks if a word was found in a location in the inverted index
     * @param word the word to check
     * @param location the location to check
     * @return true if the word was found in the location, false if the word
     * is not in the inverted index or not found in the location
     * 
     * @see #hasWord(String)
     */
    public boolean hasLocation(String word, String location) {
        return viewLocations(word).contains(location);
    }

    /**
     * Check if a word is found at a specified postion in a location in the inverted index
     * @param word the word to check
     * @param location the location to check
     * @param position the position of the word in the location
     * @return true if the word is in the position of the location, false if the the word is not in the index
     *          the location is not in the map, or the word is not found in the position
     * 
     * @see #hasLocation(String, String)
     */
    public boolean hasPosition(String word, String location, int position) {
        return viewPositions(word, location).contains(position);
    }

    /**
     * Adds words, filenames, and word position to the inverted index
     * @param word the word to add
     * @param location the location to add
     * @param position the position of the word
     */
    public void addData(String word, String location, int position) {
        invertedIndex.computeIfAbsent(word, w -> new TreeMap<>())
            .computeIfAbsent(location, p -> new TreeSet<>())
            .add(position);
        
        wordCounts.merge(location, position, (old, current) -> current);
    }
    
    /**
     * Adds a list of words to the inverted index starting at a given position
     * @param words the words to add
     * @param location the location of the words
     * @param start the starting position
     */
    public void addAll(List<String> words, String location, int start) {
        for (String word : words) {
            addData(word, location, start++);
        }
    }
    
    /**
     * Outputs contents of word count map in pretty JSON format
     * @param path destination for output
     * @throws IOException if IO error occurs
     */
    public void writeCounts(Path path) throws IOException {
        JsonWriter.writeObject(wordCounts, path);
    }

    /**
     * Outputs contents of inverted index in pretty JSON format
     * @param path destination for output
     * @throws IOException if IO error occurs
     */
    public void writeInvertedIndex(Path path) throws IOException {
    	JsonWriter.writeInvertedIndex(invertedIndex, path);
    }
    
    /**
     * A helper method to generate a list of search results
     * @param word the query word matching an inverted index entry
     * @param seenLocations a map of already created searc results
     * @param results the list of search results
     */
    private void generateResults(String word, HashMap<String, SearchResult> seenLocations, ArrayList<SearchResult> results) {
        var locations = invertedIndex.get(word).entrySet().iterator();

        while (locations.hasNext()) {

            Entry<String, TreeSet<Integer>> location = locations.next();
            SearchResult visited = seenLocations.get(location.getKey());
         
            if (visited == null) {
                visited = new SearchResult(location.getKey());
                results.add(visited);
                seenLocations.put(location.getKey(), visited);
            }

            visited.calculateScore(location.getValue().size());
        }
    }
    
    /**
     * Performs an exact search for a single search query
     * @param query the query to search
     * @return a sorted list of search results
     */
    public ArrayList<SearchResult> exactSearch(Set<String> query) {
        ArrayList<SearchResult> results = new ArrayList<>();
        HashMap<String, SearchResult> seenLocations = new HashMap<>();
        var searchWords = query.iterator();

        while(searchWords.hasNext()) {

            String word = searchWords.next();

            if (hasWord(word)) {
                generateResults(word, seenLocations, results);
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Performs a partial search for a single search query
     * @param query the query to search
     * @return a sorted list of search results
     */
    public ArrayList<SearchResult> partialSearch(Set<String> query) {
        ArrayList<SearchResult> results = new ArrayList<>();
        HashMap<String, SearchResult> seenLocations = new HashMap<>();
        var searchWords = query.iterator();

        while (searchWords.hasNext()) {

            String word = searchWords.next();
            var possibleMatches = invertedIndex.tailMap(word).entrySet().iterator();

            while (possibleMatches.hasNext()) {

                Entry<String, TreeMap<String, TreeSet<Integer>>> possibleMatch = possibleMatches.next();

                if (possibleMatch.getKey().startsWith(word)) {
                    generateResults(possibleMatch.getKey(), seenLocations, results);
                }
                else {
                    break;
                }
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Performs exact search or partial search based on flag passed
     * @param query the query to search
     * @param usePartial true for partial search, false for exact search
     * @return the search results
     */
    public ArrayList<SearchResult> search(Set<String> query, boolean usePartial) {
    	return usePartial ? partialSearch(query) : exactSearch(query);
    }

    @Override
    public String toString() {
        return invertedIndex.toString();
    }

    /** Stores a search result associated with the inverted index */
    public class SearchResult implements Comparable<SearchResult> {
        /** The location where a query word was found */
        private final String location;
    
        /** The total query words found at the location */
        private int count;
    
        /** The score defined by count / total words */
        private double score;
    
        /**
         * Class constructor
         * @param location the location of the search
         */
        public SearchResult(String location) {
            this.location = location;
            this.count = 0;
            this.score = 0;
        }
    
        /**
         * Increments match count and calculates score
         * @param matches number of matches found
         */
        private void calculateScore(int matches) {
            this.count += matches;
            this.score = (double) this.count / (double) wordCounts.get(this.location);
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
}
