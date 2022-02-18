package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import unibo.progetto.apis.AirCompanyService;

public class SendSOAPReqCancelPurchase implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String sidPaymentTicketCA = execution.getVariable("sidPaymentTicketCA").toString();
		String company	= execution.getVariable("company").toString();
		
		(new AirCompanyService(company)).reqCancelPurchase(sidPaymentTicketCA);
		return;
	}//end execute
	
}
