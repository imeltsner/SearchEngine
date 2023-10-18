package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class to process files containing search queries
 * 
 * @author Isaac Meltsner
 */
public class QueryFileProcessor {
    /** Member to store search results */
    private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> searchResults;

    /** Inverted index of words and location data */
    private final InvertedIndex index;

    /** Flag to determine type of search to perform */
    private final boolean usePartial;

    private final Stemmer stemmer;

    /**
     * Class constructor
     * @param index the inverted index to search
     * @param usePartial flag determining type of search to perform
     */
    public QueryFileProcessor(InvertedIndex index, boolean usePartial) {
        this.searchResults = new TreeMap<>();
        this.index = index;
        this.usePartial = usePartial;
        this.stemmer = new SnowballStemmer(ENGLISH);
    }

    /**
     * Takes a file of search queries and performs a search for each query
     * @param queryFile the file to process
     * @throws IOException if an IO error occurs
     * 
     * @see #processLine(String)
     */
    public void processFile(Path queryFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                processLine(reader.readLine());
            }
        }
    }

    /**
     * Parses and stems a search query, performs a search of the inverted index, and sorts results
     * @param line the line containing a search query
     */
    public void processLine(String line) {
        TreeSet<String> query = FileStemmer.uniqueStems(line, stemmer);
        String queryString = String.join(" ", query);

        if (queryString.equals("") || searchResults.containsKey(queryString)) {
            return;
        }

        ArrayList<InvertedIndex.SearchResult> result;
        result = index.search(query, usePartial);
        searchResults.put(queryString, result);
    }

    /**
     * Writes search results in pretty JSON format
     * @param path the path of the file to write to
     * @throws IOException if an IO error occurs
     */
    public void writeSearchResults(Path path) throws IOException {
        JsonWriter.writeSearchResults(searchResults, path);
    }

    /**
     * Shows a view of every search query
     * @return an unmodifiable set of the search queries
     */
    public Set<String> viewQueries() {
        return Collections.unmodifiableSet(searchResults.keySet());
    }

    /**
     * Shows a view of all search results associated with a given query
     * @param query the query associated with the results
     * @return an unmodifiable view of all results associated with a query
     * or an empty list if query not in results
     */
    public List<InvertedIndex.SearchResult> viewResult(String line) {
        String query = String.join(line, FileStemmer.uniqueStems(line, stemmer));
        List<InvertedIndex.SearchResult> results = searchResults.get(query);
        return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
    }

    /**
     * Checks if a query exists in the results
     * @param query the query to check
     * @return true if query exists, false otherwise
     */
    public boolean hasQuery(String query) {
        return searchResults.containsKey(query);
    }

    /**
     * Gets the number of queries in the results
     * @return the number of queries in the results
     */
    public int numQueries() {
        return searchResults.size();
    }
    
    /**
     * Gets the number of results associated with a given query
     * @param query the query string
     * @return the number of results associated with a given query or 0 if query is not in results
     */
    public int numResults(String query) {
        ArrayList<InvertedIndex.SearchResult> results = searchResults.get(query);
        return results != null ? results.size() : 0;
    }
}
