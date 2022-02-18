package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateOffert implements JavaDelegate {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
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
				
		boolean exist = jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM offers WHERE fly_id = ? AND company = ?)", Boolean.class, offer.getFlyID(), offer.getCompany());
		if(!exist) {
//			Se entro qui vuol dire che non era nel DB e quindi posso aggiungerla
			jdbcTemplate.update("INSERT INTO offers( depar_city, dest_city, date, price, num_seat, availability, fly_id, company) "+
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 
				offer.getDepaCity(),
				offer.getDestCity(), 
				offer.getDate(),
				offer.getPrice(),
				offer.getNum_seat(),
				offer.isAvailability(),
				offer.getFlyID(),
				offer.getCompany());
		}
		long id = jdbcTemplate.queryForObject("SELECT id FROM offers WHERE fly_id = ? AND company = ?", Long.class, offer.getFlyID(), offer.getCompany());
		execution.setVariable("offerID", id);
	}

}
