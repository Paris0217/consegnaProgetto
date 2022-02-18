package unibo.progetto.acmesky;

import java.util.Random;
import java.util.stream.IntStream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.value.JsonValue;

public class CreateTokenURI implements JavaDelegate {
	
	private String generateRandomToken(int tokenLen) {
		final String CHAR_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Random rnd = new Random();
		return IntStream.range(0, tokenLen)
						.mapToObj(i -> ""+CHAR_LIST.charAt(rnd.nextInt(CHAR_LIST.length())))
						.reduce("",String::concat);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final long offerID = (long) execution.getVariable("offerID");
		final JsonValue userIDJsonValue = execution.getVariableTyped("interestID");
		final SpinJsonNode userIDJson = userIDJsonValue.getValue();
		final long interestID = Long.parseLong(userIDJson.toString());
		final String tokenURI = generateRandomToken(20);
		
		execution.setVariable("tokenURI", tokenURI);
		execution.setVariable("offerID", offerID);
		execution.setVariable("interestID", interestID);
	}

}
