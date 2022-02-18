package unibo.progetto.acmesky;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.spin.json.SpinJsonNode;

import static org.camunda.spin.Spin.*;

public class PossibleOffers implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<Proposal, List<Long>> listToSave;

	public PossibleOffers() {
		this.listToSave = new HashMap<Proposal, List<Long>>();
	}
	
	public void addElement(Proposal proposal, Long interest_id) {
		if(!listToSave.containsKey(proposal)) {
			ArrayList<Long> list = new ArrayList<Long>();
			list.add(interest_id);
			listToSave.put(proposal, list);
		}else {
			List<Long> interests = listToSave.get(proposal);
			interests.add(interest_id);
			listToSave.replace(proposal, interests);
		}
	}
	
	public SpinJsonNode getElements() {
		SpinJsonNode jsonToSend = JSON("{}");
		ArrayList<Object> elements = new ArrayList<Object>();

		listToSave.forEach((proposal, interestsID) -> {
			SpinJsonNode proposalJson = JSON("{}");
			proposalJson.prop("id", proposal.getId());
			proposalJson.prop("depaCountry", proposal.getDepaCountry());
			proposalJson.prop("depaCity", proposal.getDepaCity());
			proposalJson.prop("destCountry", proposal.getDestCountry());
			proposalJson.prop("destCity", proposal.getDestCity());
			proposalJson.prop("date", proposal.getDateString());
			proposalJson.prop("price", proposal.getPrice());
			proposalJson.prop("num_seat", proposal.getNum_seat());
			proposalJson.prop("company", proposal.getCompany());
			ArrayList<Object> interestsIDList = new ArrayList<Object>();
			interestsID.forEach((interestID) -> interestsIDList.add(interestID));
			proposalJson.prop("interestsID", interestsIDList);
			elements.add(proposalJson);
		});
		
		jsonToSend.prop("elements", elements);

		return jsonToSend;
	}

//	serve per fare debugging
	public void print() {
		listToSave.forEach((proposal, interestsID) -> {
			System.out.print(proposal +" per gli interessi = [");
			interestsID.forEach(prop -> System.out.print(prop +", "));
			System.out.println("]");
;		});
	}
}
