# Database-Project
Simulation of a petsitting service with associated databases for storing information about users, their pets, and petsitting offers.

# Dependencies
Java 1.8 or newer. PostgreSQL 10.12 or newer.

# Usage
If compiling from source, ensure you have the official PostgreSQL JDBC driver (project was built and tested using version 42.2.12 which can be found here https://jdbc.postgresql.org/download/postgresql-42.2.12.jar) installed and added to your java path.

# Create the database 
You may associate the database with any username, password, and database name you like, however, if you want to use a custom username, password, and/or database name update the associated Login.java's line to reflect this:  
```
Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/example_db", "pet_example", "pass");
```
where ```example_db``` is your database name, ```pet_example``` is your username, and ```pass``` is your password.


If you choose to use the default values provided for username, password, and database name, then you need to ensure there exists a user with these fields. If```psql``` is installed, create the user ```pet_example``` with password ```pass``` with the following:
1. Start a psql session
```
psql
```
2. Create user/password
```
CREATE USER pet_example WITH PASSWORD 'pass';
```
3. Create database
```
CREATE DATABASE example_db OWNER pet_example;
```
4. Exit psql
```
\q
```
5. Fill database with values
```
psql example_db < resources/example_db.sql                                    
```

# Compile/Run
Run the following
```
javac Login.java

java Login
```

If running the precompiled binary (.jar) from the release: 
```
java -jar Petsitting_Services.jar
``` 
