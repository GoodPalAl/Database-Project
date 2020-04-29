// Pet Sitting Services
// Created by Emma Griffin, Al Allums, and Sydney McClure
// Due Date: 29 April 2020 (c)

package profile;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.*;
import util.Validation;
import profile.UserProfile;
import profile.PetProfile;


public class PetProfile
{
	protected static Integer curPetID;

	public static void goToPetProfile()
	{
		String PET_OPTIONS =
			"Enter \n" +
			"'a' to add a pet to your profile, \n" +
			"'u' to update an existing pet's information, \n" +
			//"'d' to delete a pet from your account\n" +
			"'v' to view your pets' information again, or\n" +
			"'e' to exit back to your account.";

		curPetID = null;
		displayPetInfo(curPetID);

		char response = 'z';
		do {
			System.out.println(PET_OPTIONS);
			String str = Validation.preventSQLInjection(Validation.input.nextLine());
			System.out.println();
			if (str.length() > 0)
			{
				response = Character.toLowerCase(str.charAt(0));
				if (response == 'a')
					addPetInfo();
				else if (response == 'u')
					editPetInfo();
				/*
				else if (response == 'd')
					deletePet();
				//*/
				else if (response == 'v')
					displayPetInfo(null);
			}
		} while(response != 'e');

		// Clear once user exits Pet Profile menu
		curPetID = null;
	}

	// Create generic update statement based on which field to update for
	// curPetID. Intended for use with boolean values.
	private static String genUpdateSQL(String field, String update, int petID) {
		return "UPDATE pets SET " + field + " = " + update + " WHERE "
			+ "petID = " + petID + ";";
	}

	// Map of characters to the corresponding method to update the associated
	// pet data
	final static Map<Character, Method> updatePetField =
			Collections.unmodifiableMap(new HashMap<Character, Method>() {{
		try {
			put('t', PetProfile.class.getMethod("updatePetType"));
			put('n', PetProfile.class.getMethod("updatePetName"));
			put('a', PetProfile.class.getMethod("updatePetAge"));
		}
		catch (java.lang.NoSuchMethodException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}});

    public static void editPetInfo()
	{
		// Default initialization to invalid option to prevent unitialized
		// value
		char c = 'z';
		boolean badOption = false;
		// If user has no pets, let them know they certainly can't update them!
		if(Validation.numMatches("SELECT petid FROM pets WHERE owner = '" +
														 Validation.curUsername + "';") == 0) {
			System.out.println("No pets found for account, would you like to " +
												 "add a pet (y/n)?");
			do {
				String y_or_n = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (badOption) {
					System.out.print("Invalid option.\n" +
													 "Please enter y to add pet or n to return to:" +
													 " pet account menu: ");
					y_or_n = Validation.preventSQLInjection(Validation.input.nextLine());
				}
				if (y_or_n.length() > 0) {
					badOption = false;
					c = Character.toLowerCase(y_or_n.charAt(0));
					System.out.println();
					if (c == 'y')
						addPetInfo();
				}
				else
					badOption = true;
			}	while (badOption);
			return;
		}
		curPetID = Validation.findPetIDFromPetName();
		displayPetInfo(curPetID);
		final String PET_EDIT_OPTIONS =		// EDITED: moved "delete pet" option to pet viewing menu
			"Enter \n" +
			"'t' for pet type, \n" +
			"'n' for pet name, \n" +
			"'a' for age, or\n" +
			"'q' to cancel editing.";

		char response;

		System.out.println("Please enter which part of your pet's information you " +
								"would like to update:\n" + PET_EDIT_OPTIONS);
		response = Validation.preventSQLInjection(Validation.input.nextLine()).charAt(0);
		System.out.println();
		do {
			if (badOption) {
				System.out.println("Invalid option.\n" +
										"Please enter which " +
										"part of your pet's information " +
										"you would like to update:\n" + PET_EDIT_OPTIONS);
				response = Validation.preventSQLInjection(Validation.input.nextLine()).charAt(0);
			}
			if (response == 'q') {
				break;
			}
			try {
				Method m = updatePetField.get(response);
				if (m == null)
					badOption = true;
				else
					m.invoke(null);
			}
			// Will catch case where a bad key is passed to
			// updatePetField map
			catch (java.lang.ReflectiveOperationException e) {
				badOption = true;
			}
		} while (badOption);
	}

	/*
		// TODO: delete from sql
		final public static void deletePet()
		{
			System.out.println("This feature coming soon!");
		}
	//*/

	final public static void updatePetType() {
		System.out.print("Enter what kind of animal your pet is: ");
		String newPetType = "'" + Validation.getPetType() + "'";
		Validation.updateSQL(genUpdateSQL("petType", newPetType, curPetID));
	}

	final public static void updatePetName() {
		System.out.print("Enter your pet's name: ");
		String newName = "'" + Validation.getPetName() + "'";
		Validation.updateSQL(genUpdateSQL("petName", newName, curPetID));
	}

	final public static void updatePetAge() {
		System.out.print("Enter age of your pet: ");
		String newAge = "age_to_birth_year(" + Validation.getPetAge() + ")";
		Validation.updateSQL(genUpdateSQL("birthyear", newAge, curPetID));
	}

    public static void addPetInfo()
    {
		String petType, petName;
		int age;

		System.out.print("Please enter your pet's name: ");
		petName = Validation.getPetName();
		// Check if owner already owns a pet with this name
		// if so, prompt to update instead.

		if (Validation.petExists(petName))
		{
			char c;
			do {
				System.out.println("You already own a pet with this name.\n" +
									"Would you like to update their "+
									"information instead? (y/n)");
				String str = Validation.preventSQLInjection(Validation.input.nextLine());
				System.out.println();
				if (str.length() > 0) {
					Validation.input.nextLine();
					c = Character.toLowerCase(str.charAt(0));
				}
				else
					c = 'z';
			} while(c != 'y' || c != 'n');
			if (c == 'y')
				editPetInfo();
		}
		else{
			System.out.println("What kind of animal fits your pet best? " +
								"Select one of the following:");
			Validation.printPetTypes();
			petType = Validation.getPetType();

			System.out.print("How old is your pet (in years)? (Enter 0 if your" +
											 " pet is < a year old) ");
			age = Validation.getPetAge();

			// FIXME: should PETID be handled on the sql side??
			String insertCMD =	"INSERT INTO pets (pettype, petname, " +
								"birthyear, owner)" +
								"VALUES('" + petType +
								"', '" + petName + "', age_to_birth_year(" + age + ") " +
								", '" + Validation.curUsername + "');";
			Validation.updateSQL(insertCMD);
			System.out.println("Pet added!\n");
		}
	}
	// If petID == NULL -> display all pets, otherwise display pet matching petID
	public static void displayPetInfo(Integer petID) {
		String query = "SELECT petname, pettype, birth_year_to_age(birthyear) as age " +
				   "FROM pets WHERE owner = '" + Validation.curUsername + "'" +
				   (petID == null ? ";" : " AND petID = " + petID + ";");

		try {
			ResultSet rs = Validation.statement.executeQuery(query);

			boolean petExists = false;
			while(rs.next()) {
				petExists = true;
				System.out.println("petname: " + rs.getString("petname"));
				System.out.println("pettype: " + rs.getString("pettype"));
				int age = rs.getInt("age");
				if (age < 1)
					System.out.println("age: less than a year");
				else
					System.out.println("age: " + age + " years");
				System.out.println();
			}

			rs.close();
			if (!petExists)
				System.out.println("No pets associated with account :(");
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}
