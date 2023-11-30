package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {
	/** The total number of links to crawl */
	private int maxLinks;

	/** The intial url to crawl */
	private final String seed;

	/** The work queue to use */
	private final WorkQueue queue;

	/** A list of all the urls to crawl */
	private final List<URL> URLs;

	/** The inverted index to use */
	private final ThreadSafeInvertedIndex index;

	public WebCrawler(String seed, int maxLinks, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = maxLinks > 0 ? maxLinks : 1;
		this.queue = queue;
		this.URLs = new ArrayList<>();
		this.index = index;
	}

	public WebCrawler(String seed, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = 1;
		this.queue = queue;
		this.URLs = new ArrayList<>();
		this.index = index;
	}

	public void crawlLinks() throws MalformedURLException, NullPointerException {
		URL seedURL = new URL(seed);
		if (URLs.size() < maxLinks) {
			URLs.add(seedURL);
		}

		String html = HtmlFetcher.fetch(seed, 3);
		HashSet<URL> uniqueLinks = LinkFinder.uniqueUrls(new URL(seed), html);
		URLs.addAll(uniqueLinks);

		String cleanHtml = HtmlCleaner.stripHtml(html);
		InvertedIndexProcessor.processString(cleanHtml, index, seed, 0);

		if (maxLinks > 1 && URLs.size() > 1) {
			for (int i = 1; i < URLs.size(); i++) {
				Task task = new Task(URLs.get(i), index);
				queue.execute(task);
			}
		}
	}

	private class Task implements Runnable {
		/** The url to parse */
		private final URL url;

		/** The main thread inverted index to use */
		private final ThreadSafeInvertedIndex index;

		/** The local index */
		private final InvertedIndex local;

		/** The stemmer to use */
		private final Stemmer stemmer;

		public Task(URL url, ThreadSafeInvertedIndex index) {
			this.url = url;
			this.index = index;
			this.local = new InvertedIndex();
			this.stemmer = new SnowballStemmer(ENGLISH);
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url);
			HashSet<URL> uniqueLinks = LinkFinder.uniqueUrls(url, html);

			synchronized (URLs) {
				URLs.addAll(uniqueLinks);
			}

			String cleanHtml = HtmlCleaner.stripHtml(html);
			InvertedIndexProcessor.processString(cleanHtml, local, url.toString(), 0, stemmer);
			index.addAll(local);
		}
	}
}
