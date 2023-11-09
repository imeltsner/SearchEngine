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

/** A thread safe class to process query files and perform a search of an inverted index */
public class QueuedQueryFileProcessor {
	/** Member to store search results */
	private final TreeMap<String, ArrayList<ThreadSafeInvertedIndex.SearchResult>> searchResults;

	/** Inverted index of words and location data */
	private final ThreadSafeInvertedIndex index;

	/** Flag to determine type of search to perform */
	private final boolean usePartial;

	/** The work queue to use */
	private final WorkQueue queue;

	/** The stemmer to use */
	private final Stemmer stemmer;

	/**
	 * Class constructor
	 * @param index the inverted index to search
	 * @param usePartial flag determining type of search to perform
	 * @param queue the work queue to use
	 */
	public QueuedQueryFileProcessor(ThreadSafeInvertedIndex index, boolean usePartial, WorkQueue queue) {
		this.searchResults = new TreeMap<>();
		this.index = index;
		this.usePartial = usePartial;
		this.queue = queue;
		this.stemmer = new SnowballStemmer(ENGLISH);
	}

	/**
	 * Takes a file of search queries and performs a search for each query
	 * @param path the file to process
	 * @throws IOException if an IO error occurs
	 */
	public void processFile(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			while (reader.ready()) {
				Task task = new Task(reader.readLine());
				queue.execute(task);
			}
		}
	}

	/**
	 * Writes search results in pretty JSON format
	 * @param path the path of the file to write to
	 * @throws IOException if an IO error occurs
	 */
	public void writeSearchResults(Path path) throws IOException {
		System.out.println("Number of lines: " + searchResults.size());
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
	 * @param line the query associated with the results
	 * @return an unmodifiable view of all results associated with a query
	 * or an empty list if query not in results
	 */
	public List<InvertedIndex.SearchResult> viewResult(String line) {
		List<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
		return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
	}

	/**
	 * Checks if a query exists in the results
	 * @param line the query to check
	 * @return true if query exists, false otherwise
	 */
	public boolean hasQuery(String line) {
		return searchResults.containsKey(joinQuery(line));
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
	 * @param line the query string
	 * @return the number of results associated with a given query or 0 if query is not in results
	 */
	public int numResults(String line) {
		ArrayList<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
		return results != null ? results.size() : 0;
	}

	/**
	 * Finds unique stems from a query line and joins them into a single string
	 * @param line the query to stem and join
	 * @return a single query string of unique stems
	 */
	private String joinQuery(String line) {
		return String.join(line, FileStemmer.uniqueStems(line, stemmer));
	}

	@Override
	public String toString() {
		return searchResults.toString();
	}

	/** Processes a single query line and performs search of the inverted index */
	private class Task implements Runnable {

		/** Stemmer to stem queries */
		private final Stemmer localStemmer;

		/** The line to process */
		private final String line;

		/**
		 * Class constructor
		 * @param line the query line to search
		 */
		private Task(String line) {
			this.line = line;
			this.localStemmer = new SnowballStemmer(ENGLISH);
		}

		@Override
		public void run() {
			TreeSet<String> query = FileStemmer.uniqueStems(line, localStemmer);
			String queryString = String.join(" ", query);

			synchronized (searchResults) {
				if (queryString.equals("") || searchResults.containsKey(queryString)) {
					return;
				}
			}
			
			ArrayList<ThreadSafeInvertedIndex.SearchResult> results = index.search(query, usePartial);
			
			synchronized (searchResults) {
				searchResults.put(queryString, results);
			}
		}
	}
}
