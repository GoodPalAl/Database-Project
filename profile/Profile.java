package profile;

// Profile page for petsitting service
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;
import util.Validation;
import java.lang.reflect.Method;



public class Profile {

	// Create generic update statement based on which field to update for
	// curUsername. Intended for use with String values
	private static String genUpdateSQL(String field, String update,
																		 String curUsername) {
		return "UPDATE accounts SET " + field + " = '" + update + "' WHERE "
			+ "username = '" + curUsername + "';";
	}

	// Create generic update statement based on which field to update for
	// curUsername. Intended for use with boolean values.
	private static String genUpdateSQLNoQuotes(String field, boolean update,
																						 String curUsername) {
		return "UPDATE accounts SET " + field + " = " + update + " WHERE "
			+ "username = '" + curUsername + "';";
	}

	public static void updateUsername(String curUsername,
																		Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter new username: ");
		String oldUsername = curUsername;
		curUsername = Validation.getUsername(statement);
		try {
			statement.executeUpdate(genUpdateSQL("username", curUsername,
																					 oldUsername));
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateFullname(String curUsername,
																		Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter full name: ");
		String newFullname = Validation.getFullname();
		try { statement.executeUpdate(genUpdateSQL("fullname", newFullname,
																							 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updatePassword(String curUsername,
																		Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter new password: ");
		String newPassword = Validation.getPassword();
		try { statement.executeUpdate(genUpdateSQL("password", newPassword,
																							 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateEmail(String curUsername,
																 Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter new email: ");
		String newEmail = Validation.getEmail(statement);
		try { statement.executeUpdate(genUpdateSQL("email", newEmail,
																							 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateIsSitter(String curUsername,
																		Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Are you a pet sitter (y/n)? ");
		boolean newIsSitter = Validation.getIsPetSitter();
		try { statement.executeUpdate(genUpdateSQLNoQuotes("issitter",
																											 newIsSitter,
																											 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateIsOwner(String curUsername,
																	 Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Are you a pet owner (y/n)? ");
		boolean newIsOwner = Validation.getIsPetOwner();
		try { statement.executeUpdate(genUpdateSQLNoQuotes("isowner",
																											 newIsOwner,
																											 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateCity(String curUsername,
																Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Please enter your city name: ");
		String newCity = Validation.getCity();
		try { statement.executeUpdate(genUpdateSQL("city", newCity,
																							 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void updateState(String curUsername,
																 Statement statement) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter two character state code (for example "
										 + "enter Florida as FL): ");
		String newState = Validation.getState();
		try { statement.executeUpdate(genUpdateSQL("state", newState,
																							 curUsername)); }
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}


	// Map of characters to the corresponding method to update the associated
	// user data
	static Map<Character, Method> updateUserField;
	static {
		try {
			updateUserField = new HashMap<Character, Method>();
			updateUserField.put('u', Profile.class.getMethod("updateUsername",
																											 String.class,
																											 Statement.class));
			updateUserField.put('p', Profile.class.getMethod("updatePassword",
																											 String.class,
																											 Statement.class));
			updateUserField.put('f', Profile.class.getMethod("updateFullname",
																											 String.class,
																											 Statement.class));
			updateUserField.put('e', Profile.class.getMethod("updateEmail",
																											 String.class,
																											 Statement.class));
			updateUserField.put('s', Profile.class.getMethod("updateIsSitter",
																											 String.class,
																											 Statement.class));
			updateUserField.put('o', Profile.class.getMethod("updateIsOwner",
																											 String.class,
																											 Statement.class));
			updateUserField.put('c', Profile.class.getMethod("updateCity",
																											 String.class,
																											 Statement.class));
			updateUserField.put('t', Profile.class.getMethod("updateState",
																											 String.class,
																											 Statement.class));
		}
		catch (java.lang.NoSuchMethodException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void displayProfile(String curUsername, Statement statement) {
		try {
			final String QUERY =
				"SELECT username, fullname, email, issitter, isowner, rating, "
				+ "tsjoined, city, state FROM accounts WHERE username = '" +
				curUsername + "';";

			ResultSet rs = statement.executeQuery(QUERY);
			if (rs.next()) {
				System.out.println("username: " + rs.getString("username"));
				System.out.println("fullname: " + rs.getString("fullname"));
				System.out.println("email: " + rs.getString("email"));
				System.out.println("pet sitter?: " +
													 (rs.getBoolean("issitter") ? "yes" : "no"));
				System.out.println("pet owner?: " +
													 (rs.getBoolean("isowner") ? "yes" : "no"));
				System.out.println("rating: " +
													 (rs.getDouble("rating") == 0.0 ? "none" :
														String.valueOf(rs.getDouble("rating"))));
				System.out.println("joined: " +
													 Validation.FORMAT.format(new Date
																				 (rs.getTimestamp("tsjoined").
																					getTime())));
				System.out.println("city: " + rs.getString("city"));
				System.out.println("state: " + rs.getString("state"));
			}
			else
				System.err.println("Internal error finding entry matching "
													 + "username.");
		}

		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void editProfile(String curUsername, Statement statement) {
			final String OPTIONS
				= "Enter 'u' for username, 'p' for password, 'f' for fullname," +
          "'e' for email, 's' for pet sitter, 'o' for pet owner, 'c' for "
				  +"city, 't' for state, or 'q' to cancel editing.";
			final String QUERY =
				"SELECT username, fullname, email, issitter, isowner, rating, "
				+ "tsjoined, city, state FROM accounts WHERE username = '" +
				curUsername + "';";
			Scanner input = new Scanner(System.in);
			char response;
			boolean badOption = false;

			System.out.println("Please enter which part of your profile you"
												 + " would like to update:\n" + OPTIONS);
			response = input.nextLine().charAt(0);
			do {
				if (badOption) {
					System.out.println("Invalid option.\n" + "Please enter which "
														 + "part of your profile you would like to "
														 + "update:\n" + OPTIONS);
					response = input.nextLine().charAt(0);
				}
				try {updateUserField.get(response).invoke(null,
																									curUsername, statement);
					badOption = false;
				}
				catch (Exception e) { badOption = true; }
			} while (badOption);
	}


	// Method used to transfer current page to Profile. Returns character
	// matching next page destination. Assumes curUsername has been validated
	// prior.
	public static char goToProfile(String curUsername, Statement statement) {
		// Display current profile information
		displayProfile(curUsername, statement);
		// Prompt user if they would like to edit profile
		Scanner input = new Scanner(System.in);
		char c;
		System.out.println("Would you like to edit your profile (y/n)? ");
		c = input.nextLine().charAt(0);
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Would you like to edit your profile (y/n)? ");
			c = input.nextLine().charAt(0);
			System.out.println();
		}
		if (c == 'y' || c == 'Y')
			editProfile(curUsername, statement);
		// Promp user if they would like to navigate to another page
		System.out.println(Validation.OPTIONS);
		c = input.nextLine().toLowerCase().charAt(0);
		System.out.println();
		while (!Validation.isValidOption(c)) {
			System.out.println("Please enter a valid option." +
												 Validation.OPTIONS);
			c = input.nextLine().toLowerCase().charAt(0);
			System.out.println();
		}
		return c;
	}
}
