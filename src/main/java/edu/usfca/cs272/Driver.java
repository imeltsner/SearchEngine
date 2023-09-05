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

		//arg processing
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-text")) {
				try {
					inString = args[++i];
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Invalid args");
					break;
				}
				
			}
			else if (args[i].equals("-counts")) {
				try {
					outString = args[++i];
				} catch (IndexOutOfBoundsException e) {
					outString = "counts.json";
				}
			}
			else {
				System.out.println("Invalid flag");
				break;
			}
		}

		//create path objects
		if (!inString.isEmpty()) {
			inPath = Path.of(PATH_START,  inString);
		}
		outFile = Path.of(PATH_START, outString);

		//iterate through directories and output counts to files
		TreeMap<String, Integer> map = new TreeMap<>();
		Processor processor = new Processor(map);
		try {
			if (Files.isDirectory(inPath)) {
				processor.processDir(inPath, outFile);
			}
			else {
				processor.processFile(inPath, inString, outFile);
			}
			JsonWriter.writeObject(processor.getMap(), outFile);
		} catch (IOException e) {
			System.out.println("File not found");
		} catch (NullPointerException e) {
			System.out.println("File not found");
		}
	}
}
