package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import unibo.progetto.apis.AirCompanyService;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Component
public class SendSOAPReqAvailableSeats implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String flyID	= execution.getVariable("flyID").toString();
		String company	= execution.getVariable("company").toString();
		
		try {
			Document document = (new AirCompanyService(company)).reqAvailableSeats(flyID);
			
			analyze(document, execution);
		} catch(Exception e) {
			throw new BpmnError("ErrorCAAvailableSeats", "errorGeneric");
		}

		return;
	}//end execute
	
	private void analyze(Document document, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			int availableSeats = Integer.parseInt(((NodeList) xPath.compile("//availableSeats").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent());
			execution.setVariable("availableSeats", availableSeats);
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
