import java.io.*;
import java.net.*;




public class MainServer {

    public static void main(String[] args) {
        try
        {
            ServerSocket serverSocket = new ServerSocket(8080); //create a server socket object
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nA New Request Has Arrived!\n");
                SingleThread SingleThread = new SingleThread(clientSocket);//create a new thread for each client
                SingleThread.start();
            }


        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

}