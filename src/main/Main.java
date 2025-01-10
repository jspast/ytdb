package main;

import java.sql.ResultSet;
import java.sql.SQLException;

import query.Querier;
import query.Query;

public class Main {

	public static void main(String[] args) {
		
		final String URL = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "123";
		
		Querier querier = null;
		try {
			querier = new Querier(URL, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Query maxLikesPerCat = Query.on("SELECT videocategory.name AS Category, max(videolikes) AS MaxLikes\n"
									   + "FROM videocategory left join (videolikes natural join video) on (name = videocategoryname)\n"
									   + "GROUP BY videocategory.name\n"
									   + "HAVING max(videolikes) > ?;").with(0);	
		
	
		try {
			ResultSet result = querier.query(maxLikesPerCat);
			while (result.next()) {
			    System.out.print("Column 1 returned ");
			    System.out.println(result.getString(1));
			}
			result = querier.query(maxLikesPerCat);
			while (result.next()) {
			    System.out.print("Column 1 returned ");
			    System.out.println(result.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
