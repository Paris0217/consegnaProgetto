package unibo.progetto.acmesky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.json.SpinJsonPropertyException;
import org.camunda.spin.plugin.variable.value.JsonValue;

import static org.camunda.spin.Spin.JSON;

public class CheckInterest implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		ObjectValue interestsListObj = execution.getVariableTyped("interests");
		@SuppressWarnings("unchecked")
		List<Interest> interests = (List<Interest>) interestsListObj.getValue();
		execution.removeVariable("interests");
		
//		creare una lista vuota per le proposte
		List<Proposal> proposals=new ArrayList<Proposal>();
//		se CAList non è null:
		if(execution.getVariable("CAList")!=null) {
			JsonValue proposalsJsonValue;
			try {
				proposalsJsonValue = execution.getVariableTyped("proposals");
				SpinJsonNode proposalsJson = proposalsJsonValue.getValue();
				String listJSONString = (String) execution.getVariable("CAList");
				SpinList<SpinJsonNode> airlineCompanies = JSON(listJSONString).prop("elements").elements();
//				Per ogni elemento.name in CAList
				Iterator<SpinJsonNode> iter = airlineCompanies.iterator();
				while(iter.hasNext()) {
					SpinJsonNode airlineCompany = iter.next();
					
					try {
	/*
	 * 					proposalsJson dovrà avere questa forma:
	 * 					{ "elements": [
	 * 						{ "nameCompany0": [{
	 * 												"id": string0
	 * 												"depaCountry": string0
	 * 												"depaCity": string0
	 * 												"destCountry": string0
	 * 												"destCity": string0
	 * 												"date": string0
	 * 												"price": float0
	 * 												"num_seat": int0
	 * 												"name": string0      <- questo è il nome della compagnia
	 * 											},
	 * 											....
	 *											{
	 * 												"id": stringM
	 * 												"depaCountry": stringM
	 * 												"depaCity": stringM
	 * 												"destCountry": stringM
	 * 												"destCity": stringM
	 * 												"date": stringM
	 * 												"price": floatM
	 * 												"num_seat": intM
	 * 												"name": stringM
	 * 											}],
	 * 						  ....,
	 * 						{ "nameCompanyN": [ ... ]}]
	 * 					}
	 */
						
						Iterator<SpinJsonNode> proposalJsonListIterator = proposalsJson.prop(airlineCompany.prop("name").stringValue()).elements().iterator();
						while(proposalJsonListIterator.hasNext()) {
							SpinJsonNode proposalJson = proposalJsonListIterator.next();
							Proposal proposal = createProposal(proposalJson, airlineCompany.prop("name").stringValue());
							addProposal(proposals,proposal);
						}
					} catch(SpinJsonPropertyException e) {
//						Se entra qui vuol dire che non ha funzionato la comunicazione con quella CA e quindi basta saltarla
					}
				}
			}catch(Exception e) {
//				Se entra qui vuol dire che nessuna CA ha risposto e si lascia le proposte vuote
			}
			
		} else {
//		altrimenti:
//			leggere l'offerta last minut ed aggiungerla alla lista delle proposte
			ObjectValue proposalsObj = execution.getVariableTyped("proposalLM");
			Proposal proposal = (Proposal) proposalsObj.getValue();
			proposals.add(proposal);
			
			execution.removeVariable("proposalLM");
		}
		
		PossibleOffers possibleOffers = new PossibleOffers();

//		Per ogni interesse
		for(Interest interest : interests) {
			for(Proposal proposal : proposals) {
//				controllare tra le quotazioni ottenute quali rispettano i campi
//				Se la città è specificata allora si guarda quella, altrimenti si guarda lo stato
//				prima quella di partenza. Poi quella di arrivo
//				Controllo che la data sia compresa nel periodo
//				Controlla che il prezzo sia inferiore
//				Controlla che il numero di posti sia sufficiente
				if( (((interest.getDepaCity()!=null) && (interest.getDepaCity().equals(proposal.getDepaCity()))) ||
					 (interest.getDepaCountry().equals(proposal.getDepaCountry())))
					&&
					(((interest.getDestCity()!=null) && (interest.getDestCity().equals(proposal.getDestCity()))) ||
					 (interest.getDestCountry().equals(proposal.getDestCountry())))
					&&
					((proposal.getDate().after(interest.getPeriodStart()) || proposal.getDate().equals(interest.getPeriodStart())) && 
					 (interest.getPeriodEnd()==null || (proposal.getDate().before(interest.getPeriodEnd())  || proposal.getDate().equals(interest.getPeriodEnd()))))
					&&
					(proposal.getPrice() <= interest.getPrice())
					&&
					(proposal.getNum_seat() >= interest.getNumSeat())
				) {
					possibleOffers.addElement(proposal, interest.getId());
				}
			}
		}
		
		execution.setVariable("possibleOffers", possibleOffers.getElements().toString());
	}
	
	private void addProposal(List<Proposal> proposals, Proposal proposal) {
		for( Proposal el : proposals ) {
			if((el.getId() == proposal.getId()) && (el.getCompany().equals(proposal.getCompany()))) {
				return;
			}
		}
		proposals.add(proposal);
	}
	
	private Proposal createProposal(SpinJsonNode proposalsJson, String companyName) {
		return new Proposal(Integer.parseInt(proposalsJson.prop("id").stringValue()),
							proposalsJson.prop("depaCountry").stringValue(),
							proposalsJson.prop("depaCity").stringValue(),
							proposalsJson.prop("destCountry").stringValue(),
							proposalsJson.prop("destCity").stringValue(),
							proposalsJson.prop("date").stringValue(),
							Float.parseFloat(proposalsJson.prop("price").numberValue().toString()),
							Integer.parseInt(proposalsJson.prop("num_seat").numberValue().toString()),
							companyName);
	}
}
