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
	final public static String greeting = "Welcome to petsitting_services.com!\n\n",
                             prompt = "Enter 'l' to login, 'c' to create account, or 'q' to quit.";

	public static Statement statement;

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

	// Returns true if format is "username@website.top-level-domain" and false otherwise
	public static boolean validEmail(String email) {
		Scanner strScanner = new Scanner(email);
		strScanner.useDelimiter("@");
		if (strScanner.hasNext()) {
			strScanner.next();
			if (strScanner.hasNext()) {
				strScanner = new Scanner(strScanner.next());
				strScanner.useDelimiter("\\."); // Escape dot to treat as a character delimiter rather than a regex
				if (strScanner.hasNext()) {
					strScanner.next();
					if (strScanner.hasNext() && strScanner.next().length() > 0)
						return true;
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
         System.err.println (e);
         System.exit (-1);
      }
      try {
				Scanner input = new Scanner(System.in);
				// open connection to database
				Connection connection
					= DriverManager.getConnection(//"jdbc:postgresql://dbhost:port/dbname", "user", "dbpass");
																				"jdbc:postgresql://127.0.0.1:5432/final_project", "emma", "pass");

				statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
																							 ResultSet.CONCUR_UPDATABLE);
				System.out.println(banner + greeting);
				String username, password;
				while(true) {
					System.out.println(prompt);
					char response = input.nextLine().charAt(0);
					if (response =='l') {
						System.out.print("Enter username: ");
						username = input.nextLine();
						System.out.println();
						System.out.print("Enter password: ");
						password = input.nextLine();
						System.out.println();
						String query
							= "SELECT * FROM accounts WHERE username = '" + username + "' AND password = crypt('" + password + "', password);";
						//ResultSet rs = statement.executeQuery (query);
						int matchesSize = numMatches(query);
						// Get size of row and ensure it equals one
						if (matchesSize == 1) {
							System.out.println("Welcome " + username + "!\n");
							break;
						}
						else if (matchesSize == 0) {
							System.out.println("Account not found\n");
							continue;
						}
						else {
							System.err.println("Internal error: duplicate data found for account");
							System.exit(-1);
						}
					}
					else if (response == 'c') {
						String fullname, email;
						char[] city = new char[2], state = new char[2];
						boolean issitter, isowner;
						System.out.print("Username: ");
						username = input.nextLine();
						System.out.println();
						System.out.print("Password: ");
						password = input.nextLine();
						System.out.println();
						System.out.print("Email: ");
						email = input.nextLine();
						while (!validEmail(email)) {
							System.out.print("Please enter a valid email in format username@website.domain: ");
							email = input.nextLine();
						}
						System.out.println();
						System.out.print("Enter two character state code (for example enter Florida as FL): ");
						input.useDelimiter(""); // Read character input one at a time
						state[0] = input.next().charAt(0);
						state[1] = input.next().charAt(0);
						input.reset(); // Reset to no delimiter
						input.nextLine(); // Flush newline

						break;
					}
					else
						break;
				}


				connection.close ();
      }
      catch (java.sql.SQLException e) {
				System.err.println (e);
				System.exit (-1);
      }
	}
}
