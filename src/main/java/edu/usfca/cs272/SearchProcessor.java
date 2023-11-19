package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/** An interface for processing search queries and performing a search */
public interface SearchProcessor {
	/**
	 * Processes a single file containing search queries
	 * 
	 * @param path the path to the file
	 * @throws IOException if an IO error occurs
	 */
	public default void processFile(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			while (reader.ready()) {
				processLine(reader.readLine());
			}
		}
	}

	/**
	 * Processes a single search query
	 * 
	 * @param line the query line to process
	 */
	public abstract void processLine(String line);

	/**
	 * Writes search results in pretty JSON format
	 * 
	 * @param path the path to the output file
	 * @throws IOException if an IO error occurs
	 */
	public abstract void writeSearchResults(Path path) throws IOException;

	/**
	 * Gets a view of all queries that have been searched
	 * 
	 * @return an unmodifiable set of search queries
	 */
	public abstract Set<String> viewQueries();

	/**
	 * Gets a view of the search results associated with a given query
	 * 
	 * @param line the query to search
	 * @return an unmodifiable list of search results
	 */
	public abstract List<InvertedIndex.SearchResult> viewResult(String line);

	/**
	 * Checks if a given query has been searched
	 * 
	 * @param line the query to check
	 * @return true if query has been searched false otherwise
	 */
	public abstract boolean hasQuery(String line);

	/**
	 * Gets the number of queries that have been searched
	 * 
	 * @return total number of searched queries
	 */
	public abstract int numQueries();

	/**
	 * Gets the number of results for a given query
	 * 
	 * @param line the query to check
	 * @return number of results
	 */
	public abstract int numResults(String line);
}