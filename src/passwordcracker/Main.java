package passwordcracker;
 
import static java.nio.charset.StandardCharsets.UTF_8;
 
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
 
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
 
public class Main {
 
    JFrame frame;
    private static JButton singleCrack;
    private static JButton massCrack;
    private static JButton save;
    private static JTextField textField;
    private static JTextField dictField;
    private static JTextField hashField;
    private static JTextField result;
    private static JTextArea solvedHashes;
    private static JScrollPane scrollPane;
    private static JProgressBar progressBar;
    private static File dictionaryFile;
    private static File hashFile;
 
    final static String manipulations = "0123456789 !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"; //Used for word manipulations
 
    final static String alphabet = "abcdefghijklmnopqrstuvwxyz"; //Used for toggle case function
 
    // Launch the application
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
 
    //Create application
    public Main() {
        initialize();
    }
 
    //Initialise contents of the frame
    private void initialize() {
 
        //Creating the main application window
        frame = new JFrame();
        frame.setResizable(false);
        frame.getContentPane().setBackground(SystemColor.menu);
        frame.setTitle("MD5 Password Cracker");
        frame.setBounds(100, 100, 700, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null);
 
        //Single crack instructions
        JTextArea singleText = new JTextArea();
        singleText.setEditable(false);
        singleText.setBackground(SystemColor.menu);
        singleText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        singleText.setBounds(93, 21, 506, 23);
        frame.getContentPane().add(singleText);
        singleText.setText("Enter a single MD5 hash below and click 'Crack' to see if it can be cracked!");
 
        //Enter hash text
        JTextPane enterText = new JTextPane();
        enterText.setBackground(SystemColor.menu);
        enterText.setEditable(false);
        enterText.setText("Enter hash:");
        enterText.setBounds(312, 55, 69, 23);
        frame.getContentPane().add(enterText);
 
        //Single hash field
        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setBounds(219, 81, 255, 23);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        textField.setDocument(new JTextFieldLimit(32)); //Setting the hash textfield limit to 32 characters
 
        //Single crack button
        singleCrack = new JButton("Crack");
        singleCrack.setBounds(302, 115, 89, 23);
        frame.getContentPane().add(singleCrack);
        singleCrack.addActionListener(new Crack());
 
        //Single hash result
        result = new JTextField();
        result.setEditable(false);
        result.setBackground(UIManager.getColor("text"));
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setBounds(219, 149, 255, 20);
        frame.getContentPane().add(result);
 
        //Separates single and mass crack
        JSeparator separator = new JSeparator();
        separator.setForeground(UIManager.getColor("TextArea.selectionBackground"));
        separator.setBounds(21, 193, 644, 2);
        frame.getContentPane().add(separator);
 
        //Mass crack instructions
        JTextArea massText = new JTextArea();
        massText.setText("Upload a dictionary (optional) and hash text file to mass crack! (Must be a .txt file)");
        massText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        massText.setEditable(false);
        massText.setBackground(SystemColor.menu);
        massText.setBounds(67, 206, 580, 23);
        frame.getContentPane().add(massText);
 
        //Upload dictionary button
        JButton uplDict = new JButton("Upload Dictionary");
        uplDict.setBounds(110, 240, 134, 23);
        frame.getContentPane().add(uplDict);
        uplDict.addActionListener(new dictionaryFile());
 
        //Upload hashes button
        JButton uplHashes = new JButton("Upload Hashes");
        uplHashes.setBounds(279, 240, 134, 23);
        frame.getContentPane().add(uplHashes);
        uplHashes.addActionListener(new hashFile());
 
        //Crack button for mass
        massCrack = new JButton("Crack");
        massCrack.setBounds(442, 240, 134, 23);
        frame.getContentPane().add(massCrack);
        massCrack.addActionListener(new massCrack());
 
        //Displays selected dictionary file
        dictField = new JTextField();
        dictField.setEditable(false);
        dictField.setBackground(UIManager.getColor("text"));
        dictField.setBounds(110, 274, 417, 20);
        frame.getContentPane().add(dictField);
        dictField.setColumns(10);
 
        //Displays selected hash file
        hashField = new JTextField();
        hashField.setEditable(false);
        hashField.setBackground(UIManager.getColor("text"));
        hashField.setColumns(10);
        hashField.setBounds(110, 295, 466, 20);
        frame.getContentPane().add(hashField);
 
        //Clear dictionary button
        JButton clearDict = new JButton("x");
        clearDict.setBounds(532, 274, 44, 20);
        frame.getContentPane().add(clearDict);
        clearDict.addActionListener(new clearDict());
 
        //Scroll bar for solved hashes text area
        scrollPane = new JScrollPane();
        scrollPane.setBounds(110, 326, 466, 119);
        frame.getContentPane().add(scrollPane);
 
        //Text area to show solved hashes
        solvedHashes = new JTextArea();
        solvedHashes.setEditable(false);
        scrollPane.setViewportView(solvedHashes);
        solvedHashes.setDropMode(DropMode.INSERT);
        solvedHashes.setLineWrap(true);
        solvedHashes.setFont(new Font("Tahoma", Font.PLAIN, 12));
 
        //Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBounds(110, 456, 466, 20);
        frame.getContentPane().add(progressBar);
 
        //Save button
        save = new JButton("Save");
        save.setBounds(279, 487, 134, 23);
        frame.getContentPane().add(save);
        save.addActionListener(new Save());
        
    }
 
    //Single crack button functionality
    static class Crack implements ActionListener { //ActionListener Interface
 
        public void actionPerformed(ActionEvent e) {  //Use methods within the ActionListener Interface
            try {
                String driver = "com.mysql.cj.jdbc.Driver";
                String url = "jdbc:mysql://localhost:3306/passwordcracker";
                String username = "root";
                String password = "a1l2e3x4m5a6t7i8";
                Class.forName(driver);
 
                Connection conn = DriverManager.getConnection(url, username, password); //Connecting to the database
                
                String hash = textField.getText(); //Getting the user input from the text field
                int lenOfHash = hash.length();
                if (lenOfHash < 32)
                    result.setText("Hash must be 32 characters long!"); //Length check for hash
                else {
                    Statement stmnt = conn.createStatement();
                    String select = "SELECT * FROM wordhashes"; //Selecting all records from wordhashes
                    ResultSet rs = stmnt.executeQuery(select); //Storing all records into a result set
                    while (rs.next()) {
                        String word = rs.getString("word");
                        String dbHash = rs.getString("hash");
                        if (dbHash.equals(hash)) { //If the hash in the database table equals the hash the user entered...
                            result.setText(word); //Match
                            break; //Need to break the loop otherwise it will always output the last word in the table when a match is found
                        }
                        if (!dbHash.equals(hash))
                            result.setText("Could not crack!"); //No match
                    }
                }
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
    }
 
    //Upload dictionary button functionality
    public class dictionaryFile implements ActionListener {
 
        public void actionPerformed(ActionEvent e) {
 
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); //Defaults to desktop
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File  (*.txt)", "txt");
            jfc.setFileFilter(filter); //Creating a filter to show only .txt files
 
            int returnValue = jfc.showOpenDialog(null);
 
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                dictionaryFile = jfc.getSelectedFile(); //Assigning the file they selected to dictionaryFile
                dictField.setText(dictionaryFile.getAbsolutePath()); //Outputting selected dictionary file to GUI
            }
        }
    }
 
    //Upload hash button functionality
    public class hashFile implements ActionListener {
 
        public void actionPerformed(ActionEvent e) {
 
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); //Defaults to desktop
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File (*.txt)", "txt");
            jfc.setFileFilter(filter); //Creating a filter to show only .txt files
 
            int returnValue = jfc.showOpenDialog(null);
 
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                hashFile = jfc.getSelectedFile(); //Assigning the file they selected to hashFile
                hashField.setText(hashFile.getAbsolutePath()); //Outputting the selected hash file to GUI
            }
        }
    }
 
    //Clear dictionary button functionality
    static class clearDict implements ActionListener {
 
        private static File dictionary = new File("defaultdict.txt"); //Initialising the dictionary to default file
 
        public void actionPerformed(ActionEvent e) {
            dictField.setText(""); //Clear the text field that shows what dictionary file has been selected
            dictionaryFile = dictionary; //Reassigning the dictionary file to the default
        }
    }
 
    //Mass crack button functionality
    static class massCrack implements ActionListener {
 
        private static Scanner input;
        private static File dictionary = new File("defaultdict.txt"); //Intialising the dictionary to default file
 
        public void actionPerformed(ActionEvent e) {
            //Disabling the crack button while it's running
            massCrack.setEnabled(false);
 
            //Have to run the whole action on a separate thread to the main thread so that the main thread
            //can return and handle any event dispatches (like setValue of progress bar)
            new Thread() {
                public void run() {
                    try {
                        String driver = "com.mysql.cj.jdbc.Driver";
                        String url = "jdbc:mysql://localhost:3306/passwordcracker";
                        String username = "root";
                        String password = "a1l2e3x4m5a6t7i8";
                        Class.forName(driver);
 
                        Connection conn = DriverManager.getConnection(url, username, password); //Connecting to the database                
 
                        String delete = "DELETE FROM hashes";
                        Statement stmnt = conn.createStatement();
                        stmnt.executeUpdate(delete); //Clearing hashes to be cracked from previous attempt
 
                        solvedHashes.setText(""); //Clearing results from previous attempt
 
                        String hash, word, hashWord, sWord, sHash, charManip, toggles = "";
 
                        //If a hash file isn't uploaded then show user the error message
                        if (hashFile == null || !hashFile.exists()) {
                            solvedHashes.setText("Please upload a text file containing the hashes you wish to crack!");
                            return;
                        }
                       
                        //If a dictionary file was uploaded then use that over the default
                        if (dictionaryFile != null && dictionaryFile.exists()) {
                            dictionary = dictionaryFile;
                        }
 
                        //Calculating the max number of processes to run to get percentage of progress
                        int hashLines = Math.toIntExact(Files.lines(Paths.get(hashFile.getPath())).count());
                        int dictLines = Math.toIntExact(Files.lines(Paths.get(dictionary.getPath())).count());
                        int numToggles = dictLines;
                        int maxProgress = hashLines + (dictLines * (1 + numToggles + manipulations.length()));
                        int cProgress = 1; //currentprogress
 
                        //Insert hashes to be cracked into database
                        try {
                            input = new Scanner(hashFile);
                            while (input.hasNext()) {
                                hash = input.nextLine();
                                String insert = "INSERT IGNORE INTO hashes (hash)" +
                                    "VALUES (?)";
                                PreparedStatement pstmnt = conn.prepareStatement(insert);
                                pstmnt.setString(1, hash);
                                pstmnt.executeUpdate();
                                //Setting the progress percentage at this current point and updating current progress
                                cProgress = setProgress(cProgress, maxProgress);
                            }
                        } catch (FileNotFoundException e2) {
                            System.out.println(e2);
                        }
 
                        //Insert Dictionary into database
                        try {
                            input = new Scanner(dictionary);
                            while (input.hasNext()) {
                                word = input.nextLine().toLowerCase();
                                hashWord = MD5(word);
 
                                String insertWord = "INSERT IGNORE INTO wordhashes (word, hash)" + //Ignore duplicate hashes for more efficiency
                                    "VALUES (?, ?)";
                                PreparedStatement pstmnt = conn.prepareStatement(insertWord);
                                pstmnt.setString(1, word);
                                pstmnt.setString(2, hashWord);
                                pstmnt.executeUpdate();
                                //Setting the progress percentage at this current point and updating current progress
                                cProgress = setProgress(cProgress, maxProgress);
 
                                //Number and special character manipulations
                                for (int i = 0; i < manipulations.length(); i++) {
                                    charManip = word + manipulations;
                                    char c = manipulations.charAt(i);
                                    charManip = word + c;
                                    hashWord = MD5(charManip);
 
                                    String insertWordChar = "INSERT IGNORE INTO wordhashes (word, hash)" + //Ignore duplicate hashes for more efficiency
                                        "VALUES (?, ?)";
                                    PreparedStatement pstmnt2 = conn.prepareStatement(insertWordChar);
                                    pstmnt2.setString(1, charManip);
                                    pstmnt2.setString(2, hashWord);
                                    pstmnt2.executeUpdate();
                                    //Setting the progress percentage at this current point and updating current progress
                                    cProgress = setProgress(cProgress, maxProgress);
                                }
 
                                //Toggle case manipulation
                                int length = word.length();
                                int max = 1 << length; //Maximum number of possible toggles (2^n) using left shift
 
                                //Use all possibilities
                                for (int j = 1; j < max; j++) {
                                    char[] allToggles = word.toCharArray();
 
                                    for (int k = 0; k < length; k++) {
                                        if (alphabet.contains(String.valueOf(word.charAt(k)))) { //Only toggle characters that are in the alphabet array
                                            if (((j >> k) & 1) == 1) allToggles[k] = (char)(allToggles[k] - 32); //Toggle
                                        }
                                    }
 
                                    toggles = new String(allToggles);
                                    hashWord = MD5(toggles);
                                    String insertWordToggle = "INSERT IGNORE INTO wordhashes (word, hash)" + //Ignore duplicate hashes for more efficiency
                                        "VALUES (?, ?)";
                                    PreparedStatement pstmnt3 = conn.prepareStatement(insertWordToggle);
                                    pstmnt3.setString(1, toggles);
                                    pstmnt3.setString(2, hashWord);
                                    pstmnt3.executeUpdate();
                                }
                            }
                            //Finished all toggles for the word so +1%
                            cProgress = setProgress(cProgress, maxProgress);
 
                            //Select matches
                            Statement stmnt1 = conn.createStatement();
                            String select = "SELECT * FROM wordhashes INNER JOIN hashes ON wordhashes.hash = hashes.hash";
                            ResultSet rs1 = stmnt1.executeQuery(select);
                            while (rs1.next()) {
                                sWord = rs1.getString("word");
                                sHash = rs1.getString("hash");
                                solvedHashes.append(sWord + " - " + sHash); //Display solved hashes to text area
                                solvedHashes.append("\r\n");
                            }
                            // Finished so max/max
                            setProgress(maxProgress, maxProgress);
                        } catch (FileNotFoundException e3) {
                            System.out.println(e3);
                        }
 
                    } catch (Exception e4) {
                        System.out.println(e4);
                    } finally {
                        massCrack.setEnabled(true); //Re-enable crack button as execution has complete
                    }
                }
            }.start(); //Executes the thread
        }
    }
 
    //Save button functionality
    static class Save implements ActionListener {
 
        public void actionPerformed(ActionEvent e) {
 
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File (*.txt)", "txt");
            jfc.setFileFilter(filter); //Creating a filter to show only .txt files
            int result = jfc.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String content = solvedHashes.getText();
                File savedFile = new File(jfc.getSelectedFile().getAbsolutePath() + ".txt"); //The new file with .txt automatically added
                int overwriteRes = 0;
                if (savedFile.exists()) {
                    // If file exists, show overwrite dialog
                    overwriteRes = JOptionPane.showConfirmDialog(jfc, "File exists, do you wish to overwrite?", "File Already Exists", JOptionPane.YES_NO_OPTION);
                    // If dialog res is not YES then return
                    if (overwriteRes != JOptionPane.YES_OPTION)
                        return;
                }
                //If the file doesn't exist or if it exists but the user agrees to overwrite file, save
                if (!savedFile.exists() || savedFile.exists() && overwriteRes == JOptionPane.YES_OPTION)
                    try {
                        FileWriter fw = new FileWriter(savedFile.getPath());
                        fw.write(content);
                        fw.flush();
                        fw.close();
                        return;
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(null, e2.getMessage());
                    }
            }
        }
    }
 
    // Setting percentage of progress out of 100 and increment current progress
    private static int setProgress(int current, int max) {
        progressBar.setValue((current * 100) / max);
        return current + 1;
    }
   
    //MD5 Hash Algorithm
    private static String MD5(String data) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5"); //Get the MD5 algorithm
        byte[] md5Digest = md5.digest(data.getBytes(UTF_8)); //Calculate Message Digest as bytes
        return String.format("%032x%n", new BigInteger(1, md5Digest)); //Convert to 32-character long string
    }
 
    //Single hash text field limit
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