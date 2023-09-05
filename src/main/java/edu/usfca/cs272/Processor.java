package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Iterates through files and directories
 * and stores file names and word counts 
 * of .txt and .text files
 * 
 * @author Isaac Meltsner
 */
public class Processor {
    public final String PATH_START = "/Users/isaacmeltsner/Desktop/CS/CS272-C/SearchEngine/project-tests/";
    public TreeMap<String, Integer> map;

    /**
     * Class constructor
     * @param map a map storing file names and word counts
     */
    public Processor(TreeMap<String, Integer> map) {
        this.map = map;
    }

	/**
	 * Reads a file, cleans each word and stems words
	 * Adds file name and count of stememd words to a TreeMap
	 * Outputs pretty JSON format of file name and count of words in file
	 * @param input path of the file
	 * @param inString relative path of file as a string
	 * @param outFile path to file where JSON format will be output
	 * @return map of file name and count of words
	 * @throws IOException if an IOException occurs
	 */
	public void processFile(Path input, String inString, Path outFile) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(input);
		if (stems.size() != 0) {
			this.map.put(inString, stems.size());
		}
	}

    /**
	 * Recursivley iterates through a directory
	 * and outputs file names and word counts
	 * @param inPath path to directory
	 * @param outFile path to output file
	 * @throws IOException if an IOException occurs
     * 
     * @see #processFile(Path, String, Path)
	 */
	public void processDir(Path inPath, Path outFile) throws IOException {
		DirectoryStream<Path> stream = Files.newDirectoryStream(inPath);
		var iterator = stream.iterator();
		while (iterator.hasNext()) {
			Path item = iterator.next();
			if (Files.isDirectory(item)) {
				System.out.println("In directory: " + item.toString());
				processDir(item, outFile);
			}
			else {
				if (item.toString().toLowerCase().endsWith(".txt") || item.toString().toLowerCase().endsWith(".text")) {
					System.out.println("Processing file: " + item.toString());
					processFile(item.toAbsolutePath(), item.toAbsolutePath().toString().replace(PATH_START, ""), outFile);
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
