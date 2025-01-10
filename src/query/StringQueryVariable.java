package query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringQueryVariable extends QueryVariable {

	private final String value;
	
	StringQueryVariable(int position, String value) {
		super(position);
		this.value = value;
	}
	
	@Override
	void addToStatement(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(super.getPosition(), value);
	}

}
