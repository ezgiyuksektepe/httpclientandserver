package homework1;

/**
 *
 * @author ezgiyuksektepe
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
        
public class HttpClient {
    
    
public static void main(String[] args) throws Exception 
{

    HttpClient h= new HttpClient();
    
}

//Constructor to Homework1 class. Gets the desired server/ip and file from the user 
//then calls the GetFromSocket function which returns the desired files html code as string
//after calls the Visualize function that uses the return value to visualizing the html code received.
private HttpClient()
{
    
    System.out.println("Please enter the server you want to connect... ");
    Scanner s = new Scanner(System.in);
    String host = s.nextLine();
    
    System.out.println("Please type in the path/file you want to reach... Press enter to see the Index...");
    Scanner x = new Scanner(System.in);
    String path = x.nextLine();
    
    String html = null;
    html = GetFromSocket(host,path);
    visualize(html,host);    

}
     
//Function to receive the desired file from desired server using HTTP protocol.
//Function takes two string as parameters: first one is the server name/ip and 
//the second one can be either a file name/path to the desired html or tab to enter.
//Returns the html file content as as string
public String GetFromSocket(String host, String path) 
{    
    
    Socket socket=null;
    OutputStream os=null;
    PrintWriter out=null;
    BufferedReader in =null;
    StringBuilder builder=null;   

    try{
   //Since it is HTTP protocol socket is opened on port 80
    System.out.println("Trying to connect the server "+host+"...");  
    socket = new Socket(host, 80);
    
   //Checks if connection established 
     if(socket.isConnected())
     { 
         
         System.out.println("Connected to "+host);
     
     }

    boolean autoflush = true;
    out = new PrintWriter(socket.getOutputStream(), autoflush);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    boolean x = true;
    builder = new StringBuilder(8096);
    
    out.println("GET /"+path  +" HTTP/1.1");
    out.println("Host: " +host + ":80");
    out.println("Connection: Close");
    out.println();
    
    
    while (x) 
    {
        
      if (in.ready()) {
          
        int i = 0;
        
        while (i != -1) 
        {
            
          i = in.read();
          builder.append((char) i);
          
        }
        
        x = false;
        
      }
    }
    //System.out.println(builder.toString());

   
    }
    catch (UnknownHostException e) 
    {
            System.err.println("Don't know about host: "+host);
            System.exit(1);
    } 
    catch (IOException e) 
    {
        
            System.err.println("Couldn't get I/O for the connection to: "+host);
            System.exit(1);
            
    }
    
    try 
    {
        
        socket.close();
        
    } catch (IOException ex) 
    {
        
        Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        
    }
  
    //Returns the html file as string
    return builder.toString();
  
}
  
//Function to visualize a html code.
//Function takes two string as parameters: first one is the html code and 
//the second one is the server name/ip. Uses JFrame to visualize. 
//Returns void.    
public void visualize(String html,String host)
{


    JEditorPane ePane = new JEditorPane();
    ePane.setEditable(false);   
    ePane.setContentType("text/html");
    ePane.setText(html);
    JScrollPane scrollPane = new JScrollPane(ePane);     
    JFrame f = new JFrame("Test HTML");
    HyperlinkListener listener;
    //hyperlinklistener is added to catch the clicks on the links.

    listener = new HyperlinkListener() 
    {
         
    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) 
    {

        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
        {
            String s = event.toString();
            String x = event.getDescription();
            System.out.println(x);
            ePane.setText(GetFromSocket(host,x));
  
        }

    }

};
               
ePane.addHyperlinkListener(listener);
f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
f.getContentPane().add(scrollPane);
f.setPreferredSize(new Dimension(800,600));
f.setVisible(true);

}
}
