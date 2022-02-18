package unibo.progetto.acmesky;

import java.util.List;
import java.util.Random;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;

public class MessageSender implements JavaDelegate {
	
	private Random rand = new Random();
	
	protected String generateBusinessKey() {
		return String.valueOf(rand.nextInt(500));
	}

	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final String msgID = (String) execution.getVariable("messageID");
		String businessKeySubInstance;
		if (execution.getVariable("businessKeySubInstance") == null) {
			businessKeySubInstance = generateBusinessKey();
			execution.setVariable("businessKeySubInstance", businessKeySubInstance);
		}else {
			businessKeySubInstance = (String) execution.getVariable("businessKeySubInstance");
		}

		MessageCorrelationBuilder msgBuilder = execution.getProcessEngineServices()
														.getRuntimeService()
														.createMessageCorrelation(msgID)
														.processInstanceBusinessKey(businessKeySubInstance);
		
		
		@SuppressWarnings("unchecked")
		List<String> variables = (List<String>) execution.getVariable("variables");
		for(String variable : variables) {
			msgBuilder=msgBuilder.setVariable(variable, execution.getVariableTyped(variable));
		}
		
		String sessionID = execution.getProcessInstanceId();
		
		msgBuilder.setVariable("callerSessionID", sessionID);

		msgBuilder.correlate();
	}

}
