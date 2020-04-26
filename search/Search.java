package search;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
                (i == arr.size()-1 ? "' " : "' OR "));
        }
        return sb.toString();
    }

    // Search Page
    public static char goToSearch() {
        ArrayList<String> includedTypes = filterPets();

        System.out.println("Searching for posts in your city...");
        int limit = 5;
        int offset = 0;
        String query = 
            "SELECT pettype, payment, tsstart, tsend, description "+ 
            "FROM offers, pets, accounts "+
            "WHERE sitting = petid AND owner = username " +
            // true = include all pets, false = only pets in "includedTypes"
            ( includedTypes.size() == Validation.petTypes.length ? "" : 
                "AND (" +
                filterQuery(includedTypes) +    
                ") ") +
            // The 5 most recent posts sorted
            // could update offset in a loop if we implement a "next" input option for user
            "ORDER BY tsposted DESC LIMIT " + limit + " OFFSET " + offset + ";"; 

        // Execute query
        // Print result set to user
        // 

		char c = 'z';
		// Promp user if they would like to navigate to another page
		do {
			System.out.println(Validation.OPTIONS);
			String str = Validation.input.nextLine();
            System.out.println();
			if (str.length() > 0) {
				c = Character.toLowerCase(str.charAt(0));
            }
            else 
                c = 'z';
		} while (!Validation.isValidOption(c));
		return c;
    }

    public static ArrayList<String> filterPets()
    {
        String str;
        ArrayList<String> include = new ArrayList<String>();
        boolean validOption = true;

        do{
            try{
                Validation.printPetTypes();
                System.out.println("What kind of pets would you like to sit? \n" +
                                    "Enter each number seperated by a space. (If no preference, hit enter.)");
                str = Validation.input.nextLine();
                System.out.print("\n");
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
        //*
        for (int i = 0; i < include.size(); ++i)
            System.out.print(include.get(i) + ", ");
        System.out.println();
        //*/

        return include;
    }
    public static void viewSearch()
    {

    }
}