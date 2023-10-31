package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/** 
 * A class to store an inverted index and 
 * map of wordcounts
 * 
 * @author Isaac Meltsner
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
    private MultiReaderLock lock;

    public ThreadSafeInvertedIndex() {
        super();
        lock = new MultiReaderLock();
    }

    @Override
    public Map<String, Integer> viewCounts() {
        lock.readLock().lock();

        try {
            return super.viewCounts();
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> viewWords() {
        lock.readLock().lock();
        
        try {
            return super.viewWords();
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> viewLocations(String word) {
        lock.readLock().lock();

        try {
            return super.viewLocations(word);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Integer> viewPositions(String word, String location) {
        lock.readLock().lock();

        try {
            return super.viewPositions(word, location);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getCount(String location) {
        lock.readLock().lock();

        try {
            return super.getCount(location);
        }
        finally {
            lock.readLock().lock();
        }
    }

    @Override
    public int numCounts() {
        lock.readLock().lock();

        try {
            return super.numCounts();
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int numWords() {
        lock.readLock().lock();

        try {
            return super.numWords();
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int numLocations(String word) {
        lock.readLock().lock();

        try {
            return super.numLocations(word);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int numPositions(String word, String location) {
        lock.readLock().lock();

        try {
            return super.numPositions(word, location);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasCount(String location) {
        lock.readLock().lock();

        try {
            return super.hasCount(location);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasWord(String word) {
        lock.readLock().lock();

        try {
            return super.hasWord(word);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasLocation(String word, String location) {
        lock.readLock().lock();

        try {
            return super.hasLocation(word, location);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasPosition(String word, String location, int position) {
        lock.readLock().lock();

        try {
            return super.hasPosition(word, location, position);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void addData(String word, String location, int position) {
        lock.writeLock().lock();
        
        try {
            super.addData(word, location, position);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void writeCounts(Path path) throws IOException {
        lock.writeLock().lock();

        try {
            super.writeCounts(path);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void writeInvertedIndex(Path path) throws IOException {
        lock.writeLock().lock();

        try {
            super.writeInvertedIndex(path);
        }
        finally {
            lock.writeLock().unlock();
        }
    }
}