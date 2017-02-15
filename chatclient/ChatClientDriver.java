package chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClientDriver
{
	private static String key;
	protected static Socket client;
	protected static String name;
	protected static int port;
	private static String host;
	private static ChatClientGUI Interface;
	protected static boolean proceed;

	/**
	 * 	main function; initializes the system, calls the GUI
	 * 	constructor, etc.
	 * 	@param args the arguments passed into the system.
	 */
	public static void main(String[] args)
	{
		try
		{
			// initializes the system.
			Interface = new ChatClientGUI();
			initializer();
			client = createConnection(host);
			DataInputStream in = getIn(client);

			Interface.GetFromIn("\nOkay, perfect! We found the server!\n"
					+ "We were given port "+port+".");

			Interface.giveOutputStream(getOut(client));
			Interface.flushField();
			Interface.SendButton.setEnabled(true);
			Interface.ScrambleButton.setEnabled(true);
			Interface.ClearButton.setEnabled(true);

			// checks if a confirmation message has been sent.
			if(in.readUTF().equals("+OK"))
			{
				Interface.GetFromIn("Alright, you're online! Start chatting!\n");
				while(true)
				{
					Interface.GetFromIn(decode(in.readUTF(),key));
				}
			}
			// if we received anything other than +OK for some reason.
			else
			{
				throw new IOException();
			}


		}
		catch (UnknownHostException e)
		{
			Interface.GetFromIn("\nHmmm... Looks like this host is broken.");
			e.printStackTrace();
		}
	   	catch (IOException e) 
		{
			Interface.GetFromIn("\nHmmm... Can't find the host for some reason...\n"
					+ "Our servers may be offline right now; please contact the host.");
		}
	}


	/**
	 * constructor for the ChatClientDriver which handles input.
	 * @param S
	 */
	public ChatClientDriver(Socket S)
	{
		// try to initialize the data stream, and rad the UTF
		try 
		{
			DataInputStream in = new DataInputStream(S.getInputStream());
			while(true)
			{
				Interface.GetFromIn(in.readUTF());
			}
		}
		// catches if we cannot create a stream, or if we fail to read the UTF
	   	catch (IOException e) 
		{
			System.exit(1);
		}
	}


	/**
	 *	disables all the components of the window.
	 *	This is used to stop the user from using the chat components before
	 *	valid information has been given to the chat program.
	 */
	public static void offAll()
	{
		Interface.KeyButton.setEnabled(false);
		Interface.SendButton.setEnabled(false);
		Interface.SystemButton.setEnabled(false);
		Interface.ScrambleButton.setEnabled(false);
		Interface.DisplayArea.setEditable(false);
		Interface.ClearButton.setEnabled(false);
	}


	/**
	 * 	Initializer code, sets up the system.
	 * 	essentially, waits on the button for new input, preventing the system
	 * 	from continuing until we have valid input for the username/hostname.
	 */
	public static void initializer()
	{
		// disables the components of the window to start
		offAll();
		name = "";

		Interface.GetFromIn("Welcome to the chatroom!\n"
				+ "Before you get chatting, we need to set some stuff up!\n"
				+ "Please enter your name in the System Input down below, then hit enter!\n");
		Interface.SystemButton.setEnabled(true);
		Wait();

		Interface.GetFromIn("Okay, you chose "+name+" as a name!\n"
				+ "Next, we need to set your key for the encryption!\n");
		Interface.KeyButton.setEnabled(true);
		Wait(); // calls the wait method until the proceed method is called

		Interface.GetFromIn("Alright, next we need a server name.\n"
				+ "Please enter one in the System Input field\n");
		Interface.SystemButton.setEnabled(true);
		Wait();

		Interface.GetFromIn("Awesome! We're almost ready now... Launching the Networking!");

	}


	/**
	 * Networking setup
	 * gets the port from the server that we are going to switch to.
	 * @param server
	 * @return the socket that we need for in/out
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static Socket createConnection(String server) throws UnknownHostException, IOException
	{
		Socket S = new Socket(server, 55005); // default

		InputStream infrom = S.getInputStream();
		DataInputStream in = new DataInputStream(infrom);

		// gets the new port, sent by the server to this client
		port = Integer.parseInt(in.readUTF());
		S.close();

		// returns the new socket, as dictated by the server's port issuing.
		return new Socket(server,port);
	}



	//----------------------------------------------encryption methods----------------------------------------------------------------------
	

	/**
	 *	decode
	 *	decodes the string, using the key to re-shift the encrypted string to the 
	 *  original form.
	 * 	@param str the string that we need to decode
	 * 	@param key the key that was used to encrypt the key (hopefully)
	 * 	@return the correctly decoded message
	 */
	public static String decode(String str, String key)
	{
		int i;

		String message = "";
		key = repeatKey(key,str);
	
		// decodes the string, piece by piece.
		for(i = 0; i <= str.length() - 1; i++)
		{
			// this indicates a correct decoding, meaning we do not need to loop around to the end of our ascii table.
			if(((int) str.charAt(i) - 32) - ((int) key.charAt(i) - 32) >= 0)
			{
				// decodes the letter at i by shifting it back to its original position
				message = message + (char)  (((((int) str.charAt(i) - 32)) - ((int) key.charAt(i) - 32)) + 32);
			}
			// indicates an underflow has occured.  we need to loop back from 126 (our ascii upper limit)
			else
			{
				message += (char)(126 + ((((int) str.charAt(i) - 32)) - ((int) key.charAt(i) - 32)) + 32);
			}
		}

		return message;
	}


	/**
	 *	repeatKey
	 *	repeats the key until it matches the length of the string.
	 *	@param key the key we will be elongating.
	 *	@param str the string whose length will be matched
	 *	@return the new key, of equal length.
	 */
	public static String repeatKey(String key, String str)
	{
		// keep adding the key to itself until its length is greater that str's
		while(key.length() < str.length())
		{
			// adds the key to itself, essentially doubling its size every time.
			key += key;
		}

		// cuts the key off at the length of the message to be encoded/decoded.
		key = key.substring(0,(str.length()));

		return key;
	}


	//----------------------------------------------getters and setters, system functions---------------------------------------------------



	
	/**
	 *	sets the global synchronization flag, proceed to true,
	 *	allowing the system to continue execution.
	 *	Used in conjunction with Wait(), to allow the system to
	 *	wait until new input is entered through the button presses.
	 */
	protected static void Proceed()
	{
		proceed = true;
	}

	/**
	 *	causes the thread to sleep until proceed is updated to true.
	 *	Also returns if the thread is sent an interrupt.
	 *	Use in conjunction with Proceed to allow a signalling system
	 *	to be used to synchronize threads/button input.
	 */
	public static void Wait()
	{
		while(!proceed){
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				return;
			}
		}

		proceed = false;
	}

	/**
	 *	generates a data input stream from a socket that we created.
	 *	@param s a socket we need to create an input stream from
	 *	@return the data stream we extracted
	 *	@throws IOException, should we fail to create a data stream.
	 */
	public static DataInputStream getIn(Socket s) throws IOException
	{
		InputStream i = s.getInputStream();
		return new DataInputStream(i);
	}

	/**
	 *	generates a data output stream from the socket we pass in.
	 *	@param s the socket we need to create a data output stream from.
	 *	@return the output stream created.
	 *	@throws IOException should we fail to create a data stream.
	 */
	public static DataOutputStream getOut(Socket s) throws IOException
	{
		OutputStream o = s.getOutputStream();
		return new DataOutputStream(o);	
	}

	/**
	 *	getter for key
	 *	@return key, this instance of key
	 */
	public static String getKey()
	{
		return key;
	}

	/**
	 *	sets the name we pass in.
	 *	@param str the string we are changing the name to.
	 */
	public static void setName(String str)
	{
		name = str;
	}

	/**
	 *	sets the key to the text passed in.
	 *	@param text the text we are changing key to.
	 */
	public static void setKey(String text)
	{
		key = text;		
	}

	/**
	 *	getter function for the name field
	 *	@return the current instance of name
	 */
	public static String getName()
	{
		return name;
	}

	/**
	 * 	sets the host name for the current instance
	 *	@param text the new host name that we will use.
	 */
	public static void setHost(String text)
	 {
		host = text;
	}

}

