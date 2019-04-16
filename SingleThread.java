import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class SingleThread extends Thread
{
    private Socket socket;
    // Reads and prints to socket streams
    private BufferedReader in;
    private PrintWriter out;
    final static String CRLF = "\r\n";

    public SingleThread(Socket clientSocket)
    {
        this.socket = clientSocket;
    }

    public void run()
    {
        try
        {
            //create a buffer reader
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //create a PrintWriter
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            String line;
            String FilePath = "D:/htdocs"; // File path to the required file. NOTE: Can be changed
            String httpRequest = "";
            while (((line = in.readLine()) != null) && !line.equals("") && !line.equals(CRLF))
            {
                httpRequest += line;
                httpRequest += CRLF;
            }
            if (!httpRequest.equals("")) {
                String[] request = httpRequest.split(" ");
                System.out.println("Client HTTP Request Header: " + CRLF + httpRequest);
                if (request[0].equals("GET")) // if line contains get
                {
                    //extract the html filename
                    if (request[1].equals("/"))
                        request[1] = "/index.html";
                    FilePath += request[1];
                    processRequestGET(FilePath); // process the request
                }
                else if (request[0].equals("POST"))
                {
                    String requestBody = "";
                    while(((line = in.readLine()) != null) && !line.equals("") && !line.equals(CRLF))
                    {
                        requestBody += line;
                        requestBody += CRLF;
                    }
                    FilePath += request[1];
                    System.out.println("Client HTTP Request Body: " + CRLF + requestBody);
                    processRequestPOST(FilePath, requestBody);
                }
                else if(request[0].equals("DELETE"))
                {
                    FilePath += request[1];
                    System.out.println("INSIDE DELETE: " + FilePath);
                    File file = new File(FilePath);
                    if(file.delete())
                    {
                        System.out.println(FilePath + " has been deleted");
                    }
                    else
                    {
                        System.out.println("File cannot be found");
                    }
                }
            }


            closeConnection(); // close the socket connection and input/output buffers
        }
        catch(Exception e) //print error stack trace
        {
            e.printStackTrace();
        }
    }

    public void processRequestGET(String FilePath) throws Exception
    {
        File file = new File(FilePath); //create a file variable
        if(file.exists() && FilePath.contains(".html")) // if file exists
        {if(file.exists() && FilePath.contains(".html")) // if file exists
        {
            Scanner reader = new Scanner(new File (FilePath));
            //sent the HTTP head (HTTP 200 OK)
            out.print("HTTP/1.0 200 OK" + CRLF); // HTTP/1.1 results in a failure in showing the content of the file
            Date date = new Date();
            out.print("Date: " + date.toString() + CRLF);
            out.print("Server: java tiny web server" + CRLF);
            out.print("Content-Type: text/html" + CRLF);
            out.print("Content-Length: " + file.length() + CRLF);
            out.println("Content-Type: text/html; charset=iso-8859-1" + CRLF);
            //end of http header

            String line = "";
            while(reader.hasNextLine()) //read a line from the html file
            {
                line = reader.nextLine();
                out.println(line); //write the line to the socket connection
            }
            reader.close(); // must close reader to allow further access of file
        }
        }
        else if (!file.exists()) //if file does not exists
        {
            //sent the HTTP head (404 Not Found)
            out.print("HTTP/1.1 404 Not Found" + CRLF);
            Date date = new Date();
            out.print("Date: " + date.toString() + CRLF);
            out.print("Server: java tiny web server" + CRLF);
            out.print("Connection: close" + CRLF);
            out.println("Content-Type: text/html; charset=iso-8859-1" + CRLF);
            //end of http header

            //send file not found message
            file = new File("D:/htdocs/404.html");
            Scanner reader = new Scanner(new FileReader(file));
            String line;
            while (reader.hasNextLine())
            {
                line = reader.nextLine();
                out.println(line);
            }
            reader.close();
        }
        else
        {
            //sent the HTTP head (415 Unsupported Media Type)
            out.print("HTTP/1.1 415 Unsupported Media Type" + CRLF);
            Date date = new Date();
            out.print("Date: " + date.toString() + CRLF);
            out.print("Server: java tiny web server" + CRLF);
            out.print("Connection: close" + CRLF);
            out.println("Content-Type: text/html; charset=iso-8859-1" + CRLF);
            //end of http header

            //send file not found message
            file = new File("D:/htdocs/415.html");
            Scanner reader = new Scanner(new FileReader(file));
            String line;
            while (reader.hasNextLine())
            {
                line = reader.nextLine();
                out.println(line);
            }
            reader.close();
        }
    }

    public void processRequestPOST(String FilePath, String RequestBody) throws Exception {

        File file = new File(FilePath);
        FileWriter writer = new FileWriter(FilePath);
        if (file.exists())
        {
            writer.flush();
        }
        else
        {
            file.createNewFile();
        }
        writer.write(RequestBody);
        writer.flush();
        processRequestGET(FilePath);
        writer.close();
    }

    private void closeConnection()
    {
        try
        {
            out.close(); // close output stream
            in.close(); // close input stream
            socket.close(); //close socket
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
}