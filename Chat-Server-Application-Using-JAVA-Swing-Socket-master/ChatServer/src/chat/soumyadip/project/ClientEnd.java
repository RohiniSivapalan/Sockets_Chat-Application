package chat.soumyadip.project;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ClientEnd {
	
	
	public JFrame frame;
	private JTextField textField;
    private static JTextArea textArea;
    private static Socket con;
    private static final File logFile = new File("client_chat_log.txt");
    DataInputStream input;
    DataOutputStream output;
    private JScrollPane scrollPane;
    

  
	public static void main(String[] args) throws UnknownHostException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientEnd window = new ClientEnd();
					window.frame.setVisible(true);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		con = new Socket("127.0.0.1", 8080);
		 while (true) {
			try {
				
				DataInputStream input = new DataInputStream(con.getInputStream());
				String string = input.readUTF();
				appendToLog(string);
				textArea.setText(textArea.getText() + "\n" + string);
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

	
	public ClientEnd() {
		initialize();
	}

 
    private static String getCurrentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return " [" + formatter.format(new Date()) + "]";
    }
    
    private static void appendToLog(String string) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(string + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("MenuBar.highlight"));
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Networking Project : Client Chat");
		
		textField = new JTextField();
		textField.setFont(new Font("Lato Medium", Font.PLAIN, 22));
		textField.setForeground(Color.ORANGE);
		textField.setBackground(Color.DARK_GRAY);
		textField.setBounds(20, 500, 600, 40);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
	                if (textField.getText().isEmpty()) {
	                    JOptionPane.showMessageDialog(null, "Please write some text!");
	                } else {
	                    String message = textField.getText();
	                    String formattedMessage = getCurrentTimestamp() + " Client: " + message;
	                    textArea.setText(textArea.getText() + "\n" + formattedMessage);
	                    try {
	                        DataOutputStream output = new DataOutputStream(con.getOutputStream());
	                        output.writeUTF(formattedMessage);
	                    } catch (IOException ex) {
	                        textArea.setText(textArea.getText() + "\n" + "Network issues");
	                    }
	                    textField.setText("");
	                }
	            }
					
		});
		btnNewButton.setFont(new Font("Georgia", Font.BOLD, 22));
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setBackground(Color.BLUE);
		btnNewButton.setBounds(640, 500, 120, 40);
		btnNewButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		frame.getContentPane().add(btnNewButton);
		
		JButton btnFile = new JButton("File");
	    btnFile.addActionListener(e -> sendFile());
	    btnFile.setFont(new Font("Georgia", Font.BOLD, 22));
	    btnFile.setForeground(Color.WHITE);
	    btnFile.setBackground(Color.RED);
	    btnFile.setBounds(640, 450, 120, 40);
	    frame.getContentPane().add(btnFile);
	    
	
	    
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 20, 740, 400);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
		textArea.setForeground(new Color(255, 255, 255));
		textArea.setBackground(new Color(0, 128, 128));
	}
	
	
	
	
	private void sendFile() {
	    JFileChooser fileChooser = new JFileChooser();
	    int returnValue = fileChooser.showOpenDialog(null);
	    if (returnValue == JFileChooser.APPROVE_OPTION) {
	        File file = fileChooser.getSelectedFile();

	        try {
	            // Notify server about the file transfer
	            DataOutputStream output = new DataOutputStream(con.getOutputStream());
	            output.writeUTF("FILE_TRANSFER");

	            // Send file metadata
	            output.writeUTF(file.getName());
	            output.writeLong(file.length());

	            // Send file content
	            try (FileInputStream fis = new FileInputStream(file)) {
	                byte[] buffer = new byte[4096];
	                int bytesRead;

	                while ((bytesRead = fis.read(buffer)) != -1) {
	                    output.write(buffer, 0, bytesRead);
	                }
	            }

	            // Notify client GUI about the sent file
	            String notification = "Sent file: " + file.getName() + " (" + file.length() + " bytes)";
	            textArea.setText(textArea.getText() + "\n" + notification);
	            appendToLog(notification);
	            
	  
	            

	        } catch (IOException e) {
	            textArea.setText(textArea.getText() + "\n" + "Error sending file.");
	        }
	    }
	}
} 





