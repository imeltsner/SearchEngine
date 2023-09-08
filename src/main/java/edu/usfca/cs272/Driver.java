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

		TreeMap<String, Integer> map = new TreeMap<>(); // TODO Move into its own class... that will eventually also have the inverted index in it
		ArgumentParser parser = new ArgumentParser(args);
		
		if (parser.hasFlag("-text")) {

			Path input = parser.getPath("-text");

			try {
				if (Files.isDirectory(input)) {
					Processor.processDir(input, map);
				}
				else {
					Processor.processFile(input, map);
				}
			} 
			catch (IOException e) {
				System.out.println("File not found at path: " + input.toString());
			}
			catch (NullPointerException e) {
				System.out.println("Path to input file not found");
			}
		}
		
		if (parser.hasFlag("-counts")) {
			
			Path output = parser.getPath("-counts", Path.of("counts.json"));

			try {
				JsonWriter.writeObject(map, output);
			}
			catch (IOException e) {
				System.out.println("Output file not found");
			}
		}
	}
}
