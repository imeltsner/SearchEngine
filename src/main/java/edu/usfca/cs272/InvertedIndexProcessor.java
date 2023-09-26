package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for iterating through files and directories
 * and storing file names and word counts of files in a map
 * 
 * @author Isaac Meltsner
 */
public class InvertedIndexProcessor {
	/** Regular expression that matches any whitespace. **/
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

	/** Regular expression that matches non-alphabetic characters. **/
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/**
	 * Cleans the text by removing any non-alphabetic characters (e.g. non-letters
	 * like digits, punctuation, symbols, and diacritical marks like the umlaut) and
	 * converting the remaining characters to lowercase.
	 *
	 * @param text the text to clean
	 * @return cleaned text
	 */
	public static String clean(String text) {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * Splits the supplied text by whitespaces.
	 *
	 * @param text the text to split
	 * @return an array of {@link String} objects
	 */
	public static String[] split(String text) {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}

	/**
	 * Parses the text into an array of clean words.
	 *
	 * @param text the text to clean and split
	 * @return an array of {@link String} objects
	 *
	 * @see #clean(String)
	 * @see #parse(String)
	 */
	public static String[] parse(String text) {
		return split(clean(text));
	}

	/**
	 * Reads a file, cleans and stems each word
	 * Adds word counts to map and word positions in files to inverted index 
	 * @param path path of the file
	 * @param index the Indexer object
	 * @throws IOException if an IOException occurs
	 * 
	 */
	public static void processFile(Path path, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String sPath = path.toString();
		int count = 0;

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			while (reader.ready()) {
				String[] words = parse(reader.readLine());
				for (String word: words) {
					index.putData(stemmer.stem(word).toString(), sPath, count + 1);
					count++;
				}
			}

			if (count != 0) {
				index.putCount(sPath, count);
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
}
