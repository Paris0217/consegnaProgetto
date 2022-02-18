package unibo.progetto.acmesky;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendTickets implements JavaDelegate  {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String sessionID = (String) execution.getVariable("callerSessionID");

		RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
		
		runtimeService.setVariableLocal(sessionID, "ticketsId", execution.getVariable("ticketsId"));

		try {
			boolean reserved = Boolean.parseBoolean(execution.getVariable("reserved").toString());
			String name = execution.getVariable("name").toString();
			String timestamp = execution.getVariable("timestamp").toString();
			
			runtimeService.setVariableLocal(sessionID, "reserved", reserved);
			runtimeService.setVariableLocal(sessionID, "name", name);
			runtimeService.setVariableLocal(sessionID, "timestamp", timestamp);


		} catch(Exception e) {
			throw new BpmnError("ErrorSendFinalTickets", "errorGeneric");
		}
		
	}

}
