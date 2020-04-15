// Login page for petsitting service
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

class Login {
	/* ASCII art generated thanks to wonderful text->ASCII art tool from
	 * http://patorjk.com/software/taag/#p=display&h=3&v=3&f=Varsity&t=petsitting_%0Aservices.com
	 * ASCII dachsund is original art from Hayley Jane Wakenshaw*/
	final static public String banner =
		"___________________________________________________________________________\n" +
		"               _         _  _    _   _                                     \n" +
		"              / |_      (_)/ |_ / |_(_)                                    \n" +
		" _ .--.  .---`| |-.--.  __`| |-`| |-__  _ .--.  .--./)                     \n" +
		"[ '/'`\\ / /__\\| |( (`\\][  || |  | |[  |[ `.-. |/ /'`\\;                     \n" +
		" | \\__/ | \\__.| |,`'.'. | || |, | |,| | | | | |\\ \\._/_______               \n" +
		" | ;.__/ '.__.\\__[\\__) [______/ \\__[___[___||__.',__|_______|              \n" +
		"[__|                      (_)                 ( ( __))                     \n" +
		" .--. .---. _ .--. _   __ __  .---. .---. .--.    .---.  .--.  _ .--..--.  \n" +
		"( (`\\/ /__\\[ `/'`\\[ \\ [  [  |/ /'`\\/ /__\\( (`\\]  / /'`\\/ .'`\\ [ `.-. .-. | \n" +
		" `'.'| \\__.,| |    \\ \\/ / | || \\__.| \\__.,`'.'. _| \\__.| \\__. || | | | | | \n" +
		"[\\__) '.__.[___]    \\__/ [___'.___.''.__.[\\__) (_'.___.''.__.'[___||__||__]\n" +
		"___________________________________________________________________________\n" +
		"\n\n"+
		"                          __\n" +
    "   ,                    ,\" e`--o\n" +
    "  ((                   (  | __,'\n" +
    "   \\\\~----------------' \\_;/    Art by Hayley Jane Wakenshaw\n" +
		"hjw (                     /\n" +
		"    /) ._______________.  )\n" +
		"   (( (               (( (\n" +
		"    ``-'               ``-'\n";
	final public static String GREETING =
		"Welcome to petsitting_services.com!\n\n",
		PROMPT = "Enter 'l' to login, 'c' to create account, or 'q' to quit.",
		OPTIONS = "Enter 'p' to view/edit your profile, 's' to search pet" +
		          " sitting posts, 'c' to create a pet sitting offer, or 'q'" +
		          " to quit";

	public static String curUsername;

	public static Statement statement;

	final public static int MAX_LENGTH = 50;
	final public static int PASS_LENGTH_MIN = 8;
	final public static int PASS_LENGTH_MAX = 15;
	final public static String SPECIALS = "`~!@#$%^&*_-=+|<>?/[]",
		                         PASSWORD_REQS = "Password " + "must: \n"+
		                                         "\t> Be between " +
		                                         PASS_LENGTH_MIN + " and " +
		                                         PASS_LENGTH_MAX +
		                                         " characters\n\t" +
		                                         "> Have at least ONE number\n"
		                                         + "\t> Have at least ONE " +
																             "special character, which " +
		                                         "include:\n\t  " + SPECIALS +
																             "\n\t> Have at least ONE " +
		                                         "upper & ONE lowercase letter"
		                                         + "\n";

	final public static String[] stateCodes
		= {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA",
			 "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
	     "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY",
	     "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX",
			 "UT", "VT", "VA", "WA", "WV", "WI", "WY"};

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

		if (PW.length() < PASS_LENGTH_MIN || PW.length() > PASS_LENGTH_MAX)
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

	public static int numMatches(String query) {
		return numMatches(query, "accounts");
	}

	public static int numMatches(String query, String database) {
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

	public static void main (String args[]) {
      try {
         Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
         System.err.println(e);
         System.exit(-1);
      }
      try {
				Scanner input = new Scanner(System.in);
				// open connection to database
				Connection connection
					= DriverManager.getConnection(//"jdbc:postgresql://dbhost:port/dbname", "user", "dbpass");
																				"jdbc:postgresql://127.0.0.1:5432/final_project", "emma", "pass");

				statement
					= connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
																			 ResultSet.CONCUR_UPDATABLE);
				System.out.println(banner + GREETING);
				String username, password;
				while(true) {
					System.out.println(PROMPT);
					char response = input.nextLine().charAt(0);
					if (response == 'l') {
						System.out.print("Enter username: ");
						username = input.nextLine();
						System.out.println();
						System.out.print("Enter password: ");
						password = input.nextLine();
						System.out.println();
						String query
							= "SELECT * FROM accounts WHERE username = '" + username
							  + "' AND password = crypt('" + password + "', password);";
						//ResultSet rs = statement.executeQuery (query);
						int matchesSize = numMatches(query);
						// Get size of row and ensure it equals one
						if (matchesSize == 1) {
							curUsername = username;
							System.out.println("Welcome " + curUsername + "!\n");
							break;
						}
						else if (matchesSize == 0) {
							System.out.println("Account not found\n");
							continue;
						}
						else {
							System.err.println("Internal error: duplicate data found for"
                                 + " account");
							System.exit(-1);
						}
					}
					else if (response == 'c') {
						String fullname, email, city, state, query;
						boolean isSitter, isOwner;
						int matchesSize = 0;
						char c;
						do {
							if (matchesSize > 0)
								System.out.println("Username already exists\n");
							System.out.print("Username: ");
							username = input.nextLine();
							while (username.length() > MAX_LENGTH) {
								System.out.println("Usernames must be " + MAX_LENGTH +
																	 " characters or fewer");
								System.out.print("Username: ");
								username = input.nextLine();
							}
							System.out.println();
							query = "SELECT username FROM accounts WHERE username = '" +
								      username + "';";
						}	while ((matchesSize = numMatches(query)) > 0);
						System.out.println("Please enter a password that meets the" +
															 " following criteria:\n" + PASSWORD_REQS);
						System.out.print("Password: ");
						password = input.nextLine();
						while (!isValidPassword(password)) {
							System.out.println("Password is not valid. "
																 + PASSWORD_REQS);
							System.out.print("Password: ");
							password = input.nextLine();
						}
						System.out.println();
						System.out.print("Email: ");
						email = input.nextLine();
						while (!isValidEmail(email)) {
							System.out.println("Please enter a valid email (less than "
															 + MAX_LENGTH + " characters) in format:\n" +
															 "\tusername@website.domain ");
							System.out.print("Email: ");
							email = input.nextLine();
						}
						System.out.println();
						System.out.print("Enter your full name: ");
						fullname = input.nextLine();
						System.out.println();
						while (fullname.length() > MAX_LENGTH) {
							System.out.println("Full names must be " + MAX_LENGTH +
																 " characters or fewer");
							System.out.print("Enter your full name: ");
							fullname = input.nextLine();
							System.out.println();
						}
						System.out.print("Please enter your city name: ");
						city = input.nextLine();
						System.out.println();
						while (city.length() > MAX_LENGTH) {
							System.out.println("City must be " + MAX_LENGTH +
																 " characters or fewer");
							System.out.print("Please enter your city name: ");
							city = input.nextLine();
							System.out.println();
						}
						System.out.print("Enter two character state code (for example "
														 + "enter Florida as FL): ");
						state = input.nextLine();
						System.out.println();
						while (!isAState(state)) {
							System.out.print("Please enter a valid two character "
																 + "state code: ");
							state = input.nextLine();
							System.out.println();
						}
						System.out.print("Are you a pet sitter (y/n)? ");
						input.useDelimiter("");
						c = input.next().charAt(0);
						input.nextLine();
						System.out.println();
						while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
							System.out.println("Please enter either y or n.");
							System.out.print("Are you a pet sitter (y/n)? ");
							c = input.next().charAt(0);
							input.nextLine();
							System.out.println();
						}

						isSitter = c == 'y' || c == 'Y' ? true : false;

						System.out.print("Are you a pet owner (y/n)? ");
						c = input.next().charAt(0);
						input.nextLine();
						System.out.println();
						while (c != 'y' && c != 'n' && c != 'Y' && c != 'N') {
							System.out.println("Please enter either y or n.");
							System.out.print("Are you a pet owner (y/n)? ");
							c = input.next().charAt(0);
							input.nextLine();
							System.out.println();
						}
						isOwner = c == 'y' || c == 'Y' ? true : false;
						input.reset();
						String insertCMD = "INSERT INTO accounts (username, fullname, "
							                 + "password, email, tsjoined, offersdone, "
                               + "issitter, isowner, city, state)" +
							                 "VALUES('" + username + "', '" + fullname +
							                 "', crypt('" + password + "', 'md5'), '" +
							                 email + "', NOW()," + " 0, '" + isSitter +
							                 "', '" + isOwner + "', '" + city + "', '" +
							                 state + "');";

						statement.executeUpdate(insertCMD);
						curUsername = username;
						break;
					}
					else if (response == 'q') {
						connection.close();
						System.exit(0);
					}
				}

				// Figure out which page/view to switch the user to now that
				// their credentials have been created or validated in the db.
				System.out.println(OPTIONS);
				char response = input.nextLine().charAt(0);
				while(true) {
					if (response == 'p')
						;//response = goToProfile(curUsername);
					else if (response == 's')
						;//response = goToSearch(curUsername);
					else if (response == 'c')
						;//response = goToCreateOffer(curUsername);
					else if (response == 'q') {
						connection.close();
						System.exit(0);
					}
					else {
						System.out.println(OPTIONS);
						response = input.nextLine().charAt(0);
					}
				}
      }
      catch (java.sql.SQLException e) {
				System.err.println(e);
				System.exit(-1);
      }
	}
}
