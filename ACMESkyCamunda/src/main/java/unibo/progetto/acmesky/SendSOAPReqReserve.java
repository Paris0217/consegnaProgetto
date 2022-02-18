package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import unibo.progetto.apis.RentalService;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Component
public class SendSOAPReqReserve implements JavaDelegate {
		
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String address	= execution.getVariable("address").toString();
		String name		= execution.getVariable("name").toString();
		String nameCN	= execution.getVariable("nameCN").toString();
		Long timestamp	= Long.parseLong(execution.getVariable("timestamp").toString());
		
		Document document = (new RentalService(nameCN)).reqReserve(address, name, timestamp);
		
		analyze(document, execution);
		
		return;
	}//end execute
	
	private void analyze(Document document, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String  idCDN = ((NodeList) xPath.compile("//sid").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent();
			boolean reserved = Boolean.parseBoolean(((NodeList) xPath.compile("//reserved").evaluate(document, XPathConstants.NODESET)).item(0).getTextContent());
			execution.setVariable("idCDN", idCDN);
			execution.setVariable("reserved", reserved);
		} catch (XPathExpressionException e) {
//			e.printStackTrace();
		}
	}
	
}
