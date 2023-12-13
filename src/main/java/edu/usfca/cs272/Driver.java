package edu.usfca.cs272;

import java.io.IOException;
import java.net.MalformedURLException;
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

		InvertedIndex index = null;
		ThreadSafeInvertedIndex safe = null;
		WorkQueue queue = null;
		ArgumentParser parser = new ArgumentParser(args);
		SearchProcessor processor = null;

		boolean html = parser.hasFlag("-html");
		boolean launchServer = parser.hasFlag("-server");
		boolean multiThread = parser.hasFlag("-threads") || html || launchServer;

		if (multiThread) {
			safe = new ThreadSafeInvertedIndex();
			index = safe;

			int threads = parser.getInteger("-threads", 5);

			if (threads < 1) {
				threads = 5;
			}

			queue = new WorkQueue(threads);
			processor = new QueuedSearchProcessor(safe, parser.hasFlag("-partial"), queue);
		}
		else {
			index = new InvertedIndex();
			processor = new BasicSearchProcessor(index, parser.hasFlag("-partial"));
		}
		
		if (parser.hasFlag("-text")) {

			Path input = parser.getPath("-text");

			try {
				if (multiThread) {
					QueuedInvertedIndexProcessor.process(input, safe, queue);
				}
				else {
					InvertedIndexProcessor.process(input, index);
				}
			} 
			catch (IOException e) {
				System.out.println("Unable to process file at path: " + input.toString());
			}
			catch (NullPointerException e) {
				System.out.println("-text flag is missing a value");
			}
		}

		if (html) {

			int maxLinks = parser.getInteger("-crawl", 1);
			
			try {
				String seed = parser.getString("-html");
				WebCrawler crawler = new WebCrawler(seed, maxLinks, queue, safe);
				crawler.crawl();
			}
			catch (NullPointerException | MalformedURLException e) {
				System.out.println("Invalid url");
			}
		}

		if (parser.hasFlag("-query")) {

			Path queryFile = parser.getPath("-query");

			try {
				processor.processFile(queryFile);
			}
			catch (IOException e) {
				System.out.println("Unable to process file at path: " + queryFile.toString());
			}
			catch (NullPointerException e) {
				System.out.println("-query flag is missing a value");
			}
		}

		if (queue != null) {
			queue.join();
		}
		
		if (parser.hasFlag("-counts")) {
			
			Path countsOutput = parser.getPath("-counts", Path.of("counts.json"));

			try {
				index.writeCounts(countsOutput);
			}
			catch (IOException e) {
				System.out.println("Unable to write to file at path: " + countsOutput.toString());
			}
		}

		if (parser.hasFlag("-index")) {

			Path indexOutput = parser.getPath("-index", Path.of("index.json"));

			try {
				index.writeInvertedIndex(indexOutput);
			} 
			catch (IOException e) {
				System.out.println("Unable to write to file at path: " + indexOutput.toString());
			}
		}

		if (parser.hasFlag("-results")) {
			
			Path searchOutput = parser.getPath("-results", Path.of("results.json"));

			try {
				processor.writeSearchResults(searchOutput);
			}
			catch (IOException e) {
				System.out.println("Unable to write to file at path: " + searchOutput.toString());
			}
		}

		if (launchServer) {

			int port = parser.getInteger("-server", 8080);

			try {
				SearchEngine searchEngine = new SearchEngine(port, safe);
				searchEngine.start();
			}
			catch (IOException e) {
				System.out.println("Servlet handler error");
			}
			catch (Exception e) {
				System.out.println("Server unable to start");
			}
		}
	}
}
