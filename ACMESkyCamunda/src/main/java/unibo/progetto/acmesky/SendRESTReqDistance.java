package unibo.progetto.acmesky;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;

public class SendRESTReqDistance implements JavaDelegate {
	
	private String encodeValue(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
//		Parametri per la richiesta
		String origin = execution.getVariable("origin").toString();
		String destination = execution.getVariable("destination").toString();
//		Formazione dell'url
		origin=encodeValue(origin.replaceAll(" ","+"));
		destination=encodeValue(destination.replaceAll(" ","+"));
		String urlString = "http://127.0.0.1:4010?origin="+origin+"&destination="+destination;
//		Creazione richiesta REST
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		try {
			if(con.getResponseCode() == 200) {
				//Leggere risposta
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuilder content = new StringBuilder();
				while ((inputLine = in.readLine()) != null) { 
							content.append(inputLine);
						}
				in.close();
				
				String JSON = content.toString();
				JsonValue jvalue = SpinValues.jsonValue(JSON).create();
				
				this.analyze(jvalue, execution);
			
			} else {
				
				//Leggere messaggio di errore
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuilder content = new StringBuilder();
				while ((inputLine = in.readLine()) != null) { 
					content.append(inputLine);
				}
				in.close();
				
				execution.setVariable("statusCode", 500);
			}
		}catch(ConnectException ex) {
			throw new BpmnError("ErrorSendReq", "connectError");
		}
		
		con.disconnect();
		
	}//end execute
	
	private void analyze(JsonValue jsonResponse, DelegateExecution execution) {
		SpinJsonNode jsonSpin = jsonResponse.getValue();
		int status = jsonSpin.prop("statusCode").numberValue().intValue();
		double distance = jsonSpin.prop("distance").numberValue().floatValue();
		execution.setVariable("statusCode", status);
		execution.setVariable("distance", distance);
	}//end analyze

}//end class
