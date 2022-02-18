package unibo.progetto.acmesky;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import unibo.progetto.apis.AirCompanyService;

public class SendSOAPReqPurchaseOffer implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		try {
		
			final int flyOfferID = (int) execution.getVariable("flyID");
			final String company = execution.getVariable("company").toString();
			final int quantity	 = (int) execution.getVariable("quantity");
			
			Document document =  (new AirCompanyService(company)).reqPurchaseOffer(flyOfferID, quantity);
			
			analyze(document, execution);
		} catch(Exception e) {
			throw new BpmnError("ErrorBuyTickets", "buyTicketsCA");
		}
		return;
	}//end execute
	
	private void analyze(Document document, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String sidPaymentTicketCA = ((NodeList) xPath.compile("//sid").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent();	
			
			NodeList nodes = (NodeList) xPath.compile("//ticket").evaluate(document, XPathConstants.NODESET);	
			List<Integer> ticketsId = new ArrayList<Integer>();
			for(int i=0; i<nodes.getLength(); i++ ) {
				NodeList attributes = nodes.item(i).getChildNodes();
				int id = Integer.parseInt(attributes.item(0).getTextContent());
				ticketsId.add(id);
			}
			
			execution.setVariable("ticketsOk", true);
			execution.setVariable("sidPaymentTicketCA", sidPaymentTicketCA);
			execution.setVariable("ticketsId", ticketsId);
			
		} catch (XPathExpressionException e) {
			execution.setVariable("ticketsOk", false);
		}
	}//end analyze
}
