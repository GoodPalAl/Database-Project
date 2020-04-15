package profile;

// Profile page for petsitting service
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import util.Validation;



public class Profile {

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
		try {
			final String OPTIONS
				= "Enter 'u' for username, 'f' for fullname, 'e' for email, 's'"
				  + " for pet sitter, 'o' for pet owner, 'c' for city, 's' for"
				  + " state, or 'q' to cancel editing.";
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
			c = input.next().charAt(0);
			input.nextLine();
			System.out.println();
		}
		if (c == 'y' || c == 'Y')
			/*editProfile(curUsername, statement)*/;
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
