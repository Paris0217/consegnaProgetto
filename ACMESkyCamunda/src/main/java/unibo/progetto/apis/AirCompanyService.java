package unibo.progetto.apis;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AirCompanyService {
	
	private String	ip;
	private int		port;
	
//	poichè le CA sono più di una, allora bisogna avere un modo per specificare quale si vuole contattare
	public AirCompanyService(String comanyName) {
		if(comanyName.equals("Wingsitaly")) {
			this.ip = "localhost";
			this.port = 5010;
		} else if(comanyName.equals("Lufthansia")) {
			this.ip = "localhost";
			this.port = 5110;
		} else if(comanyName.equals("FacileJet")) {
			this.ip = "localhost";
			this.port = 5210;
		} else {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}
	
	public void reqConfirmPurchase(String flyOfferID) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <confirmPurchase xmlns=\"AirCompanyService.xsd\">\n"
								+ "      <sid>" + flyOfferID + "</sid>\n"
								+ "    </confirmPurchase>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
						
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "confirmPurchase");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			@SuppressWarnings("unused")
			BufferedReader in = new BufferedReader(isr);
			 
		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		} catch(java.net.UnknownHostException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}//end reqConfirmPurchase
	
	public Document reqSearchOffers(List<String> depCity, List<String> depCountry, List<String> depTS, List<String> arCity, List<String> arCountry, List<String> arTS, List<Float> maxPrice) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =		  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
									+ "  <soap:Body>\n"
									+ "    <searchOffers xmlns=\"AirCompanyService.xsd\">\n";
			
			for(int prefID=0; prefID<depCity.size(); prefID++) {
				xmlInput = xmlInput + "      <preferences>\n"
									+ "        <departureCountry>"+depCountry.get(prefID)+"</departureCountry>\n"
									+ "        <preferenceID>"+prefID+"</preferenceID>\n"
									+ "        <fromTimestamp>"+depTS.get(prefID)+"</fromTimestamp>\n"
									
									+ ((depCity.get(prefID) == null || depCity.get(prefID).equals("")) ? "" : "        <departureCity>"+depCity.get(prefID)+"</departureCity>\n")
									
									+ "        <maxPrice>"+maxPrice.get(prefID)+"</maxPrice>\n"
									
									+ ((arCity.get(prefID) == null || arCity.get(prefID).equals("")) ? "" :  "        <arrivalCity>"+arCity.get(prefID)+"</arrivalCity>\n")
									
									+ "        <toTimestamp>"+arTS.get(prefID)+"</toTimestamp>\n"
									+ "        <arrivalCountry>"+arCountry.get(prefID)+"</arrivalCountry>\n"
									+ "      </preferences>\n";
			}
			xmlInput = xmlInput +	"    </searchOffers>\n"
									+ "  </soap:Body>\n"
									+ "</soap:Envelope>";
						
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "searchOffers");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			 
			String responseString = "";
			String outputString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			
			return parseXmlFile(outputString);
		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		} catch(java.net.UnknownHostException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}//end reqSearchOffers
	
	public Document reqAvailableSeats(String flyID) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <availableSeats xmlns=\"AirCompanyService.xsd\">\n"
								+ "      <offerID>" + flyID + "</offerID>\n"
								+ "    </availableSeats>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "availableSeats");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			 
			String responseString = "";
			String outputString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			
			return parseXmlFile(outputString);
		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		} catch(java.net.UnknownHostException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}//end reqAvailableSeats
	
	public void reqCancelPurchase(String flyOfferID) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <cancelPurchase xmlns=\"AirCompanyService.xsd\">\n"
								+ "      <sid>" + flyOfferID + "</sid>\n"
								+ "    </cancelPurchase>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "cancelPurchase");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			@SuppressWarnings("unused")
			BufferedReader in = new BufferedReader(isr);

		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		} catch(java.net.UnknownHostException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}//end reqCancelPurchase
	
	public Document reqPurchaseOffer(int flyID, int seats) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
					+ "  <soap:Body>\n"
					+ "    <purchaseOffer xmlns=\"AirCompanyService.xsd\">\n"
					+ "      <offerID>"+flyID+"</offerID>\n"
					+ "      <seats>"+seats+"</seats>\n"
					+ "    </purchaseOffer>\n"
					+ "  </soap:Body>\n"
					+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "purchaseOffer");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			 
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			 
			String responseString = "";
			String outputString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			
			return parseXmlFile(outputString);
		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		} catch(java.net.UnknownHostException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}
	
	private Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}//end parseXmlFile
	
}
