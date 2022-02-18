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

import unibo.progetto.apis.RentalService;

public class SendSOAPReqCancelReservation implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String idCDN	= execution.getVariable("idCDN").toString();
		String nameCN	= execution.getVariable("nameCN").toString();
		
		try {
			Document document = (new RentalService(nameCN)).reqCancelReservation(idCDN);
			
			analyze(document, execution);
		} catch (Exception e) {
			throw new BpmnError("ErrorCDNCancelReservation", "errorGeneric");
		}
		return;
	}
	
	private void analyze(Document document, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			boolean canceled = Boolean.parseBoolean(((NodeList) xPath.compile("//canceled").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent());
//			execution.setVariable("canceled", canceled);
			if(!canceled) {
				throw new BpmnError("ErrorCDNCancelReservation");
			}
		} catch (XPathExpressionException e) {
			throw new BpmnError("ErrorCDNCancelReservation");
		}
	}

}
