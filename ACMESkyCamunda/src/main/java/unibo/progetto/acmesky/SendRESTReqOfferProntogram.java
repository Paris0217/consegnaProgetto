package unibo.progetto.acmesky;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;

public class SendRESTReqOfferProntogram implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String reference	  = execution.getVariable("reference").toString();
		String sender		  = "1234567890";
		String text			  = execution.getVariable("text").toString();
		final String tokenURI = (String) execution.getVariable("tokenURI");
		text = text + ". URL: http://localhost:8080/ACMESky/offerta/" + tokenURI;
		String urlParameters  = "reference=" + reference + "&sender=" + sender + "&text=" + text;
		byte[] postData		  = urlParameters.getBytes( StandardCharsets.UTF_8 );
		
		String userNumber = execution.getVariable("userNumber").toString();
		String urlString = "http://localhost:4000/api/messages/" + userNumber;
		
		//Creazione richiesta REST
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Accept", "application/json");
		
		try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
			wr.write( postData );
		}
		
		if(con.getResponseCode() == 200) {
			//Leggere risposta
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) { 
						content.append(inputLine);
					}
			in.close();
			
			var JSON = content.toString();
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
			
//			TODO: inserire throw del messaggio di eccezione.
		}
		
		con.disconnect();
		
	}//end execute
	
	private void analyze(JsonValue jsonResponse, DelegateExecution execution) {
		SpinJsonNode jsonSpin = jsonResponse.getValue();
		execution.setVariable("statusCode", jsonSpin.prop("statusCode").numberValue().intValue());
		execution.setVariable("error", jsonSpin.prop("error").boolValue());
	}//end analyze

}//end class
