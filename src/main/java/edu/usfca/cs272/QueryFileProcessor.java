package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class to process files containing search queries
 * 
 * @author Isaac Meltsner
 */
public class QueryFileProcessor {
    /** Member to store search results */
    private final TreeMap<String, ArrayList<SearchResult>> searchResults;

    /** Inverted index of words and location data */
    private final InvertedIndex index;

    /** Flag to determine type of search */
    private boolean usePartial;

    /**
     * Class constructor
     * @param index the inverted index to search
     */
    public QueryFileProcessor(InvertedIndex index, boolean usePartial) {
        this.searchResults = new TreeMap<>();
        this.index = index;
        this.usePartial = usePartial;
    }

    /**
     * Takes a file of search queries and performs a search for each query
     * @param queryFile the file to process
     * @param usePartial true for partial search, false for exact search
     * @throws IOException if an IO error occurs
     * 
     * @see #processLine(String, boolean)
     */
    public void processFile(Path queryFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                processLine(reader.readLine());
            }
        }
    }

    /**
     * Parses and stems a search query, performs a search of the inverted index,
     * and sorts results
     * @param line the line containing a search query
     * @param usePartial true for partial search, false for exact search
     */
    public void processLine(String line) {
        TreeSet<String> query = FileStemmer.uniqueStems(line); // TODO Figure out how to share a stemmer instead
        String queryString = String.join(" ", query);

        if (queryString.equals("") || searchResults.containsKey(queryString)) {
            return;
        }

        ArrayList<SearchResult> result;
        result = index.search(query, usePartial);
        searchResults.put(queryString, result);
    }

    /**
     * Returns all search results from all queries in a file
     * @return the search results
     */
    public TreeMap<String, ArrayList<SearchResult>> getSearchResults() { // TODO Breaking encapsulation
        return searchResults;
    }
    
    // TODO Create some safe get methods etc. and add a write method
}
