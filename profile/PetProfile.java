package profile;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import util.Validation;
import profile.UserProfile;


public class PetProfile
{

	public static void editPetInfo()
	{
		;
	}
	public static void addPetInfo()
	{
		String petType, petName;
		int age;

		System.out.print("Please enter your pet's name: ");
		petName = Validation.getPetName();

		Validation.printPetTypes();
		System.out.print("Enter what kind of animal your pet is: ");
		petType = Validation.getPetType();

		System.out.print("How old is your pet? (in years) ");
		age = Validation.getPetAge();

		String insertCMD = "INSERT INTO pets (pettype, petname, " +
			"birthyear, owner)" +
			"VALUES(" + petType + "', '" + petType + "', age_to_birth_year("
			+ age + "), '" + Validation.curUsername + "');";
		Validation.updateSQL(insertCMD);

	}
	public static void displayPetInfo()
	{
		try {
			ResultSet rs
				= Validation.querySQL("SELECT " +
															"petname, pettype, " +
															"birth_year_to_age(birthyear) AS age " +
															"FROM pets WHERE owner = '"
															+ Validation.curUsername + "';");
			if (rs.next()) {
				System.out.println("petname: " + rs.getString("petname"));
				System.out.println("pettype: " + rs.getString("pettype"));
				System.out.println("age: " + rs.getInt("age") + " years");
				System.out.println();
			}
			else
				System.err.println("Internal error finding entry matching "
													 + "owner.");
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}
