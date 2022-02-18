package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SaveOfferAdHoc implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		try {
			final String tokenURI = (String) execution.getVariable("tokenURI");
			final long	 offerID  = (long) execution.getVariable("offerID");
			final long	 interestID	  = (long) execution.getVariable("interestID");
			
			JsonValue possibleOfferJsonValue = execution.getVariableTyped("possibleOffer");
			SpinJsonNode possibleOfferJson = possibleOfferJsonValue.getValue();
			
			Offer offer = new Offer(possibleOfferJson.prop("id").numberValue().intValue(),
									possibleOfferJson.prop("depaCity").stringValue(),
									possibleOfferJson.prop("destCity").stringValue(),
									possibleOfferJson.prop("date").stringValue(),
									possibleOfferJson.prop("price").numberValue().floatValue(),
									possibleOfferJson.prop("num_seat").numberValue().intValue(),
									true,
									possibleOfferJson.prop("company").stringValue());
			
			boolean exist = jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM offersAdHoc WHERE offer_id = ? AND interest_id = ?)", Boolean.class, offerID, interestID);
			
			if(exist) {
				throw new BpmnError("ErrorCreateOffer", "existOfferAdHoc");
			}
			
			OfferAdHoc offerAdHoc = new OfferAdHoc(interestID, offerID,  tokenURI);
			jdbcTemplate.update("INSERT INTO offersAdHoc( interest_id, offer_id, token_uri) "+
					"VALUES (?, ?, ?)", 
					offerAdHoc.getInterestID(),
					offerAdHoc.getOfferID(), 
					offerAdHoc.getTokeURI());
			
			final String queryPhoneNumber = "SELECT phonenumber FROM interests INNER JOIN users ON users.id = interests.user_id WHERE interests.id = "+interestID;
			String userNumber = jdbcTemplate.queryForObject(queryPhoneNumber, String.class);
			final String queryNumSeat = "SELECT num_seat FROM interests INNER JOIN users ON users.id = interests.user_id WHERE interests.id = "+interestID;
			int num_seat = jdbcTemplate.queryForObject(queryNumSeat, Integer.class);
			execution.setVariable("userNumber", userNumber);
			execution.setVariable("text", "Trovata un offerta di "+offer.getCompany()+": volo numero " + offer.getFlyID() + " da " +offer.getDepaCity()+" a "+offer.getDestCity()+" in data "+offer.getDateString()+" con "+ num_seat +" disponibili a soli "+offer.getPrice()+"€ a biglietto.");
			
		} catch (Exception e) {
			throw new BpmnError("ErrorCreateOffer", "errorCreate");
		}
	}
}