package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
 * A servlet for the web page that displays the inverted index
 * 
 * @author Isaac Meltsner
 */
public class IndexServlet extends HttpServlet {
	/** Not used */
	private static final long serialVersionUID = 1L;

	/** Title of webpage */
	private static final String title = "WikiCrawl";

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

	/** The inverted index to use */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Initializes the servlet with the inverted index to use and the paths to the html files
	 * 
	 * @param index the inverted index to use
	 * @throws IOException if an IO error occurs
	 */
	public IndexServlet(ThreadSafeInvertedIndex index) throws IOException {
		headerTemplate = Files.readString(base.resolve("header.html"), UTF_8);
		formTemplate = Files.readString(base.resolve("form.html"), UTF_8);
		footerTemplate = Files.readString(base.resolve("footer.html"), UTF_8);
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		Map<String, String> values = setValues(request);
		StringSubstitutor replacer = new StringSubstitutor(values);
		PrintWriter out = printHeader(replacer, response);

		printIndex(index.viewWords(), out, replacer);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		Map<String, String> values = setValues(request);
		StringSubstitutor replacer = new StringSubstitutor(values);
		PrintWriter out = printHeader(replacer, response);

		String queryString = request.getParameter("query");
		queryString = queryString == null || queryString.isBlank() ? "" : queryString;
		queryString = StringEscapeUtils.escapeHtml4(queryString);
		TreeSet<String> query = FileStemmer.uniqueStems(queryString);

		printIndex(query, out, replacer);
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

	/**
	 * Outputs a warning to the web page if the inverted index is empty
	 * 
	 * @param out the print writer to use
	 */
	private void indexEmptyWaring(PrintWriter out) {
		if (index.numWords() > 0) {
			return;
		}
		else {
			String warning = """
					<div class="notification is-warning">
						<button class="delete"></button>
						<p><strong>WARNING:</strong> inverted index is empty</p>
					</div>
					""";
			out.println(warning);
		}
	}

	/**
	 * Outputs the title for this webpage
	 * 
	 * @param out the print writer to use
	 */
	private void pageTitle(PrintWriter out) {
		String html = """
				<div class="container"
					<div class="box">
						<div class="box">
							<h1 class="title">Inverted Index</h1>
						</div>
				""";
		out.println(html);
	}

	/**
	 * Outputs a word from the inverted index as html
	 * 
	 * @param word the word to output
	 * @param out the print writer to use
	 */
	private void outputWord(String word, PrintWriter out) {
		String html = """
					<div class=\"box\">
						<h3><strong>%s</strong><h3>
					""";
		html = String.format(html, word);
		out.println(html);
	}

	/**
	 * Outputs a location from the inverted index as html
	 * 
	 * @param word the word from the index
	 * @param location the location associated with the word
	 * @param out the print writer to use
	 */
	private void outputLocation(String word, String location, PrintWriter out) {
		String html = "<p><a href=%s>%s</a> | <strong>%d appearances</strong> </p>\n";
		html = String.format(html, location, location, index.numPositions(word, location));
		out.println(html);
	}

	private PrintWriter printHeader(StringSubstitutor replacer, HttpServletResponse response) throws IOException {
		String head = replacer.replace(headerTemplate);
		String form = replacer.replace(formTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		indexEmptyWaring(out);
		out.println(head);
		out.println(form);
		pageTitle(out);

		return out;
	}

	private void printIndex(Set<String> query, PrintWriter out, StringSubstitutor replacer) {
		boolean wordFound = false;

		for (String word : query) {
			if (index.viewWords().contains(word)) {
				wordFound = true;
				outputWord(word, out);

				for (String location : index.viewLocations(word)) {
					outputLocation(word, location, out);
				}

				out.println("</div>");
			}
		}

		if (!wordFound) {
			out.println("<p>Not found</p>");
		}

		String foot = replacer.replace(footerTemplate);
		out.println("</div>");
		out.println("</div>");
		out.println(foot);
		out.flush();
	}
}
