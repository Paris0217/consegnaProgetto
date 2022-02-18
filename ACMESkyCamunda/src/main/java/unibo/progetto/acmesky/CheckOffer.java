package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CheckOffer implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final int flyID		 = (int) execution.getVariable("flyID");
		final String company = (String) execution.getVariable("company");
		
		try {
			final boolean availability = jdbcTemplate.queryForObject("SELECT availability FROM offers WHERE fly_id = ? AND company = ?", Boolean.class, flyID, company);
			execution.setVariable("availability", availability);
			if(!availability) {
				execution.setVariable("availableSeats", 0);
			}
		}catch(EmptyResultDataAccessException e) {
//			non è stato trovato 
			throw new BpmnError("ErrorInfoOfferta", "findResult");
		}
	}

}
