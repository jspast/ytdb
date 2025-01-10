package query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public final class Query {

	private int nextPosition = 1;
	private String queryText;
	private ArrayList<QueryVariable> vars;		

	private Query(Query query) {
		this.nextPosition = query.nextPosition;
		this.queryText = query.queryText;
		this.vars = new ArrayList<QueryVariable>(query.vars);
	}
	
	private Query(String queryText) {
		this.queryText = queryText; 
		this.vars = new ArrayList<QueryVariable>();
	}
	
	public static Query on(String queryText) {
		return new Query(queryText);
	}
	
	public Query with(int param) {
		this.vars.add(new IntegerQueryVariable(this.nextPosition, param));
		this.nextPosition++;
		return new Query(this);
	}
	
	ArrayList<QueryVariable> getVars() {
		return vars;
	}
	
	String getQueryText() {
		return queryText;
	}

}
