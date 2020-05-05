// Pet Sitting Services
// Created by Emma Griffin, Al Allums, and Sydney McClure
// Due Date: 29 April 2020 (c)

package util;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.Console;

public class Validation {
	// Shared state between pages for curUsername. Assumed to be validated
	// prior to being set externally.
	public static String curUsername;
	public static boolean userIsOwner;
	public static boolean userIsSitter;
	public static Integer curOfferID;
	
	public static Statement statement;

	final public static Scanner input = new Scanner(System.in);

	final public static SimpleDateFormat TS_FORMAT =
		new SimpleDateFormat("MMMMM dd yyyy, hh:mm aa");

	final public static SimpleDateFormat SQL_TS_FORMAT =
		new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	final public static SimpleDateFormat DATE_FORMAT =
		new SimpleDateFormat("MMMMM dd yyyy");

	final public static DecimalFormat CURRENCY_FORMAT = 
		new DecimalFormat("#.##");

	final public static int MAX_LENGTH = 50, PASS_LEN_MIN = 8,
		                      PASS_LEN_MAX = 15, DESC_MAX_LENGTH = 500,
		                      USER_LEN_MAX = 20;

	final public static String
		OPTIONS =	"Enter \n'p' to view/edit your profile, \n" +
					"'s' to search pet sitting posts, \n" +
					"'c' to create a pet sitting offer, or\n" +
					"'q' to quit",

		SPECIALS = "`~!@#$%^&*_-=+|<>?/[]",

		PASSWORD_REQS = "Password must: \n\t"+
						"> Be between "+PASS_LEN_MIN+" and "+PASS_LEN_MAX+" characters\n\t"+
						"> Have at least ONE number\n\t" +
						"> Have at least ONE special character, which include:\n\t  " +
							SPECIALS + "\n\t" +
						"> Have at least ONE upper & ONE lowercase letter\n";

	final public static String[] stateCodes
		= {	"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA",
			"HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
	    	"MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY",
	    	"NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX",
			"UT", "VT", "VA", "WA", "WV", "WI", "WY"};

	final public static String[] petTypes
		= { "Dog", "Cat", "Rabbit", "Bird", "Fish", "Reptile", "Rodent",
			"Amphibian", "Bug", "Other" };

	// Simple method to wrap all JDBC SQL update commands inside a generic
	// try-catch block
	public static void updateSQL(String updateCMD) {
		try {Validation.statement.executeUpdate(updateCMD);}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static ResultSet querySQL(String query) {
		try {return Validation.statement.executeQuery(query);}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
		return null;
	}

	// Returns number of record matches for a particular query.
	public static int numMatches(String query) {
		return numMatches(query, "accounts");
	}

	public static int numMatches(String query, String database) {
		try {
			ResultSet rs = Validation.statement.executeQuery(query);
			int ret;
			if (rs != null) {
				rs.last();
				ret =  rs.getRow();
			}
			else
				ret = 0;
			
			rs.close();
			return ret;
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}

		// Needed to quiet javac error about function not
		// returning a value
		return 0;
	}

	// Returns true if opt is a valid menu selection option, false otherwise
	public static boolean isValidOption(char opt) {
		return opt == 'p' || opt == 's' || opt == 'c' || opt == 'q';
	}

	// Returns true if SC is equal to one of the 51 stateCodes and false
	// otherwise.
	public static boolean isAState(String SC) {
		for (int i = 0; i < stateCodes.length; ++i)
			if (SC.equals(stateCodes[i]))
				return true;

		return false;
	}

	// Between 8 - 15
	// One number, one captial letter, One special character
	public static boolean isValidPassword(String PW)
	{
		boolean hasSpecial = false;
		boolean hasNum = false;
		boolean hasCap = false;

		// Test if password meets length requirement
		if (PW.length() < PASS_LEN_MIN || PW.length() > PASS_LEN_MAX)
			return false;

		for (int i = 0; i < PW.length(); ++i) {
			for (int j = 0; j < SPECIALS.length(); ++j)
			// Checks if password contains at least 1 special character
				if (PW.charAt(i) == SPECIALS.charAt(j)) {
					hasSpecial = true;
					break;
				}

			// Checks if password contains at least 1 number
			// "0" = 48, "9" = 57
			if (PW.charAt(i) >= 48 && PW.charAt(i) <= 57)
				hasNum = true;

			// Checks if password contains at least 1 capital letter
			// "A" = 65, "Z" = 90
			if (PW.charAt(i) >= 65 && PW.charAt(i) <= 90)
				hasCap = true;

			// If all become true, break loop
			if (hasNum && hasSpecial && hasCap)
				break;
		}

		return hasNum && hasSpecial && hasCap ? true : false;
	}

	// Returns true if format is "username@website.top-level-domain" and
	// false otherwise
	public static boolean isValidEmail(String email) {
		final String query = "SELECT * FROM accounts WHERE email = '" +
			                   email + "';";
		Scanner strScanner = new Scanner(email);
		strScanner.useDelimiter("@");
		if (strScanner.hasNext()) {
			int emailLength = strScanner.next().length();
			if (strScanner.hasNext()) {
				String buffer = strScanner.next();
				emailLength += buffer.length();
				strScanner = new Scanner(buffer);
				// Escape dot to treat as a character delimiter rather than a regex
				strScanner.useDelimiter("\\.");
				if (strScanner.hasNext()) {
					emailLength += strScanner.next().length();
					if (strScanner.hasNext()) {
						emailLength += strScanner.next().length();
						// Check if email already exists
						if (emailLength < MAX_LENGTH) {
							if (numMatches(query) > 0)
								System.out.println("Account already exists associated with"
																	 + " email.");
							else
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	static public String getUsername() {
		String username = null, query;
		do {
			if (username != null) {
				System.out.println("Username already exists\n");
				System.out.println("Username: ");
			}
			username = Validation.preventSQLInjection(Validation.input.nextLine());
			while (username.length() == 0 ||
						 username.length() > USER_LEN_MAX) {
				System.out.println("Usernames must be " + USER_LEN_MAX +
													 " characters or fewer");
				System.out.print("Username: ");
				username = Validation.preventSQLInjection(Validation.input.nextLine());
			}
			query = "SELECT username FROM accounts WHERE username = '" +
				username + "';";
		}	while (Validation.numMatches(query) > 0);
		System.out.println();
		return username;
	}

	static public String getPassword() {
		String password;
		// Attempt to read in the password with obscured input for added
		// added security
		Console cons;
		String confirmedPassword = null;
		if ((cons = System.console()) != null) {
			do {
				if (confirmedPassword != null)
					System.out.println("Passwords do not match.");
				System.out.print("***NOTE: For security sake, the console will not"
												 + " display input characters (input will appear "
												 + "blank)***\nPassword: ");
				password = String.valueOf(cons.readPassword());
				while (!Validation.isValidPassword(password)) {
					System.out.println("Please enter a password that meets the" +
														 " following criteria:\n"
														 +Validation.PASSWORD_REQS);
					System.out.print("***NOTE: For security sake, the console will "
													 + " not display input characters (input will "
													 + " appear blank)***\nPassword: ");
					System.out.println("Password is not valid. "
														 + Validation.PASSWORD_REQS);
					System.out.print("Password: ");
					password = String.valueOf(cons.readPassword());
				}
				System.out.print("Confirm password: ");
				confirmedPassword =
					String.valueOf(cons.readPassword());
			} while (!confirmedPassword.equals(password));
		}
		// Display as plain text instead
		else {
			System.out.print("Password: ");
			password = Validation.preventSQLInjection(Validation.input.nextLine());
			while (!Validation.isValidPassword(password)) {
				System.out.println("Password is not valid. "
													 + Validation.PASSWORD_REQS);
				System.out.print("Password: ");
				password = Validation.preventSQLInjection(Validation.input.nextLine());
			}
		}
		System.out.println();
		return password;
	}

	static public String getFullname() {
		String fullname = Validation.preventSQLInjection(Validation.input.nextLine());
		System.out.println();
		while (fullname.length() == 0 ||
					 fullname.length() > Validation.MAX_LENGTH) {
			System.out.println("Full names must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Enter your full name: ");
			fullname = Validation.preventSQLInjection(Validation.input.nextLine());
			System.out.println();
		}
		return fullname;
	}

	static public String getEmail() {
		String email = Validation.preventSQLInjection(Validation.input.nextLine());
		System.out.println();
		while (!Validation.isValidEmail(email)) {
			System.out.println("Please enter a valid email (less than " +
												 Validation.MAX_LENGTH + " characters) in format:"
												 + "\n\tusername@website.domain");
			System.out.print("Email: ");
			email = Validation.preventSQLInjection(Validation.input.nextLine());
		}
		return email;
	}

	static public String getCity() {
		String city = Validation.preventSQLInjection(Validation.input.nextLine());
		System.out.println();
		while (city.length() == 0 || city.length() > Validation.MAX_LENGTH) {
			System.out.println("City must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Please enter your city name: ");
			city = Validation.preventSQLInjection(Validation.input.nextLine());
			System.out.println();
		}
		return city;
	}

	static public String getState() {
		String state = Validation.preventSQLInjection(Validation.input.nextLine());
		System.out.println();
		while (!Validation.isAState(state)) {
			System.out.print("Please enter a valid two character "
											 + "state code: ");
			state = Validation.preventSQLInjection(Validation.input.nextLine());
			System.out.println();
		}
		return state;
	}

	static public boolean getIsPetSitter() {
		Validation.input.useDelimiter("");
		char c = Validation.input.next().charAt(0);
		Validation.input.nextLine();
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Are you a pet sitter (y/n)? ");
			c = Validation.input.next().charAt(0);
			Validation.input.nextLine();
			System.out.println();
		}
		Validation.input.reset();

		return c == 'y' || c == 'Y' ? true : false;
	}

	static public boolean getIsPetOwner() {
		Validation.input.useDelimiter("");
		char c = Validation.input.next().charAt(0);
		Validation.input.nextLine();
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Are you a pet owner (y/n)? ");
			c = Validation.input.next().charAt(0);
			Validation.input.nextLine();
			System.out.println();
		}
		Validation.input.reset();

		return c == 'y' || c == 'Y' ? true : false;
	}

	static public String getPetName()
	{
		String petname = Validation.preventSQLInjection(Validation.input.nextLine());
		System.out.println();
		while (petname.length() == 0 ||
					 petname.length() > Validation.MAX_LENGTH) {
			System.out.println("Pet names must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Enter your pet's name: ");
			petname = Validation.preventSQLInjection(Validation.input.nextLine());
			System.out.println();
		}

		return petname;
	}

	public static void printPetTypes()
	{
		for (int i = 0; i < petTypes.length; ++i)
			System.out.println(i + ") " + petTypes[i]);
	}

	public static String getPetType()
	{
		int response = Integer.parseInt(input.nextLine());
		System.out.println();
		while (response < 0 || response > petTypes.length)
		{
			System.out.println("Invalid input. \n" +
								"Please select one of the following:");
			printPetTypes();
			response = Integer.parseInt(input.nextLine());
			System.out.println();
		}

		return petTypes[response];
	}

	static public int getPetAge()
	{
		int age = Integer.parseInt(input.nextLine());
		System.out.println();
		while (age < 0 || age > 100) {
			System.out.println("Please enter a valid age. Note: if your pet is less than a year old, type 0.");
			System.out.print("Enter your pet's age (in years): ");
			age = Integer.parseInt(Validation.input.nextLine());
			System.out.println();
		}
		return age;
	}

	public static boolean petExists(String petName) {
		boolean ret = false;
		try{
			ResultSet rs = Validation.statement.executeQuery(
				"SELECT COUNT(*) as count FROM pets WHERE petName = '" +
				petName + "' AND owner = '" + curUsername + "';");
			while (rs.next()) {
				int i = rs.getInt("count");
				ret = (i == 1 ? true : false);
			}
			rs.close();
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}

		return ret;
	}

	public static String findNameFromPetID(int sitting){
		try{
			ResultSet rs = Validation.statement.executeQuery("SELECT petName FROM pets WHERE petID = " + sitting + " ;");
			String ret = rs.getString("petName");
			rs.close();
			return ret;
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
		return null;
	}

	public static int findPetIDFromPetName()
	{
		int petID = -1;
		String name;
		String query = "";
		boolean petFound = true;

		try {
			// Loop until user enters a name of a pet that actually exists.
			do{
				System.out.print("Enter the name of pet: ");
				name = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (name.length () > 0) {
					query = "SELECT petID FROM pets WHERE owner = '" +
								Validation.curUsername + "' AND petName = '" +
								name + "';";
					petFound = Validation.numMatches(query, "pets") == 1 ? true :
						                                                     false;
				}
				else{
					petFound = false;
					System.out.println("Invalid option!");
				}
			} while (!petFound);

			ResultSet rs = Validation.querySQL(query);
			while(rs.next())
				petID = rs.getInt("petID");
			rs.close();
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
		return petID;
	}

	// Prompts user for a name and returns the pet's id
	public static int getSitting()
	{
		return findPetIDFromPetName();
	}
	public static String getDescription()
	{
		String desc;
		char c = 'a';
		// Since a description can be really long,
		//	the user should verify it if is correct.
		do {
			// Reprint prompt if this is a reiteration
			if (c != 'y')
				System.out.println("Enter your post's additional information: ");

			desc = Validation.input.nextLine();
			System.out.println();

			while (desc.length() > Validation.DESC_MAX_LENGTH) {
				System.out.println("Your description exceeds the " +
													 Validation.DESC_MAX_LENGTH +
													 " limit. Length: " + desc.length());
				System.out.println("Please enter your post's additional " +
													 "information: ");
				desc = Validation.input.nextLine();
				System.out.println();
			}

			if (desc.length() == 0)
				System.out.println("Your post will have no description, is that " +
										"okay? (Type 'y' to confirm.)");
			else
			{
				System.out.println("Verify that your post's description is " +
													 "correct. (Type 'y' to confirm.)");
				System.out.println("\"" + desc +"\"");
			}
			String temp = Validation.preventSQLInjection(Validation.input.nextLine());
			if (temp.length() == 0)
				c = 'z';
			else
				c = Character.toLowerCase(temp.charAt(0));

			System.out.println();
		} while (c != 'y');

		return Validation.preventSQLInjection(desc);
	}
	public static double getPayment() {
		Double payment = 0.0;
		boolean validOption = true;
		do {
			try {
				// Rounding input to 2 decimals
				if (!validOption)
					System.out.print("Enter your post's payment amount: $");
				payment = Double.valueOf(Validation.input.nextLine());
				validOption = true;
			}
			catch (Exception e){
				System.out.println("Invalid input!");
				validOption = false;
			}
		} while (!validOption);

		return payment;
	}

	public static Timestamp getOfferStartDate()
	{
		String str = null;
		boolean badTS;
		Timestamp ts = null;
		do{
			badTS = false;
			try{
				System.out.print("Start Date and Time (ex: January 01 2020, "
												 + "12:30 pm): ");
				str = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (str.length() == 0)
					throw new Exception();

				// Convert time from input to format SQL prefers
				str = SQL_TS_FORMAT.format(TS_FORMAT.parse(str));
				ts = Timestamp.valueOf(str);
			}
			catch (Exception e) {
				badTS = true;
				System.out.println("Invalid entry.");
			}
		} while (badTS);

		return ts;
	}

	public static Timestamp getOfferEndDate()
	{
		String str = null;
		boolean badTS;
		Timestamp ts = null;
		do{
			badTS = false;
			try{
				System.out.print("End Date and Time (ex: January 01 2020, "
												 + "12:30 pm): ");
				str = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (str.length() == 0)
					throw new Exception();

				// Convert time from input to format SQL prefers
				str = SQL_TS_FORMAT.format(TS_FORMAT.parse(str));
				ts = Timestamp.valueOf(str);
			}
			catch (Exception e) {
				badTS = true;
				System.out.println("Invalid date.");
			}
		} while (badTS);
		return ts;
	}

	public static String preventSQLInjection(String query) {
		StringBuilder sb = new StringBuilder(query);
		// Insert single quote for each existing quote to escape single
		// quotes in text
		for (int i = 0, strLen = sb.toString().length(); i < strLen; ++i)
			if (sb.charAt(i) == '\'') {
				++strLen;
				sb.insert(++i, '\'');
			}

		return sb.toString();
	}

	// Undo SQLInjection prevention for displaying query results to user
	public static String halveSingleQuotes(String result) {
		StringBuilder sb = new StringBuilder();
		// Insert single quote for each existing quote to escape single
		// quotes in text
		for (int i = 0; i < result.length(); ++i) {
			if (result.charAt(i) == '\'')
				sb.append(result.charAt(i++));
			else
				sb.append(result.charAt(i));
		}
		return sb.toString();
	}
}
