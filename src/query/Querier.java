package query;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class Querier implements Closeable {

	private Connection connection;
	private ResultSet result;
	
	public Querier(String url, String username, String password) throws SQLException {
		connection = DriverManager.getConnection(url, username, password);
	}
	
	public ResultSet query(Query query) throws SQLException {
		if(Objects.nonNull(result)) {
			Statement st = result.getStatement();
			result.close();
			result = null;
			st.close();
		}
	
		ArrayList<QueryVariable> vars = query.getVars();
		if(vars.isEmpty()) {
			
			Statement statement = connection.createStatement();
			this.result = statement.executeQuery(query.getQueryText());
			
		} else {
			
			PreparedStatement statement = connection.prepareStatement(query.getQueryText());
			for(QueryVariable var : vars) var.addToStatement(statement);
			this.result = statement.executeQuery();
			
		}
		
		return result;
	}

	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("ERROR: There was an error when trying to close a querier's connection");
			e.printStackTrace();
		}
		try {
			result.close();
		} catch (SQLException e) {
			System.err.println("ERROR: There was an error when trying to close a querier's result set");
			e.printStackTrace();
		}	
	}
}
