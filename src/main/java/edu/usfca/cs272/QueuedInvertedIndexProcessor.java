package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A thread safe version of the InvertedIndexProcessor class
 * 
 * @author Isaac Meltsner
 */
public class QueuedInvertedIndexProcessor extends InvertedIndexProcessor {
     /**
	 * Recursively iterates through a directory checks if files are text files
	 * adds contents of text files to inverted index
	 * @param path path of directory
	 * @param index the Indexer object
     * @param queue the work queue to use
	 * @throws IOException if IO error occurs
	 * @throws NotDirectoryException if given path is not a directory
	 */
	public static void processDir(Path path, InvertedIndex index, WorkQueue queue) throws IOException, NotDirectoryException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);) {

			var iterator = stream.iterator();

			while (iterator.hasNext()) {

				Path newPath = iterator.next();
				
				if (Files.isDirectory(newPath)) {
					processDir(newPath, index, queue);
				}
				else if (isTextFile(newPath)) {
					Task task = new Task(newPath, index);
                    queue.execute(task);
				}
			}
		} 
	}

    /**
	 * Processes path according to path type
	 * @param path the path to process
	 * @param index the inverted index
     * @param queue the work queue to use
	 * @throws IOException if IOError occurs
	 * @throws NullPointerException if null pointer is found
	 */
	public static void process(Path path, InvertedIndex index, WorkQueue queue) throws IOException, NullPointerException {
		if (Files.isDirectory(path)) {
			processDir(path, index, queue);
		}
		else {
            Task task = new Task(path, index);
            queue.execute(task);
		}
	}

    /**
     * Processes a single file
     */
    private static class Task implements Runnable {
        /** The path of the file to process */
        private final Path path;

        /** The inverted index to use */
        private final InvertedIndex index;

        private final Stemmer stemmer = new SnowballStemmer(ENGLISH);

        /**
         * Class constructor
         * @param path the path of a file
         * @param index the inverted index to use
         */
        private Task(Path path, InvertedIndex index) {
            this.path = path;
            this.index = index;
        }

        @Override
        public void run() {
            try {
                processFile(path, index, stemmer);
            }
            catch (IOException e) {
                System.out.println("IO ERROR");
            }
        }
    }
}
