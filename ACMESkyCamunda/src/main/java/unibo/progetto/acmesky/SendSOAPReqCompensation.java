package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import unibo.progetto.apis.BankService;

public class SendSOAPReqCompensation implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String bankSid = execution.getVariable("bankSid").toString();
		
		(new BankService()).reqCompensation(bankSid);
		return;
	}//end execute
	
}
