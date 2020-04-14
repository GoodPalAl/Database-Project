//JdbcTestpostgreSQL.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

class JdbcTestPostgreSQL {
    public static void main (String args[]) {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            System.err.println (e);
            System.exit (-1);
        }
        try {
            // open connection to database
            Connection connection = DriverManager.getConnection(
            //"jdbc:postgresql://dbhost:port/dbname", "user", "dbpass");
            "jdbc:postgresql://127.0.0.1:5432/postgres", "cheet", "Sammy03");

            // build query, here we get info about all databases"
            String query = "SELECT * FROM petsit_accnts;";

            // execute query
            //*
            Statement statement = connection.createStatement ();
            // /*	
            statement.executeUpdate("INSERT INTO petsit_accnts(username, " +
                                        "fullname, password, email, tsJoined, " +
                                        "rating, offersDone, isSitter, isOwner, city, state)" +
                                        "VALUES ('GoodPalAl420', 'Dracula Tepes', 'notmypassword', 'email@usf.edu', " +
                                            "TIMESTAMP '1998-05-26', 2, 9, TRUE, TRUE, 'Tampa', 'FL')");
            //*/
            /*   
            statement.executeUpdate("CREATE TABLE petsit_accnts(" +
                                        "username VARCHAR(20) PRIMARY KEY UNIQUE NOT NULL, " +
                                        "fullname VARCHAR(30) NOT NULL, " +
                                        "password VARCHAR(50) NOT NULL, " +
                                        "email VARCHAR(30) NOT NULL UNIQUE, " +
                                        "tsJoined TIMESTAMP NOT NULL, " +
                                        "rating NUMERIC(2,1) CHECK (rating BETWEEN 1 AND 5), " +
                                        "offersDone INT CHECK (offersDone >= 0), " +
                                        "isSitter BOOL NOT NULL, " +
                                        "isOwner BOOL NOT NULL, " +
                                        "city VARCHAR(50) NOT NULL, " +
                                        "state CHAR(2) NOT NULL CHECK " +
                                            "(LEFT(state, 1) BETWEEN 'a' AND 'z' " +
                                            "AND " +
                                            "SUBSTRING(state, 2, 1) " +
                                            "BETWEEN 'a' AND 'z'));");
            //*/
            /*   
            statement.executeUpdate("CREATE TABLE petsit_pets(" +
                                        "petID INT PRIMARY KEY UNIQUE NOT NULL, " +
                                        "petType VARCHAR(50) NOT NULL, " +
                                        "petName VARCHAR(50) NOT NULL, " +
                                        "birthYear NUMERIC(4,0) CHECK (birthYear > 1900), " +
                                        "owner VARCHAR(20) REFERENCES petsit_accnts(username) NOT NULL);");
            //*/
            /*   
            statement.executeUpdate("CREATE TABLE offers(" +
                                        "offerID INT PRIMARY KEY UNIQUE NOT NULL," +
                                        "description VARCHAR(500)," +
                                        "vtsPosted TIMESTAMP NOT NULL," +
                                        "tsStart TIMESTAMP NOT NULL," +
                                        "tsEnd TIMESTAMP NOT NULL," +
                                        "payment NUMERIC(6,2) CHECK (payment >= 0)," +
                                        "acceptBy VARCHAR(20) REFERENCES petsit_accnts(username)," +
                                        "sitting INT REFERENCES petsit_pets(petID) NOT NULL);");
            //*/

            ResultSet rs = statement.executeQuery (query);

            // return query result
            //*
            while ( rs.next () ) {
                System.out.println ("username: " + rs.getString ("username"));
                System.out.println ("fullname: " + rs.getString ("fullname"));
                System.out.println ("password: " + rs.getString ("password"));
                System.out.println ("email: " + rs.getString ("email"));
                System.out.println ("tsJoined: " + rs.getString ("tsJoined"));
                System.out.println ("rating: " + rs.getString ("rating"));
                System.out.println ("offersDone: " + rs.getString ("offersDone"));
                System.out.println ("isSitter: " + rs.getString ("isSitter"));
                System.out.println ("isOwner: " + rs.getString ("isOwner"));
                System.out.println ("city: " + rs.getString ("city"));
                System.out.println ("state: " + rs.getString ("state"));
                //rs.updateString("username", "Someotherbitch");
            }
            //*/
            connection.close ();
        }
        catch (java.sql.SQLException e) {
            System.err.println (e);
            System.exit (-1);
        }
   }
}
