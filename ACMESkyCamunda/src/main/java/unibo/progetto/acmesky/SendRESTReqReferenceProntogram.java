package unibo.progetto.acmesky;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;

public class SendRESTReqReferenceProntogram implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String urlString = "http://localhost:4000/api/reference";
		
		//		Creazione richiesta REST
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
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
		execution.setVariable("reference", jsonSpin.prop("reference").stringValue());
	}//end analyze

}//end class
