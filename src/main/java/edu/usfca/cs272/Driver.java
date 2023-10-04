package edu.usfca.cs272;

import java.io.IOException;
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

		InvertedIndex index = new InvertedIndex();
		ArgumentParser parser = new ArgumentParser(args);
		
		if (parser.hasFlag("-text")) {

			Path input = parser.getPath("-text");

			try {
				InvertedIndexProcessor.process(input, index);
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
				index.writeCounts(countsOutput);
			}
			catch (IOException e) {
				System.out.println("Counts output file not found");
			}
		}

		if (parser.hasFlag("-index")) {

			Path indexOutput = parser.getPath("-index", Path.of("index.json"));

			try {
				index.writeInvertedIndex(indexOutput);
			} 
			catch (IOException e) {
				System.out.println("Index output file not found at path" + indexOutput.toString());
			}
		}

		QueryFileProcessor processor = new QueryFileProcessor(index);

		if (parser.hasFlag("-query")) {

			Path queryFile = parser.getPath("-query");

			try {
				processor.processFile(queryFile);
			}
			catch (IOException e) {
				System.out.println("Query file not found");
			}
		}

		if (parser.hasFlag("-results")) {
			
			Path searchOutput = parser.getPath("-results", Path.of("results.json"));

			try {
				JsonWriter.writeSearchResults(processor.getExactSearchResults(), searchOutput);
			}
			catch (IOException e) {
				System.out.println("Results file not found");
			}
		}
	}
}
