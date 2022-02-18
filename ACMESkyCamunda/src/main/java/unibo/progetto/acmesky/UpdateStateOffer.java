package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateStateOffer implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {

		try {
			final int flyID				= (int)		execution.getVariable("flyID");
			final String company		= (String)	execution.getVariable("company");
			final int	 availableSeats	= (int)		execution.getVariable("availableSeats");
			
			if(availableSeats>0) {
				jdbcTemplate.update("UPDATE offers SET availability=?, num_seat=? WHERE fly_id = ? AND company = ?", true, availableSeats, flyID, company);
				execution.setVariable("availability", true);
			}else {
				jdbcTemplate.update("UPDATE offers SET availability=?, num_seat=? WHERE fly_id = ? AND company = ?", false, 0, flyID, company);
				execution.setVariable("availability", false);
			}
		} catch (Exception e) {
			throw new BpmnError("ErrorInfoOfferta", "saveUpdate");
		}
	}

}
