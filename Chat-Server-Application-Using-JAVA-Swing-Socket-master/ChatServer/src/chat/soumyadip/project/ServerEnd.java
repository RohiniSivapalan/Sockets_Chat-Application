package chat.soumyadip.project;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class ServerEnd {

	private JFrame frmServerChat;
	private JTextField textField;
	private static   JTextArea textArea;
	static ServerSocket server ;
	static Socket con;
	private static final File logFile = new File("server_chat_log.txt");
	private JScrollPane scrollPane;
	private static JLabel lblNewLabel_2;
	private static JLabel lblNewLabel;
	
	private boolean isDarkTheme = true; // Default theme is Dark


	
	public static void main(String[] args) throws IOException   {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerEnd window = new ServerEnd();
					window.frmServerChat.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		 serverConnection();
	
	
	}

	private static void serverConnection() throws IOException {
		server = new ServerSocket(8080);
		
		 con = server.accept();
		 lblNewLabel_2.setText("Client found !");
			lblNewLabel_2.setForeground(new Color(0, 0, 128));
		 while (true) {
			try {
				
				DataInputStream input = new DataInputStream(con.getInputStream());
				String string = input.readUTF();
				 // Check if the message indicates a file transfer
	            if (string.equals("FILE_TRANSFER")) {
	            	String chatbotReply = receiveFile();
	            	 if (!chatbotReply.isEmpty()) {
	                     // Send the file acknowledgment to the client
	                     DataOutputStream output = new DataOutputStream(con.getOutputStream());
	                     output.writeUTF(getCurrentTimestamp() + " Server: FILE RECEIVED");
	                 }
	            	 continue;
	            	 
	            } else {
	            	appendToLog(string);
	            	textArea.setText(textArea.getText() + "\n " +string);
	            }
				

				// Generate chatbot response
				String chatbotReply = chatbotResponse(string);
				if (!chatbotReply.isEmpty()) {
					appendToLog(chatbotReply);
					textArea.setText(textArea.getText() + "\n " + chatbotReply);

					// Send the chatbot response to the client
					DataOutputStream output = new DataOutputStream(con.getOutputStream());
					output.writeUTF(chatbotReply);
				}
				continue;
				
			} catch (Exception ev) {
				 textArea.setText(textArea.getText()+" \n" +"Network issues ");
				 
					try {
						Thread.sleep(2000);
						System.exit(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}

		}
	}

	
	private static String receiveFile() {
	    try {
	        DataInputStream fileInput = new DataInputStream(con.getInputStream());

	        // Read file name and size
	        String fileName = fileInput.readUTF();
	        long fileSize = fileInput.readLong();

	        // Save the received file
	        File file = new File("Received_" + fileName);
	        try (FileOutputStream fos = new FileOutputStream(file)) {
	            byte[] buffer = new byte[4096];
	            long totalBytesRead = 0;
	            int bytesRead;

	            while (totalBytesRead < fileSize && (bytesRead = fileInput.read(buffer)) != -1) {
	                fos.write(buffer, 0, bytesRead);
	                totalBytesRead += bytesRead;
	            }
	        }

	        // Notify server GUI about the received file
	        String notification = "Received file: " + fileName + " (" + fileSize + " bytes)";
	        textArea.setText(textArea.getText() + "\n " + notification);
	        appendToLog(notification);
	        
	        return getCurrentTimestamp() + " Chatbot: " + notification;
	        
	    } catch (IOException e) {
	        textArea.setText(textArea.getText() + "\n" + "Error receiving file.");
	        return "";
	    }
	}
	
	
	
	private static String chatbotResponse(String message) {
		
		
	    message = message.toLowerCase();

	    if (message.contains("hello") || message.contains("hi")) {
	        return getCurrentTimestamp() + " Chatbot: Hello! How can I assist you today?";
	    } else if (message.contains("how are you")) {
	        return getCurrentTimestamp() + " Chatbot: I'm just a program, but I'm functioning as expected. Thank you!";
	    } else if (message.contains("time")) {
	        return getCurrentTimestamp() + " Chatbot: The current time is " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ".";
	    } else if (message.contains("date")) {
	        return getCurrentTimestamp() + " Chatbot: Today's date is " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".";
	    } else if (message.contains("day")) {
	        return getCurrentTimestamp() + " Chatbot: Today is " + new SimpleDateFormat("EEEE").format(new Date()) + ".";
	    } else if (message.contains("weather")) {
	        return getCurrentTimestamp() + " Chatbot: It's sunny with a temperature of 25°C.";
	    } else if (message.contains("joke")) {
	        return getCurrentTimestamp() + " Chatbot: Why do Java developers wear glasses? Because they can't C#!";
	    } else if (message.contains("fun fact")) {
	        return getCurrentTimestamp() + " Chatbot: Did you know? The Eiffel Tower can be 15 cm taller during the summer due to thermal expansion.";
	    } else if (message.contains("motivation") || message.contains("quote")) {
	        return getCurrentTimestamp() + " Chatbot: Keep pushing forward. You're doing great!";
	    } else if (message.contains("system info")) {
	        return getCurrentTimestamp() + " Chatbot: I'm running on a Java-based server. Everything is functioning optimally.";
	    } else if (message.contains("help") || message.contains("commands")) {
	        return getCurrentTimestamp() + " Chatbot: Here are some things I can help you with:\n" +
	               "- \"time\": Get the current time.\n" +
	               "- \"date\": Get today's date.\n" +
	               "- \"day\": Find out what day it is.\n" +
	               "- \"weather\": Get the current weather.\n" +
	               "- \"joke\": Hear a funny joke.\n" +
	               "- \"fun fact\": Learn something interesting.\n" +
	               "- \"motivation\": Receive an inspiring message.";
	    } else if (message.contains("bye")) {
	        return getCurrentTimestamp() + " Chatbot: Goodbye! Have a great day!";
	    }

	    // Default response for unrecognized messages
	    //return getCurrentTimestamp() + " Chatbot: I'm sorry, I didn't understand that. Can you try asking something else?";
	    return " ";
	}


    private static String getCurrentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return "[" + formatter.format(new Date()) + "]";
    }


	private static void appendToLog(String string) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(string + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
	
	
	public ServerEnd() throws IOException {
		initialize();


		 
	}

	private void initialize() throws IOException {
		frmServerChat = new JFrame();
		frmServerChat.getContentPane().setBackground(UIManager.getColor("MenuBar.highlight"));
		frmServerChat.setForeground(Color.WHITE);
		frmServerChat.setBackground(Color.WHITE);
		frmServerChat.setTitle("Networking Project : Server Chat");
		frmServerChat.getContentPane().setForeground(Color.WHITE);
		frmServerChat.setBounds(100, 100, 800, 600);
		frmServerChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServerChat.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Lato Semibold", Font.PLAIN, 24));
		textField.setForeground(new Color(255, 255, 255));
		textField.setBackground(Color.DARK_GRAY);
		textField.setBounds(20, 500, 600, 40);
		frmServerChat.getContentPane().add(textField);
		textField.setColumns(10);
		
		 JButton btnNewButton = new JButton("Send");
        btnNewButton.addActionListener(e -> {
            if (textField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please write some text!");
            } else {
                String message = textField.getText();
                String formattedMessage = getCurrentTimestamp() + " Server: " + message;
                textArea.setText(textArea.getText() + "\n" + formattedMessage);
                try {
                    DataOutputStream output = new DataOutputStream(con.getOutputStream());
                    output.writeUTF(formattedMessage);
                } catch (IOException ex) {
                    textArea.setText(textArea.getText() + "\n" + "Network issues");
                }
                textField.setText("");
            }
        });		
        
        
        btnNewButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.setBackground(Color.BLUE);
		btnNewButton.setBounds(640, 500, 120, 40);
		btnNewButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		frmServerChat.getContentPane().add(btnNewButton);
		
		// Add theme toggle button
        JButton btnToggleTheme = new JButton("Theme");
        btnToggleTheme.addActionListener(e -> toggleTheme());
        btnToggleTheme.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        btnToggleTheme.setBounds(640, 450, 120, 40);
        frmServerChat.getContentPane().add(btnToggleTheme);
		 
		 scrollPane = new JScrollPane();
		 scrollPane.setBounds(20, 20, 740, 400);
		 frmServerChat.getContentPane().add(scrollPane);
		
		 textArea = new JTextArea();
		 scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		textArea.setForeground(Color.ORANGE);
		textArea.setBackground(Color.DARK_GRAY);
		
		lblNewLabel = new JLabel();
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		
		lblNewLabel.setBounds(20, 450, 300, 30); /*154, 13, 242, 33*/
		frmServerChat.getContentPane().add(lblNewLabel);
		 if (server.isClosed()) {
			lblNewLabel.setText("Server is closed");
		}else{
			lblNewLabel.setText("Waiting for connection");
			lblNewLabel.setForeground(Color.GREEN);
		}
		
		 JLabel lblNewLabel_1 = new JLabel("Status");
			lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 22));
			lblNewLabel_1.setBounds(20, 500, 300, 30);  /*37, 12, 95, 30*/
			frmServerChat.getContentPane().add(lblNewLabel_1);
			
			lblNewLabel_2 = new JLabel();
			lblNewLabel_2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
			lblNewLabel_2.setBounds(22, 303, 128, 30);
			frmServerChat.getContentPane().add(lblNewLabel_2);
	}
		 
		 private void toggleTheme() {
		        isDarkTheme = !isDarkTheme;  // Toggle theme flag

		        if (isDarkTheme) {
		            frmServerChat.getContentPane().setBackground(Color.DARK_GRAY);
		            textArea.setBackground(Color.BLACK);
		            textArea.setForeground(Color.WHITE);
		            textField.setBackground(Color.GRAY);
		            textField.setForeground(Color.WHITE);
		        } else {
		            frmServerChat.getContentPane().setBackground(Color.WHITE);
		            textArea.setBackground(Color.LIGHT_GRAY);
		            textArea.setForeground(Color.BLACK);
		            textField.setBackground(Color.WHITE);
		            textField.setForeground(Color.BLACK);
		        }
		    }

		   
	
	
	
	
}





