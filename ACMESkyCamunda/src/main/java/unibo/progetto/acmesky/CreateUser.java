package unibo.progetto.acmesky;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateUser implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final String name = (String) execution.getVariable("name");
		final String surname = (String) execution.getVariable("surname");
		final String email = (String) execution.getVariable("email");
		final String password = (String) execution.getVariable("password");
		final String birthdayS = (String) execution.getVariable("birthday");
		Date birthday;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try { birthday = format.parse(birthdayS); }catch(Exception e) { birthday = null; }
		final String nationality = (String) execution.getVariable("nationality");
		String cardNumber = (String) execution.getVariable("cardNumber");
		if(cardNumber.equals("")) {	cardNumber = null;	}
		final String phoneNumber = (String) execution.getVariable("phoneNumber");
	
		try {
//			Controlla se c'è già un utente con quella mail
			@SuppressWarnings("deprecation")
			int n_mail = jdbcTemplate.queryForObject(
				    "SELECT COUNT(*) FROM users WHERE email = ?",new Object[] {email}, Integer.class);
			if(n_mail>0) {
				execution.setVariable("checkID", false);
			}else {
//				Se non c'è, allora posso creare l'utente
				jdbcTemplate.update("INSERT INTO users (name, surname, email, password, birthday, nationality, cardNumber, phoneNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
									, name, surname, email, password, birthday, nationality, cardNumber, phoneNumber); 
				execution.setVariable("checkID", true);
			}
		}catch(Exception e) {
			execution.setVariable("checkID", false);
		}

	}
}
