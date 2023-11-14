package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface SearchProcessor {
    public abstract void processFile(Path path) throws IOException;

    public abstract void processLine(String line);

    public abstract void writeSearchResults(Path path) throws IOException;

    public abstract Set<String> viewQueries();

    public abstract List<InvertedIndex.SearchResult> viewResult(String line);

    public abstract boolean hasQuery(String line);

    public abstract int numQueries();

    public abstract int numResults(String line);
}