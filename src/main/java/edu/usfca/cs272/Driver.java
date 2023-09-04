package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

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
	public static HashMap<String, Integer> processFile(Path input, String inString, Path outFile) throws IOException {
		ArrayList<String> stems = FileStemmer.listStems(input);
		HashMap<String, Integer> obj = new HashMap<>();
		obj.put(inString, stems.size());
		JsonWriter.writeObject(obj, outFile);
		return obj;
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

		try {
			HashMap<String, Integer> obj = processFile(inFile, inString, outFile);
		} catch (IOException e) {
			System.out.println("File not found");
		}

		
	}
}
