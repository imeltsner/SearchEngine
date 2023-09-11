package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

		Indexer index = new Indexer();
		ArgumentParser parser = new ArgumentParser(args);
		
		if (parser.hasFlag("-text")) {

			Path input = parser.getPath("-text");

			try {
				if (Files.isDirectory(input)) {
					Processor.processDir(input, index);
				}
				else {
					Processor.processFile(input, index);
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
			
			Path countsOutput = parser.getPath("-counts", Path.of("counts.json"));

			try {
				JsonWriter.writeObject(index.getCounts(), countsOutput);
			}
			catch (IOException e) {
				System.out.println("Counts output file not found");
			}
		}

		if (parser.hasFlag("-index")) {

			Path indexOutput = parser.getPath("-index", Path.of("index.json"));

			//print debugging
			// for (String word : index.getWordMap().keySet()) {
			// 	System.out.println(word);
			// 	for (String file : index.getFileMap(word).keySet()) {
			// 		System.out.println("\t" + file + " : " + index.getFileMap(word).get(file).toString());
			// 	}
			// }

			//System.out.println(JsonWriter.writeInvertedIndex(index));

			try {
				JsonWriter.writeInvertedIndex(index, indexOutput);
			} catch (IOException e) {
				System.out.println("Index output file not found");
			}
		}
	}
}
