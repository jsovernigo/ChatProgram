package chatclient;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;


public class ChatClientGUI extends JFrame
{

	private static final long serialVersionUID = 8331078391862169729L;
	private static final int WIDTH = 700;
	private static final int HEIGHT = 600;

	private JLabel KeyFieldL, SystemFieldL;
	private JTextField KeyField, SystemField, EnterField; 
	protected JButton KeyButton, ScrambleButton, ClearButton;
	protected JButton SystemButton;
	protected JButton SendButton;
	protected JTextArea DisplayArea;
	private Container pane;
	protected DataOutputStream out;
	protected int stage = 0;
	private static long lastEnter = 0;;

	public ChatClientGUI()
	{
		setTitle("ChatClientGUI");
		KeyFieldL = new JLabel("Key Field", SwingConstants.RIGHT);
		SystemFieldL = new JLabel("System Input", SwingConstants.RIGHT);
		KeyField = new JTextField(10);
		SystemField = new JTextField(10);
		EnterField = new JTextField(10);
		KeyButton = new JButton("Enter");
		ClearButton = new JButton("WIPE CHAT");
		ScrambleButton = new JButton("Generate Key");
		SystemButton = new JButton("Enter");
		SendButton = new JButton("Send");
		DisplayArea = new JTextArea(0,500);
		DisplayArea.setLineWrap(true);


		// adds a new action listener to the key input button.
		KeyButton.addActionListener(
			new java.awt.event.ActionListener() 
			{
				/**
				 *	executes if the button receives a click action, essentially.
				 *	@param evt the event that we received.
				 */
				public void actionPerformed(java.awt.event.ActionEvent evt) 
				{
					KeyButtonActionPerformed(evt);
				}
			});

		ScrambleButton.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *	executes if the button receives a click action, essentially.
				 *	@param evt the event that we received.
				 */
				public void actionPerformed(java.awt.event.ActionEvent evt) 
				{
					ScrambleButtonActionPerformed(evt);
				}
			});

		SystemButton.addActionListener(
			new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt) 
				{
					SystemButtonActionPerformed(evt);
				}
			});

		ClearButton.addActionListener(
			new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					ClearButtonActionPerformed(evt);
				}
			});

		SendButton.addActionListener(
			new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt) 
				{
					SendButtonActionPerformed(evt);
				}
			});

		EnterField.addActionListener(
			new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					EnterFieldActionPerformed(evt);
				}
			});

		JScrollPane scroll = new JScrollPane();
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		pane = getContentPane();
		pane.setLayout(null);

		pane.add(scroll);
		pane.add(DisplayArea);
		DisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
		pane.add(KeyButton);
		pane.add(KeyField);
		pane.add(SystemField);
		pane.add(EnterField);
		pane.add(ScrambleButton);
		pane.add(ClearButton);
		pane.add(SystemButton);
		pane.add(SendButton);
		pane.add(KeyFieldL);
		pane.add(SystemFieldL);	
		Insets insets = pane.getInsets();

		// sets the bounds of the components relative to the edges.
		KeyFieldL.setBounds(insets.left + 435, insets.top + 30, insets.left+140, insets.top+ 30);
		KeyButton.setBounds(insets.left + 525, insets.top + 80, insets.left+70, insets.top+ 25);
		KeyField.setBounds(insets.left + 525, insets.top+55, insets.left+140, insets.top +20);
		ScrambleButton.setBounds(insets.left +525, insets.top + 210, insets.left + 140, insets.top+25);
		ClearButton.setBounds(insets.left+525, insets.top+260,insets.left+140, insets.top+25);
		SystemFieldL.setBounds(insets.left + 458, insets.top + 380, insets.left+140, insets.top+ 30);
		SystemButton.setBounds(insets.left + 525, insets.top + 430, insets.left+70, insets.top+ 25);
		SystemField.setBounds(insets.left + 525, insets.top+405, insets.left+140, insets.top +20);
		EnterField.setBounds(insets.left + 15, insets.top + 500, insets.left+500, insets.top + 30);
		SendButton.setBounds(insets.left + 530, insets.top+500, insets.left + 70, insets.top+25);
		scroll.setBounds(15+insets.left, 15 + insets.top, 490 + insets.left, insets.top + 450);

		scroll.setViewportView(DisplayArea);

		pack();
		setSize(WIDTH, HEIGHT);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	/**
	 * 	Display method, writes to the screen
	 * 	@param str the string we are printing to the screen
	 */
	private void printToDisplay(String str)
	{
		if(!DisplayArea.getText().equals(""))
		{
			DisplayArea.setText(DisplayArea.getText() + "\n" + str);
		}
		else
		{
			DisplayArea.setText(str);
		}
	}

	/**
	 *	prints the string to the screen.
	 *	Used by the button listeners to print to the screen.
	 */
	protected void GetFromIn(String S)
	{
		printToDisplay(S);
	}

	/**
	 * 	clears the display area screen.
	 */
	public void flushField()
	{
		DisplayArea.setText("");
	}

	/**
	 *	assigns the passed in output stream to the instance variable
	 *	data field we are using to send messages.
	 */
	protected void giveOutputStream(DataOutputStream o)
	{
		out = o;
	}

	//---------------------------encryption methods--------------------------------------
	

	/**
	 * encrypting method; shifts the message by each corresponding character value in
	 * the key passed in.
	 * @param str the string we will be encrypting
	 * @param key the key we will be encrypting with
	 * @return the encrypted message.
	 */
	public static String encode(String str, String key)
	{
		int i;

		String message = "";
		
		// repeats the key to match the length of the string.
		key = repeatKey(key,str);

		// loops through each character of the string.
		for(i = 0; i < str.length(); i++)
		{
			// shifts the character at i by the value of the key at the current position.
			message += (char) (((((int) (str.charAt(i)) + (int) (key.charAt(i))) - 64) % 126) + 32);
		}

		return message;
	}


	/**
	 * repeating method which repeats the key to the same length of the message.
	 * @param key the key we are repeating
	 * @param str the string that we are using to match lengths.
	 * @return the newly repeating key.
	 */
	public static String repeatKey(String key, String str)
	{
		while(key.length() < str.length())
		{
			// doubles the length of the key
			key+=key;
		}

		// truncates the key at the message's length
		key = key.substring(0, (str.length()));

		return key;
	}


	//-----------------------------action listener methods---------------------------------
	
	/**
	 *	invoked when the scrable key button was clicked
	 *	@param evt the event that was triggered.
	 */
	protected void ScrambleButtonActionPerformed(ActionEvent evt)
	{
		int i;
		int length;
		String str;
		Random rand;

		str = new String();
		rand = new Random();
		length = rand.nextInt(16) + 8;

		for(i = 0; i < length; i++)
		{
			str += (char) (rand.nextInt(126) + 32);
		}

		printToDisplay("Generating Key...\n" + "New key: " + str);
		ChatClientDriver.setKey(str);
	}

	/**
	 *	invoked by pressing the clear button.
	 *	@param evt the event that was triggered on the button
	 */
	protected void ClearButtonActionPerformed(ActionEvent evt)
   	{
		// clears the input field.
		flushField();
	}

	/**
	 *	the system button's action listener.
	 *	@param evt the event that was triggered
	 */
	protected void SystemButtonActionPerformed(ActionEvent evt)
	{
		// if we are at the first stage, essentially entering the name.
		if(stage == 0)
		{
			// gets the input from the button, and then clears the text.
			ChatClientDriver.setName(SystemField.getText());
			SystemField.setText("");

			ChatClientDriver.Proceed();

			SystemButton.setEnabled(false);

			// triggers the state change.
			stage++;
		}
		// while we are waiting for the host name.
		else if(stage == 1)
		{
			// gets the text from the system field
			ChatClientDriver.setHost(SystemField.getText());
			SystemField.setText("");

			ChatClientDriver.Proceed();

			SystemButton.setEnabled(false);
			stage++;
		}
	}


	/**
	 * 	handling code for pressing the send button.
	 * 	This code will simply write the message to the file.
	 * 	@param evt the event that was triggered.
	 */
	private void SendButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		// if there is text we can use.
		if(!EnterField.getText().equals(null))
		{
			try
			{
				// writes the message that was typed, plus the username, to the server.
				out.writeUTF(encode("<" + ChatClientDriver.getName() + "> " + EnterField.getText(), ChatClientDriver.getKey()));
			}
			catch (IOException e)
			{
				printToDisplay("Message Failed to send.");
			}
			// regardless of the outcome of the try, clear the enter field. (the message that was sent.
			finally
			{
				EnterField.setText("");
			}
		}
	}

	/**
	 *	the key button listener actionhandler.
	 *	@param evt the event that was triggered.
	 */
	private void KeyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		// if there was actually a change in the key.
		if(!KeyField.getText().equals(ChatClientDriver.getKey()))
		{
			ChatClientDriver.setKey(KeyField.getText());
			KeyField.setText("");
			ChatClientDriver.Proceed();
		}
	}

	/**
	 *
	 */
	private void EnterFieldActionPerformed(java.awt.event.ActionEvent evt){
		EnterField.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent evt) {
				long now = System.currentTimeMillis();
				if(now-lastEnter>500){
					if(evt.getKeyCode()==KeyEvent.VK_ENTER){
						if(!EnterField.getText().equals(null)){
							try {
								out.writeUTF(encode("<"+ChatClientDriver.getName()+"> "+EnterField.getText(),ChatClientDriver.getKey()));
								lastEnter = System.currentTimeMillis();
							} catch (IOException e) {
								printToDisplay("Message Failed to send.");
							}finally{
								EnterField.setText("");
							}
						}
					}
				}
			}
			public void keyReleased(KeyEvent evt) {}
			public void keyTyped(KeyEvent evt) {}
		});
	}
}
