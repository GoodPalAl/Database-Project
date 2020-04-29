// Pet Sitting Services
// Created by Emma Griffin, Al Allums, and Sydney McClure
// Due Date: 29 April 2020 (c)

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
		
		curOfferID = null;

		char c = 'z';
		// Promp user if they would like to navigate to another page
		do {
			System.out.println(Validation.OPTIONS);
			String str = Validation.input.nextLine();
			System.out.println();
			if (str.length() > 0) {
				c = Character.toLowerCase(str.charAt(0));
			}
		} while (!Validation.isValidOption(c));
		return c;
	}
	//Editted by Sydney
    public static void createOffer()
    {
		//StringBuilder desc;
		String desc;
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
		desc = Validation.preventSQLInjection(Validation.getDescription());
		/*
			desc = new StringBuilder(Validation.getDescription());

			// Insert single quote for each existing quote to escape single
			// quotes in text
			for (int i = 0, strLen = desc.toString().length(); i < strLen; ++i)
				if (desc.charAt(i) == '\'') {
					++strLen;
					desc.insert(++i, '\'');
				}
		//*/

        String insertCMD = "INSERT INTO offers (description, " +
                                "tsposted, tsstart, tsend, payment, " +
                                "sitting) " +
					      	"VALUES('"+ desc.toString() + "', NOW() , " +
								"TIMESTAMP '" + start + "', " +
								"TIMESTAMP '" + end +  "', " +
								payment + ", " + sitting + ");";

		Validation.updateSQL(insertCMD);
		System.out.println("Offer created!");
		System.out.println();
		
		displayOfferInfo(curOfferID);
		System.out.println();
	}

	public static void displayOfferInfo(Integer petID) {
	String query =
		"SELECT description, tsposted, tsstart, tsend, payment, petname, acceptby " +
		"FROM offers, pets, accounts WHERE sitting = petID " +
		"AND owner = username " + 
		(petID == null ? "" : " AND petID = " + petID + "") +
		"ORDER BY tsposted;";
		try {
			ResultSet rs = Validation.statement.executeQuery(query);
			while (rs.next()) {
				System.out.println("Pet Name: " + rs.getString("petName"));
				System.out.println("Time posted: " + 
										Validation.TS_FORMAT.format
										(new Timestamp
										(rs.getTimestamp("tsposted").getTime())));
				System.out.print("Schedule: " + 
										Validation.TS_FORMAT.format
										(new Timestamp
										(rs.getTimestamp("tsstart").getTime())));
				System.out.println(" to " +  
										Validation.TS_FORMAT.format
										(new Timestamp
										(rs.getTimestamp("tsend").getTime())));
				Double pay = rs.getDouble("payment");
				if (pay != 0.0) {
					System.out.print("Payment: $");
					System.out.printf("%.02f", pay);
					System.out.println();
				}
				else
					System.out.println("Payment: none posted");

				String desc = rs.getString("description");
				if (desc.length() > 0)
					System.out.println("Description:\n    \"" +
										desc + "\"");
				else { 
					System.out.println("Description: none");
				}
				String acceptedBy = rs.getString("acceptby");
				if (!rs.wasNull())
					System.out.println("Offer Accepted by: " + acceptedBy);
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
	public static void displayOfferInfo(int offset, ArrayList<String> includedTypes) {
		String query = 
			"SELECT description, tsposted, tsstart, tsend, payment, petname " +
			"FROM offers, pets, accounts " +
			"WHERE sitting = petid AND owner = username " +
			(includedTypes.size() ==
				Validation.petTypes.length ? "" : "AND ("
				+ Search.filterQuery(includedTypes) + ") ") +
			"ORDER BY tsposted DESC LIMIT 1 " +
			"OFFSET " + offset + ";";
		try {
			ResultSet rs = Validation.statement.executeQuery(query);
			while (rs.next()) {
				System.out.println("Pet Name: " + rs.getString("petName"));
				System.out.println("Time posted: " + 
										Validation.TS_FORMAT.format
										(new Timestamp
										(rs.getTimestamp("tsposted").getTime())));
				System.out.print("Schedule: " + 
										Validation.TS_FORMAT.format
										(new Timestamp
										(rs.getTimestamp("tsstart").getTime())));
				System.out.println(" to " +  
										Validation.TS_FORMAT.format
										(new Timestamp
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

	public static void displayOfferHistory() {
		final String OFFER_HISTORY_OPTIONS =
			"Enter\n" + 
			"'a' to view accepted offers,\n" +
			"'p' to view pending offers, or\n" + 
			"'e' to exit.";
		String in;
		do {
			System.out.println(OFFER_HISTORY_OPTIONS);
			in = Validation.input.nextLine();
			System.out.println();
			if (in.length() == 1){
				if (Character.toLowerCase(in.charAt(0)) == 'a')
					displayAcceptedOffers();
				else if (Character.toLowerCase(in.charAt(0)) == 'p')
					displayPendingOffers();
			}
			else
				in = "z";
		} while (Character.toLowerCase(in.charAt(0)) != 'e');
	}

	public static void displayPendingOffers() {
		final String PENDING_OFFERS_OPTIONS = 
		"Enter\n'o' to edit an offer, or \n'e' to exit.";

		String query = 
			"SELECT * " +
			"FROM offers, pets, accounts " +
			"WHERE acceptby IS NULL " +
			"AND sitting = petid AND owner = username " +
			"AND owner = '" + Validation.curUsername + "';";
		
		try {
			ArrayList<Integer> acceptedPets = new ArrayList<Integer>();
			ResultSet rs = Validation.statement.executeQuery(query);

			while(rs.next()){
				int temp = rs.getInt("sitting");
				if (!rs.wasNull())
					acceptedPets.add(temp);
			}
			rs.close();

			if (acceptedPets.size() == 0){				
				System.err.println("You have no pending offers!");
				System.out.println();
				return;
			}
			
			else {
				for (int i = 0; i < acceptedPets.size(); ++i) {
					int petid = acceptedPets.get(i);
					System.out.println("Offer #" + (i + 1));
					Offer.displayOfferInfo(petid);
				}
			}
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}

		String in;
		do {
			System.out.println(PENDING_OFFERS_OPTIONS);
			in = Validation.input.nextLine();
			System.out.println();
			if (in.length() == 1){
				if (Character.toLowerCase(in.charAt(0)) == 'o') {
					System.out.println("This feature is coming soon!"); // TODO:
				}
			}
			else
				in = "z";
		}while (Character.toLowerCase(in.charAt(0)) != 'e');
	}

	public static void displayAcceptedOffers(){
		final String ACCEPTED_OFFERS_OPTIONS = 
		"Enter\n'r' to rate an offer, or \n'e' to exit.";

		String query = 
			"SELECT * " +
			"FROM offers, pets " +
			"WHERE acceptby IS NOT NULL " +
			"AND sitting = petid " +
			"AND owner = '" + Validation.curUsername + "' " + 
			"ORDER BY tsposted DESC;";
		
		try {
			ArrayList<Integer> acceptedPets = new ArrayList<Integer>();
			ResultSet rs = Validation.statement.executeQuery(query);

			while(rs.next()){
				int temp = rs.getInt("sitting");
				if (!rs.wasNull())
					acceptedPets.add(temp);
			}
			rs.close();

			if (acceptedPets.size() == 0){				
				System.err.println("You have no accepted offers at the moment.");
				System.out.println();
				return;
			}
			
			else {
				for (int i = 0; i < acceptedPets.size(); ++i) {
					int petid = acceptedPets.get(i);
					System.out.println("Offer #" + (i + 1));
					Offer.displayOfferInfo(petid);
				}
			}

			String in;
			do {
				System.out.println(ACCEPTED_OFFERS_OPTIONS);
				in = Validation.input.nextLine();
				System.out.println();
				if (in.length() == 1){
					if (Character.toLowerCase(in.charAt(0)) == 'r') {
						System.out.print("Enter offer number you would " +
												"like to rate: ");
						int selection = -1;
						String response = Validation.input.nextLine();
						System.out.println();
						if (response.length() > 0)
							selection = Character.getNumericValue(response.charAt(0));
						if (selection >= 1) {
							giveRating(selection - 1);
							return;
						}
						// Invalid offer
						else {
							System.out.println("Invalid offer number");
							continue;
						}
					}
				}
				else
					in = "z";
			}while (Character.toLowerCase(in.charAt(0)) != 'e');
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	public static void giveRating(int offset){		
		// Get username of person who accepted the offer 
		//		and their previous rating
		String query = 
			"SELECT offerid, acceptby, rating " +
			"FROM offers, pets, accounts " +
			"WHERE acceptby IS NOT NULL " +
			"AND sitting = petid " +
			"AND owner = username " +
			"AND owner = '" + Validation.curUsername + "' " + 
			"ORDER BY tsposted DESC LIMIT 1 OFFSET " + offset + ";";
		try {
			String acceptedBy = "";
			Double oldRating = null;
			Integer offerid = null;
			ResultSet rs = Validation.statement.executeQuery(query);
			
			while (rs.next()) {
				acceptedBy = rs.getString("acceptby"); 
				oldRating = rs.getDouble("rating");
				offerid = rs.getInt("offerid");
			}
			rs.close();

			if (acceptedBy.equals("") || acceptedBy.equals(null) || offerid == null){
				System.out.println("Something's wrong here.");
				return;
			}

			boolean validOption;
			do {
				System.out.print("On a scale of 1 to 5, how would you rate their " +
									"service? ");
				int rating = -1;
				String response = Validation.input.nextLine();
				System.out.println();
				if (response.length() > 0)
					rating = Character.getNumericValue(response.charAt(0));
				if (rating >= 1 && rating <= 5) {
					validOption = true;
					// First rating!
					if (oldRating == 0.0)
						Validation.updateSQL("UPDATE accounts SET rating = " +  rating +
											" WHERE username = '" + acceptedBy + 
											"';");
					else
						Validation.updateSQL("UPDATE accounts SET rating = " +
											"(((rating * offersdone) + " + 
											rating + ") / (offersdone + 1)) " +
											"WHERE username = '" + acceptedBy + "';");
					
					// Increment # of offers done when rating is being given
					Validation.updateSQL("UPDATE accounts SET offersdone = " +
											"(offersdone + 1) WHERE username = '" + 
											acceptedBy + "';");
					System.out.println("Rating submitted!\n");
					Validation.statement.execute("DELETE FROM offers WHERE " +
														"offerid = " + offerid + ";");
				}
				else
					validOption = false;
			} while (!validOption);
			
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}
