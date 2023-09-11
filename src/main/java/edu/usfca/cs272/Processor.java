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
public class Processor {
	/**
	 * Reads a file, cleans each word and stems words
	 * Adds word counts and word positions in files to Indexer 
	 * @param inPath path of the file
	 * @param index the Indexer object
	 * @throws IOException if an IOException occurs
	 * 
	 */
	public static void processFile(Path inPath, Indexer index) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(inPath);

		if (stems.size() != 0) {
			index.getCounts().put(inPath.toString(), stems.size());
		}

		for (int i = 0; i < stems.size(); i++) {
			index.putData(stems.get(i), inPath.toString(), i);
		}
	}

    /**
	 * Recursivley iterates through a directory
	 * and outputs file names and word counts
	 * @param inPath path of directory
	 * @param index the Indexer object
     * 
     * @see #processFile(Path, Indexer)
	 */
	public static void processDir(Path inPath, Indexer index) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(inPath);) {
			var iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path item = iterator.next();
				if (Files.isDirectory(item)) {
					processDir(item, index);
				}
				else if (item.toString().toLowerCase().endsWith(".txt") || item.toString().toLowerCase().endsWith(".text")) {
					processFile(item, index);
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
