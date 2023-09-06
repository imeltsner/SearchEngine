package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Isaac Meltsner
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {
	public static final String PATH_START = "/Users/isaacmeltsner/Desktop/CS/CS272-C/SearchEngine/project-tests/";

	/**
	 * Initializes the classes necessary based on the provided command-line 
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		String inString = "";
		String outString = "";
		Path inPath = null;
		Path outFile = null;
		TreeMap<String, Integer> map = new TreeMap<>();
		Processor processor = new Processor(map);
		ArgumentParser parser = new ArgumentParser(args);

		if (parser.hasFlag("-counts")) {
			outString = parser.getString("-counts", "counts.json");
			outFile = Path.of(PATH_START, outString);
		}

		if (parser.hasFlag("-text")) {
			inString = parser.getString("-text");
			inPath = (inString != null) ? Path.of(PATH_START, inString) : null;
		}

		if (inPath == null && outFile == null) {
			System.out.println("Invalid args: Program expects either -text or -counts");
		}
		else if (inPath == null) {
			try {
				JsonWriter.writeObject(map, outFile);
			}
			catch (IOException e) {
				System.out.println("Output file not found");
			}
		}
		else {
			try {
				if (Files.isDirectory(inPath)) {
					processor.processDir(inPath);
				}
				else {
					processor.processFile(inPath, inString);
				}

				if (outFile != null) {
					JsonWriter.writeObject(map, outFile);
				}
			}
			catch (IOException e) {
				System.out.println("Input file not found");
			}
		}
	}
}
