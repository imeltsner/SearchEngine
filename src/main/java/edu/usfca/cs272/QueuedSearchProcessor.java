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

/** A thread safe class to process query files and perform a search of an inverted index */
public class QueuedSearchProcessor implements SearchProcessor {
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
	 * Initializes the inverted index and work queue to use. Sets the type of search to perform
	 * 
	 * @param index the inverted index to search
	 * @param usePartial flag determining type of search to perform
	 * @param queue the work queue to use
	 */
	public QueuedSearchProcessor(ThreadSafeInvertedIndex index, boolean usePartial, WorkQueue queue) {
		this.searchResults = new TreeMap<>();
		this.index = index;
		this.usePartial = usePartial;
		this.queue = queue;
		this.stemmer = new SnowballStemmer(ENGLISH);
	}

	/**
	 * Takes a file of search queries and performs a search for each query
	 * 
	 * @param path the file to process
	 * @throws IOException if an IO error occurs
	 */
	public void processFile(Path path) throws IOException {
		SearchProcessor.super.processFile(path);
		queue.finish();
	}

	/**
	 * Parses and stems a search query, performs a search of the inverted index, and sorts results
	 * 
	 * @param line the line containing a search query
	 */
	public void processLine(String line) {
		Task task = new Task(line);
		queue.execute(task);
	}

	/**
	 * Writes search results in pretty JSON format
	 * 
	 * @param path the path of the file to write to
	 * @throws IOException if an IO error occurs
	 */
	public void writeSearchResults(Path path) throws IOException {
		synchronized (searchResults) {
			JsonWriter.writeSearchResults(searchResults, path);
		}
	}

	/**
	 * Shows a view of every search query
	 * 
	 * @return an unmodifiable set of the search queries
	 */
	public Set<String> viewQueries() {
		synchronized (searchResults) {
			return Collections.unmodifiableSet(searchResults.keySet());
		}
	}

	/**
	 * Shows a view of all search results associated with a given query
	 * 
	 * @param line the query associated with the results
	 * @return an unmodifiable view of all results associated with a query
	 * or an empty list if query not in results
	 */
	public List<InvertedIndex.SearchResult> viewResult(String line) {
		synchronized (searchResults) {
			List<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
			return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
		}
	}

	/**
	 * Checks if a query exists in the results
	 * 
	 * @param line the query to check
	 * @return true if query exists, false otherwise
	 */
	public boolean hasQuery(String line) {
		synchronized (searchResults) {
			return searchResults.containsKey(joinQuery(line));
		}
	}

	/**
	 * Gets the number of queries in the results
	 * 
	 * @return the number of queries in the results
	 */
	public int numQueries() {
		synchronized (searchResults) {
			return searchResults.size();
		}
	}
	
	/**
	 * Gets the number of results associated with a given query
	 * 
	 * @param line the query string
	 * @return the number of results associated with a given query or 0 if query is not in results
	 */
	public int numResults(String line) {
		synchronized (searchResults) {
			ArrayList<InvertedIndex.SearchResult> results = searchResults.get(joinQuery(line));
			return results != null ? results.size() : 0;
		}
	}

	/**
	 * Finds unique stems from a query line and joins them into a single string
	 * 
	 * @param line the query to stem and join
	 * @return a single query string of unique stems
	 */
	private String joinQuery(String line) {
		return String.join(" ", FileStemmer.uniqueStems(line, stemmer));
	}

	@Override
	public String toString() {
		synchronized(searchResults) {
			return searchResults.toString();
		}
	}

	/** Processes a single query line and performs search of the inverted index */
	private class Task implements Runnable {

		/** Stemmer to stem queries */
		private final Stemmer localStemmer;

		/** The line to process */
		private final String line;

		/**
		 * Initialzies the query string and the local stemmer to use
		 * 
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
				else {
					searchResults.put(queryString, null);
				}
			}
			
			ArrayList<ThreadSafeInvertedIndex.SearchResult> results = index.search(query, usePartial);
			
			synchronized (searchResults) {
				searchResults.put(queryString, results);
			}
		}
	}
}
