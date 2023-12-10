package edu.usfca.cs272;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * A class to start a jetty server that hosts a basic search engine 
 * 
 * @author Isaac Meltsner
*/
public class SearchEngine {
    /** The server to use */
    private final Server server;

    /** The servlet handler to use */
    private final ServletHandler handler;

    /**
     * Initializes a server with the specified port. Creates a search engine servlet with the given inverted index
     * 
     * @param port the port to use
     * @param index the index to use
     * @throws IOException if an IO error occurs
     * @throws Exception if an error occurs
     */
    public SearchEngine(int port, ThreadSafeInvertedIndex index) throws IOException, Exception {
        this.server = new Server(port);
        this.handler = new ServletHandler();

        handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet(index)), "/search");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
