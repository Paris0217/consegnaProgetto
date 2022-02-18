package unibo.progetto.acmesky;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import unibo.progetto.apis.BankService;

public class SendSOAPReqRequestPayment implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		try {
			
			int paymentId	= Integer.parseInt(execution.getVariable("paymentID").toString());
			double cost	= Double.parseDouble(execution.getVariable("cost").toString());
			String card	= execution.getVariable("card").toString();
			int secretNumber = Integer.parseInt(execution.getVariable("secretNumber").toString());
			
			Document document = (new BankService()).reqRequestPayment(paymentId, cost, card, secretNumber);
			analyze(document, execution);
		} catch (Exception e) {
			throw new BpmnError("ErrorBank", "buyTicketsCA");
		}
	}
	
	private void analyze(Document document, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String sid = ((NodeList) xPath.compile("//sid").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent();
			execution.setVariable("sid", sid);
			boolean error = Boolean.parseBoolean(((NodeList) xPath.compile("//error").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent());
			execution.setVariable("error", error);
		} catch (XPathExpressionException e) {

		}
	}
	
}
