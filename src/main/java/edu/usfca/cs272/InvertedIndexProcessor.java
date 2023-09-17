package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class responsible for iterating through files and directories
 * and storing file names and word counts of files in a map
 * 
 * @author Isaac Meltsner
 */
public class InvertedIndexProcessor {
	/**
	 * Reads a file, cleans each word and stems words
	 * Adds word counts and word positions in files to Indexer 
	 * @param inPath path of the file
	 * @param index the Indexer object
	 * @throws IOException if an IOException occurs
	 * 
	 */
	public static void processFile(Path inPath, InvertedIndex index) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(inPath);

		if (stems.size() != 0) {
			index.getWordCounts().put(inPath.toString(), stems.size());
		}

		for (int i = 0; i < stems.size(); i++) {
			index.putData(stems.get(i), inPath.toString(), i+1);
		}
	}

    /**
	 * Recursively iterates through a directory
	 * checks if files are text files
	 * adds contents of text files to inverted index
	 * @param inPath path of directory
	 * @param index the Indexer object
	 * @throws IOException if IO error occurs
	 * @throws NotDirectoryException if given path is not a directory
     * 
     * @see #processFile(Path, InvertedIndex)
	 */
	public static void processDir(Path inPath, InvertedIndex index) throws IOException, NotDirectoryException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(inPath);) {
			var iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path path = iterator.next();
				if (Files.isDirectory(path)) {
					processDir(path, index);
				}
				else if (isTextFile(path)) {
					processFile(path, index);
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
		return path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text");
	}
}







