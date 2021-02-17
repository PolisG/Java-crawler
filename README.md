# Java crawler
## A simplified implementation of a Web crawler  
  
A crawler is a distributed system employed by a Web search engine with the aim of fetching Web pages and files. The files are then stored into a local repository, and indexed by the engine’s text indexers.
  
1. The Client reads a local file that contains URLs and saves them into a list.
2. The Client sends the first URL of the list to the Server.
3. The Server downloads the content of the Web page under this URL.
4. The Server stores the HTML content into a file. The writing must be in “append” mode. The file may contain at most 10 Web pages. If this limit is reached, the file is closed and another file is opened.

...Example: Client has asked from the server to download the 23rd file.  
...The Server downloads the file and saves it in File 3 (Files 1 and 2 are full). Then, it responds to the Client that the Web page has been stored in File 3, in location 400 (=150+250), which is the starting position of Doc23 inside File3.

5. The Server sends back to the Client the filename where the document has been saved and the location in the file where the document has been saved.
6. The Client sends the next URL to the Server to be downloaded.
