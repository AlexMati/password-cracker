package passwordcracker;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class Main {

	private static Connection conn;
	static String hash;
	static String word;
	static String sWord;
	static String sHash;
	static String toggles;
	static File dictionary;
	static Scanner input;
	
	final static String numbers = "0123456789";
	final static String specialChr = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"; // Used for word manipulations
	final static String alphabet = "abcdefghijklmnopqrstuvwxyz"; // Used for toggle case function


	private static void dbConnect() {
		try {

			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/passwordcracker?serverTimezone=UTC";
			String username = "root";
			String password = "a1l2e3x4m5a6t7i8";
			Class.forName(driver);

			conn = DriverManager.getConnection(url, username, password);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	static class singleCrack implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			dbConnect();

			String hash = GUI.textField.getText();
			int lenOfHash = hash.length();
			if (lenOfHash < 32)
				GUI.result.setText("Hash must be 32 characters long!");
			else {
				try {
					Statement stmnt = Main.conn.createStatement();
					String select = "SELECT * FROM wordhashes";
					ResultSet rs = stmnt.executeQuery(select);
					while (rs.next()) {
						String word = rs.getString("word");
						String dbHash = rs.getString("hash");
						if (dbHash.equals(hash)) {
							GUI.result.setText(word);
							break;
						}
						if (!dbHash.equals(hash))
							GUI.result.setText("Could not crack!");
					}
				} catch (Exception e1) {
					System.out.println(e1);
				}
			}
		}
	}

	static class dictionaryFile implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File  (*.txt)", "txt");
				jfc.setFileFilter(filter);

				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					GUI.dictionaryFile = jfc.getSelectedFile();
					GUI.dictField.setText(GUI.dictionaryFile.getAbsolutePath());
				}
			} catch (Exception e2) {
				System.out.println(e2);
			}
		}
	}

	static class hashFile implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File (*.txt)", "txt");
			jfc.setFileFilter(filter);

			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				GUI.hashFile = jfc.getSelectedFile();
				GUI.hashField.setText(GUI.hashFile.getAbsolutePath());
			}
		}
	}

	static class clearDict implements ActionListener {

		private static File dictionary = new File("defaultdict.txt");

		public void actionPerformed(ActionEvent e) {
			GUI.dictField.setText("");
			GUI.dictionaryFile = dictionary;
		}
	}

	public static void clearHash() throws SQLException {
		String delete = "DELETE FROM hashes";
		Statement stmnt = conn.createStatement();
		stmnt.executeUpdate(delete);
	}

	public static void checkHashFile() {
		if (GUI.hashFile == null || !GUI.hashFile.exists()) {
			GUI.solvedHashes.setText("Please upload a text file containing the hashes you wish to crack!");
			return;
		}
	}

	public static void checkDictionary() {
		dictionary = new File("defaultdict.txt");

		if (GUI.dictionaryFile != null && GUI.dictionaryFile.exists()) {
			dictionary = GUI.dictionaryFile;
		}
	}	

	public static void insertHashes() {	
		try {
			input = new Scanner(GUI.hashFile);
			while (input.hasNext()) {
				hash = input.nextLine();
				String insert = "INSERT IGNORE INTO hashes (hash)" + "VALUES (?)";
				PreparedStatement pstmnt = conn.prepareStatement(insert);
				pstmnt.setString(1, hash);
				pstmnt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void addDictionary() {
		try {
			input = new Scanner(dictionary);
			while (input.hasNext()) {
				word = input.nextLine().toLowerCase();
				String hashWord = MD5(word);

				insertWordHashes(word, hashWord);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void insertWordHashes(String word, String hash) {
		try {
			String insertWordChar = "INSERT IGNORE INTO wordhashes (word, hash)" + "VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(insertWordChar);
			stmt.setString(1, word);
			stmt.setString(2, hash);
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.print(e);
		}
	}

	public static void addNumbers() {
		try {
			input = new Scanner(dictionary);
			while (input.hasNext()) {
				word = input.nextLine().toLowerCase();
				for (int i = 0; i < numbers.length(); i++) {
					char c = numbers.charAt(i);
					String numManip = word + c;
					String hashWord = MD5(numManip);

					insertWordHashes(numManip, hashWord);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void addSpecialCharacters() {
		try {
			input = new Scanner(dictionary);
			while (input.hasNext()) {
				word = input.nextLine().toLowerCase();
				for (int i = 0; i < specialChr.length(); i++) {
					char c = specialChr.charAt(i);
					String manipulatedWord = word + c;
					String hashWord = MD5(manipulatedWord);

				insertWordHashes(manipulatedWord, hashWord);
				}
			}
		} catch (Exception e) {
			System.out.print(e);
		}
	}

	public static void toggleCase() {
		try {
			input = new Scanner(dictionary);
			while (input.hasNext()) {
				word = input.nextLine().toLowerCase();
				int length = word.length();
				int max = 1 << length;

				for (int j = 1; j < max; j++) {
					char[] allToggles = word.toCharArray();

					for (int k = 0; k < length; k++) {
						if (alphabet.contains(String.valueOf(word.charAt(k)))) {
							if (((j >> k) & 1) == 1)
								allToggles[k] = (char) (allToggles[k] - 32);
						}
					}

					String toggles = new String(allToggles);
					String hashWord = MD5(toggles);
					insertWordHashes(toggles, hashWord);
				}
			}
		} catch (Exception e) {
			System.out.print(e);
		}
	}

	public static void replaceCharacters() {
		try {
			input = new Scanner(dictionary);
			while (input.hasNext()) {
				word = input.nextLine().toLowerCase();
				String Rep = word.replace('a', '4');
				System.out.println(Rep);
			}
		} catch (Exception e7) {
			System.out.println(e7);
		}

	}

	public static void selectMatches() throws SQLException {
		Statement stmnt1 = conn.createStatement();
		String select = "SELECT * FROM wordhashes INNER JOIN hashes ON wordhashes.hash = hashes.hash";
		ResultSet rs1 = stmnt1.executeQuery(select);
		while (rs1.next()) {
			sWord = rs1.getString("word");
			sHash = rs1.getString("hash");
			GUI.solvedHashes.append(sWord + " - " + sHash);
			GUI.solvedHashes.append("\r\n");
		}
	}
		
	static class massCrack implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			GUI.massCrack.setEnabled(false);
			
			new Thread() {
				public void run() {
					try {
						dbConnect();
						clearHash();
						GUI.solvedHashes.setText("");
						checkHashFile();
						insertHashes();
						checkDictionary();
						// Calculating the max number of processes to run to get percentage of progress
						int hashLines = Math.toIntExact(Files.lines(Paths.get(GUI.hashFile.getPath())).count());
						int dictLines = Math.toIntExact(Files.lines(Paths.get(dictionary.getPath())).count());
						int numToggles = dictLines;
						int maxProgress = hashLines + (dictLines * (1 + numToggles + numbers.length()));
						int cProgress = 1;
						
						addDictionary();

						if(GUI.cbNumbers.isSelected()) {
							addNumbers();
						}
						
						if(GUI.cbSpecial.isSelected()) {
							addSpecialCharacters();
						}
						
						if(GUI.cbToggle.isSelected()) {
							toggleCase();
						}
						
						if(GUI.cbReplace.isSelected()) {
							replaceCharacters();
						}

						selectMatches();

					} catch (FileNotFoundException e3) {
						System.out.println(e3);
					}

					catch (Exception e4) {
						System.out.println(e4);
					} finally {
						GUI.massCrack.setEnabled(true);
					}
				}
			}.start(); // Executes the thread
		}
	}

	private static String MD5(String data) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5"); // Get the MD5 algorithm
		byte[] md5Digest = md5.digest(data.getBytes(UTF_8)); // Calculate Message Digest as bytes
		return String.format("%032x%n", new BigInteger(1, md5Digest)); // Convert to 32-character long string
	}
}
