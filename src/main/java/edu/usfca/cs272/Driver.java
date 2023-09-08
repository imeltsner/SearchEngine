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
	/**
	 * Initializes the classes necessary based on the provided command-line 
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		String inString = null;
		String outString = null;
		Path inPath = null;
		Path outFile = null;
		TreeMap<String, Integer> map = new TreeMap<>(); // TODO Move into its own class... that will eventually also have the inverted index in it
		Processor processor = new Processor(map);
		ArgumentParser parser = new ArgumentParser(args);
		
		/* TODO 
		if (parser.hasFlag("-text")) {
			Path input = parser.getPath("-text");
			
			try {
				
			}
			catch ( ) {
				Unable to index the file(s) at path: ...
			}
		}
		*/
		
		
		if (parser.hasFlag("-counts")) {
			outString = parser.getString("-counts", "counts.json");
			outFile = Path.of(outString);
		}

		if (parser.hasFlag("-text")) {
			inString = parser.getString("-text");
			inPath = (inString != null) ? Path.of(inString) : null;
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
					processor.processFile(inPath);
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
