package profile;

// UserProfile page for petsitting service
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import util.Validation;
import java.lang.reflect.Method;
import java.util.Collections;



public class UserProfile {

	// Create generic update statement based on which field to update for
	// curUsername. Intended for use with boolean values.
	private static String genUpdateSQL(String field, String update,
																		 String curUsername) {
		return "UPDATE accounts SET " + field + " = " + update + " WHERE "
			     + "username = '" + curUsername + "';";
	}

	final public static void updateUsername() {
		System.out.print("Enter new username: ");
		String oldUsername = Validation.curUsername;
		Validation.curUsername = Validation.getUsername();
		Validation.updateSQL(genUpdateSQL("fullname",
																			"'" + Validation.curUsername + "'",
																			oldUsername));
	}

	final public static void updateFullname() {
		System.out.print("Enter full name: ");
		String newFullname = "'" + Validation.getFullname() + "'";
		Validation.updateSQL(genUpdateSQL("fullname", newFullname,
																			Validation.curUsername));
	}

	final public static void updatePassword() {
		System.out.println("Enter new password: ");
		String newPassword = "crypt('" + Validation.getPassword() + "', " +
			                   "gen_salt('md5'))";
		Validation.updateSQL(genUpdateSQL("password", newPassword,
																			Validation.curUsername));
	}

	final public static void updateEmail() {
		System.out.print("Enter new email: ");
		String newEmail = "'" + Validation.getEmail() + "'";
		Validation.updateSQL(genUpdateSQL("email", newEmail,
																			Validation.curUsername));
	}

	final public static void updateIsSitter() {
		System.out.print("Are you a pet sitter (y/n)? ");
		String newIsSitter = Validation.getIsPetSitter() ? "true" : "false";
		Validation.updateSQL(genUpdateSQL("issitter", newIsSitter,
																			Validation.curUsername));
	}

	final public static void updateIsOwner() {
		System.out.print("Are you a pet owner (y/n)? ");
		String newIsOwner = Validation.getIsPetOwner() ? "true" : "false";
		Validation.updateSQL(genUpdateSQL("isowner", newIsOwner,
																			Validation.curUsername));
	}

	final public static void updateCity() {
		System.out.print("Please enter your city name: ");
		String newCity = "'" + Validation.getCity() + "'";
		Validation.updateSQL(genUpdateSQL("city", newCity,
																			Validation.curUsername));
	}

	final public static void updateState() {
		System.out.print("Enter two character state code (for example "
										 + "enter Florida as FL): ");
		String newState =  "'" + Validation.getState() + "'";
		Validation.updateSQL(genUpdateSQL("state", newState,
																			Validation.curUsername));
	}


	// Map of characters to the corresponding method to update the associated
	// user data
	final static Map<Character, Method> updateUserField
		= Collections.unmodifiableMap(new HashMap<Character, Method>() {{
		try {
			put('u', UserProfile.class.getMethod("updateUsername"));
			put('p', UserProfile.class.getMethod("updatePassword"));
			put('f', UserProfile.class.getMethod("updateFullname"));
			put('e', UserProfile.class.getMethod("updateEmail"));
			put('s', UserProfile.class.getMethod("updateIsSitter"));
			put('o', UserProfile.class.getMethod("updateIsOwner"));
			put('c', UserProfile.class.getMethod("updateCity"));
			put('t', UserProfile.class.getMethod("updateState"));
		}
		catch (java.lang.NoSuchMethodException e) {
			System.err.println(e);
			System.exit(-1);
		}
		}});

	public static void displayUserProfile() {
		try {
			ResultSet rs = Validation.querySQL("SELECT username, fullname, "
																				 +"email, issitter, isowner, " +
																				 "rating, tsjoined, city, " +
																				 "state FROM accounts WHERE "
																				 + "username = '" +
																				 Validation.curUsername + "';");
			if (rs.next()) {
				System.out.println("username: " + rs.getString("username"));
				System.out.println("fullname: " + rs.getString("fullname"));
				System.out.println("email: " + rs.getString("email"));
				System.out.println("pet sitter?: " +
													 (rs.getBoolean("issitter") ? "yes" : "no"));
				System.out.println("pet owner?: " +
													 (rs.getBoolean("isowner") ? "yes" : "no"));
				if (rs.getBoolean("isowner")) {
					// TODO print number of pets with *maybe* basic summarized info
				}
				System.out.println("rating: " +
													 (rs.getDouble("rating") == 0.0 ? "none" :
														String.valueOf(rs.getDouble("rating"))));
				System.out.println("joined: " +
													 Validation.FORMAT.format(new Date
																										(rs.getTimestamp
																										 ("tsjoined").
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

	public static void editUserProfile() {
			final String OPTIONS
				= "Enter 'u' for username, 'p' for password, 'f' for fullname," +
          "'e' for email, 's' for pet sitter, 'o' for pet owner, 'c' for "
				  +"city, 't' for state, or 'q' to cancel editing.";
			char response;
			boolean badOption = false;

			System.out.println("Please enter which part of your profile you"
												 + " would like to update:\n" + OPTIONS);
			response = Validation.input.nextLine().charAt(0);
			do {
				if (badOption) {
					System.out.println("Invalid option.\n" + "Please enter which "
														 + "part of your profile you would like to "
														 + "update:\n" + OPTIONS);
					response = Validation.input.nextLine().charAt(0);
				}
				try {
					updateUserField.get(response).invoke(null);
					badOption = false;
				}
				// Will catch case where a bad key is passed to
				// updateUserField map
				catch (java.lang.ReflectiveOperationException e) {
					badOption = true;
				}
			} while (badOption);
	}


	// Method used to transfer current page to Profile. Returns character
	// matching next page destination. Assumes curUsername has been validated
	// prior.
	public static char goToUserProfile() {
		// Display current profile information
		displayUserProfile();
		// Prompt user if they would like to edit profile
		char c;
		System.out.println("Would you like to edit your profile (y/n)? ");
		c = Validation.input.nextLine().charAt(0);
		System.out.println();
		while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
			System.out.println("Please enter either y or n.");
			System.out.print("Would you like to edit your profile (y/n)? ");
			c = Validation.input.nextLine().charAt(0);
			System.out.println();
		}
		if (c == 'y' || c == 'Y') {
			do {
				System.out.println("Enter 'a' to edit account information, 'p' to "
													 + "edit pet information, or 'e' to exit: ");
				c = Validation.input.nextLine().charAt(0);
				if (c == 'a' || c == 'A')
					editUserProfile();
				else if (c == 'p' || c == 'P')
					PetProfile.editPetInfo();
			} while (c != 'e' && c != 'E');
		}
		// Promp user if they would like to navigate to another page
		System.out.println(Validation.OPTIONS);
		c = Validation.input.nextLine().toLowerCase().charAt(0);
		System.out.println();
		while (!Validation.isValidOption(c)) {
			System.out.println("Please enter a valid option." +
												 Validation.OPTIONS);
			c = Validation.input.nextLine().toLowerCase().charAt(0);
			System.out.println();
		}
		return c;
	}
}
