package edu.usfca.cs272;

import java.util.HashSet;
import java.util.Set;

public class WebCrawler {
	/** The total number of links to crawl */
	private int maxLinks;

	/** The intial url to crawl */
	private final String seed;

	/** The work queue to use */
	private final WorkQueue queue;

	/** A set of all urls that have been crawled */
	private final Set<String> seenURLs;

	/** The inverted index to use */
	private final ThreadSafeInvertedIndex index;

	public WebCrawler(String seed, int maxLinks, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = maxLinks > 0 ? maxLinks : 1;
		this.queue = queue;
		this.seenURLs = new HashSet<>();
		this.index = index;
	}

	public WebCrawler(String seed, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = 1;
		this.queue = queue;
		this.seenURLs = new HashSet<>();
		this.index = index;
	}

	public void crawlLinks() {
		String html = HtmlFetcher.fetch(seed, 3);
		String cleanHtml = HtmlCleaner.stripHtml(html);
		InvertedIndexProcessor.processString(cleanHtml, index, seed, 0);
	}
}
