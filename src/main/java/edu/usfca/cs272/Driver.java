package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
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

	public static final String PATH_START = "/Users/isaacmeltsner/Desktop/CS/CS272-C/SearchEngine/project-tests/";

	/**
	 * Reads a file, cleans each word and stems words
	 * Adds stememd words to a list
	 * @param input path of the file
	 * @return list of stems
	 * @throws IOException
	 */
	public static HashMap<String, Integer> processFile(Path input, String inString, Path outFile) throws IOException {
		HashMap<String, Integer> obj = new HashMap<>();
		ArrayList<String> stems = FileStemmer.listStems(input);
		if (stems.size() != 0) {
			obj.put(inString, stems.size());
		}
		JsonWriter.writeObject(obj, outFile);
		return obj;
	}

	public static void processDir(Path inPath, Path outFile) throws IOException {
		DirectoryStream<Path> stream = Files.newDirectoryStream(inPath);
		var iterator = stream.iterator();
		while (iterator.hasNext()) {
			Path item = iterator.next();
			if (Files.isDirectory(item)) {
				System.out.println("In directory: " + item.toString());
				processDir(item, outFile);
			}
			else {
				if (item.toString().contains(".txt") || item.toString().contains(".text")) {
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

		//Arg processing
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-text")) {
				inString = args[++i];
			}
			else if (args[i].equals("-counts")) {
				try {
					outString = args[++i];
				} catch (IndexOutOfBoundsException e) {
					outString = "counts.json";
				}
			}
		}

		//create path objects
		if (!inString.isEmpty()) {
			inPath = Path.of(PATH_START,  inString);
		}
		outFile = Path.of(PATH_START, outString);

		if (Files.isDirectory(inPath)) {
			try {
				processDir(inPath, outFile);
			} catch (IOException e) {
				System.out.println("File not found");			}
		}
		else {
			try {
				HashMap<String, Integer> obj = processFile(inPath, inString, outFile);
			} catch (IOException e) {
				System.out.println("File not found");
			}
		}
		
	}
}
