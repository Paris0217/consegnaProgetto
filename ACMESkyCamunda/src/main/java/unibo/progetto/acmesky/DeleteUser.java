package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteUser implements JavaDelegate {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final long id = (long) execution.getVariable("id");
		
		try {
			jdbcTemplate.update("DELETE FROM users WHERE id = ?", new Object[] { id });
			execution.setVariable("checkDeleteUser", false);	
		}catch(Exception e) {
			execution.setVariable("checkDeleteUser", true);
			e.printStackTrace();
		}
		
	}

}
