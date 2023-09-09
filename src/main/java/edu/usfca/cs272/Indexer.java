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

    public TreeMap<String, Integer> getCounts() {
        return wordCounts;
    }

    public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getOuter() {
        return invertedIndex;
    }

    public TreeMap<String, ArrayList<Integer>> getInner(String key) {
        return invertedIndex.get(key);
    }

    public boolean containsWord(String word) {
        return invertedIndex.get(word) == null;
    }
}
