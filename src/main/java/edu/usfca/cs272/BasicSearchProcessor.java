package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
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
 * A class to process files containing search queries and perform a search of of an inverted index 
 * based on the queries
 * 
 * @author Isaac Meltsner
 */
public class BasicSearchProcessor implements SearchProcessor {
	/** Member to store search results */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> searchResults;

	/** Inverted index of words and location data */
	private final InvertedIndex index;

	/** Flag to determine type of search to perform */
	private final boolean usePartial;

	/** Stemmer to stem queries */
	private final Stemmer stemmer;

	/**
	 * Initializes the inverted index and sets the search type
	 * 
	 * @param index the inverted index to search
	 * @param usePartial flag determining type of search to perform
	 */
	public BasicSearchProcessor(InvertedIndex index, boolean usePartial) {
		this.searchResults = new TreeMap<>();
		this.index = index;
		this.usePartial = usePartial;
		this.stemmer = new SnowballStemmer(ENGLISH);
	}

	/**
	 * Parses and stems a search query, performs a search of the inverted index, and stores results
	 * 
	 * @param line the line containing a search query
	 */
	public void processLine(String line) {
		TreeSet<String> query = FileStemmer.uniqueStems(line, stemmer);
		String queryString = String.join(" ", query);

		if (queryString.equals("") || searchResults.containsKey(queryString)) {
			return;
		}

		ArrayList<InvertedIndex.SearchResult> results = index.search(query, usePartial);
		searchResults.put(queryString, results);
	}

	/**
	 * Writes search results in pretty JSON format
	 * 
	 * @param path the path of the file to write to
	 * @throws IOException if an IO error occurs
	 */
	public void writeSearchResults(Path path) throws IOException {
		JsonWriter.writeSearchResults(searchResults, path);
	}

	/**
	 * Shows a view of every search query
	 * 
	 * @return an unmodifiable set of the search queries
	 */
	public Set<String> viewQueries() {
		return Collections.unmodifiableSet(searchResults.keySet());
	}

	/**
	 * Shows a view of all search results associated with a given query
	 * 
	 * @param line the query string
	 * @return an unmodifiable view of all results associated with a query
	 * or an empty list if query not in results
	 */
	public List<InvertedIndex.SearchResult> viewResult(String line) {
		List<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
		return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
	}

	/**
	 * Checks if a query has been searched
	 * 
	 * @param line the query string
	 * @return true if query has been searched, false otherwise
	 */
	public boolean hasQuery(String line) {
		return searchResults.containsKey(joinQuery(line));
	}

	/**
	 * Gets the number of queries that have been searched
	 * 
	 * @return the number of queries that have been searched
	 */
	public int numQueries() {
		return searchResults.size();
	}
	
	/**
	 * Gets the number of results associated with a given query
	 * 
	 * @param line the query string
	 * @return the number of results associated with a given query or 0 if query is not in results
	 */
	public int numResults(String line) {
		ArrayList<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
		return results != null ? results.size() : 0;
	}

	/**
	 * Finds unique stems from a query line and joins them into a single string
	 * 
	 * @param line the query string
	 * @return a single query string of unique stems
	 */
	private String joinQuery(String line) {
		return String.join(" ", FileStemmer.uniqueStems(line, stemmer));
	}

	@Override
	public String toString() {
		return searchResults.toString();
	}
}
