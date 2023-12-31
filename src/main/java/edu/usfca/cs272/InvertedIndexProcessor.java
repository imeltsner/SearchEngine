package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for iterating through files and directories
 * and storing file names and word counts of files in a map
 * 
 * @author Isaac Meltsner
 */
public class InvertedIndexProcessor {
	/** Stemmer to stem words */
	public static Stemmer stemmer = new SnowballStemmer(ENGLISH);

	/**
	 * Reads a file, cleans and stems each word
	 * Adds word counts to map and word positions in files to inverted index
	 *  
	 * @param path path of the file
	 * @param index the Indexer object
	 * @throws IOException if an IOException occurs
	 */
	public static void processFile(Path path, InvertedIndex index) throws IOException {
		int count = 0;

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			while (reader.ready()) {
				count = processString(reader.readLine(), index, path.toString(), count);
			}
		}
	}

	/**
	 * Reads a file, cleans and stems each word
	 * Adds word counts to map and word positions in files to inverted index
	 *  
	 * @param path path of the file
	 * @param index the Indexer object
	 * @param stemmer the stemmer to use
	 * @throws IOException if an IOException occurs
	 */
	public static void processFile(Path path, InvertedIndex index, Stemmer stemmer) throws IOException {
		int count = 0;

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			while (reader.ready()) {
				
				String[] words = FileStemmer.parse(reader.readLine());
				
				for (String word: words) {
					index.addData(stemmer.stem(word).toString(), path.toString(), count + 1);
					count++;
				}
			}
		}
	}

	/**
	 * Cleans, parses, and stems a string and adds each word found to the inverted index
	 * 
	 * @param content the string to parse
	 * @param index the inverted index to use
	 * @param location the location where the content was found
	 * @param count the position of a word in the content
	 * @return the last position of the word
	 */
	public static int processString(String content, InvertedIndex index, String location, int count) {
		String[] words = FileStemmer.parse(content);
				
		for (String word : words) {
			index.addData(stemmer.stem(word).toString(), location, count + 1);
			count++;
		}

		return count;
	}

	/**
	 * Cleans, parses, and stems a string and adds each word found to the inverted index
	 * 
	 * @param content the string to parse
	 * @param index the inverted index to use
	 * @param location the location where the content was found
	 * @param count the position of a word in the content
	 * @param stemmer the stemmer to use
	 * @return the last position of the word
	 */
	public static int processString(String content, InvertedIndex index, String location, int count, Stemmer stemmer) {
		String[] words = FileStemmer.parse(content);
				
		for (String word : words) {
			index.addData(stemmer.stem(word).toString(), location, count + 1);
			count++;
		}

		return count;
	}

	/**
	 * Recursively iterates through a directory, checks if files are text files
	 * and adds contents of text files to inverted index
	 * 
	 * @param path path of directory
	 * @param index the Indexer object
	 * @throws IOException if IO error occurs
	 * @throws NotDirectoryException if given path is not a directory
	 * 
	 * @see #processFile(Path, InvertedIndex)
	 */
	public static void processDir(Path path, InvertedIndex index) throws IOException, NotDirectoryException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);) {

			var iterator = stream.iterator();

			while (iterator.hasNext()) {

				Path newPath = iterator.next();
				
				if (Files.isDirectory(newPath)) {
					processDir(newPath, index);
				}
				else if (isTextFile(newPath)) {
					processFile(newPath, index);
				}
			}
		} 
	}

	/**
	 * Checks to see if the given path is a text file
	 * 
	 * @param path the path to check
	 * @return true if path is a text file false otherwise
	 */
	public static boolean isTextFile(Path path) {
		String lowerCasePath = path.toString().toLowerCase();
		return lowerCasePath.endsWith(".txt") || lowerCasePath.endsWith(".text");
	}
	
	/**
	 * Processes path according to path type
	 * 
	 * @param path the path to process
	 * @param index the inverted index
	 * @throws IOException if IOError occurs
	 * @throws NullPointerException if null pointer is found
	 * 
	 * @see #processDir(Path, InvertedIndex)
	 * @see #processFile(Path, InvertedIndex)
	 */
	public static void process(Path path, InvertedIndex index) throws IOException, NullPointerException {
		if (Files.isDirectory(path)) {
			processDir(path, index);
		}
		else {
			processFile(path, index);
		}
	}
}
