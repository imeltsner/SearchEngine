package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class to crawl links based on a seed url and add the contents of web pages to an inverted index
 * 
 * @author Isaac Meltsner
 */
public class WebCrawler {
	/** The total number of links to crawl */
	private int maxLinks;

	/** The intial url to crawl */
	private final String seed;

	/** The work queue to use */
	private final WorkQueue queue;

	/** A list of all the urls to crawl */
	private final Set<URL> URLs;

	/** The inverted index to use */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Initializes the web crawler with a seed url, a work queue, an inverted index, and a max links to crawl
	 * 
	 * @param seed the seed url to start the crawl
	 * @param maxLinks the total number of links to crawl
	 * @param queue the work queue to use
	 * @param index the inverted index to use
	 */
	public WebCrawler(String seed, int maxLinks, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = maxLinks > 0 ? maxLinks : 1;
		this.queue = queue;
		this.URLs = new HashSet<>();
		this.index = index;
	}

	/**
	 * Initializes the web crawler with a seed url, a work queue, and an inverted index
	 * 
	 * @param seed the seed url to start the crawl
	 * @param queue the work queue to use
	 * @param index the inverted index to use
	 */
	public WebCrawler(String seed, WorkQueue queue, ThreadSafeInvertedIndex index) {
		this.seed = LinkFinder.removeFragment(seed);
		this.maxLinks = 1;
		this.queue = queue;
		this.URLs = new HashSet<>();
		this.index = index;
	}

	/**
	 * Starts a web crawl using the seed link
	 * 
	 * @throws MalformedURLException if the seed link is not a valid url
	 * @throws NullPointerException if a null error occurs
	 */
	public void crawlLinks() throws MalformedURLException, NullPointerException { // TODO seed should be a param here
		URL seedURL = new URL(seed);
		URLs.add(seedURL);
		Task task = new Task(seedURL, index);
		queue.execute(task);
		queue.finish();
	}

	/**
	 * Creates a task for each link provided that cleans and scrapes html and adds the contents to the inverted index
	 * 
	 * @param links the links to crawl
	 */
	private void crawlLinks(List<URL> links) {
		synchronized (URLs) {
			for (URL link : links) {
				if (URLs.size() > maxLinks) {
					break;
				}
				
				if (!URLs.contains(link) && URLs.size() < maxLinks) { // TODO Can remove && URLs.size() < maxLinks
					URLs.add(link);
					Task task = new Task(link, index);
					queue.execute(task);
				}
			}
		}
	}

	/** Cleans and parses html, adds contents to inverted index, and find links on the page */
	private class Task implements Runnable {
		/** The url to parse */
		private final URL url;

		/** The main thread inverted index to use */
		private final ThreadSafeInvertedIndex index;

		/** The local index */
		private final InvertedIndex local;

		/** The stemmer to use */
		private final Stemmer stemmer;

		/**
		 * Creates a task to parse a url and add its contents to the inverted index
		 * 
		 * @param url the url to parse and clean
		 * @param index the index to use
		 */
		public Task(URL url, ThreadSafeInvertedIndex index) {
			this.url = url;
			this.index = index;
			this.local = new InvertedIndex();
			this.stemmer = new SnowballStemmer(ENGLISH);
			
			// TODO URLs.add(link);
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);

			if (html == null) {
				return;
			}
			
			// TODO stripBlockElements is called twice
			
			crawlLinks(LinkFinder.listUrls(url, HtmlCleaner.stripBlockElements(html)));

			InvertedIndexProcessor.processString(HtmlCleaner.stripHtml(html), local, url.toString(), 0, stemmer);
			index.addAll(local);
		}
	}
}
