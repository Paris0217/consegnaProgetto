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

public class BankService {
	
	private String	ip;
	private int		port;
	
	public BankService() {
		this.ip = "localhost";
		this.port = 5001;
	}
	
	public void reqCompensation(String paymentSid) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <compensation xmlns=\"BankService.xsd\">\n"
								+ "      <sid>" + paymentSid + "</sid>\n"
								+ "    </compensation>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "compensation");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			@SuppressWarnings("unused")
			BufferedReader in = new BufferedReader(isr);
			 
			return;
		} catch(ConnectException e) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
	}//end reqCompensation
	
	public void reqConcludePayment(String paymentSid) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setConnectTimeout(5000);
			httpConn.setReadTimeout(5000);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <concludePayment xmlns=\"BankService.xsd\">\n"
								+ "      <sid>" + paymentSid + "</sid>\n"
								+ "    </concludePayment>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
						
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "concludePayment");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			@SuppressWarnings("unused")
			BufferedReader in = new BufferedReader(isr);

			return;
		} catch(ConnectException e) {
			throw new BpmnError("ErrorConcludePayment", "connectError");
		}
	}//end reqConcludePayment
	
	public Document reqRequestPayment(int paymentId, double cost, String card, int secretNumber) throws MalformedURLException, IOException {
		try {
			URL url = new URL("http://"+this.ip+":"+this.port);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			String xmlInput =	  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
								+ "  <soap:Body>\n"
								+ "    <requestPayment xmlns=\"BankService.xsd\">\n"
								+ "      <cost>" 		+ cost + "</cost>\n"
								+ "      <paymentID>" 	+ paymentId + "</paymentID>\n"
								+ "      <secretNumber>"+ secretNumber + "</secretNumber>\n"
								+ "      <card>" 		+ card + "</card>\n"
								+ "    </requestPayment>\n"
								+ "  </soap:Body>\n"
								+ "</soap:Envelope>";
									
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "requestPayment");
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
		}
	}//end reqSearchOffers

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
