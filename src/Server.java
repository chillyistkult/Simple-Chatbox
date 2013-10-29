import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable
{  private ServerThread clients[] = new ServerThread[50]; //Anzahl der maximal möglichen Clients - schmiert bei 20+ schon ab wegen diversen Timeouts
   private ServerSocket server;
   private Thread thread;
   private int clientCount = 0; //aktuelle Anzahl verbundener Clients

   public Server(int port)
   {  
	  try
      {  
		  //Initialisierung des Sockets
		 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port); 
         System.out.println("Server started: " + server);
         start(); 
      }
      catch(IOException ioe)
      {  
    	  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
	  }
   }
   
   public void run()
   {  
	  //Server sucht nach Clients die sich verbinden wollen
	  while (thread != null)
      {  
		 try
         {  
			System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); 
         }
         catch(IOException ioe)
         {  
        	 System.out.println(ioe); 
        	 stop(); 
         }
      }
   }
   
   //Thread wird gestartet
   public void start()  
   {
	   if (thread == null)
	   {  
		   thread = new Thread(this); 
		   thread.start();
	   } 
   }
   
   //Thread wird angehalten
   @SuppressWarnings("deprecation")
   public void stop()   
   { 
	  if (thread != null)
	  {  
		  thread.stop(); 
		  thread = null;
	  } 
   }
   
   //Client kann über die Thread-Id gesucht werden
   private int searchClient(int ID)
   {  
	   for (int i = 0; i < clientCount; i++) 
	   {
		   if (clients[i].getID() == ID) {
			   return i;
		   }
	   }
	   return -1;
   }
   
   public synchronized void handle(int ID, String serverAddress, String input)
   { 
	  if (input.equals(".bye"))
      {  
		  clients[searchClient(ID)].send(".bye");
		  remove(ID); 
      }
      else
      {
         for (int i = 0; i < clientCount; i++) 
         {
            clients[i].send(serverAddress + ": " + input); 
         }
      }
   }
   
   @SuppressWarnings("deprecation")
   public synchronized void remove(int ID)
   {  
	  //Server sucht den zu entfernenden Client aus dem Array über die ID
	  int pos = searchClient(ID);
      if (pos >= 0)
      {  
    	 //entsprechender Server-Thread wird zugewiesen
    	 ServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
        	 //Array aus Clients wird defragmentiert wegen Lücken und so...
            for (int i = pos+1; i < clientCount; i++) {
               clients[i-1] = clients[i];
            }
         	clientCount--;
         try
         {  
        	 //und versucht zu beenden
        	 toTerminate.close(); 
    	 }
         catch(IOException ioe)
         {  
	 		System.out.println(ioe); }
     		toTerminate.stop(); 
         }
   }
   
   private void addThread(Socket socket)
   {  
	  if (clientCount < clients.length)
      {  
   		 System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ServerThread(this, socket);
         try
         {  
        	//neuer ServerThread wird gestartet
        	clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; 
         }
         catch(IOException ioe)
         {  
        	 System.out.println(ioe); 
         } 
      }
      else 
      {
         System.out.println("Client refused: maximum " + clients.length + " reached.");
      }
   }
   
   public static void main(String args[])
   {  
	  Server server;
	  BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	  //Eingabe des listening Ports und anlegen des Servers
	  try 
	  {
  		System.out.print("Enter a listening port: ");
  		//InetAddress bindAddress =  InetAddress.getLocalHost();
  		server = new Server(Integer.parseInt(console.readLine()));
      } catch (IOException ioe) {
    	  System.out.println("IO error!");
          System.exit(1);
      }
   }
}