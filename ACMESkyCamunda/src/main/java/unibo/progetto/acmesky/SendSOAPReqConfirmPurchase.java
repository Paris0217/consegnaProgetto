package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import unibo.progetto.apis.AirCompanyService;

@Component
public class SendSOAPReqConfirmPurchase implements JavaDelegate {
		
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		try {
			
			String sidPaymentTicketCA = execution.getVariable("sidPaymentTicketCA").toString();
			String company	= execution.getVariable("company").toString();
	
			(new AirCompanyService(company)).reqConfirmPurchase(sidPaymentTicketCA);
			
		} catch (Exception e) {
			throw new BpmnError("ErrorTicketsFinal", "errorGeneric");
		}
		return;
	}//end execute
	
}
