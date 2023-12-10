package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A class for the home page of the search engine
 * 
 * @author Isaac Meltsner
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class SearchEngineServlet extends HttpServlet {
	/** Not used */
	private static final long serialVersionUID = 1L;

	/** Title of webpage */
	private static final String title = "Isaac Search";

	/** Logger to use */
	private static final Logger log = LogManager.getLogger();

	/** Path to html files */
	private static final Path base = Path.of("src", "main", "resources", "html");

	/** Date format */
	public static final String longDateFormat = "hh:mm a 'on' EEEE, MMMM dd yyyy";

	/** Date formatter */
	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(longDateFormat);

	/** Top the of html page */
	private final String headerTemplate;

	/** Body of the html page */
	private final String formTemplate;

	/** Bottom of the html page */
	private final String footerTemplate;

	/** Bottom of the html page */
	private final String allResultsTemplate;

	/** Template for a single search result */
	private final String resultTemplate;

	/** The inverted index to use */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Initializes the servlet with the inverted index and the path to all the templates
	 * 
	 * @param index the index to use
	 * @throws IOException if an IO error occurs
	 */
	public SearchEngineServlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		headerTemplate = Files.readString(base.resolve("header.html"), UTF_8);
		formTemplate = Files.readString(base.resolve("form.html"), UTF_8);
		footerTemplate = Files.readString(base.resolve("footer.html"), UTF_8);
		allResultsTemplate = Files.readString(base.resolve("all-results.html"), UTF_8);
		resultTemplate = Files.readString(base.resolve("result.html"));
		
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		Map<String, String> values = setValues(request);
		StringSubstitutor replacer = new StringSubstitutor(values);
		String head = replacer.replace(headerTemplate);
		String form = replacer.replace(formTemplate);
		String foot = replacer.replace(footerTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		if (index.numWords() == 0) {
			String warning = """
				<div class="notification is-warning">
					<button class="delete"></button>
					<p><strong>WARNING:</strong> inverted index is empty</p>
			  	</div>
					""";
			out.println(warning);
		}

		out.println(head);
		out.println(form);
		out.println(foot);
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		String queryString = request.getParameter("query");
		queryString = queryString == null || queryString.isBlank() ? "" : queryString;

		queryString = StringEscapeUtils.escapeHtml4(queryString);

		TreeSet<String> query = FileStemmer.uniqueStems(queryString);
		ArrayList<InvertedIndex.SearchResult> results = index.search(query, true);

		Map<String, String> values = setValues(request);
		values.put("query", queryString);

		synchronized (results) {

			StringBuilder resultsHTML = new StringBuilder();

			if (results.isEmpty()) {
				resultsHTML.append("<p>No results.</p>");
			}
			else {
				for (InvertedIndex.SearchResult result : results) {
					values.put("result", result.getLocation());
					resultsHTML.append(StringSubstitutor.replace(resultTemplate, values));
				}
			}

			values.put("results-list", resultsHTML.toString());
		}
		
		StringSubstitutor replacer = new StringSubstitutor(values);
		String head = replacer.replace(headerTemplate);
		String form = replacer.replace(formTemplate);
		String allResults = replacer.replace(allResultsTemplate);
		String foot = replacer.replace(footerTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.println(head);
		out.println(form);
		out.println(allResults);
		out.println(foot);
		out.flush();
	}

	/**
	 * Adds the values for the html template
	 * 
	 * @param request the http request to use
	 * @return a map of the template keys and corresponding values
	 */
	private Map<String, String> setValues(HttpServletRequest request) {
		Map<String, String> values = new HashMap<>();
		values.put("title", title);
		values.put("updated", dateFormatter.format(LocalDateTime.now()));
		values.put("method", "POST");
		values.put("action", request.getServletPath());
		return values;
	}
}
