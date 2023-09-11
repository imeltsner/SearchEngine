package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.TreeMap;

public class Indexer {
    /**
     * Stores filenames and wordcounts
     */
    private TreeMap<String, Integer> wordCounts;

    /**
     * Stores words, filenames, and positions in file
     */
    private TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndex; 

    public Indexer() {
        wordCounts = new TreeMap<>();
        invertedIndex = new TreeMap<>();
    }

    
    /**
     * Getter function for retrieving TreeMap containing
     * file names and word counts
     * @return the Treemap containing file names and word counts
     */
    public TreeMap<String, Integer> getCounts() {
        return wordCounts;
    }

    
    /**
     * Getter function for retrieving the inverted index data structure
     * @return the inverted index
     */
    public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getWordMap() {
        return invertedIndex;
    }

    /**
     * Getter function for retrieving TreeMap associated with a word
     * containing file names and word positions
     * @param word key associated with inner TreeMap
     * @return inner TreeMap of inverted index
     */
    public TreeMap<String, ArrayList<Integer>> getFileMap(String word) {
        return invertedIndex.get(word);
    }

    /**
     * Adds position data to the inverted index
     * @param word the word in the file
     * @param fileName the name of the file
     * @param position the words position in the file
     * 
     * @see #getFileMap(String)
     */
    public void putPosition(String word, String fileName, int position) {
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
        invertedIndex.putIfAbsent(word, new TreeMap<>());
        getFileMap(word).putIfAbsent(fileName, new ArrayList<>());
        putPosition(word, fileName, position);
    }
}
