package query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerQueryVariable extends QueryVariable {
	
	private final int value;
	
	IntegerQueryVariable(int position, int value) {
		super(position);
		this.value = value;
	}
	
	@Override
	void addToStatement(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setInt(super.getPosition(), value);
	}

}
