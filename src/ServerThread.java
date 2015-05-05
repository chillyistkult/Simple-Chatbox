import java.net.*;
import java.io.*;

public class ServerThread extends Thread
{  private Server server;
   private Socket socket;
   private int ID = -1;
   private String serverAddress;
   private DataInputStream streamIn;
   private DataOutputStream streamOut;

   public ServerThread(Server server, Socket socket)
   {  
	  super();
      this.server = server;
      this.socket = socket;
      ID = socket.getPort();
      serverAddress = socket.getLocalAddress().toString();
   }

   @SuppressWarnings("deprecation")
   public void send(String msg)
   {   
	   try
       {
          streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  
    	   System.err.println(ID + " ERROR sending: " + ioe.getMessage());
    	   server.remove(ID);
    	   stop();
       }
   }
   
   public int getID()
   {  
	   return ID;
   }
   
   @SuppressWarnings("deprecation")
   public void run()
   {  
	  System.out.println("Server Thread " + ID + " running.");
      while (true)
      {  
    	 try
         {  
    	    server.handle(ID, serverAddress, streamIn.readUTF());
         }
         catch(IOException ioe)
         {
            System.err.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   
   public void open() throws IOException
   {  
	  streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }
   
   public void close() throws IOException
   {  
	  if (socket != null) {
		  socket.close();
	  }
      if (streamIn != null) {
    	  streamIn.close();
      }
      if (streamOut != null) {
    	  streamOut.close();
      }
   }
}