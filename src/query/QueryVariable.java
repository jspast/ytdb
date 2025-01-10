package query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class QueryVariable {
	
	private final int position;

	protected QueryVariable(int position) {
		this.position = position;
	}
	
	abstract void addToStatement(PreparedStatement statement) throws SQLException;
	
	protected int getPosition() {
		return position;
	}
	
}
