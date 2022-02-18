package unibo.progetto.acmesky;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class GetUserInformations implements JavaDelegate {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final long id = (long) execution.getVariable("id");
		
		User user = jdbcTemplate.query(	"SELECT * FROM users WHERE id = ?", 
															new RowMapper<User>() {
																public User mapRow(ResultSet rs, int rowNum) throws SQLException {
																	return new User(rs.getLong("id"),
																					rs.getString("name"),
																					rs.getString("surname"),
																					rs.getString("email"),
																					rs.getString("password"),
																					rs.getDate("birthday"),
																					rs.getString("nationality"),
																					rs.getString("cardNumber"),
																					rs.getString("phoneNumber"));
																}
															},
															id).get(0);
				
		ObjectValue customerDataValue = Variables.objectValue(user)
												.serializationDataFormat(Variables.SerializationDataFormats.JAVA)
												.create();
		
		execution.setVariable("userProfile", customerDataValue);
		//questo sotto serve per quando si fa l'acquisto a causa di un problema di 'deserialization'
		execution.setVariable("cardNumber", user.getCardNumber());
		execution.setVariable("name", user.getName());
	}

}