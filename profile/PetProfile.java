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

			System.out.println("Please enter your pet's name: ");
			petName = Validation.getPetName();

			System.out.println("Enter what kind of animal your pet is: ");
			petType = Validation.getPetType();

			System.out.println("How old is your pet? (in years) ");
			age = Validation.getAge();

			String insertCMD = "INSERT INTO pets (petid, pettype, petname, " +
				"birthyear, owner)" +
				"VALUES(" + petID + ", '" + petName +
				"', '" + petType + "', " + birthYear +
				", '" + Validation.curUsername + "');";
			Validation.updateSQL(genUpdateSQL(insertCMD));

    }
    public static void displayPetInfo()
    {
			try {
			ResultSet rs
				= Validation.querySQL("SELECT " +
															"petname, pettype, birthyear, owner " +
															"FROM pets WHERE owner = '"
															+ Validation.curUsername + "';");
			if (rs.next()) {
				System.out.println("petname: " + rs.getString("petname"));
				System.out.println("pettype: " + rs.getString("pettype"));
				System.out.println("age: "
													 + Validation.getPetAge(rs.getInt("birthyear"))
													 + " years");
				//System.out.println("owner: " + rs.getString("owner"));
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
