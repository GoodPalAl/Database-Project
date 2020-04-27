package search;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import util.Validation;
//import profile.UserProfile;
import profile.PetProfile;


public class Offer 
{
	protected static Integer curOfferID;
	
	public static char goToCreateOffer()
	{
		curOfferID = null;
		// Account must be an owner to make a post
		if (!Validation.userIsOwner) {
			System.out.println(
				"It seems like your account is not " +
				"labelled as a pet owner. \n" +
				"If you would like to be able to create a post, " +
				"please edit your account accordingly.");
		}
		else {
			int numPets = 0;
			try{
				ResultSet rs = Validation.statement.executeQuery(
					"SELECT COUNT(*) as count FROM pets WHERE owner = '" + 
					Validation.curUsername + "';");
				while(rs.next())
					numPets = rs.getInt("count");
				rs.close();
			}
			catch (java.sql.SQLException e) {
				System.err.println(e);
				System.exit(-1);
			}
			// Account must own at least one pet 
			if (numPets > 0) {
				PetProfile.displayPetInfo(null);
				createOffer();
			}
			else {
				System.out.println(
					"There are no pets on your account! \n"+
					"Add a pet to your account before making an offer post.");
			}
		}
		curOfferID = null;

		char c = 'z';
		// Promp user if they would like to navigate to another page
		do {
			System.out.println(Validation.OPTIONS);
			String str = Validation.input.nextLine();
			if (str.length() > 0) {
				c = Character.toLowerCase(str.charAt(0));
				System.out.println();
			}
		} while (!Validation.isValidOption(c));
		return c;
	}
	//Editted by Sydney
    public static void createOffer()
    {
		StringBuilder desc;
		Timestamp start, end;
        double payment; 
        int sitting;

        System.out.print("Which of your pets is this offer for? ");
        sitting = Validation.getSitting();	// FIXME:
        
        System.out.println("When does your pet need to be sat? ");
        start = Validation.getOfferStartDate();
        end = Validation.getOfferEndDate();

        System.out.print("Enter payment amount: $");
        payment = Validation.getPayment();

        System.out.println("Please provide any additional information " +
                           "regarding this post. Limit of " +
													 Validation.DESC_MAX_LENGTH + " characters. " +
													 "Press enter when done.\n");
        desc = new StringBuilder(Validation.getDescription());

				// Insert single quote for each existing quote to escape single
				// quotes in text
				for (int i = 0, strLen = desc.toString().length(); i < strLen; ++i)
					if (desc.charAt(i) == '\'') {
						++strLen;
						desc.insert(++i, '\'');
					}

        String insertCMD = "INSERT INTO offers (description, " +
                                "tsposted, tsstart, tsend, payment, " +
                                "sitting) " +
					      "VALUES('"+ desc.toString() + "', NOW() , " +
								"TIMESTAMP '" + start + "', " +
								"TIMESTAMP '" + end +  "', " +
								payment + ", " + sitting + ");";

		Validation.updateSQL(insertCMD);
		System.out.println("Offer created!");
		//curOfferID;
		displayOfferInfo(curOfferID);
		System.out.println();
	}

	public static void displayOfferInfo(Integer petID) {
	String query =
		"SELECT description, tsposted, tsstart, tsend, payment, petname " +
		"FROM offers, pets WHERE sitting = petID " +
		"AND owner = username " +
		(petID == null ? "" : " AND petID = " + petID + "") +
		"ORDER BY tsposted;";
		try
			{
				ResultSet rs = Validation.statement.executeQuery(query);
				while (rs.next()) {
					System.out.println("Pet Name: " + rs.getString("petName"));
					System.out.println("Time posted: " + 
														 Validation.TS_FORMAT.format(new Timestamp
																												 (rs.getTimestamp("tsposted").getTime())));
					System.out.print("Schedule: " + 
													 Validation.TS_FORMAT.format(new Timestamp
																											 (rs.getTimestamp("tsstart").getTime())));
					System.out.println(" to " +  
														 Validation.TS_FORMAT.format(new Timestamp
																												 (rs.getTimestamp("tsend").getTime())));
					System.out.println("Payment: $" + rs.getDouble("payment"));
					System.out.println("Description: \"" + rs.getString("description") + "\"");
					System.out.println();
				}
				rs.close();
			}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}


	//Editted by Sydney
	public static void displayOfferInfo(int offset,
																			ArrayList<String> includedTypes) {
			String query = "SELECT description, tsposted, tsstart, tsend, payment, " +
				"petname FROM offers, pets, accounts " +
				"WHERE sitting = petid AND owner = username " +
				(includedTypes.size() ==
				 Validation.petTypes.length ? "" : "AND ("
				 + Search.filterQuery(includedTypes) + ") ") +
				"ORDER BY tsposted DESC LIMIT 1 " +
				"OFFSET " + offset + ";";
			try
        {
					ResultSet rs = Validation.statement.executeQuery(query);
					while (rs.next()) {
						System.out.println("Pet Name: " + rs.getString("petName"));
						System.out.println("Time posted: " + 
															 Validation.TS_FORMAT.format(new Timestamp
																													 (rs.getTimestamp("tsposted").getTime())));
						System.out.print("Schedule: " + 
														 Validation.TS_FORMAT.format(new Timestamp
																												 (rs.getTimestamp("tsstart").getTime())));
						System.out.println(" to " +  
															 Validation.TS_FORMAT.format(new Timestamp
																													 (rs.getTimestamp("tsend").getTime())));
						Double pay = rs.getDouble("payment");
						if (pay != null){
							System.out.print("Payment: $");
							System.out.printf("%.02f", pay);
							System.out.println();
						}
						System.out.println("Description: \"" + rs.getString("description") + "\"");
						System.out.println();
					}
					rs.close();
        }
			catch (java.sql.SQLException e) {
				System.err.println(e);
				System.exit(-1);
			}
	}
}
