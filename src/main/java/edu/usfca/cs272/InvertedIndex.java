package edu.usfca.cs272;

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
    private TreeMap<String, Integer> wordCounts;

    /**
     * Stores words, filenames, and positions in file
     */
    private TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

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
    public TreeMap<String, Integer> getCounts() {
        return wordCounts;
    }

    
    /**
     * Getter function for retrieving the inverted index data structure
     * @return the inverted index
     */
    public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getWordMap() {
        return invertedIndex;
    }

    /**
     * Getter function for retrieving TreeMap associated with a word
     * containing file names and word positions
     * @param word key associated with inner TreeMap
     * @return inner TreeMap of inverted index
     */
    public TreeMap<String, TreeSet<Integer>> getFileMap(String word) {
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
        getFileMap(word).putIfAbsent(fileName, new TreeSet<>());
        putPosition(word, fileName, position);
    }
    
    /*
     * TODO 
     * More generally useful data structure methods here
     * 
     * boolean methods
     * less nested get methods
     * toString
     */
}
