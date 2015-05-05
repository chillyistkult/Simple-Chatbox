import java.net.*;
import java.io.*;

public class ClientThread extends Thread
{  private Socket socket;
   private Client client;
   private String userName;
   private DataInputStream streamIn;

   public ClientThread(Client client, Socket socket, String userName)
   {  
	  this.userName = userName;
	  this.client = client;
      this.socket = socket;
      open();  
      start();
   }
   
   public String getUserName()
   {
	   return userName;
   }
   
   public void open()
   {  
	  try
      {
		 streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {  
    	 System.err.println(ioe);
         client.stop();
      }
   }
   
   public void close()
   {  
	  try
      {  
		  if (streamIn != null) 
		  {
			  streamIn.close();
		  }
      }
      catch(IOException ioe)
      {  
    	  System.err.println(ioe);
      }
   }
   
   public void run()
   {  
	  while (true)
      {  
		 try
         {
			 client.handle(streamIn.readUTF());
         }
         catch(IOException ioe)
         {  
        	 System.err.println(ioe.getMessage());
        	 client.stop();
         }
      }
   }
}