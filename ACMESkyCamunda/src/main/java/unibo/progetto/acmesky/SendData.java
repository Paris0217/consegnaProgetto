package unibo.progetto.acmesky;

import java.util.Random;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendData implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {		
		
		String sessionID = (String) execution.getVariable("callerSessionID");
		final int quantity = (int) execution.getVariable("quantity");
		
		RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
		
		runtimeService.setVariableLocal(sessionID, "checkURI", execution.getVariable("checkURI"));
		runtimeService.setVariableLocal(sessionID, "userProfile", execution.getVariable("userProfile"));
		runtimeService.setVariableLocal(sessionID, "quantity", quantity);
		
		final double priceOneTicket = (float) execution.getVariable("priceOneTicket");
		
		int payment_id = Math.abs((new Random()).nextInt());
		double priceTotal = priceOneTicket * quantity;
		
		execution.setVariable("payment_id", payment_id);
		execution.setVariable("priceTotal", priceTotal);
		runtimeService.setVariableLocal(sessionID, "payment_id", payment_id);
		runtimeService.setVariableLocal(sessionID, "priceTotal", priceTotal);
		runtimeService.setVariableLocal(sessionID, "cardNumber", execution.getVariable("cardNumber"));
	}

}
