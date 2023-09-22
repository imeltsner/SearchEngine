package edu.usfca.cs272;

import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/** 
 * A class to store an inverted index and a 
 * map of wordcounts
 * 
 * @author Isaac Meltsner
 */
public class InvertedIndex {
    /**
     * Stores filenames and wordcounts
     */
    private final TreeMap<String, Integer> wordCounts;

    /**
     * Stores words, filenames, and positions in file
     */
    private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

    /**
     * Class constructor to init map and inverted index
     */
    public InvertedIndex() {
        wordCounts = new TreeMap<>();
        invertedIndex = new TreeMap<>();
    }
    
    /**
     * Getter function for retrieving TreeMap containing
     * file names and word counts
     * @return the Treemap containing file names and word counts
     */
    public TreeMap<String, Integer> getWordCounts() {
        return wordCounts; // TODO Collections.unmodifableMap(...)
    }

    /**
     * Gets a word count from the wordCounts map
     * @param file the file associated with the count
     * @return the number of words in a file or 0 if file not found
     * 
     * @see #wordCountHas(String)
     */
    public int getCount(String file) {
    	// TODO return wordCounts.getOrDefault(file, 0);
        return wordCountHas(file) ? wordCounts.get(file) : 0;
    }

    /**
     * Checks if wordCount map contains a given file name
     * @param file the file name to check for
     * @return true if file name is in map false otherwise
     */
    public boolean wordCountHas(String file) {
        return wordCounts.containsKey(file);
    }
    
    /**
     * Getter function for retrieving the inverted index data structure
     * @return the inverted index
     */
    public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getInvertedIndex() { // TODO Eventually remove
        return invertedIndex;
    }

    /**
     * Checks if a given word is stored in the inverted index
     * @param word the word to check
     * @return true if word is in inverted index false otherwise
     */
    public boolean hasWord(String word) {
        return invertedIndex.containsKey(word);
    }

    /**
     * Checks if a given word was found in a given file in the inverted index
     * @param word the word to check
     * @param file the file to check
     * @return true if the word was found in the file, false if the word
     * is not in the inverted index or not found in the file
     * 
     * @see #hasWord(String)
     * @see #getFileMap(String)
     */
    public boolean inFile(String word, String file) {
        return hasWord(word) ? getFileMap(word).containsKey(file) : false;
    }
    
    // TODO foundAt(String word, String file, int position)

    /**
     * Getter function for retrieving TreeMap with all 
     * file names word appeared in and positions in the file word appeared
     * @param word key associated with inner TreeMap
     * @return inner TreeMap of inverted index
     */
    public TreeMap<String, TreeSet<Integer>> getFileMap(String word) { // TODO Remove
        return invertedIndex.get(word);
    }
    
    /*
     * TODO 

    public Set<Integer> getPositions(String word, String filename) {
    	return inFile(word, file) ? Collections.unmodifiableSet(invertedIndex.get(...).get(...)) : Collections.emptySet();
    }
    
    getLocations(String word) --> a view of the inner map keyset
    
    getWords() --> a view of the outer keyset
    
    numWords --> getWords().size() -or- have an efficient implementation
    */

    /**
     * Adds the position a word was found to the inverted index
     * @param word the word in the file
     * @param fileName the name of the file
     * @param position the words position in the file
     * 
     * @see #getFileMap(String)
     */
    public void putPosition(String word, String fileName, int position) { // TODO Remove or make private
        getFileMap(word).get(fileName).add(position);
    }

    /**
     * Adds words, filenames, and word position
     * to the inverted index
     * @param word the word to add
     * @param fileName the file to add
     * @param position the position of the word
     * 
     * @see #getFileMap(String)
     * @see #putPosition(String, String, int)
     */
    public void putData(String word, String fileName, int position) {
    	/* TODO 
    	invertedIndex.putIfAbsent(word, new TreeMap<>());
    	invertedIndex.get(word).putIfAbsent(word, new TreeSet<>());
    	invertedIndex.get(word).get(fileName).add(position);
    	
    	-or- look into computeIfAbsent
    	
    	Decide for the entire class if want to promote concise code or efficient code
    	*/
    	
        invertedIndex.putIfAbsent(word, new TreeMap<>());
        getFileMap(word).putIfAbsent(fileName, new TreeSet<>());
        putPosition(word, fileName, position);
    }
    
    @Override
    public String toString() {
        return invertedIndex.toString();
    }
    
    /* TODO 
    public void writeJson(Path path) throws IOException {
    	JsonWriter.writeInvertedIndex(invertedIndex, path);
    }
    */
}
