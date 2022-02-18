package unibo.progetto.acmesky;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CheckTokenURI implements JavaDelegate {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final String tokenURI = (String) execution.getVariable("tokenURI");
		execution.removeVariable("tokenURI");
		try {
			Offer interestRefs = jdbcTemplate.query("SELECT fly_id, company, date, interests.user_id, interests.num_seat, offers.price, offers.depar_city FROM offersAdHoc INNER JOIN offers ON offers.id = offersAdHoc.offer_id INNER JOIN interests ON offersAdHoc.interest_id = interests.id WHERE offersAdHoc.token_uri = ?", 
													new RowMapper<Offer>() {
														public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
															Offer offer = new Offer();
															offer.setFlyID(rs.getInt("fly_id"));
															offer.setCompany(rs.getString("company"));
															offer.setDate(rs.getTimestamp("date"));
															offer.setId(rs.getLong("user_id"));			//in realtà dentro ad id c'è l'id utente per non fare una nuova classe
															offer.setNum_seat(rs.getInt("num_seat"));	//in realtà qui ci sono i posti che l'utente vuole e non quelli dell'offerta. (stesso discorso sopra)
															offer.setPrice(rs.getFloat("price"));
															offer.setDepaCity(rs.getString("depar_city"));
															return offer;
														}
													}, tokenURI).get(0);
			execution.setVariable("flyID", interestRefs.getFlyID());
			execution.setVariable("company", interestRefs.getCompany());
			execution.setVariable("userId", interestRefs.getId());			//dentro id c'è l'id dell'utente!!! prima si è messo in offer.id l'offer.user_id!
			execution.setVariable("quantity", interestRefs.getNum_seat());
			execution.setVariable("priceOneTicket", interestRefs.getPrice());
			execution.setVariable("timestamp", interestRefs.getDate().getTime());
			execution.setVariable("destination", interestRefs.getDepaCity());
			long nowLong = System.currentTimeMillis();
			Date now = new Date(nowLong);
			
			if(interestRefs.getDate().after(now)) {
				execution.setVariable("checkURI", true);
			}else {
				execution.setVariable("checkURI", false);
			}
		}catch(EmptyResultDataAccessException e) {
			execution.setVariable("checkURI", false);
		}catch(IndexOutOfBoundsException e) {
			execution.setVariable("checkURI", false);
		}catch(Exception e) {
			execution.setVariable("checkURI", false);
		}
	}
	
}
