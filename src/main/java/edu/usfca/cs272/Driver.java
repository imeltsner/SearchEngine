package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Isaac Meltsner
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {

	/**
	 * Reads a file, cleans each word and stems words
	 * Adds stememd words to a list
	 * @param input path of the file
	 * @return list of stems
	 * @throws IOException
	 */
	public static ArrayList<String> processFile(Path input) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(input);
		return stems;
	}


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
		Path inFile = null;
		Path outFile = null;

		//Arg processing
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-text")) {
				inString = args[++i];
			}
			if (args[i].equals("-counts")) {
				try {
					outString = args[++i];
				} catch (IndexOutOfBoundsException e) {
					outString = "counts.json";
				}
			}
		}

		if (!inString.isEmpty()) {
			inFile = Path.of("/Users/isaacmeltsner/Desktop/CS/CS272-C/SearchEngine/project-tests/",  inString);
		}
		outFile = Path.of("/Users/isaacmeltsner/Desktop/CS/CS272-C/SearchEngine/project-tests/", outString);

		System.out.println("Input file: " + inFile.toString());
		System.out.println("Output file: " + outFile.toString() );
	}
}
