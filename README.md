# Search Engine

This project is a multi-threaded search engine that uses a web crawler to parse content from web pages and store data in an inverted index. 
Users can make queries to the inverted index and see all of the links to pages where their query string was found. Users can also view the entire contents of the inverted index. 

## Usage

1. Clone the repo
2. Start the program with the following command line arguments (see [Optional Arguments](#optional-arguments))
    - ``-html [link]`` where the **[link]** argument is the url to begin the web crawl to build the inverted index
    - **-server [port]** where the **[port]** argument is the port the web server will use to accept connections. Defaults to 8080
3. Open a web browser and navigate to http://localhost:[port]/search where **[port]** is the port number you specified. Users can search the inverted index here.
4. Additional endpoints
    - /index - this page allows users view the entire contents of the inverted index, or search for a specific word, where they will be shown a sub-index if the word is in the inverted index

#### Optional Arguments

- **-crawl [value]** where the **[value]** argument specifies the maximum number of links to crawl. Defaults to 1
- **-threads [value]** where the **[value]** argument specifies the number of worker threads to use. Defaults to 5
- **-text [path]** where the **[path]** argument is a path to a file or directory containing text files to store in the inverted index
    - use this argument in place of -html if you want to build the inverted index based on locally stored files instead of a web crawl
- **-counts [path]** where the **[path]** argument is a path to a file where locations and counts in the inverted index will be output in JSON format. Defaults to [counts.json]
- **-index [path]** where the **[path]** argument is a path to a file where the contents of the inverted index will be output in JSON format. Defaults to [index.json]
- **-query [path]** where the **[path]** argument is a path to a file containing queries for the inverted index

