package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;

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
	 * @param path path of the file
	 * @param index the Indexer object
	 * @throws IOException if an IOException occurs
	 */
	public static void processFile(Path path, InvertedIndex index) throws IOException {
		int start = 0;

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			while (reader.ready()) {
				ArrayList<String> words = FileStemmer.listStems(reader.readLine(), stemmer);
				index.addAll(words, path.toString(), start);
				start += words.size();
			}
		}
	}

    /**
	 * Recursively iterates through a directory
	 * checks if files are text files
	 * adds contents of text files to inverted index
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
	 * @param path the path to check
	 * @return true if path is a text file false otherwise
	 */
	public static boolean isTextFile(Path path) {
		String lowerCasePath = path.toString().toLowerCase();
		return lowerCasePath.endsWith(".txt") || lowerCasePath.endsWith(".text");
	}
	
	/**
	 * Processes path according to path type
	 * @param path the path to process
	 * @param index the inverted index
	 * @throws IOException if IOError occurs
	 * @throws NullPointerException if null pointer is found
	 */
	public static void process(Path path, InvertedIndex index) throws IOException, NullPointerException {
		if (Files.isDirectory(path)) {
			InvertedIndexProcessor.processDir(path, index);
		}
		else {
			InvertedIndexProcessor.processFile(path, index);
		}
	}
}
