package unibo.progetto.acmesky;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChangeInfo implements JavaDelegate{

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final String name		= (String) execution.getVariable("name");
		final String surname	= (String) execution.getVariable("surname");
		final String email		= (String) execution.getVariable("email");
		final String password	= (String) execution.getVariable("password");
		final String birthdayS	= (String) execution.getVariable("birthday");

		Date birthday;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try { birthday = format.parse(birthdayS); }catch(Exception e) { birthday = null; }
		final String nationality = (String) execution.getVariable("nationality");
		String cardNumber = (String) execution.getVariable("cardNumber");
		if(cardNumber.equals("")) {	cardNumber = null;	}
		final String phoneNumber = (String) execution.getVariable("phoneNumber");
		
		try {
			jdbcTemplate.update("UPDATE users SET name = ?, surname = ?, password = ?, birthday = ?, nationality = ?, cardNumber = ?, phoneNumber = ? WHERE email = ? ", new Object[] {name, surname, password, birthday, nationality, cardNumber, phoneNumber, email});
			execution.setVariable("checkChangeInfo", false);	
		}catch(Exception e) {
			execution.setVariable("checkChangeInfo", true);
			e.printStackTrace();
		}
	}

}
