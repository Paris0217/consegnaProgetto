package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CheckCredentials implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final String email = (String) execution.getVariable("email");
		final String password = (String) execution.getVariable("password");
		
		final String query = "SELECT id FROM users WHERE "+"email='"+email+"' AND password='"+password+"'";
		execution.removeVariable("email");
		execution.removeVariable("password");

		try {
			Long id = jdbcTemplate.queryForObject(query, Long.class);
			execution.setVariable("checkLogin", true);
			execution.setVariable("id", id);
		}catch(EmptyResultDataAccessException e) {
//			Questo errore mi avvisa che ho trovato 0 elementi nella query e quindi non c'era l'utente che cercavo. Quindi non esiste
			execution.setVariable("checkLogin", false);
		}catch(Exception e) {
			execution.setVariable("checkLogin", false);
		}
	}
}
