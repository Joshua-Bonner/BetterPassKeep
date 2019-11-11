// CMPSC 444 - Assignment#5 Final Password Vault
// Authors:
// Joshua Bonner
// Alyssa Abram
// Ariel Rupp

import java.io.*;
import java.lang.*;
import java.math.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.*;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;

public class BetterPassKeep {

	// GLOBAL VARIABLES
	public static boolean isFileCreated = false;
	public static PrintWriter pw;
	public static BufferedReader br;
	public static FileReader fr;
	public static Scanner sc = new Scanner(System.in);
	public static Console con = System.console();
	public static ArrayList<String> idList = new ArrayList<String>();
	public static ArrayList<String> userList = new ArrayList<String>();
	public static ArrayList<String> passList = new ArrayList<String>();
	
	// MAIN
	public static void main(String[] args)
	throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
				 IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, 
				 InvalidAlgorithmParameterException, InterruptedException, NoSuchProviderException {

	  boolean correctInput = false;
	  int userChoice = -1;
	  File passKeep = new File("PassKeep");
	  isFileCreated = passKeep.exists();
	  if (isFileCreated == true){
	  	readFile();
	  	if(!verifyPassword()) System.exit(0);
	  }

	  cls();

	  // USER MENU
      do {
      	do {
      		System.out.println("Welcome to Password Keeper\n\n"
          	+ "Please select from the following options:\n"
          	+ "   1. Initialize Password Keeper file\n"
          	+ "   2. Change master password\n"
          	+ "   3. Add new password\n"
          	+ "   4. Retrieve password information\n"
          	+ "   5. Share password information\n"
          	+ "   6. Exit Password Keeper");
          	System.out.print("User Input : ");

          if (sc.hasNextInt()){
          	userChoice = sc.nextInt();
            correctInput = true;
          }
          else {
          	sc.nextLine();
            System.out.println("\nIncorrect input, please provide an integer numbered 1 through 6\n");
            cls();
          }
      	} while (correctInput == false);

      	// USER MENU CHOICES
	  	switch (userChoice){

	  		// INITIALIZE PASSWORD FILE WITH A USER CREATED MASTER PASSWORD
          case 1:
          	if (isFileCreated == true){
          		if(verifyPassword()){
          			sc = new Scanner(System.in);
          			System.out.println("\nDoing so will overwrite the existing file!");
          			System.out.print("\nConfirm by entering 'Y' any other input will abort this operation : ");
          			String in = sc.nextLine();
          			if (in.equals("Y")){
          				idList.clear();
          				userList.clear();
          				passList.clear();
          				createFile();
          				System.out.println("File Overwritten!");
          			}
          			else System.out.println("Aborting operation!");
          		}
          	}
          	else {
          		createFile();
            	System.out.println("File Initialized!");
            }
            cls();
          	break;
					
          	// CHANGE MASTER PASSWORD
          case 2:
          	if (isFileCreated == false)
          		System.out.println("\nFile not created! Please choose option 1");
          	else if(verifyPassword()){
			// TODO: add method to change master password
          	}
          	cls();
          	break;

			// ADD PASSWORDS
          case 3:
          	if (isFileCreated == false)
          		System.out.println("\nFile not created! Please choose option 1");
          	else if(verifyPassword()){
				// TODO: add method to add passwords
				readFile();	//read username/passwords from current file
				addPassword();	//add username/passwords to arraylists
				//TODO: add username/passwords in arraylist to current file
          	}
          	cls();
          	break;
					
          	// PRINT PASSWORD FILE CONTENTS
          case 4:
          	if (isFileCreated == false)
            	System.out.println("\nFile not created! Please choose option 1");
          	// TODO: Add method to Decrypt File with Master Password and Then Show Results
		  	cls();
          	break;
					
          	// SHARE FILE
          case 5:
          	if (isFileCreated == false)
            	System.out.println("\nFile not created! Please choose option 1");
          	// TODO: Add method to Share a password in a seperate file
		  	cls();
          	break;

          	// EXIT PROGRAM
		  case 6:
		  	System.out.println("\nThank you for using Password Keeper!\n");
		  	cls();
		  	System.exit(0);
		  	break;
            
          default:
          	System.out.println("\nIncorrect input, please provide an integer numbered 1 through 6\n");
          	cls();
        }
			} while (userChoice != 6);
      System.out.println("\nUnknown Error Occured");
	}

	public static void addPassword(){
		Scanner sc = new Scanner( System.in );
		char[] s = {'0'};
		Base64.Encoder enc = Base64.getEncoder();

		//Read in new username/password combo
		System.out.println( "Enter an ID for your username and password combination: " );
		String id = sc.nextLine();
		System.out.println( "Enter the username for " + id + " : " );
		String userName = sc.nextLine();
		System.out.println( "Enter the password for " + id + " : " );
		char[] password = con.readPassword();

		//Encrypt
		try
		{
			s = passList.get(0).toCharArray();
			PBEKeySpec pbeKeySpec = new PBEKeySpec( s );

			//https://nvisium.com/blog/2016/03/31/secure-password-strings.html
			Arrays.fill( s,'0' );

			//not 100% certain about this algorithm, mainly b/c it may be outdated
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance( "PBEWithHmacSHA256AndAES_128" );
			SecretKey secretKey = secretKeyFactory.generateSecret( pbeKeySpec );

			Cipher cipher = Cipher.getInstance( "PBEWithHmacSHA256AndAES_128" );
			cipher.init( Cipher.ENCRYPT_MODE,secretKey );

			byte[] cipherText = cipher.doFinal(String.valueOf(password).getBytes("UTF-8"));
			Arrays.fill( password,'0' );
			String encodedPass = enc.encodeToString(cipherText);
			passList.add( encodedPass );

			cipherText = cipher.doFinal(userName.getBytes("UTF-8"));
			encodedPass = enc.encodeToString(cipherText);
			userList.add( encodedPass );

			idList.add( id );
			int i = idList.indexOf( id );

			FileWriter passKeep = new FileWriter("PassKeep", true);
			pw = new PrintWriter(passKeep);
			pw.println(idList.get(i) +"\t"+ userList.get(i) +"\t"+ passList.get(i));
			pw.close();
		}
		catch ( NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException
				| BadPaddingException | IllegalBlockSizeException | IOException e )
		{
			e.printStackTrace();	//TODO: Remove this line - Debugging ONLY!!!!!!
			System.out.println( "Error. Can not add Username and Password Combo." );
			Arrays.fill( s,'0' );	//double check just in case program fails before the one in the try block
		}
	}

	public static void cls() throws InterruptedException{
		Thread.sleep(1000);
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
	
	public static void createFile() throws NoSuchProviderException, NoSuchAlgorithmException {
		Scanner sc = new Scanner(System.in);
		try {
			// Create secure random number
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			byte[] randBy = new byte[128];
			rand.nextBytes(randBy);
			
			// Create file to hold salt value
			File hashNum = new File("HashNum");
			if(!hashNum.exists()) hashNum.createNewFile();
			pw = new PrintWriter(hashNum);
			int r = rand.nextInt(99999999);
			pw.print(r);
			pw.close();
			
			// Read in User and Password
			System.out.println("Enter a username : ");
			String userName = sc.nextLine();
			System.out.println("Enter a password : ");
			char[] passwrd = con.readPassword();
			String pass = String.valueOf(passwrd);
			
			//Create hash of master password
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			Base64.Encoder enc = Base64.getEncoder();
			String hashPass = r+pass;
			byte[] hash = md.digest(hashPass.getBytes(StandardCharsets.UTF_8));
			String HashedPass = enc.encodeToString(hash);
			
			// Add appropriate values to their respective array lists
			idList.add("Master");
			userList.add(userName);
			passList.add(HashedPass);
			
			// Create PassKeep file where values are stored
			File passKeep = new File("PassKeep");
      		if(!passKeep.exists()) passKeep.createNewFile();
      		pw = new PrintWriter(passKeep);
      		pw.println(idList.get(0) +"\t"+ userList.get(0) +"\t"+ passList.get(0));
      		pw.close();
      		isFileCreated = true;
		} catch (IOException e) {System.out.println("ERROR CREATING FILE");}
	}
	
	public static boolean verifyPassword() throws NoSuchProviderException, NoSuchAlgorithmException, InterruptedException{

		// Read in salt from hashNum
		try{
			br = new BufferedReader(new FileReader("HashNum"));
			String r = br.readLine();
			br.close();
		
			// Get password guess from user
			sc = new Scanner(System.in);
			System.out.println("Enter master password : ");
			char[] passwrd = con.readPassword();
			String guess = String.valueOf(passwrd);
			String saltedGuess = r + guess;
		
			// Test guess versus master password
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			Base64.Encoder enc = Base64.getEncoder();
			byte[] hash = md.digest(saltedGuess.getBytes(StandardCharsets.UTF_8));
			String hashedGuess = enc.encodeToString(hash);
			if(hashedGuess.equals(passList.get(0))){
				System.out.println("Password Verified!");
				return true;
			}
			else {
				System.out.println("Password authentication failed!");
				System.out.println("Aborting operation!");
				return false;
			}
		} catch (IOException e) {System.out.println("ERROR READING FILE");}
		return false;
	}
	
	public static void readFile(){
		try{
			br = new BufferedReader(new FileReader("PassKeep"));
			String line = null;
			while((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				idList.add(String.valueOf(tokens[0]));
				userList.add(String.valueOf(tokens[1]));
				passList.add(String.valueOf(tokens[2]));
			}
			br.close();
		} catch (IOException e) {System.out.println("ERROR READING FILE\n");}
	}
	
	// SOURCE: https://www.baeldung.com/java-generate-secure-password
	public static Stream<Character> getRandomSpecialChars( int count){
		Random random = new SecureRandom();
		IntStream specialChars = random.ints(count, 33, 45);
    return specialChars.mapToObj( data ->(char) data );
  }

	public static Stream<Character> getRandomNumbers(int count){
		Random random  = new SecureRandom();
		IntStream randomNumbers = random.ints(count, 48, 57);
    return randomNumbers.mapToObj( data ->(char) data );
  }

	public static Stream<Character> getRandomAlphabets (int count, boolean upperCase){
		Random random = new SecureRandom();
    	IntStream randomAlphabets;
    	if(upperCase)
    		randomAlphabets = random.ints(count, 65, 90);
    	else
     		randomAlphabets = random.ints(count, 97, 122);
    	return randomAlphabets.mapToObj(data -> (char) data);
	}

	public static String randomPassword(){
		Stream<Character> passwordStream = Stream.concat(getRandomNumbers(2), Stream.concat(getRandomSpecialChars(2),
          Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(10, false))));
		List<Character> charList = passwordStream.collect( Collectors.toList());
		Collections.shuffle( charList );
		return charList.stream().collect( StringBuilder::new, StringBuilder::append, StringBuilder::append ).toString();
	}
}
