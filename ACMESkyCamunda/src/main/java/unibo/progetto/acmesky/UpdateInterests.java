package unibo.progetto.acmesky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateInterests implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<Interest> deleteOps = new ArrayList<Interest>();
		List<Interest> updateOps = new ArrayList<Interest>();
		List<Interest> insertOps = new ArrayList<Interest>();
		JsonValue interestsJson = execution.getVariableTyped("interests");
		boolean check = true;
		
//		{
//			"interests": [{
//				"operation": "DELETE/INSERT/UPDATE",
//				"interest": {
//					...
//					vari campi
//					...
//				},
//			{
//				"operation": ....
//		
//			}]
//		}
		
		SpinList<SpinJsonNode> interestsOp=interestsJson.getValue().prop("interests").elements();
		Iterator<SpinJsonNode> iter = interestsOp.iterator();
		while(iter.hasNext()) {
			SpinJsonNode interestOp = iter.next();
			String op = interestOp.prop("operation").stringValue();
			SpinJsonNode interestJson = interestOp.prop("interest");
			if(	interestJson.prop("depaCountry").stringValue().equals("") ||
				interestJson.prop("destCountry").stringValue().equals("") ||
				interestJson.prop("periodStart").stringValue().equals("") ||
				interestJson.prop("periodEnd").stringValue().equals("")	) {
				continue;
			}
			
			Interest interest = new Interest((interestJson.prop("id").stringValue()!="" ? Long.parseLong(interestJson.prop("id").stringValue()) : 0),
											Long.parseLong(interestJson.prop("userId").stringValue()),
											interestJson.prop("depaCountry").stringValue(),
											(interestJson.prop("depaCity").stringValue().equals("") ? null : interestJson.prop("depaCity").stringValue()),
											interestJson.prop("destCountry").stringValue(),
											(interestJson.prop("destCity").stringValue().equals("") ? null : interestJson.prop("destCity").stringValue()),
											interestJson.prop("periodStart").stringValue(),
											(interestJson.prop("periodEnd").stringValue().equals("") ? null : interestJson.prop("periodEnd").stringValue()),
											(interestJson.prop("price").stringValue()!="" ? Float.parseFloat(interestJson.prop("price").stringValue()) : 0),
											(interestJson.prop("numSeat").stringValue()!="" ? Integer.parseInt(interestJson.prop("numSeat").stringValue()) : 0));
			
			if		(op.equals("DELETE"))	{	deleteOps.add(interest);	}
			else if	(op.equals("UPDATE"))	{	updateOps.add(interest);	}
			else if	(op.equals("INSERT"))	{	insertOps.add(interest);	}
		}
		
		try {
			deleteOps.forEach((interest) -> {
				jdbcTemplate.update("DELETE FROM interests WHERE id = ?", interest.getId());
			});
		} catch (Exception e) {
			check = false;
		}
		
		try {
			updateOps.forEach((interest) ->{
				jdbcTemplate.update("UPDATE interests "+
									"SET user_id=?, depa_country=?, depa_city=?, dest_country=?, dest_city=?, period_start=?, period_end=?, price=?, num_seat=? "+
									"WHERE id = ?",
									interest.getUserId(),
									interest.getDepaCountry(), 
									interest.getDepaCity(),
									interest.getDestCountry(),
									interest.getDestCity(),
									interest.getPeriodStart(),
									interest.getPeriodEnd(),
									interest.getPrice(),
									interest.getNumSeat(),
									interest.getId());
			});
		} catch (Exception e) {
			check = false;
		}
		
		try {
			insertOps.forEach((interest) -> {
				jdbcTemplate.update("INSERT INTO interests(user_id, depa_country, depa_city, dest_country, dest_city, period_start, period_end, price, num_seat) "+
									"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", 
									interest.getUserId(),
									interest.getDepaCountry(), 
									interest.getDepaCity(),
									interest.getDestCountry(),
									interest.getDestCity(),
									interest.getPeriodStart(),
									interest.getPeriodEnd(),
									interest.getPrice(),
									interest.getNumSeat());
			});
		} catch (Exception e) {
			check = false;
		}
		
		execution.setVariable("checkResUpdateInterests", check);
	}

}
