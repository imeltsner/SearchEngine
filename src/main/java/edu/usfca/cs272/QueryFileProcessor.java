package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class to process files containing search queries
 * 
 * @author Isaac Meltsner
 */
public class QueryFileProcessor {
    /** Member to store search results */
    private final TreeMap<String, ArrayList<SearchResult>> exactSearchResults; // TODO results

    // TODO option 2: different maps (one for exact, one for partial)... need to pass the flag to the write method too then
    
    /** Inverted index of words and location data */
    private final InvertedIndex index;

    /**
     * Class constructor
     * @param index the inverted index to search
     */
    public QueryFileProcessor(InvertedIndex index) { // TODO Pass in the partial flag here and store as a member (option 1)
        this.exactSearchResults = new TreeMap<>();
        this.index = index;
    }

    /**
     * Takes a file of search queries and performs a search for each query
     * @param queryFile the file to process
     * @param usePartial true for partial search, false for exact search
     * @throws IOException if an IO error occurs
     * 
     * @see #processLine(String, boolean)
     */
    public void processFile(Path queryFile, boolean usePartial) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                processLine(reader.readLine(), usePartial);
            }
        }
    }

    /**
     * Parses and stems a search query, performs a search of the inverted index,
     * and sorts results
     * @param line the line containing a search query
     * @param usePartial true for partial search, false for exact search
     */
    public void processLine(String line, boolean usePartial) {
        TreeSet<String> query = FileStemmer.uniqueStems(line); // TODO Figure out how to share a stemmer instead
        String queryString = String.join(" ", query);

        if (queryString.equals("")) {
            return;
        }
        
        // TODO Also return if you already have these results

        ArrayList<SearchResult> result;

        if (usePartial) { // TODO Call search instead
            result = index.partialSearch(query);
        }
        else {
            result = index.exactSearch(query);
        }

        Collections.sort(result); // TODO Remove from here (should be in search)
        exactSearchResults.put(queryString, result);
    }

    /**
     * Returns all search results from all queries in a file
     * @return the search results
     */
    public TreeMap<String, ArrayList<SearchResult>> getExactSearchResults() { // TODO Breaking encapsulation
        return exactSearchResults;
    }
    
    // TODO Create some safe get methods etc. and add a write method
}
