import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client implements Runnable
{  
   private Socket socket;
   private Thread thread;
   private DataInputStream  console;
   private DataOutputStream streamOut;
   private ClientThread client;
   private String userName;

   public Client(String userName, InetAddress serverName, int serverPort)
   {  
	  this.userName = userName;
	  System.out.println("Establishing connection ...");
      try
      {  
    	 socket = new Socket(serverName, serverPort);
         System.out.println("Connected ...");
         System.out.println("Please type in your Messages:");
         start();
      }
      catch(IOException ioe)
      {  
    	  System.out.println(ioe.getMessage()); 
	  }
   }
   
   public String getUserName()
   {
	   return userName;
   }
   
   
   @SuppressWarnings("deprecation")
   public void run()
   {  
	  while (thread != null)
      {  
		 try
         {  
    	  	streamOut.writeUTF(console.readLine());
            streamOut.flush();
         }
         catch(IOException ioe)
         {  
        	System.out.println(ioe.getMessage());
            stop();
         }
      }
   }
   
   public void handle(String msg)
   { 
	  if (msg.equals(".exit"))
      {  
		 System.out.println("Good bye.");
         stop();
      }
      else 
      {
    	 System.out.println(msg); 
      }         
   }
   
   public void start() throws IOException
   {  
	  console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  
    	 client = new ClientThread(this, socket, userName);
         thread = new Thread(this);                   
         thread.start();
      }
   }
   
   @SuppressWarnings("deprecation")
   public void stop()
   {  
	   if (thread != null)
	   { 
	   		thread.stop();  
	   		thread = null;
	   }
	   try
	   {  
		   if (console   != null) {
			   console.close();
		   }
    	  
		   if (streamOut != null) {
			   streamOut.close(); 
		   }
                                 
		   if (socket    != null){
			   socket.close();
		   }
	   }
	   catch(IOException ioe)
	   {  
		   	System.out.println("Error closing ..."); 
	   }
	   		client.close();  
	   		client.stop();
   }
   
   public static void main(String args[])
   {  
	  Client client;
	  BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	  try 
	  {
  		System.out.print("Enter your name: ");
	  	String userName = console.readLine();
  		System.out.print("Enter the server address: ");
  		InetAddress serverName = InetAddress.getByName(console.readLine());
  		System.out.print("Enter the host port: ");
  		int hostPort = Integer.parseInt(console.readLine());
  		client = new Client(userName, serverName, hostPort);
      } catch (IOException ioe) {
    	  System.out.println("IO error!");
          System.exit(1);
      }
   }
}