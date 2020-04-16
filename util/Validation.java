package util;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.io.Console;

public class Validation {
	final public static SimpleDateFormat FORMAT =
		new SimpleDateFormat("MMMMM dd yyyy");

	final public static int MAX_LENGTH = 50, PASS_LEN_MIN = 8,
		                      PASS_LEN_MAX = 15;
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

	// Returns number of record matches for a particular query.
	public static int numMatches(String query, Statement statement) {
		return numMatches(query, "accounts", statement);
	}

	public static int numMatches(String query, String database,
															 Statement statement) {
		try {
			ResultSet rs = statement.executeQuery(query);
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
	public static boolean isValidEmail(String email, Statement statement) {
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
							if (numMatches(query, statement) > 0)
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

	static public String getUsername(Statement statement) {
		Scanner input = new Scanner(System.in);
		String username = null, query;
		do {
			if (username != null) {
				System.out.println("Username already exists\n");
				System.out.println("Username: ");
			}
			username = input.nextLine();
			while (username.length() == 0 ||
						 username.length() > Validation.MAX_LENGTH) {
				System.out.println("Usernames must be " + Validation.MAX_LENGTH +
													 " characters or fewer");
				System.out.print("Username: ");
				username = input.nextLine();
			}
			query = "SELECT username FROM accounts WHERE username = '" +
				username + "';";
		}	while (Validation.numMatches(query, statement) > 0);
		return username;
	}

	static public String getPassword() {
		Scanner input = new Scanner(System.in);
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
			password = input.nextLine();
			while (!Validation.isValidPassword(password)) {
				System.out.println("Password is not valid. "
													 + Validation.PASSWORD_REQS);
				System.out.print("Password: ");
				password = input.nextLine();
			}
		}
		System.out.println();
		return password;
	}

	static public String getFullname() {
		Scanner input = new Scanner(System.in);
		String fullname = input.nextLine();
		System.out.println();
		while (fullname.length() == 0 ||
					 fullname.length() > Validation.MAX_LENGTH) {
			System.out.println("Full names must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Enter your full name: ");
			fullname = input.nextLine();
			System.out.println();
		}
		return fullname;
	}

	static public String getEmail(Statement statement) {
		Scanner input = new Scanner(System.in);
		String email = input.nextLine();
		System.out.println();
		while (!Validation.isValidEmail(email, statement)) {
			System.out.println("Please enter a valid email (less than " +
												 Validation.MAX_LENGTH + " characters) in format:"
												 + "\n\tusername@website.domain");
			System.out.print("Email: ");
			email = input.nextLine();
		}
		return email;
	}

	static public String getCity() {
		Scanner input = new Scanner(System.in);
		String city = input.nextLine();
		System.out.println();
		while (city.length() == 0 || city.length() > Validation.MAX_LENGTH) {
			System.out.println("City must be " + Validation.MAX_LENGTH +
												 " characters or fewer");
			System.out.print("Please enter your city name: ");
			city = input.nextLine();
			System.out.println();
		}
		return city;
	}

	static public String getState() {
		Scanner input = new Scanner(System.in);
		String state = input.nextLine();
		System.out.println();
		while (!Validation.isAState(state)) {
			System.out.print("Please enter a valid two character "
											 + "state code: ");
			state = input.nextLine();
			System.out.println();
		}
		return state;
	}

	static public boolean getIsPetSitter() {
		Scanner input = new Scanner(System.in);
		input.useDelimiter("");
		char c = input.next().charAt(0);
		input.nextLine();
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Are you a pet sitter (y/n)? ");
			c = input.next().charAt(0);
			input.nextLine();
			System.out.println();
		}

		return c == 'y' || c == 'Y' ? true : false;
	}

	static public boolean getIsPetOwner() {
		Scanner input = new Scanner(System.in);
		input.useDelimiter("");
		char c = input.next().charAt(0);
		input.nextLine();
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Are you a pet owner (y/n)? ");
			c = input.next().charAt(0);
			input.nextLine();
			System.out.println();
		}

		return c == 'y' || c == 'Y' ? true : false;
	}
}
