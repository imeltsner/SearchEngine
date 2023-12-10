package edu.usfca.cs272;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchEngine {
    private final Server server;

    private final ServletHandler handler;

    public SearchEngine(int port) throws IOException, Exception {
        this.server = new Server(port);
        this.handler = new ServletHandler();

        handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/search");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
