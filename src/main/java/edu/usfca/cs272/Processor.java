package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Class responsible for iterating through files and directories
 * and storing file names and word counts of files in a map
 * 
 * @author Isaac Meltsner
 */
public class Processor {
	/**
	 * Reads a file, cleans each word and stems words
	 * Adds file name and count of stememd words to a TreeMap
	 * @param inPath path of the file
	 * @throws IOException if an IOException occurs
	 */
	public static void processFile(Path inPath, TreeMap<String, Integer> map) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(inPath);

		if (stems.size() != 0) {
			map.put(inPath.toString(), stems.size());
		}
	}

    /**
	 * Recursivley iterates through a directory
	 * and outputs file names and word counts
	 * @param inPath path of directory
     * 
     * @see #processFile(Path)
	 */
	public static void processDir(Path inPath, TreeMap<String, Integer> map) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(inPath);) {
			var iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path item = iterator.next();
				if (Files.isDirectory(item)) {
					processDir(item, map);
				}
				else if (item.toString().toLowerCase().endsWith(".txt") || item.toString().toLowerCase().endsWith(".text")) {
					processFile(item, map);
				}
			}
		} 
		catch (NotDirectoryException e) {
			System.out.println("Path given is not a directory: " + inPath.toString());
		} 
		catch (IOException e) {
			System.out.println("Path not found");
		}
	}
}
