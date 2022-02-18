package unibo.progetto.acmesky;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import wsdl.service.OfferLastMinuteRequest;
import wsdl.service.PaymentNotificationRequest;
import wsdl.service.PaymentNotificationResponse;

@Endpoint
public class ServicesEndpoint {
	
	private static final String NAMESPACE_URI = "Services.wsdl";
	
	private BankRepository bankRepository;

	@Autowired
	public ServicesEndpoint(BankRepository bankRepository) {
		this.bankRepository = bankRepository;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "paymentNotificationRequest")
	@ResponsePayload
	public PaymentNotificationResponse paymentNotificationRequest(@RequestPayload PaymentNotificationRequest request) {

		String sessionBussinessKey = bankRepository.findSession(request.getPaymentID().intValue());
		PaymentNotificationResponse response = new PaymentNotificationResponse();
		response.setSid(request.getSid());
		
		if(sessionBussinessKey!=null) {
			String msgName = "MsgConfirmBuy";
			
			bankRepository.removeSession(request.getPaymentID().intValue());
			
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService=processEngine.getRuntimeService();
			runtimeService.createMessageCorrelation(msgName)
					.processInstanceBusinessKey(sessionBussinessKey)
					.setVariable("bankSid", request.getSid())
					.correlate();
			
			response.setError(false);
		}else {
//			se entro qui vuol dire che non era valido quel identificativo di pagamento
			response.setError(true);
		}
		
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "offerLastMinuteRequest")
	public void offerLastMinuteRequest(@RequestPayload OfferLastMinuteRequest request) {
		Proposal proposal = new Proposal(request.getId().intValue(), 
				request.getDepaCountry(),
				request.getDepaCity(),
				request.getDestCountry(),
				request.getDestCity(),
				request.getTimestamp(),
				(float) request.getPrice(),
				request.getNumSeat(),
				request.getCompany()
				);

		String msgName = "MsgNewLastMinutOffer";

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService=processEngine.getRuntimeService();
		runtimeService.createMessageCorrelation(msgName)
				.setVariable("proposalLM", proposal)
				.correlate();
	}

}
