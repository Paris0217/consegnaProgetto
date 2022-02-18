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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RentalService {
	
	private String	ip;
	private int		port;
	
	public RentalService(String comanyName) {
		if(comanyName.equals("prima")) {
			this.ip = "localhost";
			this.port = 5004;
		} else if(comanyName.equals("seconda")) {
			this.ip = "localhost";
			this.port = 5005;
		} else {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}
	
	public Document reqReserve(String address, String name, Long timestamp) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
							+ "  <soap:Body>\n"
							+ "    <reserve xmlns=\"ServerService.xsd\">\n"
							+ "      <address>"   + address +	"</address>\n"
							+ "      <name>"	  + name +		"</name>\n"
							+ "      <timestamp>" + timestamp + "</timestamp>\n"
							+ "    </reserve>\n"
							+ "  </soap:Body>\n"
							+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "reserve");
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
	}//end reqReserve
	
	public Document reqCancelReservation(String idCDN) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <cancelReservation xmlns=\"RentalService.xsd\">\n"
								+ "      <id>\n"
								+ "        <sid>" + idCDN + "</sid>\n"
								+ "      </id>\n"
								+ "    </cancelReservation>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "cancelReservation");
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
	}//end reqCancelReservation
	
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
