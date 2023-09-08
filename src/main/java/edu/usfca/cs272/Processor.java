package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
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
     *TreeMap with filenames as keys and wordcounts as values
     */
    public TreeMap<String, Integer> map;

    /**
     * Class constructor to initialze TreeMap
     * @param map a map storing file names and word counts
     */
    public Processor(TreeMap<String, Integer> map) {
        this.map = map;
    }

	/**
	 * Reads a file, cleans each word and stems words
	 * Adds file name and count of stememd words to a TreeMap
	 * @param inPath path of the file
	 * @throws IOException if an IOException occurs
	 */
	public void processFile(Path inPath) throws IOException { // TODO make static and pass in the new data structure class processFile(Path, InvertedIndex)
		ArrayList<String> stems = FileStemmer.listStems(inPath);

		if (stems.size() != 0) {
			this.map.put(inPath.toString(), stems.size());
		}
	}

    /**
	 * Recursivley iterates through a directory
	 * and outputs file names and word counts
	 * @param inPath path of directory
	 * @throws IOException if an IOException occurs
     * 
     * @see #processFile(Path)
	 */
	public void processDir(Path inPath) throws IOException {
		DirectoryStream<Path> stream = Files.newDirectoryStream(inPath); // TODO try-with-resources
		var iterator = stream.iterator();

		while (iterator.hasNext()) {
			Path item = iterator.next();

			if (Files.isDirectory(item)) {
				processDir(item);
			}
			else {
				if (item.toString().toLowerCase().endsWith(".txt") || item.toString().toLowerCase().endsWith(".text")) {
					processFile(item);
				}
				else {
					continue;
				}
			}
		}
	}

    /**
     * Returns the TreeMap associated with this class
     * @return a TreeMap object storing file names and word counts
     */
    public TreeMap<String, Integer> getMap() {
        return map;
    }
}
