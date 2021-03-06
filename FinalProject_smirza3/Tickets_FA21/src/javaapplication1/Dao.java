package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE smirza3_tickets_test(ticket_id INT AUTO_INCREMENT PRIMARY KEY, " +
				"ticket_issuer VARCHAR(30), " +
				"ticket_description VARCHAR(200), " +
				"start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
				"end_date TIMESTAMP NULL," +
				"status int DEFAULT 1)";
		final String createUsersTable = "CREATE TABLE smirza3_users_test(uid INT AUTO_INCREMENT PRIMARY KEY, " +
				"uname VARCHAR(30), " +
				"upass VARCHAR(30), " +
				"admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into smirza3_users_test(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("INSERT INTO smirza3_tickets_test" + "(ticket_issuer, ticket_description) VALUES(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = getConnection().createStatement();
			results = statement.executeQuery("SELECT * FROM smirza3_tickets_test");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	// continue coding for updateRecords implementation
	public void updateRecords(String id, String oldParameter, String updatedParameter) {
		try {
			statement = getConnection().createStatement();

			//set update target to be the tickets table
			statement.executeUpdate("UPDATE smirza3_tickets_test SET " + oldParameter + " = '" + updatedParameter + "' WHERE ticket_id = " + id + ";");
			//apply updates to selected parameter (ticket_id, ticket_issuer, or ticket_description)
			//statement.executeUpdate("SET " + oldParameter + " = '" + updatedParameter + "'");
			//set update target to the ticket entry with provided id
			//statement.executeUpdate("WHERE ticket_id = " + id);

			//print to console
			System.out.println("Record " + id + " has been updated...");

			//close objects
			statement.close();
			connect.close();
		}

		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("There was a problem updating the record");
			System.out.println(se.getMessage());
		}

	}

	// continue coding for deleteRecords implementation
	public void deleteRecords(String id) {
		try {
			statement = getConnection().createStatement();

			//delete record entry in the tickets table with given id
			statement.executeUpdate("DELETE FROM smirza3_tickets_test WHERE ticket_id = " + id);

			//print to console
			System.out.println("Record " + id + " has been deleted...");

			//close objects
			statement.close();
			connect.close();
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("There was a problem deleting the record");
			System.out.println(se.getMessage());
		}
	}

	// close records implementation
	public void closeRecords(String id) {
		try {
			statement = getConnection().createStatement();

			// set table target for update
			statement.executeUpdate("UPDATE tickets.smirza3_tickets_test SET status = '0', end_date = current_timestamp() WHERE ticket_id = " + id + ";");

			// update the status of the ticket and add an end_date timestamp
			//statement.executeUpdate("");

			// set ticket selection criteria for update
			//statement.executeUpdate("WHERE ticket_id = " + id);

			// print to console
			System.out.println("Ticket ID: " + id + " has been closed...");

			//close objects
			statement.close();
			connect.close();
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("There was a problem closing the record");
			System.out.println(se.getMessage());
		}
	}
}