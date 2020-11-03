package passwordcracker;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {

    JFrame frame;
    private static JButton singleCrack;
    public static JButton massCrack;
    private static JButton save;
    public static JTextField textField;
    public static JTextField dictField;
    public static JTextField hashField;
    public static JTextField result;
    public static JTextArea solvedHashes;
    private static JScrollPane scrollPane;
    public static ProgressBar progressBar;
    public static File dictionaryFile;
    public static File hashFile;
    public static JCheckBox cbNumbers;
    public static JCheckBox cbSpecial;
    public static JCheckBox cbToggle;
    public static JCheckBox cbReplace;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		
		//Application Window
        frame = new JFrame();
        frame.setResizable(false);
        frame.getContentPane().setBackground(SystemColor.menu);
        frame.setTitle("MD5 Password Cracker");
        frame.setBounds(100, 100, 900, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);
           
        //Single Crack Instructions
        JTextArea singleText = new JTextArea();
        singleText.setEditable(false);
        singleText.setBackground(SystemColor.menu);
        singleText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        singleText.setBounds(189, 11, 506, 23);
        frame.getContentPane().add(singleText);
        singleText.setText("Enter a single MD5 hash below and click 'Crack' to see if it can be cracked!");
        
        //Enter hash text
        JTextPane enterText = new JTextPane();
        enterText.setEditable(false);
        enterText.setBackground(SystemColor.menu);
        enterText.setText("Enter hash:");
        enterText.setBounds(411, 56, 85, 23);
        frame.getContentPane().add(enterText);
        
        //Single hash field
        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setBounds(314, 79, 255, 23);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        textField.setDocument(new JTextFieldLimit(32)); //Setting the hash textfield limit to 32 characters
        
        //Single crack button
        singleCrack = new JButton("Crack");
        singleCrack.setBounds(397, 113, 89, 23);
        frame.getContentPane().add(singleCrack);
        singleCrack.addActionListener(new Main.singleCrack());
        
        //Single hash result
        result = new JTextField();
        result.setEditable(false);
        result.setBackground(UIManager.getColor("text"));
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setBounds(314, 147, 255, 20);
        frame.getContentPane().add(result);
 
        //Separates single and mass crack
        JSeparator separator = new JSeparator();
        separator.setForeground(UIManager.getColor("TextArea.selectionBackground"));
        separator.setBounds(80, 193, 724, 2);
        frame.getContentPane().add(separator);
 
        //Mass crack instructions
        JTextArea massText = new JTextArea();
        massText.setText("Upload a dictionary (optional) and hash text file to mass crack! (Must be a .txt file)");
        massText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        massText.setEditable(false);
        massText.setBackground(SystemColor.menu);
        massText.setBounds(164, 206, 555, 23);
        frame.getContentPane().add(massText);
 
        //Upload dictionary button
        JButton uplDict = new JButton("Upload Dictionary");
        uplDict.setBounds(294, 240, 134, 23);
        frame.getContentPane().add(uplDict);
        uplDict.addActionListener(new Main.dictionaryFile());
 
        //Upload hashes button
        JButton uplHashes = new JButton("Upload Hashes");
        uplHashes.setBounds(450, 240, 134, 23);
        frame.getContentPane().add(uplHashes);
        uplHashes.addActionListener(new Main.hashFile());
 
        //Displays selected dictionary file
        dictField = new JTextField();
        dictField.setEditable(false);
        dictField.setBackground(UIManager.getColor("text"));
        dictField.setBounds(209, 274, 417, 20);
        frame.getContentPane().add(dictField);
        dictField.setColumns(10);
 
        //Displays selected hash file
        hashField = new JTextField();
        hashField.setEditable(false);
        hashField.setBackground(UIManager.getColor("text"));
        hashField.setColumns(10);
        hashField.setBounds(209, 295, 466, 20);
        frame.getContentPane().add(hashField);
        
        //Clear dictionary button
        JButton clearDict = new JButton("x");
        clearDict.setBounds(631, 274, 44, 20);
        frame.getContentPane().add(clearDict);
        clearDict.addActionListener(new Main.clearDict());
        
        JTextArea manipulationText = new JTextArea();
        manipulationText.setText("Select manipulations below:");
        manipulationText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        manipulationText.setEditable(false);
        manipulationText.setBackground(SystemColor.menu);
        manipulationText.setBounds(347, 326, 190, 23);
        frame.getContentPane().add(manipulationText);
        
        cbNumbers = new JCheckBox("Numbers");
        cbNumbers.setBounds(209, 356, 83, 23);
        frame.getContentPane().add(cbNumbers);
        
        cbSpecial = new JCheckBox("Special Characters");
        cbSpecial.setBounds(294, 356, 134, 23);
        frame.getContentPane().add(cbSpecial);
        
        cbToggle = new JCheckBox("Toggle Case");
        cbToggle.setBounds(430, 356, 98, 23);
        frame.getContentPane().add(cbToggle);
        
        cbReplace = new JCheckBox("Replace Characters");
        cbReplace.setBounds(530, 356, 145, 23);
        frame.getContentPane().add(cbReplace);
 
        //Crack button for mass
        massCrack = new JButton("Crack");
        massCrack.setBounds(375, 395, 134, 23);
        frame.getContentPane().add(massCrack);
        massCrack.addActionListener(new Main.massCrack());
        
        //Scroll bar for solved hashes text area
        scrollPane = new JScrollPane();
        scrollPane.setBounds(209, 431, 466, 157);
        frame.getContentPane().add(scrollPane);
 
        //Text area to show solved hashes
        solvedHashes = new JTextArea();
        solvedHashes.setEditable(false);
        scrollPane.setViewportView(solvedHashes);
        solvedHashes.setDropMode(DropMode.INSERT);
        solvedHashes.setLineWrap(true);
        solvedHashes.setFont(new Font("Tahoma", Font.PLAIN, 12));
 
        //Progress bar
        progressBar = new ProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBounds(209, 599, 466, 20);
        frame.getContentPane().add(progressBar);
 
        //Save button
        save = new JButton("Save");
        save.setBounds(375, 630, 134, 23);
        frame.getContentPane().add(save);
        //save.addActionListener(new Main.Save());
	}
	
    static class JTextFieldLimit extends PlainDocument {
        private int maxLimit;
        JTextFieldLimit(int limit) {
            super();
            this.maxLimit = limit;
        }
 
        public void insertString(int offset, String hash, AttributeSet attr) throws BadLocationException {
            if (hash == null) return;
 
            if ((getLength() + hash.length()) <= maxLimit)
                super.insertString(offset, hash, attr);
        }
    }
}
