package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.TreeMap;

public class Indexer {

    private TreeMap<String, Integer> wordCounts;
    private TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndex; 

    public Indexer() {
        wordCounts = new TreeMap<>();
        invertedIndex = new TreeMap<>();
    }

    public TreeMap<String, Integer> getCounts() {
        return wordCounts;
    }

    public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getIndex() {
        return invertedIndex;
    }
}
