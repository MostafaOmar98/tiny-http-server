# tiny-http-server  

Tiny http server/client using socket programming with java.  

# Server
The server supports 4 functions (GET, POST, PUT, DELETE)  to operate on files which are stored locally in 'D:/htdocs"  
Server implements multi-threading which allows multiple client to send requests at once  

# NOTES:  
- To change the location for which the files are stored, you need to change the path in file 'SingleThread.java' in 3 places: 
  - Line 28: FilePath variable
  - Line 121: opening the 404.html
  - Line 143: opening the 415.html  
- Server supports .txt and .html extensions only, otherwise it will respond with 415 unsupported media type.
- At the client, do NOT start your URI with the directory in which your files are stored, it is appended automatically in the server.
  Example: if we want to fetch D:/htdocs/index.html at the client, we're only gonna enter /index.html in the URI field
