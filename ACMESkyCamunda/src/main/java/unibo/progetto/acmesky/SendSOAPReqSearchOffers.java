package unibo.progetto.acmesky;

import static org.camunda.spin.Spin.JSON;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.spin.json.SpinJsonNode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import unibo.progetto.apis.AirCompanyService;


public class SendSOAPReqSearchOffers implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String elementCAJsonString = execution.getVariable("elementCA").toString();
		String company  = JSON(elementCAJsonString).prop("name").stringValue();
		
		ObjectValue interestsListObj = execution.getVariableTyped("interestsMerged");
		@SuppressWarnings("unchecked")
		List<Interest> interests = (List<Interest>) interestsListObj.getValue();
		
		List<String> depCity = new ArrayList<String>();
		List<String> depCountry = new ArrayList<String>();
		List<String> depTS = new ArrayList<String>();
		List<String> arCity = new ArrayList<String>();
		List<String> arCountry = new ArrayList<String>();
		List<String> arTS = new ArrayList<String>();
		List<Float> maxPrice = new ArrayList<Float>();
		for(Interest interest : interests) {
			depCity.add(interest.getDepaCity());
			depCountry.add(interest.getDepaCountry());
			depTS.add(interest.getPeriodStartString());
			arCity.add(interest.getDestCity());
			arCountry.add(interest.getDestCountry());
			arTS.add(interest.getPeriodEndString());
			maxPrice.add(interest.getPrice());
		}
		
		Document document = (new AirCompanyService(company)).reqSearchOffers(depCity, depCountry, depTS, arCity, arCountry, arTS, maxPrice);
//		
		analyze(document, company, execution);
		return;
	}//end execute
	
	private void analyze(Document document, String companyName, DelegateExecution execution) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			
/*
 * 			CONTENUTO DELLA RISPOSTA
 *			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 * 				<soap:Body>
 * 					<searchOffersResponse xmlns="AirCompanyService.xsd">
 * 						<offers>
 * 							<offer>
 * 								<departure_city>string0</city>
 * 								<departure_country>string0</country>
 *  * 							<destination_city>string0</city>
 * 								<destination_country>string0</country>
 * 								<price>81</price>
 * 								<num_seat>10</num_seat>
 * 								<offerID>0</offerID>
 * 								<timestamp>100</timestamp>
 * 							</offer>
 * 							...
 * 							<offer>
 * 								<departure_city>stringN</city>
 * 								<departure_country>stringN</country>
 *  * 							<destination_city>stringN</city>
 * 								<destination_country>stringN</country>
 * 								<price>23</price>
 * 								<offerID>N</offerID>
 *  * 							<num_seat>32</num_seat>
 * 								<timestamp>100</timestamp>
 * 							</offer>
 * 						</offers>
 * 					</searchOffersResponse>
 * 				</soap:Body>
 * 			</soap:Envelope>
 */
			String proposals = execution.getVariable("proposals").toString();
			SpinJsonNode proposalsJson = JSON(proposals);
			ArrayList<Object> elements = new ArrayList<Object>();
			
			NodeList nodes = (NodeList) xPath.compile("//offers").evaluate(document, XPathConstants.NODESET);	
			for(int i=0; i<nodes.getLength(); i++ ) {
				NodeList attributes = nodes.item(i).getChildNodes();
				String depacountry = attributes.item(0).getTextContent();
//				String preferenceID = attributes.item(1).getTextContent();
//				String airportID = attributes.item(2).getTextContent();
				String depacity = attributes.item(3).getTextContent();
				float price = Float.parseFloat(attributes.item(4).getTextContent());
				String id = attributes.item(5).getTextContent();
				String destcity = attributes.item(6).getTextContent();
				int num_seat = Integer.parseInt(attributes.item(7).getTextContent());
				String date = attributes.item(8).getTextContent();
				String destcountry = attributes.item(9).getTextContent();
				
				SpinJsonNode proposalJson = JSON("{}");
				proposalJson.prop("id", id);
				proposalJson.prop("depaCountry", depacountry);
				proposalJson.prop("depaCity", depacity);
				proposalJson.prop("destCountry", destcountry);
				proposalJson.prop("destCity", destcity);
				proposalJson.prop("date", date);
				proposalJson.prop("price", price);
				proposalJson.prop("num_seat", num_seat);
				proposalJson.prop("name", companyName);
				
				elements.add(proposalJson);
			}
			
			proposalsJson.prop(companyName, elements);
			
			execution.setVariable("proposals", proposalsJson);

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
