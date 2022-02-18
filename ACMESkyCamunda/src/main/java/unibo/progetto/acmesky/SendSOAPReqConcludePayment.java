package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import unibo.progetto.apis.BankService;

public class SendSOAPReqConcludePayment implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		try {
			String bankSid = execution.getVariable("bankSid").toString();
			(new BankService()).reqConcludePayment(bankSid);
		}catch(Exception e) {
			throw new BpmnError("ErrorConcludePayment", "errorGeneric");
		}
		return;
	}//end execute
	
}
