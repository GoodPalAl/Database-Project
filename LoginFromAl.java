// Login page for petsitting service
import java.util.Scanner;

import javafx.concurrent.Worker.State;

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

	final public static int MAX_LENGTH = 50;
	final public static int PASS_LENGTH_MIN = 8;
	final public static int PASS_LENGTH_MAX = 15;
	final public static String SPECIALS = "`~!@#$%^&*_-=+|<>?/[]";

	public static String[] stateCodes
        = {	"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
			"MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY",
			"NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", 
			"UT", "VT", "VA", "WA", "WV", "WI", "WY"};

	// Returns true if SC is equal to one of the 51 stateCodes.
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

	// Returns true if format is "username@website.top-level-domain" and false otherwise
	public static boolean validEmail(String email) 
	{
		Scanner strScanner = new Scanner(email);
		strScanner.useDelimiter("@");
		if (strScanner.hasNext()) 
		{
			int emailLength = strScanner.next().length();
			if (strScanner.hasNext()) 
			{
				String buffer = strScanner.next();
				emailLength += buffer.length();
				strScanner = new Scanner(buffer);
				strScanner.useDelimiter("\\."); // Escape dot to treat as a character delimiter rather than a regex
				if (strScanner.hasNext()) 
				{
					emailLength += strScanner.next().length();
					if (strScanner.hasNext())
					{
						emailLength += strScanner.next().length();
						return emailLength < MAX_LENGTH ? true : false;
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
         System.err.println (e);
         System.exit (-1);
      }
      try {
				Scanner input = new Scanner(System.in);
				// open connection to database
				Connection connection
					= DriverManager.getConnection(//"jdbc:postgresql://dbhost:port/dbname", "user", "dbpass");
													"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "PASSWORD");

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
						String fullname, email, city;
						String state;
						boolean isSitter, isOwner;
						char c;
						System.out.print("Username: ");
						username = input.nextLine();
						while (username.length() > MAX_LENGTH)
						{
							System.out.println("Username must be " + MAX_LENGTH + " or fewer characters.");
							System.out.print("Username: ");
							username = input.nextLine();
						}
						System.out.println();

						System.out.print("Password: ");
						password = input.nextLine();
						while (!isValidPassword(password))
						{
							System.out.println("Password is not valid. Your password must: \n" +
													"> Be between "+ PASS_LENGTH_MIN + " and " + PASS_LENGTH_MAX + " characters\n" +
													"> Have at least ONE number\n" +
													"> Have at least ONE special character, which include: " + SPECIALS + "\n" +
													"> Have at least ONE capital letter\n");
							System.out.print("Password: ");
							password = input.nextLine();
						}
						System.out.println();

						System.out.print("Email (must be " + MAX_LENGTH + " characters or fewer" + 
											"and in the format username@website.domain): ");
						email = input.nextLine();
						while (!validEmail(email)) {
							System.out.print("Please enter a valid email in format username@website.domain: ");
							email = input.nextLine();
						}
						System.out.println();

						System.out.print("Enter your full name: ");
						fullname = input.nextLine();
						while (fullname.length() > MAX_LENGTH)
						{
							System.out.println("Full name must be " + MAX_LENGTH + " or fewer characters.");
							System.out.print("Full name: ");
							fullname = input.nextLine();
						}
						System.out.println();

						System.out.print("Please enter your city: ");
						city = input.nextLine();
						while (city.length() > MAX_LENGTH)
						{
							System.out.println("City must be fewer than " + MAX_LENGTH + " characters.");
							System.out.print("City: ");
							city = input.nextLine();
						}
						System.out.println();

						System.out.print("Enter two character state code (for example enter Florida as FL): ");
						state = input.nextLine();
						while(!isAState(state))
						{
							System.out.print("Please enter a valid two character state code: ");
							state = input.nextLine();
						}
						System.out.println();
						input.reset(); // Reset to no delimiter

						System.out.print("Are you a pet sitter (y/n)? ");
						input.useDelimiter("");
						c = input.next().charAt(0);
						while (c != 'y' && c != 'n' && c != 'Y' && c != 'N')
						{
							System.out.println("Please enter either y or n.");
							System.out.print("Are you a pet sitter (y/n)? ");
							c = input.next().charAt(0);
						}
						isSitter = c == 'y' || c == 'Y' ? true : false;
						input.reset(); // Reset to no delimiter
						input.nextLine(); // Flush newline
						System.out.println();

						System.out.print("Are you a pet owner (y/n)? ");
						c = input.next().charAt(0);
						while (c != 'y' && c != 'n' && c != 'Y' && c != 'N')
						{
							System.out.println("Please enter either y or n.");
							System.out.print("Are you a pet owner (y/n)? ");
							c = input.next().charAt(0);
						}
						isOwner = c == 'y' || c == 'Y' ? true : false;
						input.reset(); // Reset to no delimiter
						input.nextLine(); // Flush newline
						System.out.println();

						String query = "INSERT INTO accounts (username, fullname, email, tsjoined, offersdone, issitter, isowner, city)" + 
										"VALUES('" + username + "', '" + fullname + "', '" + email + "', NOW()," + 
										" 0, '" + isSitter + "', '" + isOwner + "', '" + city + "');";
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
