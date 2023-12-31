package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Isaac Meltsner
 * @version Fall 2023
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("[");

		var iterator = elements.iterator();
		
		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}

		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * A helper method for writeObject() that writes some of the JSON output
	 * 
	 * @param entry the entry from the map
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeObjectEntry(Entry<String, ? extends Number> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeQuote(entry.getKey(), writer, indent + 1);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("{");

		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			writeObjectEntry(iterator.next(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",");
			writeObjectEntry(iterator.next(), writer, indent);
		}

		writer.write("\n");
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * A helper method for writeObjectArrays that writes a portion of the JSON ouptut
	 * 
	 * @param entry the entry from the map
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeQuote(String, Writer, int)
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeObjectArraysEntry(Entry<String, ? extends Collection<? extends Number>> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeQuote(entry.getKey(), writer, indent + 1);
		writer.write(": ");
		writeArray(entry.getValue(), writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("{");

		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			writeObjectArraysEntry(iterator.next(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",");
			writeObjectArraysEntry(iterator.next(), writer, indent);
		}

		writer.write("\n");
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("[");

		var iterator = elements.iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}

		writer.write("\n]");
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * A helper method for writeInvertedIndex() that writes a portion of the JSON output
	 * 
	 * @param entry the entry from the map
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeQuote(String, Writer, int)
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeInvertedIndexEntry(Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeIndent(writer, indent + 1);
		writeQuote(entry.getKey(), writer, indent);
		writer.write(": ");
		writeObjectArrays(entry.getValue(), writer, indent + 1);
	}
	
	/**
	 * Formats the inverted index as a pretty JSON array with nested objects
	 * 
	 * @param index the inverted index
	 * @param writer the writer to use
	 * @param indent the starting indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeInvertedIndexEntry(Entry, Writer, int)
	 */
	public static void writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Writer writer, int indent) throws IOException {
		writer.write("{");

		var iterator = index.entrySet().iterator();

		if (iterator.hasNext()) {
			writeInvertedIndexEntry(iterator.next(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",");
			writeInvertedIndexEntry(iterator.next(), writer, indent);
		}

		writer.write("\n}");
	}

	/**
	 * Writes the inverted index in pretty JSON format to file
	 * 
	 * @param index the inverted index
	 * @param path the path to the file
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeInvertedIndex(Map, Writer, int)
	 */
	public static void writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeInvertedIndex(index, writer, 0);
		}
	}

	/**
	 * Returns the inverted index as a pretty JSON array
	 * 
	 * @param index the inverted index
	 * @return a String containing the elements in pretty JSON format
	 * 
	 * @see #writeInvertedIndex(Map, Writer, int)
	 */
	public static String writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index) {
		try {
			StringWriter writer = new StringWriter();
			writeInvertedIndex(index, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Outputs a single search result in pretty JSON format
	 * 
	 * @param result the search result
	 * @param writer the writer to use
	 * @param indent the initial indent
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeQuote(String, Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeResult(InvertedIndex.SearchResult result, Writer writer, int indent) throws IOException {
		writeIndent("{", writer, indent + 2);
		writer.write("\n");
		writeQuote("count", writer, indent + 3);
		writer.write(": ");
		writer.write(String.valueOf(result.getCount()));
		writer.write(",\n");
		writeQuote("score", writer, indent + 3);
		writer.write(": ");
		writer.write(String.format("%.8f", result.getScore()));
		writer.write(",\n");
		writeQuote("where", writer, indent + 3);
		writer.write(": ");
		writeQuote(result.getLocation(), writer, indent);
		writer.write("\n");
		writeIndent(writer, indent + 2);
		writer.write("}");
	}

	/**
	 * Outputs list of search results as pretty JSON format
	 * 
	 * @param results the list of results
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeResult(edu.usfca.cs272.InvertedIndex.SearchResult, Writer, int)
	 */
	 public static void writeSearchResults(Collection<InvertedIndex.SearchResult> results, Writer writer, int indent) throws IOException {
		boolean empty = true;
		var iterator = results.iterator();

		if (iterator.hasNext()) {
			empty = false;
			writeResult(iterator.next(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			writeResult(iterator.next(), writer, indent);
		}

		if (!empty) {
			writer.write("\n");
		}
	}

	/**
	 * Helper method for writeSearchResults that outputs map entry in pretty JSON format
	 * 
	 * @param entry the entry to output
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeQuote(String, Writer, int)
	 * @see #writeSearchResults(Collection, Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeResultsEntry(Entry<String, ? extends Collection<InvertedIndex.SearchResult>> entry, Writer writer, int indent) throws IOException {
		writeQuote(entry.getKey(), writer, indent + 1);
		writer.write(": [\n");
		writeSearchResults(entry.getValue(), writer, indent);
		writeIndent(writer, indent + 1);
		writer.write("]");
	}

	/**
	 * Outputs map of search results as pretty JSON format
	 * 
	 * @param results a map containing the query string and the search results
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeResultsEntry(Entry, Writer, int)
	 */
	public static void writeSearchResults(Map<String, ? extends Collection<InvertedIndex.SearchResult>> results, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		var iterator = results.entrySet().iterator();

		if (iterator.hasNext()) {
			writeResultsEntry(iterator.next(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			writeResultsEntry(iterator.next(), writer, indent);
		}

		writer.write("\n}");
	}

	/**
	 * Writes search results in pretty JSON format to a file
	 * 
	 * @param results the search results to write
	 * @param path the path to the output file
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #writeSearchResults(Collection, Writer, int)
	 */
	public static void writeSearchResults(Map<String, ? extends Collection<InvertedIndex.SearchResult>> results, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeSearchResults(results, writer, 0);
		}
	}
	
	/**
	 * Returns the search results as a pretty JSON array
	 * @param results the search results
	 * @return a String containing the elements in pretty JSON format
	 * 
	 * @see #writeSearchResults(Collection, Writer, int)
	 */
	public static String writeSearchResults(Map<String, ? extends Collection<InvertedIndex.SearchResult>> results) {
		try {
			StringWriter writer = new StringWriter();
			writeSearchResults(results, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
}
