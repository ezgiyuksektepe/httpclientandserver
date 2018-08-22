/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ezgiyuksektepe
 */
public class HttpServer implements Runnable {

int port = 80;
ServerSocket  sSocket = null;
Thread curThread = null;
boolean isRunning = true;



public HttpServer(int port) {
    this.port = port;
}

public void run () {
    synchronized(this) {
            this.curThread = Thread.currentThread();
        }
        openServerSocket();
        
        while(isServerRunning()) {
            Socket cSocket = null; // Client Socket 
            try {
                cSocket = this.sSocket.accept(); 
            } catch (IOException e) {
                if(!isServerRunning()) {
                    System.out.println("Server stopped running.") ;
                    return;
                }
                throw new RuntimeException("Error occured while accepting the client connection", e);
            }
            try {
                processClientRequest(cSocket);
            } catch (IOException e) {
                //log exception and go on to next request.
            }
        }
        
        System.out.println("Server stopped running.");
}

private void openServerSocket () {
        try {
            this.sSocket = new ServerSocket(this.port);  //Port = 80
        } catch (IOException e) {
            throw new RuntimeException("Error occured while opening port 80", e);
        }
}

private synchronized boolean isServerRunning () {
        return this.isRunning;
}

private static void processClientRequest (Socket cSocket) throws IOException {
        OutputStream output = cSocket.getOutputStream();
        InputStreamReader  input  =  new InputStreamReader(cSocket.getInputStream());
        BufferedReader br = new BufferedReader(input);

        String page = br.readLine(); // Gets the requested page from client
        
        String[] request = page.split("/"); //Splits the request in order to get the path 
        String[] path = request[1].split(" ");
        
        page = path[0]; 
       
        try {
            File file;
            if (page.isEmpty()) {
                file = new File ("Index.html");
            }
            else if(!isValid(page)) {
                file = new File("PageNotFound.html");
            }
             else {
                file = new File (page);
            }
            FileInputStream  fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte [] mybytearray  = new byte [(int)file.length()];

            bis.read(mybytearray,0,mybytearray.length);
            output.write(mybytearray);
            
          
            
            bis.close();
         } catch (FileNotFoundException e) {
                  System.out.println(e.getMessage());
         }
        
        input.close();
        output.close();
}

public static boolean isValid (String pick) {
        switch (pick) {
            case "Koudelka.html" :
                return  true;
            case "India-SteveMcCurry.html" :
                return true;
            case "Binghamton.html" :
                return true;
            case "Dilbert.html" :
                return true;
            default :
                return false;      
        }
}

public synchronized void stop () {
        this.isRunning = false;
        try {
            this.sSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error occured while closing the server", e);
        }
 }

public static void main (String[] args)  {     
       HttpServer server = new HttpServer(80); //This is not the HttpServer class from Java Library, it is the constructor of the program
       new Thread(server).start();
       
       try {
            Thread.sleep(100 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  
        }
        System.out.println("Server stopped running.");
        server.stop();    
}

}
