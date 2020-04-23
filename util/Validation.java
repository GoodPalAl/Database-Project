package util;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Console;

public class Validation {
	// Shared state between pages for curUsername. Assumed to be validated
	// prior to being set externally.
	public static String curUsername;
	public static Statement statement;

	final public static Scanner input = new Scanner(System.in);

	final public static SimpleDateFormat FORMAT =
		new SimpleDateFormat("MMMMM dd yyyy");

	final public static int MAX_LENGTH = 50, PASS_LEN_MIN = 8,
		                      PASS_LEN_MAX = 15, DESC_MAX_LENGTH = 500;
	final public static String
		OPTIONS = "Enter 'p' to view/edit your profile, 's' to search pet" +
		" sitting posts, 'c' to create a pet sitting offer, or 'q'" +
		" to quit",
		SPECIALS = "`~!@#$%^&*_-=+|<>?/[]",
		PASSWORD_REQS = "Password must: \n\t"+
		"> Be between "+PASS_LEN_MIN+" and "+PASS_LEN_MAX+" characters\n\t"+
		"> Have at least ONE number\n\t" +
		"> Have at least ONE special character, which include:\n\t  " +
		SPECIALS + "\n\t" +
		"> Have at least ONE upper & ONE lowercase letter\n";

	final public static String[] stateCodes
		= {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA",
			 "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
	     "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY",
	     "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX",
			 "UT", "VT", "VA", "WA", "WV", "WI", "WY"};

	// ADDED 04/22/20
	final public static String []petTypes
		= {"Dogs", "Cats", "Rabbits", "Birds", "Fish", "Reptiles", "Rodents",
	     "Amphibians", "Bugs", "Other"};

	public static void printPetTypes() {
		for (int i = 0; i < petTypes.length; ++i)
			System.out.println(i + ") " + petTypes[i]);
	}


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
			if (rs != null) {
				rs.last();
				return rs.getRow();
			}
			else
				return 0;
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

		if (PW.length() < PASS_LEN_MIN || PW.length() > PASS_LEN_MAX)
			return false;

		for (int i = 0; i < PW.length(); ++i) {
			for (int j = 0; j < SPECIALS.length(); ++j)
				if (PW.charAt(i) == SPECIALS.charAt(j)) {
					hasSpecial = true;
					break;
				}

			// "0" = 48, "9" = 57
			if (PW.charAt(i) >= 48 && PW.charAt(i) <= 57)
				hasNum = true;

			// "A" = 65, "Z" = 90
			if (PW.charAt(i) >= 65 && PW.charAt(i) <= 90)
				hasCap = true;

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
			username = Validation.input.nextLine();
			while (username.length() == 0 ||
						 username.length() > Validation.MAX_LENGTH) {
				System.out.println("Usernames must be " + Validation.MAX_LENGTH +
													 " characters or fewer");
				System.out.print("Username: ");
				username = Validation.input.nextLine();
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
			password = Validation.input.nextLine();
			while (!Validation.isValidPassword(password)) {
				System.out.println("Password is not valid. "
													 + Validation.PASSWORD_REQS);
				System.out.print("Password: ");
				password = Validation.input.nextLine();
			}
		}
		System.out.println();
		return password;
	}

	static public String getFullname() {
		String fullname = Validation.input.nextLine();
		System.out.println();
		while (fullname.length() == 0 ||
					 fullname.length() > Validation.MAX_LENGTH) {
			System.out.println("Full names must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Enter your full name: ");
			fullname = Validation.input.nextLine();
			System.out.println();
		}
		return fullname;
	}

	static public String getEmail() {
		String email = Validation.input.nextLine();
		System.out.println();
		while (!Validation.isValidEmail(email)) {
			System.out.println("Please enter a valid email (less than " +
												 Validation.MAX_LENGTH + " characters) in format:"
												 + "\n\tusername@website.domain");
			System.out.print("Email: ");
			email = Validation.input.nextLine();
		}
		return email;
	}

	static public String getCity() {
		String city = Validation.input.nextLine();
		System.out.println();
		while (city.length() == 0 || city.length() > Validation.MAX_LENGTH) {
			System.out.println("City must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Please enter your city name: ");
			city = Validation.input.nextLine();
			System.out.println();
		}
		return city;
	}

	static public String getState() {
		String state = Validation.input.nextLine();
		System.out.println();
		while (!Validation.isAState(state)) {
			System.out.print("Please enter a valid two character "
											 + "state code: ");
			state = Validation.input.nextLine();
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
		String petname = Validation.input.nextLine();
		System.out.println();
		while (petname.length() == 0 ||
					 petname.length() > Validation.MAX_LENGTH) {
			System.out.println("Pet names must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Enter your pet's name: ");
			petname = Validation.input.nextLine();
			System.out.println();
		}

		return petname;
	}

	static public String getPetType()
	{
		int pettypeNum = Integer.parseInt(input.nextLine());
		System.out.println();
		while (pettypeNum < 0 || pettypeNum >= petTypes.length) {
			System.out.println("Please enter a valid number.");
			printPetTypes();
			System.out.print("Enter what kind of animal your pet is: ");
			pettypeNum = Integer.parseInt(input.nextLine());
			System.out.println();
		}

		return petTypes[pettypeNum];
	}

	// TO DO : emma said she had a plan for this one
	static public int getPetAge()
	{
		int age = Integer.parseInt(input.nextLine());
		System.out.println();
		while (age >= 0 && age <= 100) {
			System.out.println("Please enter a valid age. Note: if your pet is less than a year old, type 0.");
			System.out.print("Enter your pet's age (in years): ");
			age = Integer.parseInt(Validation.input.nextLine());
			System.out.println();
		}
		return age;
	}

	static public int getOfferID()
	{
		try
		{
			String query = "SELECT offerID FROM offers ORDER BY offerID DESC LIMIT 1;";

			if (numMatches(query, "offers") == 1)
			{
				ResultSet rs = querySQL(query);
				return rs.getInt("offerID") + 1;
			}
			// First offer entry in table
			if (numMatches(query, "offers") == 0)
			{	
				return 1;
			}
		}
		catch (java.sql.SQLException e)
		{
			System.err.println(e);
			System.exit(-1);
		}

		// Leave me alone javac
		return -1;
	}
	// Prompts user for a name and returns the pet's id
	public static int getSitting()
	{
		try
		{
			String petname = Validation.input.nextLine();
			System.out.println();

			String query = "SELECT petID from pets WHERE petname = '"+
							petname +"' AND owner = '" + 
							Validation.curUsername +"';";

			while (numMatches(query, "pets") != 1) {
				System.out.println("Couldn't find a pet on your account " +
									"with the name " + petname + ".");
				System.out.print("Please enter your pet's name: ");
				petname = Validation.input.nextLine();
				System.out.println();
				query = "SELECT petID from pets WHERE petname = '"+
							petname +"' AND owner = '" + 
							Validation.curUsername +"';";
			}

			ResultSet rs = querySQL(query);
			return rs.getInt("petID");
		}
		catch (java.sql.SQLException e)
		{
			System.err.println(e);
			System.exit(-1);
		}

		return -1;
	}
	public static String getDescription()
	{
		String desc;
		char c = 'a';
		// Since a description can be really long,
		//	the user should verify it if is correct.
		do {
			// Reprint prompt if this is a reiteration
			if (c != 'y' || c != 'Y')
				System.out.println("Enter your post's additional information: ");

			desc = Validation.input.nextLine();
			System.out.println();

			while (desc.length() > Validation.DESC_MAX_LENGTH) {
				System.out.println("Your description exceeds the " + Validation.DESC_MAX_LENGTH +
									" limit. Length: " + desc.length());
				System.out.println("Please enter your post's additional information: ");
				desc = Validation.input.nextLine();
				System.out.println();
			}

			if (desc.length() == 0)
				System.out.println("Your post will have no description, is that okay? (Type 'y' to confirm.)");
			else
			{
				System.out.println("Verify that your post's description is correct. (Type 'y' to confirm.)");
				System.out.println(desc);
			}
			c = Validation.input.next().charAt(0);
		} while (c != 'y' || c != 'Y');

		return desc;
	}
	public static double getPayment()
	{
		// Rounding input to 2 decimals
		double payment = Math.round(Double.parseDouble(Validation.input.nextLine()) * 100) / 100;
		System.out.println();

		while (payment < 0 || payment >= 10000)
		{
			System.out.println("Invalid value entered.");
			System.out.println("Enter your post's payment amount: ");
			payment = Math.round(Double.parseDouble(Validation.input.nextLine()) * 100) / 100;
			System.out.println();
		}

		return payment;
	}
	// TO DO
	public static String getOfferStartTime()
	{
		String str = null;
		boolean badTS = false;
		Date time = new Date();
		SimpleDateFormat time_format = new SimpleDateFormat("hh:mm aa");
		do
		{
			try
			{			
				System.out.println("Start Time (hh:mm am/pm) (if none, hit enter): ");
				str = Validation.input.nextLine();
				System.out.println();
				if (str.length() == 0)
					str = "3:00 pm";
				
				time = time_format.parse(str);
			}
			catch (Exception e) { badTS = true; }
		} while (badTS);
		
		return str;
	}
	// TO DO
	public static String getOfferStartDate()
	{
		String str = null;
		boolean badTS = false;
		Date date = new Date();
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		do
		{
			try
			{			
				System.out.println("Start Date (yyyy-MM-dd): ");
				str = Validation.input.nextLine();
				System.out.println();
				if (str.length() == 0)
					throw new Exception();
				
				date = date_format.parse(str);
			}
			catch (Exception e) { badTS = true; }
		} while (badTS);
		
		return str;
	}
	// TO DO
	public static String getOfferEndTime()
	{
		String str = null;
		boolean badTS = false;
		Date time = new Date();
		SimpleDateFormat time_format = new SimpleDateFormat("hh:mm aa");
		do
		{
			try
			{			
				System.out.println("End Time (hh:mm am/pm) (if none, hit enter): ");
				str = Validation.input.nextLine();
				System.out.println();
				if (str.length() == 0)
					str = "3:00 pm";
				
				time = time_format.parse(str);
			}
			catch (Exception e) { badTS = true; }
		} while (badTS);
		
		return str;
	}
	// TO DO
	public static String getOfferEndDate()
	{
		String str = null;
		boolean badTS = false;
		Date date = new Date();
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		do
		{
			try
			{			
				System.out.println("Start Date (yyyy-MM-dd): ");
				str = Validation.input.nextLine();
				System.out.println();
				if (str.length() == 0)
					throw new Exception();
				
				date = date_format.parse(str);
			}
			catch (Exception e) { badTS = true; }
		} while (badTS);
		
		return str;
	}
}
