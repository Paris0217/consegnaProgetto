package unibo.progetto.acmesky;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class GetUserInterests implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final long user_id = (long) execution.getVariable("id");
		
		List<Interest> interestsList = jdbcTemplate.query(	"SELECT * FROM interests WHERE user_id = ?", 
															new RowMapper<Interest>() {
																public Interest mapRow(ResultSet rs, int rowNum) throws SQLException {
																	return new Interest(rs.getLong("id"),
																						rs.getLong("user_id"),
																						rs.getString("depa_country"),
																						rs.getString("depa_city"),
																						rs.getString("dest_country"),
																						rs.getString("dest_city"),
																						rs.getDate("period_start"),
																						rs.getDate("period_end"),
																						rs.getFloat("price"),
																						rs.getInt("num_seat"));
																}
															},
															user_id);
				
		ObjectValue customerDataValue = Variables.objectValue(interestsList)
												.serializationDataFormat(Variables.SerializationDataFormats.JAVA)
												.create();
		execution.setVariable("interests", customerDataValue);
	}

}
