package unibo.progetto.acmesky;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import org.camunda.bpm.engine.impl.el.JuelExpression;

public class RemovePayment implements ExecutionListener {

	private JuelExpression paymentId;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		int paymentID = Integer.parseInt(paymentId.getValue(execution).toString());
		(new BankRepository()).removeSession(paymentID);

	}

}
