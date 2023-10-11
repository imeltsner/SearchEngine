package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
     * @throws IOException if an IO error occurs
     * 
     * @see #processLine(String, Stemmer)
     */
    public void processFile(Path queryFile) throws IOException {
        Stemmer stemmer = new SnowballStemmer(ENGLISH);
        try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
            while (reader.ready()) {
                processLine(reader.readLine(), stemmer);
            }
        }
    }

    /**
     * Parses and stems a search query, performs a search of the inverted index, and sorts results
     * @param line the line containing a search query
     * @param stemmer the stemmer to use
     */
    public void processLine(String line, Stemmer stemmer) {
        TreeSet<String> query = FileStemmer.uniqueStems(line, stemmer);
        String queryString = String.join(" ", query);

        if (queryString.equals("") || searchResults.containsKey(queryString)) {
            return;
        }

        ArrayList<SearchResult> result;
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
    
    // TODO Create some safe get methods etc. and add a write method
}
