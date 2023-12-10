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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchEngineServlet extends HttpServlet {
	private static final long serialVersionUID = 202308;
	private static final String title = "isaac.search";
	private static final Logger log = LogManager.getLogger();
	private static final Path base = Path.of("src", "main", "resources", "html");
	public static final String longDateFormat = "hh:mm a 'on' EEEE, MMMM dd yyyy";
	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(longDateFormat);

	private final String headTemplate;
	private final String footTemplate;

	public SearchEngineServlet() throws IOException {
		super();
		System.out.println(base);
		headTemplate = Files.readString(base.resolve("bulma-head.html"), UTF_8);
		footTemplate = Files.readString(base.resolve("bulma-foot.html"), UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", title);
		values.put("thread", Thread.currentThread().getName());
		values.put("updated", dateFormatter.format(LocalDateTime.now()));
		//values.put("updated", "now");
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		StringSubstitutor replacer = new StringSubstitutor(values);
		String head = replacer.replace(headTemplate);
		String foot = replacer.replace(footTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.println(head);

		// TODO

		out.println(foot);
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		String query = request.getParameter("name");

		query = query == null || query.isBlank() ? "" : query;

		query = StringEscapeUtils.escapeHtml4(query);

		// Message current = null; // TODO
		// log.info("Created message: {}", current);

		// synchronized (messages) {
		// 	messages.add(current);

		// 	while (messages.size() > 5) {
		// 		Message first = messages.poll();
		// 		log.info("Removing message: {}", first);
		// 	}
		// }

		response.sendRedirect(request.getServletPath());
	}
}
