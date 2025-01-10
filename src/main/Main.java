package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import query.Querier;
import query.Query;

public class Main {

	public static void main(String[] args) {
		
		final String URL = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "123";
		
		UI.run(URL, username, password);
		
	}

}
