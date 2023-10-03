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
     * @param path the file associated with the count
     * @return the number of words in a file or 0 if file not found
     */
    public int getCount(String path) {
        return wordCounts.getOrDefault(path, 0);
    }

    /**
     * Returns the number files in the map
     * @return the number of files in the map
     */
    public int numFiles() {
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
     * Checks if wordCount map contains a given file name
     * @param file the file name to check for
     * @return true if file name is in map false otherwise
     */
    public boolean hasCount(String file) {
        return wordCounts.containsKey(file);
    }
    
    /**
     * Checks if a given word is stored in the inverted index
     * @param word the word to check
     * @return true if word is in inverted index false otherwise
     */
    public boolean hasWord(String word) {
        return invertedIndex.containsKey(word);
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
        return hasWord(word) ? invertedIndex.get(word).containsKey(location) : false;
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
    public boolean wordAtPosition(String word, String location, int position) {
        return hasLocation(word, location) ? invertedIndex.get(word).get(location).contains(position) : false;
    }

    /**
     * Adds file names and word counts to map
     * @param path string representation of path to file
     * @param wordCount the number of words in the file
     */    
    public void addCount(String path, int wordCount) {
        wordCounts.put(path, wordCount);
    }

    /**
     * Adds words, filenames, and word position to the inverted index
     * @param word the word to add
     * @param path the file to add
     * @param position the position of the word
     */
    public void addData(String word, String path, int position) {
        invertedIndex.putIfAbsent(word, new TreeMap<>());
        invertedIndex.get(word).putIfAbsent(path, new TreeSet<>());
        invertedIndex.get(word).get(path).add(position);
    }
    
    /**
     * Adds a list of words to the inverted index starting at a given position
     * @param words the words to add
     * @param path the location of the words
     * @param start the starting position
     */
    public void addAll(List<String> words, String path, int start) {
        for (String word : words) {
            addData(word, path, start++);
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
     * Performs an exact search of a single search query
     * @param query the query to search
     * @return a sorted list of search results
     */
    public TreeSet<SearchResult> exactSearchSingle(TreeSet<String> query) {

        TreeSet<SearchResult> results = new TreeSet<>();
        HashMap<String, SearchResult> seenLocations = new HashMap<>();
        var searchWords = query.iterator();

        while(searchWords.hasNext()) {

            String word = searchWords.next();

            if (hasWord(word)) {

                var locations = invertedIndex.get(word).entrySet().iterator();

                while (locations.hasNext()) {

                    Entry<String, TreeSet<Integer>> location = locations.next();
                    SearchResult visited = seenLocations.get(location.getKey());
                    
                    if (visited == null) {
                        SearchResult result = new SearchResult(String.join(" ", query), location.getKey(), wordCounts.get(location.getKey()));
                        result.calculateScore(location.getValue().size());
                        //results.add(result);
                        seenLocations.put(location.getKey(), result);
                    }
                    else {
                        visited.calculateScore(location.getValue().size());
                    }
                }
            }
        }

        var seenIterator = seenLocations.entrySet().iterator();

        while (seenIterator.hasNext()) {
            results.add(seenIterator.next().getValue());
        }

        return results;
    }

    /**
     * Perfoms exact search on multiple queries
     * @param queries the queries to search for
     * @return a map containing the search queries and the results for each query
     */
    public TreeMap<String, TreeSet<SearchResult>> exactSearch(ArrayList<TreeSet<String>> queries) {

        TreeMap<String, TreeSet<SearchResult>> allResults = new TreeMap<>();

        for (TreeSet<String> query : queries) {
            TreeSet<SearchResult> results = exactSearchSingle(query);
            String queryString = String.join(" ", query);
            if (queryString.equals("")) {
                continue;
            }
            allResults.put(queryString, results);
        }

        return allResults;
    }

    @Override
    public String toString() {
        return invertedIndex.toString();
    }
}
