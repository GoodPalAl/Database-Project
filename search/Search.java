// Pet Sitting Services
// Created by Emma Griffin, Al Allums, and Sydney McClure
// Due Date: 29 April 2020

package search;

import java.util.Scanner;

import profile.UserProfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import util.Validation;
import search.Offer;


public class Search {

    // Returns the "pettype = '' OR pettype = '' OR ... OR pettype = ''"
    //  section of a query as a string
    public static String filterQuery(ArrayList<String> arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.size(); ++i) {
            sb.append("pettype = '" +
                arr.get(i) +
                (i == arr.size()-1 ? "'" : "' OR "));
        }
        return sb.toString();
    }

    // Search Page
    public static char goToSearch() {
			final int DESC_PREVIEW_LEN = 50;
			ArrayList<String> includedTypes = filterPets();
			System.out.println("Searching for posts in your city...");
			System.out.println();
			boolean userStillActive = true;
			// limit indicates how many offers to display per page. Offset keeps
			// track of how many offers have already been displayed in case
			// the user wants to view successive pages of offers
			final int limit = 5;
			int offset = 0;
			boolean anyPostFound = false;
			do {
				String query =
					"SELECT owner, pettype, payment, tsposted, tsstart, tsend, description "+
					"FROM offers, pets, accounts " +
					"WHERE sitting = petid AND owner = username " +
					// User cannot search for their own posts
					"AND owner <> '" + Validation.curUsername + "' " +
					// true = include all pets in the search,
					// false = only pets in "includedTypes"
					(includedTypes.size() == Validation.petTypes.length ? "" :
					 "AND (" +
					 filterQuery(includedTypes) +
					 ") ") +
					// The 5 most recent posts sorted
					// could update offset in a loop if we implement a "next"
					// input option for user
					"ORDER BY tsposted DESC LIMIT " + limit + " OFFSET " + offset +
					";";

				offset += limit;
				boolean morePages = false;
				// Execute query
				try {
					ResultSet rs = Validation.statement.executeQuery(query);
					for(int i = 1; rs.next(); ++i) {
						anyPostFound = true;
						morePages = true;
						System.out.println("Offer #" + (i + (offset-limit)));
						System.out.println("    Posted By: " +
															 rs.getString("owner"));
						System.out.println("    Pet Type: " +
															 rs.getString("pettype"));
						System.out.print("    Schedule: "  +
														 Validation.TS_FORMAT.format
														 (new Timestamp
															(rs.getTimestamp("tsstart").getTime())));
						System.out.println(" to " +
															 Validation.TS_FORMAT.format
															 (new Timestamp
																(rs.getTimestamp("tsend").getTime())));
						Double pay = rs.getDouble("payment");
						if (pay != null) {
							System.out.print("    Payment: $");
							System.out.printf("%.02f", pay);
							System.out.println();
						}
						// if payment == null, don't print payment
						String desc = rs.getString("description");
						if (desc.length() > DESC_PREVIEW_LEN)
							System.out.println("    Description (preview):\n" +
																 "        \"" +
																 desc.substring(0, DESC_PREVIEW_LEN) +
																 "\"...");
						else if (desc.length() > 0 &&
										 desc.length() < DESC_PREVIEW_LEN)
							System.out.println("    Description:\n        \"" +
																 desc + "\"");
						// if description is empty, don't print it
						System.out.println();
					}
					rs.close();
					if (!anyPostFound) {
						userStillActive = false;
						System.out.println("No offers found in your city " +
															 "matching your search criteria.\n");
					}
					else {
						if (!morePages) {
							System.out.println("There are no more posts meeting your criteria.\n");
						}
						System.out.println("Enter\n'n' for the next " + limit +
															 " posts\n'v' to view an offer " +
															 "or\n'q' to quit.");
						String response = Validation.preventSQLInjection(Validation.input.nextLine());
						System.out.println();
						char c = 'z';
						if (response.length() > 0)
							c = Character.toLowerCase(response.charAt(0));
						boolean validResponse =
							(c == 'n' || c == 'v' || c == 'q');
						do {
							if (!validResponse) {
								System.out.println("Enter\n'n' for the next " + limit +
																	 " posts\n'v' to view an " +
																	 "offer or\n'q' to quit.");
								response = Validation.preventSQLInjection(Validation.input.nextLine());
								System.out.println();
							}
							if (response.length() > 0)
								c = Character.toLowerCase(response.charAt(0));
							if (c == 'v') {
								System.out.print("Enter offer number you would " +
																 "like to view: ");
								int selection = -1;
								response = Validation.preventSQLInjection(Validation.input.nextLine());
								System.out.println();
								if (response.length() > 0)
									selection =
										Character.getNumericValue(response.charAt(0));
								if (selection >= 1 && selection <= offset) {
									Offer.displayOfferInfo(selection - 1, includedTypes);
									System.out.print("Type 'y' to accept offer " +
																	 "(type anything else to " +
																	 "decline): ");
									response = Validation.preventSQLInjection(Validation.input.nextLine());
									System.out.println();
									if (response.length() > 0)
										if (Character.toLowerCase(response.charAt(0)) == 'y') {
											if (Validation.userIsSitter)
												acceptOffer(selection - 1, includedTypes);
											else {
												System.out.print("It seems like your account is not labelled " +
																				 "as a pet sitter, and therefore, cannot accept offers.\n " +
																				 "Would you like to update your account info? (Type y to accept.) ");
												response = Validation.preventSQLInjection(Validation.input.nextLine());
												System.out.println();
												if (Character.toLowerCase(response.charAt(0)) == 'y') {
													UserProfile.editUserProfile();
													c = 'q';
													break;
												}
											}
										}

									userStillActive = false;
								}
								// Invalid offer
								else {
									System.out.println("Invalid offer number");
									continue;
								}
							}
							validResponse = (c == 'n' || c == 'v' || c == 'q');
						} while (!validResponse);
						if (c == 'q')
							userStillActive = false;
					}
				}
				catch (java.sql.SQLException e) {
					System.err.println(e);
					System.exit(-1);
				}

			} while(userStillActive);

			char c = 'z';
			// Prompt user if they would like to navigate to another page
			do {
				System.out.println(Validation.OPTIONS);
				String str = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (str.length() > 0) {
					c = Character.toLowerCase(str.charAt(0));
				}
				else
					c = 'z';
			} while (!Validation.isValidOption(c));
			return c;
    }

	public static void acceptOffer(int offset, ArrayList<String> includedTypes) {
		Validation.updateSQL("UPDATE offers SET acceptBy = '" +
												 Validation.curUsername +
												 "' WHERE offerid IN (SELECT" +
												 " offerid FROM offers, pets, accounts " +
												 "WHERE sitting = petid AND owner = " +
												 "username " +
												 (includedTypes.size() ==
													Validation.petTypes.length ? "" : "AND ("
													+ filterQuery(includedTypes) + ") ") +
												 "ORDER BY tsposted DESC LIMIT 1 " +
												 "OFFSET " + offset + ");");

		System.out.println("Offer accepted!\n");
	}

    public static ArrayList<String> filterPets() {
        String str;
        ArrayList<String> include = new ArrayList<String>();
        boolean validOption = true;

        do{
            try{
                Validation.printPetTypes();
                System.out.println("What kind of pets would you like to " +
										"sit? \n Enter each number " +
                                   		"seperated by a space. " +
										"(If no preference, hit enter.)");
                str = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
                if (str.length() == 0) {
                    for(int i = 0; i < Validation.petTypes.length; ++i)
                        include.add(Validation.petTypes[i]);
                    validOption = true;
                }
                else {
                    String[] arrStr = str.split(" ");
                    for(int i = 0; i < arrStr.length; ++i){
                        int temp = Integer.parseInt(arrStr[i]);
                        if (temp >= 0 && temp < Validation.petTypes.length)
                            include.add(Validation.petTypes[temp]);
                        else
                            throw new Exception();
                    }
                    validOption = true;
                }
            }
            catch (NumberFormatException e){    // If user enters something thats not an int
                System.out.println("Invalid option!");
                validOption = false;
            }
            catch (Exception e){
                System.out.println("Invalid option!");
                validOption = false;
            }
        } while(!validOption);

        return include;
    }
}
